#!/usr/bin/env python3
"""Validate packaged artifacts before publishing RPC experimental 4.

This is a packaging check, not a substitute for running games or Discord on a
physical Android device.
"""
import argparse
import os
from pathlib import Path
import subprocess
import sys
import tarfile
import tempfile
import zipfile


def validate_tar_asset(apk, asset, required_dir):
    if asset not in apk.namelist():
        raise ValueError(f"Missing packaged asset: {asset}")
    with tempfile.TemporaryDirectory(prefix="rpc-v4-") as temp:
        archive_path = Path(temp) / "payload.tzst"
        with apk.open(asset) as src, archive_path.open("wb") as dest:
            while True:
                block = src.read(1024 * 1024)
                if not block:
                    break
                dest.write(block)
        proc = subprocess.Popen(["zstd", "-q", "-dc", str(archive_path)], stdout=subprocess.PIPE)
        found = False
        try:
            with tarfile.open(fileobj=proc.stdout, mode="r|") as archive:
                for member in archive:
                    name = member.name.lstrip("./")
                    if member.isfile() and name.startswith(required_dir):
                        found = True
            proc.stdout.close()
            if proc.wait() != 0:
                raise ValueError(f"Corrupt Zstd/TAR asset: {asset}")
        finally:
            if proc.poll() is None:
                proc.kill()
                proc.wait()
        if not found:
            raise ValueError(f"Missing Windows 32-bit files under {required_dir} in {asset}")
        print(f"OK 32-bit runtime: {asset} -> {required_dir}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("apk", type=Path)
    args = parser.parse_args()
    if not args.apk.is_file():
        raise ValueError("APK does not exist: " + str(args.apk))
    with zipfile.ZipFile(args.apk) as apk:
        bad = apk.testzip()
        if bad:
            raise ValueError("Corrupt APK ZIP member: " + bad)
        names = set(apk.namelist())
        if "lib/arm64-v8a/libwinlator_discord.so" not in names:
            raise ValueError("Discord RPC native bridge missing: build using official SDK AAR")
        if any(n.lower().endswith("lossless.dll") for n in names):
            raise ValueError("A proprietary Lossless.dll must not be bundled")
        for asset, directory in (
            ("assets/rootfs.tzst", "opt/wine/lib/wine/i386-windows/"),
            ("assets/wincomponents/direct3d.tzst", "syswow64/"),
        ):
            validate_tar_asset(apk, asset, directory)
    print("APK integrity, RPC native bridge and Windows 32-bit file checks passed.")
    print("Device RPC/login and game compatibility still require manual testing.")


if __name__ == "__main__":
    try:
        main()
    except (OSError, ValueError, tarfile.TarError, zipfile.BadZipFile) as exc:
        print("RPC experimental 4 verification failed:", exc, file=sys.stderr)
        sys.exit(1)
