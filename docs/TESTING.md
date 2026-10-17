# Device testing and benchmark matrix

Do not announce FPS, RAM or compatibility improvements before repeatable device testing.

## Capture for every report
| Item | Details |
| --- | --- |
| Device | Manufacturer/model |
| Chip | SoC, GPU name and GPU family (Adreno A6xx/A7xx, Mali G52, other) |
| Software | Android version, Winlator branch/commit, driver/version |
| Workload | Game/application, executable architecture, graphics API, Wine and Box64 versions |
| Display | Resolution, graphics settings, refresh rate |
| LSFG | Off, 2x/3x/4x, selected preset |
| Baseline | Repeated FPS, median/p95/p99 frame times, observed stutter |
| Memory | Android process RAM and relevant guest/container memory measurements (method stated) |
| Quality | Crashes, rendering errors, input latency, loading time, heat/thermal throttling |
| Evidence | Reproduction steps, anonymized logs/screenshots, number of runs |

## Suggested comparison procedure
1. Reboot or reset the test setup as appropriate; report the method.
2. Fix the game scene, resolution, driver and background conditions.
3. Measure the current `main` baseline with LSFG off, then LSFG on.
4. Repeat using the candidate change with the **same** conditions.
5. Compare raw frame times and resource use. Higher displayed frame rate from generation is not the same as higher native rendering FPS.
6. Log regressions separately for each GPU family and explicitly mark anything not tested.

## Compatibility coverage
- **Adreno A6xx and A7xx**: relevant Vulkan driver configuration, rendering and stability.
- **Mali G52**: use a compatible Mali driver path; do not assume Qualcomm Turnip is supported.
- **Windows 64-bit applications on Android ARM64**: check Wine/Box64/container startup and x86_64 launch.
- **Windows 32-bit applications/launchers**: document missing support in restricted 64-bit-only builds.
- **Discord RPC**: test opt-in, detection, session cleanup, app absence and privacy behavior separately.

A passed CI build is not proof of correct behavior on Android hardware.
