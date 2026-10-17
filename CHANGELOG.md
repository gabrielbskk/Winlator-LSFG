# Changelog

## 11.2-rpc-experimental.4 — development candidate (not published)

### Proposed
- Android Discord Rich Presence includes safer process snapshot handling, reduced callback polling when stable, SDK shutdown handling, and sanitized session titles.
- Restore full Windows 32/64-bit runtime assets in ARM64 Android APK builds; no 64-bit-only repacking.
- Preserve Aurora visual identity and credits to Rafaela's Winlator LSFG source fork.

### Validation still required
- Rebuild with the official, separately supplied Discord Social SDK AAR (1.10+).
- Run Gradle/JVM tests, native/JNI build, device RPC sign-in/reconnection tests, and verify real Windows 32-bit and 64-bit games.
- Check APK integrity and release signature before publishing.

## v1.0-preview.2

Second public preview.

### Added
- Online Game Settings with cloud profiles shared by users
- Hardware compatibility checks for shared settings
- More Box64 Dynarec options
- Unity added alongside the existing Box64 presets
- Additional Turnip driver versions

### Improved
- Shared settings detection and application
- Hardware detection for SoC and GPU
- Dynarec help descriptions

### Notes
- Online Game Settings is still experimental and some profiles may require manual adjustments.
- `Lossless.dll` is not included and must be provided by the user.
