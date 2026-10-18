# Instruções para agentes de código — Winlator LSFG Aurora

## Missão e limites
Trabalhe **somente no Aurora** (Android ARM64), mantendo compatibilidade de jogos Windows x86 e x64 dentro dos contêineres. Não integrar XenDroid/Xbox 360. Prioridades: estabilidade, drivers Adreno/Mali, LSFG-VK/frame pacing, memória e Discord RPC opcional.

## Estado de integração verificado (18/10/2026)

- **`main`** inclui os PRs #7/#19 (RPC e consolidação), #31 (identidade e documentação), #32 (aviso da RC8) e **#33 (código RC9)**. Não reabrir ou mesclar novamente esses PRs.
- **Candidato de código atual:** `11.3-aurora-rc.9` (`versionCode 47`), integrado por `14898844688a33480df6e967ab02787778ec3a47`. Testes JVM, resources, Android debug SDK-free e compilação AndroidTest passaram no SHA da integração; isso não comprova compatibilidade física nem é Stable.
- **Instaladores RC8:** o mantenedor relatou que só o Vesktop já disponível funcionou. Na RC9, botões Endcord/LegCord/Vesktop/Firefox estão ocultos e a cópia existente do Vesktop é reutilizada; nenhum arquivo antigo foi apagado. **PR #30** foi encerrado como substituído pelo #33.
- **PR #26:** candidato RC2 legado em base antiga com conflitos. Não mesclar nem reverter para `versionCode 40`; apenas ajustes relevantes de download do SDK são reavaliados no PR #34.
- **Release pública RC8:** tag `aurora-11.3-rc8-discord-firefox` mantém APK experimental `389ee866` de assinatura debug; ela **não** foi substituída pela RC9 de código. Builds SDK-free não têm RPC Android nativo.
- **Stable:** requer assinatura de distribuição, backups/upgrade/rollback e testes em dispositivos/jogos (Adreno/Mali, Windows x86/x64, LSFG e RPC real). Nunca declarar estabilidade baseada só em CI.
- **Correção de estabilidade `8e43032f`:** preservar suporte a `KeyValueSet` malformado e testes de regressão.

## Antes de qualquer mudança
1. Leia `README.md`, `ROADMAP.md`, `docs/DEVELOPMENT_WORKFLOW.md`, `docs/GPU_COMPATIBILITY_AURORA.md` e `docs/DRIVER_PLAN.md`.
2. Consulte PRs abertos e a árvore da branch atual; **não duplique trabalho em andamento**.
3. Registre hipótese, dispositivos afetados, arquivos alterados, plano de teste e critério de reversão.
4. Crie branch própria e PR pequeno em draft quando depender de hardware ou bibliotecas externas.
5. Para integração, execute `python3 scripts/test_aurora_resources.py`, `python3 scripts/test_driver_inventory.py`, `python3 scripts/audit_graphics_drivers.py` e `cd app && ./gradlew :app:testDebugUnitTest :app:assembleDebug` no ambiente Android configurado; quando aplicável, validar também o build nativo autorizado do RPC.
6. Use `docs/STABLE_RELEASE_GATE.md` e `docs/AURORA_STABLE_TRACKER.md` para registrar o que passou **no mesmo commit**; não converter build verde em afirmação de estabilidade física.

## Ordem de implementação
1. **Inventário e testes de regressão**: mapear driver embutido vs instalável, versão, arquitetura, origem/licença e caminhos de seleção; documentar resultados sem alterar binários.
2. **Adreno A6xx/A7xx**: melhorar detecção para diagnóstico e seleção **manual** de Turnip/versão, preservando versões antigas. Nunca escolher driver ou versão via modelo da GPU. AdrenoTools já está presente no código nativo; não remover nem ampliar seu uso sem demonstrar dependências e impacto.
3. **Mali G52 e PANVK**: analisar viabilidade real do driver PANVK no Android/Winlator (kernel, DRM, Vulkan ICD, distribuição e licenças). Não oferecer PANVK na UI se não houver pacote funcional e validação. Manter Vortek/Vulkan do sistema e fallback OpenGL.
4. **UI de drivers**: manter Vulkan/OpenGL, versões instaladas, GPU detectada para diagnóstico, avisos e fallback. **Novo contêiner inicia em Vortek/Gladio, sem seleção automática por GPU. Turnip é opção explicitamente selecionada com versão manual.** Configurações legadas devem permanecer legíveis.
5. **LSFG-VK/frame generation**: instrumentar frame time, latência, RAM e stutter antes de alterar presets 2x/3x/4x; manter `Lossless.dll` fora do repositório.
6. **Memória/performance ARM64**: medir uso de heap, caches e inicialização por GPU e jogo; otimizar somente com comparações reproduzíveis.
7. **Discord RPC e clientes Windows**: revisar o código da RC8 em PR #30, mantendo os downloads opcionais verificados, sem prometer RPC vivo; não confundir APK SDK-free com integração nativa. SDK proprietário somente em CI autorizado, com hash conferido.

## Regras de segurança e compatibilidade
- Não apagar/consolidar Turnip. Não reintroduzir seleção automática: driver e versão Turnip são manuais por decisão do mantenedor. Preservar o fallback fixo para contêineres antigos com versão vazia/`auto`.
- Não distribuir `Lossless.dll`, Discord Social SDK/AAR ou binários sem origem/licença verificada.
- Não fazer force-push/rebase destrutivo, alterar créditos upstream, excluir releases/tags ou mesclar código da RC2/RC8 sem resolver a ancestralidade e os conflitos com `main`.
- Não afirmar ganhos de FPS, RAM, suporte PANVK, estabilidade ou compatibilidade de aparelho sem testes físicos.
- Preservar atribuição a Rafaela (@amyroseee), brunodev85 e autores dos componentes.
- Não alterar a release principal ao criar PRs experimentais.

## Entrega por PR
Descrever: objetivo; GPU/SoC e Android; versões dos drivers; diferenças antes/depois; testes JVM/Android CI; testes em dispositivo (ou **pendentes**); riscos; fallback; plano de reversão. Preferir commits `feat(driver):`, `fix(gpu):`, `test(gpu):`, `perf(lsfg):` e `docs(driver):`.
