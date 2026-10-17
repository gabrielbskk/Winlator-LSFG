<p align="center">
	<img src="assets/branding/aurora-symbol.svg" width="170" height="170" alt="Winlator LSFG Aurora independent symbol" />
</p>

<h1 align="center">Winlator LSFG Aurora</h1>

<p align="center"><strong>Experiments in Android ARM64 Windows gaming and LSFG-VK integration.</strong></p>

> **Attribution:** This community branch is based on [Rafaela's Winlator LSFG fork (@amyroseee)](https://github.com/amyroseee/Winlator-LSFG), itself derived from [Winlator by brunodev85](https://github.com/brunodev85/winlator). Aurora is a distinct, unofficial experimental identity; the original projects and their authors retain full credit.

An experimental derivative of Winlator LSFG with a separate visual identity and a focus on compatibility, frame generation, and clear reporting of changes.

## Quick start

**Requirements:** Android **8.0+**, **ARM64** device, supported graphics driver, and free storage for game containers. Support varies across Adreno/Mali GPUs and Windows applications; these are minimum app requirements, **not** a guarantee of performance or compatibility.

1. Read the [available releases](https://github.com/gabrielbskk/Winlator-LSFG/releases), choose an appropriate build, and back up existing containers before installation.
2. Create a container using suitable Wine/Box64, GPU driver and renderer settings.
3. For LSFG-VK, import your own legally obtained compatible `Lossless.dll` and configure a per-container 2x/3x/4x preset. The DLL is **not distributed** with this fork.
4. Start with LSFG disabled to check basic game compatibility, then compare presets using reproducible settings.

More details: [Getting started](docs/GETTING_STARTED.md) · [Discord Rich Presence setup](docs/DISCORD_RPC.md) · [Testing checklist](docs/TESTING.md).

## Features

- **LSFG-VK integration**
  - Adapted for the Winlator glibc environment
  - Uses a user-provided `Lossless.dll`
  - `Lossless.dll` is not bundled with the app

- **Frame Generation**
  - 2x
  - 3x
  - 4x
  - The current Winlator integration is still experimental and may improve over time

- **LSFG presets**
  - Performance
  - Balanced
  - Quality
  - Custom

- **Per-container LSFG settings**
  - LSFG can be configured separately for each container

- **High Contrast FPS Counter**
  - Simple performance overlay focused on readability during gameplay

- **Save Backup & Restore**
  - Backup and restore game saves directly from the container menu
  - Supports common Wine save locations

- **Visual changes**
  - Cyan app theme
  - Updated launcher and internal visuals

## Lossless.dll

LSFG requires a compatible `Lossless.dll` provided by the user.

This project does not include or distribute `Lossless.dll`.

## Screenshots

<img width="1600" height="720" alt="Winlator LSFG gameplay" src="https://github.com/user-attachments/assets/1d243d94-510a-4554-91c5-a81b5e82497b" />
<img width="1600" height="720" alt="Winlator LSFG FPS counter" src="https://github.com/user-attachments/assets/af0fc1da-5216-42ca-b2d5-5de50119c2b5" />
<img width="1600" height="720" alt="Winlator LSFG settings" src="https://github.com/user-attachments/assets/fdb83480-d537-4713-ad3f-8a6d0315cfa2" />

## Project status and roadmap

The integration is experimental. Features listed above are described by this fork; performance and device compatibility may vary, and changes under development are **not** guaranteed.

- [Development roadmap](ROADMAP.md): proposed LSFG-VK, GPU, RAM, ARM64 and Discord RPC improvements
- [Contributor guide](CONTRIBUTING.md): how to report, test and submit changes
- [License (LGPL-2.1)](LICENSE) and component-specific licenses
- [Testing matrix](docs/TESTING.md): how to reproduce performance and compatibility reports
- [Architecture orientation](docs/ARCHITECTURE.md)
- [Changelog](CHANGELOG.md)
- [Pull requests](../../pulls) — experimental work and community proposals (GitHub Issues are currently disabled; see repository Settings → General → Features to enable them)

**Android Discord Rich Presence** remains experimental. The consolidated [experimental 4 PR #7](../../pull/7) passed SDK-free JVM tests, but no verified RPC v4 APK is published. It requires a compatible, separately supplied official Discord SDK AAR and real-device tests before release; see the [RPC guide](docs/DISCORD_RPC.md). Do not confuse SDK-free CI APKs with functional Discord builds.

## Maintainer and contributors

**Maintainer of this derivative:** [@gabrielbskk](https://github.com/gabrielbskk).

**Original Winlator LSFG fork credit:** Rafaela, [@amyroseee](https://github.com/amyroseee), [original source repository](https://github.com/amyroseee/Winlator-LSFG).

See [CONTRIBUTORS.md](CONTRIBUTORS.md) for project participation and acknowledgments. Contributions are welcome through issues and pull requests.

## Credits

- **Rafaela / [@amyroseee](https://github.com/amyroseee)** — original Winlator LSFG fork and its LSFG integration work.
- **[brunodev85](https://github.com/brunodev85)** — original Winlator.
- Current derivative maintenance: **[@gabrielbskk](https://github.com/gabrielbskk)**.

LSFG-VK and other third-party components belong to their respective authors.

## Disclaimer

This is an unofficial derivative of Winlator LSFG. It is not affiliated with or endorsed by Winlator, Rafaela's source project, Discord or Lossless Scaling. The new name and artwork do not imply original authorship of upstream code.
