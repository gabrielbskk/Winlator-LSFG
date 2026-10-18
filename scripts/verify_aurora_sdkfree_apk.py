#!/usr/bin/env python3
"""Verify SDK-free Aurora APK packaging before uploading CI artifacts.

This is a read-only packaging check. It does NOT establish a valid signing key,
compatibility with any GPU/game, RPC support, or permission to publish Stable.
"""
import argparse
from pathlib import Path
import sys
import zipfile

REQUIRED = {
    "AndroidManifest.xml",
    "classes.dex",
    "assets/rootfs.tzst",
    "assets/wincomponents/direct3d.tzst",
}
PROHIBITED_NAMES = {
    "lossless.dll",
    "discord_partner_sdk.aar",
    "libdiscord_partner_sdk.so",
    "libwinlator_discord.so",
}


def validate(apk_path):
    path = Path(apk_path)
    if not path.is_file() or path.stat().st_size == 0:
        raise ValueError("Missing or empty Android APK")
    with zipfile.ZipFile(path) as apk:
        names = set(apk.namelist())
        missing = sorted(REQUIRED - names)
        if missing:
            raise ValueError("Missing packaged files: " + ", ".join(missing))
        for name in names:
            parts = name.replace("\\", "/").lower().split("/")
            if any(part in PROHIBITED_NAMES for part in parts):
                raise ValueError("Forbidden proprietary/SDK content in SDK-free APK: " + name)
        libraries = [
            name for name in names
            if name.startswith("lib/") and name.lower().endswith(".so")
        ]
        if not libraries:
            raise ValueError("Android native libraries absent")
        for library in libraries:
            parts = library.split("/")
            if len(parts) != 3 or parts[1] != "arm64-v8a":
                raise ValueError("Unexpected Android ABI/native library: " + library)
        broken = apk.testzip()
        if broken:
            raise ValueError("Corrupt APK ZIP member: " + broken)
    print("SDK-free APK ZIP, native ABI, runtime assets and prohibited-content checks: OK")
    print("Device, games, frame generation, update/signing and RPC validation: NOT TESTED")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("apk", type=Path)
    args = parser.parse_args()
    try:
        validate(args.apk)
    except (OSError, ValueError, zipfile.BadZipFile) as error:
        print("Aurora SDK-free APK preflight failed:", error, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
