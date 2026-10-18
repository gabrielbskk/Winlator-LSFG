<p align="center">
	<img src="assets/branding/aurora-symbol.svg" width="170" height="170" alt="Winlator LSFG Aurora independent symbol" />
</p>

<h1 align="center">Winlator LSFG Aurora</h1>

<p align="center"><strong>A Winlator LSFG fork focused on Discord RPC and Windows compatibility.</strong></p>

> **Attribution:** This community branch is based on [Rafaela's Winlator LSFG fork (@amyroseee)](https://github.com/amyroseee/Winlator-LSFG), itself derived from [Winlator by brunodev85](https://github.com/brunodev85/winlator). Aurora is a distinct, unofficial experimental identity; the original projects and their authors retain full credit.

An experimental derivative of Winlator LSFG with a separate visual identity and a focus on optional Discord RPC, Windows application compatibility and frame generation.

## Quick start

**Requirements:** Android **8.0+**, **ARM64** device, supported graphics driver, and free storage for game containers. Support varies across Adreno/Mali GPUs and Windows applications; these are minimum app requirements, **not** a guarantee of performance or compatibility.

1. Read the [available releases](https://github.com/gabrielbskk/Winlator-LSFG/releases), choose an appropriate build, and back up existing containers before installation.
2. Create a container using suitable Wine/Box64, GPU driver and renderer settings.
   - **Aurora experimental development:** new containers start with Vortek/Gladio. On Adreno, choose **Vulkan → Turnip**, then open driver settings and choose the **Turnip version manually**. Existing container choices remain saved. No GPU-based automatic Turnip selection.
   - Mali defaults to the system Vulkan path; dedicated PANVK is **not** validated or bundled. Vortek currently cannot provide the LSFG swapchain behavior required for frame generation.
3. For LSFG-VK, import your own legally obtained compatible `Lossless.dll` and configure a per-container 2x/3x/4x preset. The DLL is **not distributed** with this fork.
4. Start with LSFG disabled to check basic game compatibility, then compare presets using reproducible settings.

More details: [Getting started](docs/GETTING_STARTED.md) · [Discord Rich Presence setup](docs/DISCORD_RPC.md) · [Testing checklist](docs/TESTING.md).

## Discord RPC no Aurora

Em **Configurações → Clientes Discord Windows**, instale Endcord GUI, LegCord ou Vesktop diretamente pelo Aurora. Os pacotes oficiais Windows x64 são baixados sob demanda, têm tamanho e SHA-256 conferidos e recebem atalhos no menu Iniciar do contêiner. Endcord é a primeira opção para testar menor uso de memória; a RAM e o RPC ao vivo ainda dependem de validação em aparelho. O APK experimental não inclui o RPC Android antigo que exigia Application ID. Veja o [guia de clientes Windows](docs/DISCORD_RPC_WINLATOR.md).

O Aurora também baixa o instalador oficial do Firefox ESR Windows, verifica o SHA-256 e adiciona um atalho para executá-lo no contêiner. A instalação e o uso do navegador no Wine ainda precisam de teste físico.

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
  - Aurora blue/indigo app theme
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
- [Aurora 11.3 RC2 source and validation](docs/RELEASE_CANDIDATE_11_3.md): candidate branch in PR #19, not a published Stable APK
- [Stable release acceptance gate](docs/STABLE_RELEASE_GATE.md): required device validation, signing and regression checks before any version is called Stable
- [Contributor guide](CONTRIBUTING.md): how to report, test and submit changes
- [License (LGPL-2.1)](LICENSE) and component-specific licenses
- [Testing matrix](docs/TESTING.md): how to reproduce performance and compatibility reports
- [Architecture orientation](docs/ARCHITECTURE.md)
- [Changelog](CHANGELOG.md)
- [Pull requests](../../pulls) — experimental work and community proposals (GitHub Issues are currently disabled; see repository Settings → General → Features to enable them)

**Pré-release atual:** [Aurora 11.3 RC8](https://github.com/gabrielbskk/Winlator-LSFG/releases/tag/aurora-11.3-rc8-discord-firefox), APK ARM64 com assinatura debug, instalador opcional de clientes Discord Windows, download do Firefox ESR e atalhos de navegador. É uma prévia experimental: RPC ao vivo e compatibilidade em aparelho ainda precisam de testes. Releases anteriores são mantidas como rascunhos, com tags e assets preservados. Veja [histórico de versões](docs/RELEASES.md) e [guia de Discord no Winlator](docs/DISCORD_RPC_WINLATOR.md).

## Maintainer and contributors

**Maintainer of this derivative:** [@gabrielbskk](https://github.com/gabrielbskk).

**Original Winlator LSFG fork credit:** Rafaela, [@amyroseee](https://github.com/amyroseee), [original source repository](https://github.com/amyroseee/Winlator-LSFG).

See [CONTRIBUTORS.md](CONTRIBUTORS.md) for project participation and acknowledgments. Contributions are welcome via pull requests; GitHub Issues is currently disabled on this fork. See [development workflow](docs/DEVELOPMENT_WORKFLOW.md).

## Credits

- **Rafaela / [@amyroseee](https://github.com/amyroseee)** — original Winlator LSFG fork and its LSFG integration work.
- **[brunodev85](https://github.com/brunodev85)** — original Winlator.
- Current derivative maintenance: **[@gabrielbskk](https://github.com/gabrielbskk)**.

LSFG-VK and other third-party components belong to their respective authors.

## Disclaimer

This is an unofficial derivative of Winlator LSFG. It is not affiliated with or endorsed by Winlator, Rafaela's source project, Discord or Lossless Scaling. The new name and artwork do not imply original authorship of upstream code.
