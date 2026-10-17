#!/usr/bin/env python3
"""Check structure of a locally supplied official Discord Social SDK AAR.

This check does NOT authenticate the binary's origin, license or SDK version.
For those, use the official Discord Developer Portal and SDK documentation.
"""
import argparse
import hashlib
from pathlib import Path
import struct
import sys
import zipfile


REQUIRED = (
    "prefab/prefab.json",
    "prefab/modules/discord_partner_sdk/include/discordpp.h",
    "prefab/modules/discord_partner_sdk/include/cdiscord.h",
    "prefab/modules/discord_partner_sdk/libs/android.arm64-v8a/libdiscord_partner_sdk.so",
    "jni/arm64-v8a/libdiscord_partner_sdk.so",
    "libs/discord_partner_sdk.jar",
)


def inspect(path: Path) -> None:
    if not path.is_file():
        raise ValueError(f"AAR not found: {path}")
    with zipfile.ZipFile(path) as package:
        if (bad := package.testzip()) is not None:
            raise ValueError(f"Corrupt AAR member: {bad}")
        members = set(package.namelist())
        missing = [name for name in REQUIRED if name not in members]
        if missing:
            raise ValueError("Missing Discord Android SDK components: " + ", ".join(missing))

        prefab = package.read("prefab/prefab.json").decode("utf-8", "replace")
        if "discord_partner_sdk" not in prefab:
            raise ValueError("The Prefab package name does not match discord_partner_sdk")

        with zipfile.ZipFile(package.open("libs/discord_partner_sdk.jar")) as sdk_java:
            if "com/discord/socialsdk/DiscordSocialSdkInit.class" not in sdk_java.namelist():
                raise ValueError("DiscordSocialSdkInit Java class is not present")

        header = package.read(
            "prefab/modules/discord_partner_sdk/include/discordpp.h"
        ).decode("utf-8", "replace")
        for symbol in ("GetVersionMajor", "GetVersionMinor", "RunCallbacks", "UpdateRichPresence"):
            if symbol not in header:
                raise ValueError(f"Required C++ SDK API is missing: {symbol}")

        elf = package.read("jni/arm64-v8a/libdiscord_partner_sdk.so")
        if len(elf) < 64 or elf[:4] != b"\x7fELF" or elf[4] != 2:
            raise ValueError("The arm64-v8a library is not a 64-bit ELF")
        if struct.unpack_from("<H", elf, 18)[0] != 183:
            raise ValueError("The included ELF library is not AArch64")

    digest = hashlib.sha256(path.read_bytes()).hexdigest()
    print("AAR structure verified: Prefab, C++ headers, ARM64 ELF and Java bootstrap")
    print(f"Local AAR SHA-256: {digest}")
    print("Warning: version >= 1.10, license rights and actual RPC behavior are NOT verified.")


if __name__ == "__main__":
    args = argparse.ArgumentParser()
    args.add_argument("aar", type=Path, help="Local path; do not commit the SDK")
    opts = args.parse_args()
    try:
        inspect(opts.aar)
    except (OSError, ValueError, zipfile.BadZipFile) as exc:
        print(f"Discord AAR validation failed: {exc}", file=sys.stderr)
        raise SystemExit(1)
