# Handoff para Codex — Aurora RC9 (18/10/2026)

> Documento **operacional** para manutenção no estado atual. Não é evidência de compatibilidade em dispositivo nem autorização para liberar uma Stable. Confirme sempre o SHA atual de `main`, PRs e runs antes de começar.

## Escopo e decisões imutáveis

- **Projeto:** `gabrielbskk/Winlator-LSFG` — **Aurora** somente; Android ARM64 hospedando aplicativos Windows x86/x64 por Wine/Box64. Não misturar XenDroid ou Xbox 360.
- **Drivers:** manter todas as versões existentes de Turnip (empacotadas e instaláveis), sem “Turnip unificado” e sem **seleção automática** por GPU. Escolher driver e versão manualmente. Novos contêineres usam o padrão Vortek/Gladio; contêineres legados preservam configuração. Não remover AdrenoTools sem análise nativa de dependências.
- **Mali/PANVK:** fallback do sistema/Vortek para modelos suportados; PANVK **não integrado e não validado**.
- **LSFG-VK:** `Lossless.dll` **não é distribuído**. Não declarar Vortek compatível com LSFG sem suporte de swapchain/validação. Não prometer FPS ou RAM.
- **Discord:** a RC8 pública foi a build **SDK-free** (sem RPC Android nativo). Foi relatado pelo mantenedor que somente o **Vesktop previamente existente** abriu; Endcord, LegCord, Firefox e os botões de instalação não tiveram resultado útil. A RC9 esconde os botões e reutiliza Vesktop existente. Não pedir tokens ou segredos de contas.
- **Releases:** a tag pública `aurora-11.3-rc8-discord-firefox` aponta o código antigo; **não mover, atualizar ou substituir** tag, APK ou assets. A `main` usa `versionName=11.3-aurora-rc.9` e `versionCode=47` como **código candidato, não uma APK RC9 pública**. O canal Stable continua **bloqueado**.

## PRs já integrados e a não repetir

| PR | O que foi incorporado |
| --- | --- |
| #7/#19 | RPC/consolidação Aurora, GPU e controles manuais |
| #31/#32 | Identidade, documentação RC8 e aviso do problema |
| #33 | Fonte RC9 sobre a `main`, botões desativados, reutilização Vesktop |
| #34 | Integridade e URL allowlist do SDK MediaFire |
| #35 | Atualização de documentação/release gates |
| #36 | Ignorar executáveis Windows vazios e preservar fallback Vesktop |
| #37 | Verificar SHA-256 do Firefox legado na consulta |

Os PRs #26 e #30 foram encerrados como substituídos; **não mesclar suas branches em `main`**. Para o trabalho atual, consultar PRs abertos no momento; os PRs de auditoria de menu e CI criados depois do #37 precisam de aprovação de CI no SHA final antes de mesclar.

## Mapa rápido dos componentes

| Domínio | Entrada e restrição |
| --- | --- |
| Wine / atalhos | `app/app/src/main/java/com/winlator/core/WineStartMenuCreator.java`; preservar arquivos e atalhos válidos, evitar instalar/abrir clientes não validados |
| Editor / iniciar Vesktop | `ContainerDetailFragment.java`; preferência por instalação válida do Aurora e fallback Downloads |
| Discord opcional | `core/DiscordClientInstaller.java` e `core/FirefoxInstaller.java`; downloads ocultos, não prometer suporte |
| Driver de GPU | `container/GraphicsDrivers.java`, `contentdialog/TurnipConfigDialog.java`, `core/GpuCompatibility.java`, `core/TurnipVersionSelection.java`; não autodetectar Turnip |
| Box64/Electron | `box64/Box64PresetManager.java` e `assets/box64/env_vars.json`; não reativar LIBCEF/allocator hacks sem comparação |
| LSFG | `contentdialog/LSFGVKConfigDialog.java`, `XServerDisplayActivity.java`, `tools/lsfg-vk-glibc/` |
| RPC nativo | `app/app/src/main/cpp/discordrpc/` e `scripts/check_discord_aar.py`; SDK licenciado fornecido somente por canal autorizado |
| CI Android SDK-free | `.github/workflows/android-debug.yml` e `app/app/build.gradle` |
| Release real | `docs/STABLE_RELEASE_GATE.md`, `docs/AURORA_STABLE_TRACKER.md` e `docs/RELEASES.md` |

