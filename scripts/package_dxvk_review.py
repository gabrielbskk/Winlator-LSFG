#!/usr/bin/env python3
"""Prepare optional Winlator DXVK packages from pinned upstream Wine DLL archives.

No bundled DXVK version is removed or replaced. Packages are for manual review;
they are not silently selected on Adreno or Mali devices.
"""
import argparse
import hashlib
import io
import json
from pathlib import Path
import subprocess
import tarfile
import tempfile
import urllib.request

RELEASES = {
    "2.6.2": "17761876556afd55736cb895d184f5a1c55d43350f1b1e3b129f8d28706d7992",
    "2.7.1": "d85ce7c79f57ecd765aaa1b9e7007cb875e6fde9f6d331df799bce73d513ce87",
}
DLLS = ("d3d9.dll", "d3d10core.dll", "d3d11.dll", "dxgi.dll")
MAX_FILE = 64 * 1024 * 1024


def verify_pe(payload: bytes, machine: int):
    if len(payload) < 0x44 or payload[:2] != b"MZ":
        raise ValueError("Not a Windows DLL (MZ signature missing)")
    offset = int.from_bytes(payload[0x3c:0x40], "little")
    if offset < 0x40 or offset + 6 > len(payload) or payload[offset:offset + 4] != b"PE\\0\\0":
        raise ValueError("Invalid PE header")
    actual = int.from_bytes(payload[offset + 4:offset + 6], "little")
    if actual != machine:
        raise ValueError(f"Unexpected DLL CPU architecture: {actual:#x}")


def put(tar: tarfile.TarFile, path: str, data: bytes):
    entry = tarfile.TarInfo(path)
    entry.size = len(data)
    entry.mode = 0o644
    entry.mtime = 0
    entry.uid = entry.gid = 0
    entry.uname = entry.gname = ""
    tar.addfile(entry, io.BytesIO(data))


def build_import_tar(source: Path, destination: Path, version: str):
    if version not in RELEASES:
        raise ValueError("Unpinned version")
    destination.parent.mkdir(parents=True, exist_ok=True)
    manifest = {"type": "DXVK", "versionName": version, "files": []}
    with tarfile.open(source, "r:gz") as official, tarfile.open(destination, "w") as output:
        members = {entry.name.removeprefix("./"): entry for entry in official.getmembers() if entry.isfile()}
        for original_dir, target_dir, machine in (("x64", "system32", 0x8664), ("x32", "syswow64", 0x014c)):
            for dll in DLLS:
                official_name = f"dxvk-{version}/{original_dir}/{dll}"
                member = members.get(official_name)
                if member is None or member.size > MAX_FILE:
                    raise ValueError(f"Official release missing expected DLL: {official_name}")
                stream = official.extractfile(member)
                if stream is None:
                    raise ValueError(f"Cannot read {official_name}")
                payload = stream.read(MAX_FILE + 1)
                if len(payload) != member.size or len(payload) > MAX_FILE:
                    raise ValueError(f"Unexpected size: {official_name}")
                verify_pe(payload, machine)
                target = f"{target_dir}/{dll}"
                put(output, target, payload)
                manifest["files"].append({"source": target, "target": target})
        # Preserve upstream license if present and include attribution inside the review archive.
        license_entry = members.get(f"dxvk-{version}/LICENSE") or members.get(f"dxvk-{version}/LICENSE.md")
        if license_entry:
            stream = official.extractfile(license_entry)
            if stream is not None:
                license_data = stream.read(128 * 1024)
                if len(license_data) > 0:
                    put(output, "DXVK-UPSTREAM-LICENSE.txt", license_data)
        put(output, "aurora-dxvk-manifest.json",
            (json.dumps(manifest, ensure_ascii=False, sort_keys=True, indent=2) + "\\n").encode("utf-8"))


def package(version: str, outdir: Path):
    if version not in RELEASES:
        raise ValueError(f"Unapproved release {version}")
    url = f"https://github.com/doitsujin/dxvk/releases/download/v{version}/dxvk-{version}.tar.gz"
    outdir.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as workdir:
        upstream = Path(workdir) / "upstream.tar.gz"
        with urllib.request.urlopen(urllib.request.Request(url, headers={"User-Agent": "Aurora-DXVK-Packager/1"}), timeout=90) as response:
            with upstream.open("wb") as output:
                while chunk := response.read(1024 * 1024):
                    output.write(chunk)
        digest = hashlib.sha256(upstream.read_bytes()).hexdigest()
        if digest != RELEASES[version]:
            raise ValueError(f"Upstream SHA-256 mismatch for {version}: {digest}")
        intermediate = Path(workdir) / "dxvk.tar"
        build_import_tar(upstream, intermediate, version)
        final = outdir / f"dxvk-{version}.tzst"
        subprocess.run(["zstd", "-T2", "-10", "-q", "-f", "-o", str(final), str(intermediate)], check=True)
        with tarfile.open(fileobj=__import__("io").BytesIO(subprocess.check_output(["zstd", "-q", "-d", "-c", str(final)])), mode="r:") as archive:
            names = set(archive.getnames())
            expected = {f"{folder}/{dll}" for folder in ("system32", "syswow64") for dll in DLLS}
            if not expected.issubset(names) or "aurora-dxvk-manifest.json" not in names:
                raise ValueError("Packaged archive failed layout validation")
        return final


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, default=Path("dist/dxvk-review"))
    parser.add_argument("--version", choices=RELEASES, action="append")
    args = parser.parse_args()
    versions = args.version or list(RELEASES)
    for version in versions:
        file = package(version, args.output)
        sha = hashlib.sha256(file.read_bytes()).hexdigest()
        print(f"{sha}  {file.name}")
    sums = args.output / "SHA256SUMS.txt"
    sums.write_text("".join(
        f"{hashlib.sha256(file.read_bytes()).hexdigest()}  {file.name}\\n"
        for file in sorted(args.output.glob("dxvk-*.tzst"))), encoding="utf-8")


if __name__ == "__main__":
    main()
