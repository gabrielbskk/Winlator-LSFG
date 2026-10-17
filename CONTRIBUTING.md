# Contributing to Winlator LSFG

Thanks for helping with Winlator LSFG. This is an unofficial Winlator fork focused on Android ARM64 and experimental LSFG-VK integration.

## Before contributing
- Check existing issues and pull requests, including experimental work.
- Check existing PRs and the [development workflow](docs/DEVELOPMENT_WORKFLOW.md) before starting. GitHub Issues is currently disabled on this fork; discuss larger proposals in a draft PR until the maintainer enables Issues.
- Keep changes focused: one feature or fix per branch/PR.
- Do not commit proprietary `Lossless.dll`, `discord_partner_sdk.aar`, split SDK ZIP archives, copyrighted game files, credentials or device identifiers. GitHub Actions may fetch an authorized SDK to a temporary runner location with SHA-256 checking.

## Development and testing
- State the build environment, Android SDK/NDK versions, build variant and any required dependencies.
- Report whether the feature was tested on physical hardware, in an emulator, or only through automated tests.
- For GPU or LSFG improvements, attach a reproducible baseline and comparable measurements; include failures and regressions.
- Document device model, SoC/GPU, driver, Android version, test title and graphics settings.
- Preserve compatibility unless an intentionally limited build is clearly labeled.

## Pull requests
Use a descriptive title and explain the motivation, changed areas, verification and risks. Mark unfinished work as draft. Link related issues and update documentation if user-facing behavior changes.

## Attribution and licensing
Respect the repository's LICENSE and third-party component licenses. Retain upstream attribution. No contribution should imply affiliation with official Winlator or Lossless Scaling.
