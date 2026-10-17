#!/usr/bin/env python3
"""Inventory packaged graphics drivers without altering or installing binaries.

Usage: python3 scripts/audit_graphics_drivers.py
"""
from pathlib import Path
import hashlib
import json
import re

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/app/src/main/assets/graphics_driver"
INSTALLABLE = ROOT / "installable_components/turnip"
SOURCE = ROOT / "app/app/src/main/java/com/winlator/container/GraphicsDrivers.java"


def inventory():
    if not ASSETS.is_dir() or not INSTALLABLE.is_dir() or not SOURCE.is_file():
        raise FileNotFoundError("Required Aurora driver paths are missing")
    packaged = sorted(ASSETS.glob("*.tzst"))
    downloadable = sorted(INSTALLABLE.glob("*.tzst"))
    if not packaged:
        raise ValueError("No packaged graphics drivers found")
    source = SOURCE.read_text(encoding="utf-8")
    names = re.findall(r'public static final String (TURNIP|VORTEK|ZINK|VIRGL|GLADIO) = "([^"]+)";', source)
    def describe(path):
        return {
            "path": path.relative_to(ROOT).as_posix(),
            "size_bytes": path.stat().st_size,
            "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
        }
    return {
        "declared_drivers": dict(names),
        "panvk_declared": bool(re.search(r'\bPANVK\b', source)),
        "packaged": [describe(p) for p in packaged],
        "installable_turnip": [describe(p) for p in downloadable],
        "turnip_index": (INSTALLABLE / "index.txt").read_text(encoding="utf-8").splitlines(),
    }


if __name__ == "__main__":
    print(json.dumps(inventory(), ensure_ascii=False, indent=2))
