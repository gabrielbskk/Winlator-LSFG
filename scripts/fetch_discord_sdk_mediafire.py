#!/usr/bin/env python3
"""Download a user-provided MediaFire file for a private local/CI SDK build.

No SDK binary is checked into the repository. Always verify the expected SHA256.
Works with a normal public file page, not captcha/password protected links.
"""
import argparse
import base64
import hashlib
from html.parser import HTMLParser
import json
from pathlib import Path
import re
import sys
import urllib.error
import urllib.parse
import urllib.request

MAX_BYTES = 100 * 1024 * 1024
MIN_BYTES = 1 * 1024 * 1024
USER_AGENT = "Mozilla/5.0 (compatible; Winlator-LSFG-Aurora-CI/1.0)"
CDN_HOST = re.compile(r"download[0-9]+\\.mediafire\\.com", re.I)


def open_url(url):
    request = urllib.request.Request(url, headers={
        "User-Agent": USER_AGENT, "Accept-Encoding": "identity",
    })
    return urllib.request.urlopen(request, timeout=35)


def allowed_download_url(url):
    try:
        u = urllib.parse.urlparse(url)
    except ValueError:
        return False
    return (u.scheme == "https" and bool(CDN_HOST.fullmatch(u.hostname or ""))
            and u.username is None and u.password is None)


class ButtonParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.urls = []

    def handle_starttag(self, tag, attrs):
        if tag.lower() != "a":
            return
        a = dict(attrs)
        if a.get("id") != "downloadButton":
            return
        if a.get("data-scrambled-url"):
            try:
                decoded = base64.b64decode(a["data-scrambled-url"]).decode("utf-8")
                self.urls.append(decoded)
            except (ValueError, UnicodeError):
                pass
        if a.get("href"):
            self.urls.append(a["href"])


def extract_link(page):
    parser = ButtonParser()
    parser.feed(page)
    candidates = list(parser.urls)
    # The regular expression is only a fallback for documented MediaFire pages;
    # the allowlist is enforced on all extracted links.
    candidates.extend(re.findall(r'https://download[0-9]+\\.mediafire\\.com/[^\\"\'<> ]+', page))
    for candidate in candidates:
        candidate = candidate.replace("&amp;", "&")
        if allowed_download_url(candidate):
            return candidate
    return None


def sdk_link(quickkey):
    if not re.fullmatch(r"[A-Za-z0-9]{11}|[A-Za-z0-9]{15}", quickkey):
        raise ValueError("Invalid MediaFire file key")
    sources = []
    api = ("https://www.mediafire.com/api/1.5/file/get_info.php"
           "?quick_key=" + quickkey + "&response_format=json")
    try:
        with open_url(api) as response:
            data = json.load(response)
        info = data.get("response", {}).get("file_info", {})
        source = info.get("links", {}).get("normal_download")
        if source:
            sources.append(source)
    except (ValueError, KeyError, OSError, json.JSONDecodeError):
        pass
    sources.append("https://www.mediafire.com/file/" + quickkey + "/discord_partner_sdk.aar")
    sources.append("https://www.mediafire.com/download/" + quickkey + "/discord_partner_sdk.aar")
    for page_url in sources:
        try:
            with open_url(page_url) as response:
                url = response.geturl()
                if allowed_download_url(url):
                    return url
                if response.headers.get("Content-Length") and int(response.headers["Content-Length"]) > 2000000:
                    continue
                page = response.read(2 * 1024 * 1024 + 1).decode("utf-8", "replace")
                found = extract_link(page)
                if found:
                    return found
        except (OSError, ValueError):
            continue
    raise RuntimeError(
        "MediaFire public download link was unavailable. "
        "If the file requires a download key, configure a private SDK URL in Actions secrets."
    )


def download_and_verify(source, path, expected):
    if not re.fullmatch(r"[0-9a-fA-F]{64}", expected):
        raise ValueError("Expected SHA-256 must be 64 hexadecimal digits")
    sha = hashlib.sha256()
    count = 0
    path.parent.mkdir(parents=True, exist_ok=True)
    try:
        with open_url(source) as response, path.open("wb") as output:
            if not allowed_download_url(response.geturl()):
                raise ValueError("MediaFire redirected outside its expected download CDN")
            while True:
                block = response.read(1024 * 1024)
                if not block:
                    break
                count += len(block)
                if count > MAX_BYTES:
                    raise ValueError("Downloaded AAR exceeds size limit")
                sha.update(block)
                output.write(block)
        if count < MIN_BYTES or sha.hexdigest().lower() != expected.lower():
            raise ValueError("MediaFire AAR size/hash mismatch; refusing unverified SDK")
    except Exception:
        path.unlink(missing_ok=True)
        raise
    print("Downloaded and verified licensed SDK candidate: bytes", count)
    print("SHA-256:", sha.hexdigest())


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--quickkey", required=True)
    parser.add_argument("--sha256", required=True)
    parser.add_argument("--out", required=True, type=Path)
    args = parser.parse_args()
    link = sdk_link(args.quickkey)
    download_and_verify(link, args.out, args.sha256)


if __name__ == "__main__":
    try:
        main()
    except (OSError, ValueError, RuntimeError) as error:
        print("MediaFire SDK retrieval failed:", error, file=sys.stderr)
        sys.exit(1)
