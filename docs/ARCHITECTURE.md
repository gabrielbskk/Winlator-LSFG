# Architecture overview

This is a high-level orientation to top-level directories, not an exhaustive code audit.

| Area | Location | Purpose |
| --- | --- | --- |
| Android application | `app/` | Android app sources and integration |
| Native audio | `android_alsa/` | ALSA integration |
| Graphics-related components | `gladio/` | Native project component; consult its code before changing behavior |
| Runtime patches | `glibc_patches/` | Patches associated with the glibc environment |
| Controls | `input_controls/` | Input control resources |
| Installable assets | `installable_components/` | Components distributed/managed by the project |
| Project automation | `.github/` | Contribution templates and workflows, where available |

## Areas to investigate
- **LSFG-VK**: trace Android configuration through launch/container logic into the frame-generation integration.
- **Graphics compatibility**: identify renderer selection, driver packaging, Vulkan capabilities and error handling.
- **Memory**: profile both the Android process and guest environment before changing caches.
- **Discord RPC**: see experimental PR #1; not considered part of the default branch until merged and verified.
- **ARM64 distribution**: differentiate Android host architecture from Windows guest executable bitness.

## Safety of changes
Keep edits to core rendering, Wine, Box64 and runtime code separate from documentation changes. Preserve third-party licenses and verify build/release assets before publishing.
