#!/usr/bin/env python3
"""Guard against accidental resurrection of obsolete public-release actions.

These are static checks, not permission to release or evidence of signing.
"""
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
WORKFLOWS = ROOT / ".github" / "workflows"


class LegacyReleaseGuardTest(unittest.TestCase):
    def test_old_debug_latest_promotion_stays_disabled(self):
        text = (WORKFLOWS / "organize-aurora-releases.yml").read_text(encoding="utf-8")
        self.assertIn("if: ${{ false }}", text)
        self.assertIn("ARCHIVED - Organize Aurora releases", text)

    def test_existing_release_assets_are_not_overwritten(self):
        text = (WORKFLOWS / "publish-rpc-apk.yml").read_text(encoding="utf-8")
        self.assertIn("gh release upload", text)
        self.assertNotIn("--clobber", text)

    def test_sdkfree_upload_is_preceded_by_apk_preflight(self):
        text = (WORKFLOWS / "android-debug.yml").read_text(encoding="utf-8")
        check = text.index("python3 scripts/verify_aurora_sdkfree_apk.py")
        upload = text.index("Upload debug APK for CI inspection")
        self.assertLess(check, upload)
        self.assertIn("python3 scripts/test_aurora_sdkfree_apk.py", text)


if __name__ == "__main__":
    unittest.main()
