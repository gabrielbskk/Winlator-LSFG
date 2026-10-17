# Winlator LSFG — Roadmap

This document tracks proposed work, not completed features. The project's main target is Android ARM64 and 64-bit Windows game compatibility.

## Priorities

### 1. Stability and regression testing
- Establish reproducible Android ARM64 builds.
- Test container creation, game launching, save backup/restore, and LSFG configurations.
- Record baseline frame times, FPS, peak RAM, APK size, and power/thermal observations.
- Test both Adreno and Mali variants before claiming compatibility or speedups.

### 2. LSFG-VK integration
- Profile frame-generation overhead and investigate stutter, pacing, and input latency.
- Evaluate 2x/3x/4x and Performance/Balanced/Quality/Custom presets.
- Keep any changes measurable and separate from unrelated GPU optimizations.
- Do not claim that the LSFG algorithm or Lossless.dll has been modified without verifiable code changes.

### 3. GPU support and memory efficiency
- Investigate compatibility and driver selection for Adreno A6xx, A7xx and Mali G52.
- Benchmark each hardware family independently; do not assume Turnip applies to Mali.
- Profile CPU overhead, memory allocations, texture/cache use, and initialization time.
- Avoid regressions in existing devices and games.

### 4. Discord Rich Presence on Android
- Continue Android Discord RPC integration and Steam/game artwork verification in [draft PR #7](https://github.com/gabrielbskk/Winlator-LSFG/pull/7); PRs #1, #4, and #5 are historical and superseded. The available SDK-backed Experimental 5 release is a test candidate, not proof of device-level stability.
- Test game detection, lifecycle, user opt-in and behavior without the Discord app.
- Keep RPC optional and avoid leaking game or device information without consent.

### 5. ARM64 / 64-bit builds
- Prefer Android ARM64 packages containing both Windows 32-bit and 64-bit game assets; verify `i386-windows` and `syswow64` before release. Historical stripped-down APKs remain archived.
- Verify bundle/archive integrity, licensing, and reproducible release steps.
- Keep broader compatibility builds available where practical.

## Proposed workflow
1. Describe the symptom or goal and a reproducible baseline in an issue (if Issues is enabled) or a draft PR.
2. Implement one independent change per feature branch.
3. Open a draft pull request; attach tests and before/after measurements.
4. Merge only after review and compatibility checks.

## Measurements to include in optimization PRs
Device/SoC; GPU and driver version; Android version; game; graphics API; Wine/Box64 config; resolution; preset; FPS and frame-time percentiles; RAM usage; thermal conditions; repeat count.

No performance, compatibility or memory reduction is guaranteed until measured on actual devices.
