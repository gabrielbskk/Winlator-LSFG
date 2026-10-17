#!/usr/bin/env python3
"""Offline regression tests for optional, pinned DXVK review archives."""
from pathlib import Path
import io
import json
import sys
import tarfile
import tempfile
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parent))
from package_dxvk_review import DLLS, build_import_tar, verify_pe


def fake_dll(machine):
    data = bytearray(0x100)
    data[:2] = b"MZ"
    data[0x3c:0x40] = (0x80).to_bytes(4, "little")
    data[0x80:0x84] = b"PE" + bytes(2)
    data[0x84:0x86] = machine.to_bytes(2, "little")
    return bytes(data)


def add(tar, name, content):
    item = tarfile.TarInfo(name)
    item.size = len(content)
    tar.addfile(item, io.BytesIO(content))


class PackageDXVKReviewTest(unittest.TestCase):
    def test_build_manifest_both_wine_architectures(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            official = root / "upstream.tar.gz"
            with tarfile.open(official, "w:gz") as tar:
                for src, machine in (("x64", 0x8664), ("x32", 0x014c)):
                    for dll in DLLS:
                        add(tar, f"dxvk-2.7.1/{src}/{dll}", fake_dll(machine))
                add(tar, "dxvk-2.7.1/LICENSE", b"upstream-license")
            wrapped = root / "import.tar"
            build_import_tar(official, wrapped, "2.7.1")
            with tarfile.open(wrapped) as tar:
                names = set(tar.getnames())
                for destination in ("system32", "syswow64"):
                    for dll in DLLS:
                        self.assertIn(f"{destination}/{dll}", names)
                self.assertIn("DXVK-UPSTREAM-LICENSE.txt", names)
                manifest = json.load(tar.extractfile("aurora-dxvk-manifest.json"))
                self.assertEqual("DXVK", manifest["type"])
                self.assertEqual("2.7.1", manifest["versionName"])
                self.assertEqual(8, len(manifest["files"]))
                for item in manifest["files"]:
                    self.assertEqual(item["source"], item["target"])
                    self.assertIn(item["source"], names)

    def test_rejects_unexpected_architecture(self):
        with self.assertRaises(ValueError):
            verify_pe(fake_dll(0x8664), 0x014c)

    def test_rejects_missing_dll_and_unknown_version(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = root / "empty.tar.gz"
            with tarfile.open(source, "w:gz"):
                pass
            with self.assertRaises(ValueError):
                build_import_tar(source, root / "broken.tar", "2.6.2")
            with self.assertRaises(ValueError):
                build_import_tar(source, root / "broken.tar", "99.99.99")


if __name__ == "__main__":
    unittest.main()
