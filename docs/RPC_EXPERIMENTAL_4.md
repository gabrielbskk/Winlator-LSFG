# Winlator LSFG Aurora — Discord RPC experimental 4

**Status: candidate branch; APK not yet built or published.** Do not label this
release stable without Android device tests. The Android APK is ARM64, with
Windows 32-bit and 64-bit runtime assets kept together.

## Changes since experimental 3

- Retains the original Windows `i386-windows` / `syswow64` files in the
  Android ARM64 APK, rather than repacking a 64-bit-Windows-only variant.
- Uses a lower-overhead SDK callback polling cadence: 250 ms while an RPC
  update awaits acknowledgement and 1000 ms when idle/published/retrying.
- Improves RPC shutdown: the dedicated worker thread ends even if native
  SDK destruction reports a Java/JNI failure.
- Sanitizes control characters in the game name used for public presence.
- Serializes process-batch collection on Android's main thread, discarding
  incomplete/out-of-order snapshots and clearing pending batches on pause,
  resume and close.
- Adds automated JVM regression checks and an APK packaging verifier.
- Inherits Aurora artwork, package label and prominent attribution to
  Rafaela's original [Winlator LSFG](https://github.com/amyroseee/Winlator-LSFG)
  from the main branch.

## Build requirements

Android SDK 35, Android NDK 27.2.12479018 for the Discord JNI/Prefab build,
CMake 3.22.1, JDK 17, Python 3, Zstd and a **licensed official Discord
Social SDK AAR version 1.10+** provided independently by the builder.

The AAR is **not** in this repository. No user token, password or Discord OAuth
client secret is needed for RPC. Do not distribute SDK binaries without checking
the applicable Discord terms.

From a local checkout, with the full source assets available:

```sh
bash scripts/build_rpc_v4.sh /absolute/path/discord_partner_sdk.aar
```

The script runs Gradle/JVM tests, compiles the debug APK with the native RPC
bridge, validates archive integrity and required 32-bit Windows assets, and
creates `dist/rpc-experimental-4/` with the APK and SHA-256 manifest.

To run only automated JVM tests without a proprietary AAR:

```sh
cd app
./gradlew :app:testDebugUnitTest
```

That SDK-free test does **not** prove that Discord RPC works on a device.
Use the official AAR for any distributable RPC build.

## Manual release checklist

- [ ] Gradle/JVM tests passed; APK built with licensed SDK AAR and native bridge.
- [ ] Android APK versionName = `11.2-rpc-experimental.4` and versionCode = 36.
- [ ] APK signature, package identifier and ZIP/TAR checks passed.
- [ ] Tested in the Android Discord app: accepted presence and visibility from another account.
- [ ] Tested startup, unexpected Discord app termination/restart, network interruptions and retry.
- [ ] Tested game switching, session timer, pause/PiP/return, forced close and repeated container launches.
- [ ] Tested several Windows 32-bit and 64-bit games or launchers with real devices.
- [ ] Checked memory, wakeups, frame pacing, crash logs and regressions.
- [ ] Included SHA-256, build type, limitations and credits in the prerelease notes.

## Known limitations

The SDK may confirm it accepted an update without guaranteeing Discord will
display it. Window/process detection is heuristic and cannot cover all games.
The Windows 32-bit runtime files being present does not itself prove all
32-bit launchers run. No FPS or RAM reduction is claimed without measurements.
`Lossless.dll` is user-supplied; it must not be bundled.

**Upstream credits:** Rafaela (@amyroseee), original Winlator LSFG;
brunodev85, original Winlator; LSFG-VK and other respective upstream authors.
