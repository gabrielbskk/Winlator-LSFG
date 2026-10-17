<p align="center">
	<img src="logo.png" width="376" height="128" alt="Winlator LSFG Logo" />
</p>

# Winlator LSFG

A Winlator fork focused on integrating LSFG-VK frame generation into Android, along with a few practical additions for everyday use.

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
- [Testing matrix](docs/TESTING.md): how to reproduce performance and compatibility reports
- [Architecture orientation](docs/ARCHITECTURE.md)
- [Changelog](CHANGELOG.md)
- [Open issues](../../issues) and [pull requests](../../pulls)

Work on **Android Discord Rich Presence** and a possible **64-bit-only package** is experimental in [PR #1](../../pull/1); it is not a released default-branch feature.

## Maintainer and contributors

**Maintainer / repository owner:** [@gabrielbskk](https://github.com/gabrielbskk).

See [CONTRIBUTORS.md](CONTRIBUTORS.md) for project participation and acknowledgments. Contributions are welcome through issues and pull requests.

## Credits

Based on Winlator by **brunodev85**.

LSFG-VK and other third-party components belong to their respective authors.

## Disclaimer

This is an unofficial Winlator fork and is not affiliated with the original Winlator project or Lossless Scaling.
