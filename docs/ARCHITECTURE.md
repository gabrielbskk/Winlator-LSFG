# Repository architecture (high-level map)

This is an orientation to the checked-in repository, not a complete dependency or security audit.

| Directory | Scope |
| --- | --- |
| `app/` | Android/Gradle project, Java sources, native C/C++ integration and packaged assets |
| `android_alsa/` | Native Android ALSA integration |
| `gladio/` | Separate native graphics-related component |
| `glibc_patches/` | glibc-related patches |
| `input_controls/` | Game-specific input control profiles |
| `installable_components/` | Downloadable or installable runtime components and indexes |
| `tools/lsfg-vk-glibc/` | Build script and compatibility patch for LSFG-VK in glibc environments |
| `vortek/` | Native graphics/Vulkan-related project |
| `wine_addons/` | Tracked Wine Mono/Gecko installers |
| `.github/` | Issue/PR templates and code ownership guidance |

## Specific entry points

- Android app Gradle module: `app/app/build.gradle`
- Android Java sources: `app/app/src/main/java/com/winlator/`
- Native components: `app/app/src/main/cpp/`
- LSFG configuration UI: `app/app/src/main/java/com/winlator/contentdialog/LSFGVKConfigDialog.java`
- LSFG-VK integration assets: `app/app/src/main/assets/lsfg-vk/`
- GPU driver assets: `app/app/src/main/assets/graphics_driver/`

## Proposed investigation paths
- **Frame generation**: trace LSFG settings into container startup, Vulkan layer configuration and frame pacing. Test on-device before claiming improvements.
- **Graphics**: identify actual Vulkan renderer and driver loading paths for each GPU; Turnip drivers target Qualcomm, not Mali.
- **Memory**: profile allocations and guest/container RAM separately before changing resource caches.
- **Discord Rich Presence**: experimental PR #1. No default-branch support until integrated.
- **ARM64 builds**: distinguish Android host ARM64 ABI from the architecture (32/64-bit) of Windows applications run through Wine/Box64.

Do not broadly move native directories or large runtime archives in a housekeeping PR. See [Repository audit](REPOSITORY_AUDIT.md) and [Testing matrix](TESTING.md).
