# Changelog

## 11.3-aurora-rc.9 — código candidato (NÃO publicado)

- Integra o código de compatibilidade Windows da RC8 na base atual de `main`, sem modificar tags ou APKs existentes.
- Mantém Vesktop previamente instalado (armazenamento do Aurora ou Downloads) e seus atalhos; editor de contêiner passa a detectar ambos para iniciar ou ativar o companheiro opcional.
- Oculta nas Configurações os botões de instalação de Endcord, LegCord, Vesktop e Firefox, devido ao relato do mantenedor de que não funcionaram/utilizaram recursos desnecessariamente na RC8. Pacotes existentes **não são apagados**; o código do instalador permanece inativo para depuração futura.
- Adiciona testes JVM para prioridade da instalação existente e testes estáticos que impedem retorno acidental dos botões.
- O Vesktop foi o único cliente relatado funcional; isso não comprova RPC ao vivo, games x86/x64, FPS, RAM nem compatibilidade ampla.
- `versionCode=47`, `versionName=11.3-aurora-rc.9`; o número identifica código novo, não uma release Stable.
- **Antes de qualquer distribuição:** verificar testes Gradle, conteúdo APK, assinatura e instalação/atualização; gates físicos de `docs/STABLE_RELEASE_GATE.md` continuam pendentes.


## 11.2-rpc-experimental.5 — source candidate (not released)

### Proposed
- Discord Rich Presence per Windows game, with actual EXE icon extraction/preview inside shortcut settings.
- Steam/SteamDB App ID can be entered manually or discovered from a neighboring `steam_appid.txt`; name lookup offers choices that must be confirmed.
- Approximate Rich Presence preview inside shortcut settings, with Steam CDN cover when available and EXE icon as fallback.
- Manually configured Discord asset key or HTTPS image takes precedence over Steam. No game executable or icon is uploaded automatically.
- Includes prior RPC lifecycle, polling, naming, Android ARM64 and Windows 32/64-bit asset-preservation work.
- Retains Winlator LSFG Aurora branding and all upstream credits.

### Verification
- Steam App ID and URI/local-marker JVM tests passed on the feature branch.
- Android/JVM build and resource checks passed in GitHub Actions without the proprietary Discord SDK.
- A full native SDK-backed APK and actual Discord display still need validation on Android hardware.
- No Experimental 5 release has been published.

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
