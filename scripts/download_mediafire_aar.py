#!/usr/bin/env python3
"""Download a maintainer-provided MediaFire AAR during a disposable CI build.

Do not check the proprietary SDK into git. Always authenticate the exact
binary against the independently checked SHA-256 before Gradle sees it.
"""
import hashlib
from html.parser import HTMLParser
import html
import os
from pathlib import Path
import re
import sys
import time
from urllib.parse import urljoin, urlsplit
from urllib.request import Request, urlopen

MAX_PAGE = 2_000_000
MAX_AAR = 80_000_000
EXPECTED = "b1b2491f1e1848c79fd6f1986d5aa1e0c8019e88a6e62c7be06b89e8e4870933"
DEFAULT_URL = "https://www.mediafire.com/file/ryfql41ytwr2rrs/discord_partner_sdk.aar?dkey=yy4g363zy15&r=1089"


def allowed(url):
    parsed = urlsplit(url)
    host = (parsed.hostname or "").lower()
    return (parsed.scheme == "https"
            and (host == "mediafire.com" or host.endswith(".mediafire.com")
                 or host == "mediafireusercontent.com"
                 or host.endswith(".mediafireusercontent.com")))


class DownloadLinks(HTMLParser):
    def __init__(self):
        super().__init__()
        self.candidates = []

    def handle_starttag(self, tag, attrs):
        if tag != "a":
            return
        d = dict(attrs)
        href = html.unescape(d.get("href", ""))
        if not href:
            return
        attrs_text = " ".join([d.get("id", ""), d.get("class", "")]).lower()
        if "download" in attrs_text or "mediafire.com" in href:
            self.candidates.append((0 if "downloadbutton" in attrs_text else 1, href))


def fetch(url, limit):
    if not allowed(url):
        raise ValueError("Refusing a non-MediaFire or non-HTTPS download host")
    req = Request(url, headers={"User-Agent": "Mozilla/5.0 (compatible; Winlator-LSFG-CI/1.0)",
                                "Accept": "*/*"})
    with urlopen(req, timeout=35) as response:
        if not allowed(response.url):
            raise ValueError("Download redirected outside the trusted MediaFire domains")
        contents = response.read(limit + 1)
        if len(contents) > limit:
            raise ValueError("Response exceeded maximum allowed byte count")
        return contents, response.headers.get("Content-Type", ""), response.url


def download(url, destination):
    page, content_type, final_url = fetch(url, MAX_PAGE)
    if page.startswith(b"PK\\x03\\x04"):
        raise ValueError("Direct binary response is larger than webpage limit")
    decoder = DownloadLinks()
    doc = page.decode("utf-8", "replace")
    decoder.feed(doc)
    links = sorted(decoder.candidates)
    # Some variants expose their CDN download URL in scripts instead of an href.
    for link in re.findall(r"https?(?:\\\\/\\\\/|://)[^\\\"'<>\\s]+", doc):
        link = link.replace("\\/", "/")
        if "download" in link and "mediafire" in link:
            links.append((2, link))
    tried = set()
    for _, href in links:
        target = urljoin(final_url, href)
        if target in tried or not allowed(target):
            continue
        tried.add(target)
        if target == final_url:
            continue
        try:
            data, mime, resolved = fetch(target, MAX_AAR)
        except (OSError, ValueError) as error:
            print("Candidate failed:", type(error).__name__, file=sys.stderr)
            continue
        if not data.startswith(b"PK\\x03\\x04"):
            continue
        digest = hashlib.sha256(data).hexdigest()
        if digest != EXPECTED:
            raise ValueError("AAR SHA-256 mismatch; aborting: " + digest)
        destination.parent.mkdir(parents=True, exist_ok=True)
        destination.write_bytes(data)
        print("Verified licensed AAR: size=%d SHA-256=%s" % (len(data), digest))
        return
    raise RuntimeError("No verified binary downloaded from MediaFire share page")


if __name__ == "__main__":
    output = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("app/app/libs/discord_partner_sdk.aar")
    share_url = os.environ.get("MEDIAFIRE_AAR_URL", DEFAULT_URL)
    for attempt in range(1, 4):
        try:
            download(share_url, output)
            break
        except (OSError, RuntimeError, ValueError) as ex:
            print("Download attempt %d failed: %s" % (attempt, ex), file=sys.stderr)
            if attempt == 3:
                raise SystemExit(1)
            time.sleep(5)
