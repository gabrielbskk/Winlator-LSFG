#!/usr/bin/env python3
"""Offline unit tests for the SDK-free APK artifact preflight."""
from pathlib import Path
from tempfile import TemporaryDirectory
import unittest
from zipfile import ZipFile, ZIP_DEFLATED

from verify_aurora_sdkfree_apk import REQUIRED, validate


class SdkFreeApkTest(unittest.TestCase):
    def setUp(self):
        self.temp = TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.apk = Path(self.temp.name) / "app-debug.apk"

    def write_zip(self, *, missing=(), extra=()):
        with ZipFile(self.apk, "w", compression=ZIP_DEFLATED) as output:
            for name in REQUIRED - set(missing):
                output.writestr(name, b"test-data")
            output.writestr("lib/arm64-v8a/libaurora.so", b"test-data")
            for name in extra:
                output.writestr(name, b"test-data")

    def test_well_formed_sdk_free_package_passes(self):
        self.write_zip()
        validate(self.apk)

    def test_missing_runtime_is_rejected(self):
        self.write_zip(missing={"assets/rootfs.tzst"})
        with self.assertRaisesRegex(ValueError, "Missing packaged files"):
            validate(self.apk)

    def test_proprietary_lossless_dll_is_rejected(self):
        self.write_zip(extra=["assets/lsfg/Wine/Lossless.DLL"])
        with self.assertRaisesRegex(ValueError, "Forbidden proprietary"):
            validate(self.apk)

    def test_discord_native_sdk_is_not_allowed_in_sdk_free_build(self):
        self.write_zip(extra=["lib/arm64-v8a/libdiscord_partner_sdk.so"])
        with self.assertRaisesRegex(ValueError, "Forbidden proprietary"):
            validate(self.apk)

    def test_non_arm64_library_is_rejected(self):
        self.write_zip(extra=["lib/armeabi-v7a/libunwanted.so"])
        with self.assertRaisesRegex(ValueError, "Unexpected Android ABI"):
            validate(self.apk)

    def test_empty_apk_is_rejected(self):
        self.apk.write_bytes(b"")
        with self.assertRaisesRegex(ValueError, "Missing or empty"):
            validate(self.apk)


if __name__ == "__main__":
    unittest.main()
