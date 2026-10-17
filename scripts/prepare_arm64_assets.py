#!/usr/bin/env python3
"""Prepare the optional 64-bit Windows game asset set for an Android build."""

import argparse
import shutil
import subprocess
import tarfile
from pathlib import Path


def repack(source: Path, destination: Path, excluded_directory: str) -> None:
    decoder = subprocess.Popen(["zstd", "-dc", str(source)], stdout=subprocess.PIPE)
    with destination.open("wb") as output:
        encoder = subprocess.Popen(
            ["zstd", "-q", "-17", "--long=27", "-T2", "-c"],
            stdin=subprocess.PIPE,
            stdout=output,
        )
        try:
            with tarfile.open(fileobj=decoder.stdout, mode="r|") as archive:
                with tarfile.open(fileobj=encoder.stdin, mode="w|") as reduced:
                    for member in archive:
                        # Keep tar member metadata and 64-bit files intact.
                        name = member.name.lstrip("./")
                        if name.startswith(excluded_directory) and not member.isdir():
                            continue
                        body = archive.extractfile(member) if member.isfile() else None
                        try:
                            reduced.addfile(member, body)
                        finally:
                            if body is not None:
                                body.close()
        finally:
            decoder.stdout.close()
            encoder.stdin.close()
        if decoder.wait() or encoder.wait():
            destination.unlink(missing_ok=True)
            raise RuntimeError(f"Could not repack {source}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=Path)
    parser.add_argument("destination", type=Path)
    args = parser.parse_args()
    if args.destination.exists():
        shutil.rmtree(args.destination)
    shutil.copytree(args.source, args.destination)
    for name, excluded_directory in (
        ("rootfs.tzst", "opt/wine/lib/wine/i386-windows/"),
        ("wincomponents/direct3d.tzst", "syswow64/"),
        ("dxwrapper/d8vk-1.0.tzst", "syswow64/"),
        ("dxwrapper/dxvk-1.10.3.tzst", "syswow64/"),
        ("dxwrapper/dxvk-2.4.1.tzst", "syswow64/"),
        ("dxwrapper/vkd3d-2.14.1.tzst", "syswow64/"),
    ):
        source = args.source / name
        destination = args.destination / name
        repack(source, destination, excluded_directory)
        print(f"{name}: {source.stat().st_size} -> {destination.stat().st_size} bytes")


if __name__ == "__main__":
    main()
