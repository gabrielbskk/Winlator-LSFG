# Instruções para agentes de código — Winlator LSFG Aurora

## Missão e limites
Trabalhe **somente no Aurora** (Android ARM64), mantendo compatibilidade de jogos Windows x86 e x64 dentro dos contêineres. Não integrar XenDroid/Xbox 360. Prioridades: estabilidade, drivers Adreno/Mali, LSFG-VK/frame pacing, memória e Discord RPC opcional.

## Estado de integração verificado (18/10/2026)

- **`main`** já recebeu o PR #19 (consolidação Aurora) e o PR #7 (histórico RPC), ambos mesclados em 17/10/2026. Não pedir novo merge deles nem duplicar seus commits. O código de `main` ainda usa `11.3-aurora-rc.1` (`versionCode 39`).
- **PR #31** foi mesclado à `main`: README, identidade e documentação agora anunciam a **RC8 experimental**. Divulgar a release não significa que o código correspondente já esteja em `main`.
- **PR #26** (`release/aurora-11-3-rc2-validation`) é um draft legado, ainda aberto, com conflitos em relação à antiga base `feat/aurora-consolidation`. A RC2 não deve ser promovida isoladamente nem usada para reverter trabalho posterior.
- **PR #30** (`feat/electron-vesktop-compat`) é o draft que contém o código da **RC8** e descendente da branch RC2. CI SDK-free passou para `389ee866`; faltam integração revisada com `main` e testes físicos. Não mesclar #26 e #30 de forma duplicada.
- **Release RC8:** a tag imutável `aurora-11.3-rc8-discord-firefox` aponta a `389ee866`; a APK é experimental, de assinatura debug. Builds **SDK-free não disponibilizam o RPC Android nativo**, mesmo que tenham clientes Discord Windows opcionais. Não mover a tag nem chamar essa release de Stable.
- **Correção de estabilidade** `8e43032f`: `KeyValueSet` ignora campos importados malformados; testes JVM cobrem leitura, persistência e pares de drivers. Preservar essa proteção.

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