## Auditoria de 18/10/2026: trabalhos técnicos

1. **P0 — preservar dados:** verificar instalação limpa, update/debug-signature, migração de contêineres, saves, rollback; sem desinstalar o aplicativo para “resolver” incompatibilidade de assinatura. Exige dispositivo e backup.
2. **P0 — publicação:** adicionar e manter verificação SDK-free de APK/ABI/arquivos proibidos no workflow antes do upload; não validar APK por nome ou apenas por build verde. Workflows antigos de publicação devem permanecer guardados contra promoção de versões legadas ou sobrescrita de assets. Para release real: tag inédita, chave autorizada, SHA-256 e revisão do responsável.
3. **P1 — inicialização e atalhos Wine:** caminhos/atalhos de Vesktop existente; evitar hashing síncrono de Firefox legado na abertura do contêiner; não reintroduzir painéis de download que falharam; não apagar instaladores armazenados.
4. **P1 — GPU/jogos:** testes reproduzíveis x86/x64, drivers Qualcomm (versão explícita), Mali fallback, DXVK, frame pacing LSFG e medição RAM. Se hardware indisponível, marcar como *pendente*, não passar gate por inferência.
5. **P2 — RPC/navegador:** ao ter hardware, validar presença Discord ao vivo, login opcional, troca e encerramento; Firefox/Endcord/LegCord devem ser corrigidos em PRs separados e só reaparecer na UI com validação real.
6. **P2 — documentação histórica:** `docs/DEVELOPMENT_WORKFLOW.md`, `docs/ARCHITECTURE.md` e documentos RC1/11.2 não podem ser tratados como retrato da `main`. Suas seções históricas devem ser rotuladas, sem apagar créditos e commits.

## Testes e evidências exigidos por PR

1. `python3 scripts/test_aurora_resources.py`
2. `python3 scripts/test_driver_inventory.py`
3. `python3 scripts/audit_graphics_drivers.py` (somente auditoria)
4. `python3 scripts/test_discord_sdk_source.py`
5. `python3 scripts/test_aurora_sdkfree_apk.py` e, **após build**, `python3 scripts/verify_aurora_sdkfree_apk.py app/app/build/outputs/apk/debug/app-debug.apk`
6. `cd app && ./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest --no-daemon`

A verificação `assembleDebugAndroidTest` **compila** instrumentação, não a executa no Android. Build SDK-free **não contém** SDK Discord RPC nativo; build com SDK autorizado deve passar por verificação própria e nunca publicar SDK/licenças indevidas. Registre run, SHA, resultado, arquivos alterados, riscos e instruções de reversão.

## Release: bloqueios que CI não resolve

- [ ] Aprovação de assinatura de distribuição (não debug) e teste de atualização/downgrade, com backup confirmado.
- [ ] Adreno 619, 642/642L, A7xx e Mali G52 conforme suporte anunciado: contêineres, driver manual, DXVK/WineD3D e jogos Windows x86/x64.
- [ ] LSFG 2x/3x/4x onde tecnicamente suportado: FPS nativo/gerado, p95/p99 frametime, RAM e travamentos.
- [ ] Vesktop/RPC ao vivo, falta/retorno da rede e clientes/navegadores adicionais, quando anunciado.
- [ ] Auditoria dos arquivos Windows 32/64-bit, SHA-256, licenças, rollback e logs sem credenciais.

**Regra de decisão:** se faltar qualquer evidência crítica, publicar no máximo candidato **experimental claramente rotulado**, sob autorização expressa do mantenedor; não chamar de Stable. Não criar uma publicação automaticamente.
