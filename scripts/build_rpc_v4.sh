#!/usr/bin/env bash
# Reproducible local build for the experimental 4 candidate.
# Only use an AAR obtained from the official Discord developer distribution.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SDK_AAR="$ROOT/app/app/libs/discord_partner_sdk.aar"
if [ "$#" -gt 0 ]; then SDK_AAR="$1"; fi
if [ ! -f "$SDK_AAR" ]; then
  echo "Missing official Discord Social SDK AAR: $SDK_AAR" >&2
  exit 2
fi
SDK_AAR="$(realpath "$SDK_AAR")"

for bin in python3 java zstd tar unzip sha256sum; do
  if ! command -v "$bin" >/dev/null 2>&1; then
    echo "Required executable missing: $bin" >&2
    exit 2
  fi
done

# Validate AAR shape (does not establish SDK authenticity or exact version).
python3 "$ROOT/scripts/check_discord_aar.py" "$SDK_AAR"

# Build with SDK 1.10+, including the JNI bridge, never a no-RPC fallback.
(
  cd "$ROOT/app"
  ./gradlew :app:clean :app:testDebugUnitTest :app:assembleDebug \
    -PdiscordSdkAar="$SDK_AAR" -PandroidNdkVersion=27.2.12479018 \
    --no-daemon --stacktrace
)

APK="$ROOT/app/app/build/outputs/apk/debug/app-debug.apk"
python3 "$ROOT/scripts/verify_rpc_v4_apk.py" "$APK"

OUT="$ROOT/dist/rpc-experimental-4"
mkdir -p "$OUT"
NAME="Winlator-LSFG-Aurora-11.2-rpc-experimental.4-arm64.apk"
cp -f "$APK" "$OUT/$NAME"
(
  cd "$OUT"
  sha256sum "$NAME" > SHA256SUMS.txt
)
echo "Prepared candidate APK: $OUT/$NAME"
echo "SHA-256 manifest: $OUT/SHA256SUMS.txt"
echo "Not published. Device testing and signature/manifest inspection are required."

# Publishing deliberately remains a separate reviewed maintainer action. This
# script cannot confirm that RPC is displayed to other Discord users.
