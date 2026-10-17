#!/usr/bin/env python3
"""Offline regression checks for the read-only Turnip package inventory."""
import hashlib
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

import audit_graphics_drivers as audit


class InventoryTest(unittest.TestCase):
    def test_inventory_lists_all_sources_without_modifying_files(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            assets = root / "app/app/src/main/assets/graphics_driver"
            installed = root / "installable_components/turnip"
            source = root / "app/app/src/main/java/com/winlator/container/GraphicsDrivers.java"
            assets.mkdir(parents=True)
            installed.mkdir(parents=True)
            source.parent.mkdir(parents=True)
            driver = assets / "turnip-26.1.0.tzst"
            driver.write_bytes(b"example-driver")
            downloaded = installed / "turnip-24.1.0.tzst"
            downloaded.write_bytes(b"example-alternative")
            index = installed / "index.txt"
            index.write_text("turnip-24.1.0.tzst\n", encoding="utf-8")
            source.write_text('public static final String TURNIP = "turnip";\n', encoding="utf-8")

            with patch.object(audit, "ROOT", root), \
                 patch.object(audit, "ASSETS", assets), \
                 patch.object(audit, "INSTALLABLE", installed), \
                 patch.object(audit, "SOURCE", source):
                report = audit.inventory()
            self.assertFalse(report["panvk_declared"])
            self.assertEqual("turnip", report["declared_drivers"]["TURNIP"])
            self.assertEqual(1, len(report["packaged"]))
            self.assertEqual(1, len(report["installable_turnip"]))
            self.assertEqual(hashlib.sha256(b"example-driver").hexdigest(),
                             report["packaged"][0]["sha256"])
            self.assertEqual(["turnip-24.1.0.tzst"], report["turnip_index"])
            self.assertEqual(b"example-driver", driver.read_bytes())
            self.assertEqual(b"example-alternative", downloaded.read_bytes())

    def test_missing_sources_fail_explicitly(self):
        with tempfile.TemporaryDirectory() as tmp:
            with patch.object(audit, "ASSETS", Path(tmp) / "missing"):
                with self.assertRaises(FileNotFoundError):
                    audit.inventory()


if __name__ == "__main__":
    unittest.main()
