"""Offline checks for the approved MediaFire SDK redirect allowlist."""
import importlib.util
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("mediafire", ROOT / "fetch_discord_sdk_mediafire.py")
mediafire = importlib.util.module_from_spec(spec)
spec.loader.exec_module(mediafire)


class SdkDownloadSafetyTest(unittest.TestCase):
    def test_allows_https_mediafire_cdn_and_first_party_redirects(self):
        self.assertTrue(mediafire.allowed_download_url("https://download2387.mediafire.com/example/file"))
        self.assertTrue(mediafire.allowed_download_url("https://www.mediafire.com/file/example"))
        self.assertFalse(mediafire.allowed_download_url("http://download2387.mediafire.com/file"))
        self.assertFalse(mediafire.allowed_download_url("https://download2387.mediafire.com:8443/file"))

    def test_rejects_external_redirect_and_userinfo(self):
        for url in ("https://download2387.mediafire.com.evil.test/a",
                    "https://evil.test/mediafire.com/a",
                    "https://someone:secret@download2387.mediafire.com/a",
                    "https://evil-mediafire.com/a", "file:///tmp/discord.aar"):
            self.assertFalse(url, mediafire.allowed_download_url(url))

    def test_public_mediafire_page_is_not_treated_as_download_link(self):
        self.assertIsNone(mediafire.extract_link('<a id="downloadButton" href="https://evil.test/a">Download</a>'))


if __name__ == "__main__":
    unittest.main()
