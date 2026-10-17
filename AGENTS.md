# Instruções para agentes de código — Winlator LSFG Aurora

## Missão e limites
Trabalhe **somente no Aurora** (Android ARM64), mantendo compatibilidade de jogos Windows x86 e x64 dentro dos contêineres. Não integrar XenDroid/Xbox 360. Prioridades: estabilidade, drivers Adreno/Mali, LSFG-VK/frame pacing, memória e Discord RPC opcional.

## Branch de integração em andamento

- **PR #19** (`feat/aurora-consolidation` → `main`) já contém tema, detecção Adreno/Mali, seleção manual de Turnip (driver e versão), correções Vulkan, RPC e CI. Continue a partir desse trabalho; não replique essas alterações em outro PR.
- **PRs #20 e #21** já foram mesclados à `main` e incorporados ao PR #19 pelo commit `fffef037` (merge de histórico, sem force-push): script de auditoria, testes e política de Turnip inteiramente manual. Não repetir nem reverter.
- **PR #7** permanece como referência histórica RPC; não mesclá-lo independentemente, pois o PR #19 já reúne RPC e Aurora para revisão na `main`.
- **RC1:** código-fonte 11.3-aurora-rc.1 (versionCode 39), sem Stable publicada. Ver `docs/RELEASE_CANDIDATE_11_3.md` e gates antes de distribuição.
- **Correção de estabilidade** `8e43032f`: `KeyValueSet` ignora campos importados malformados; testes JVM verificam leitura, persistência e pares de drivers. Revisar antes de qualquer edição nesse parser.

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
7. **Discord RPC**: trabalhar a partir do PR #7 sem confundir código experimental com `main` ou APK publicado; SDK proprietário somente em CI autorizado, com hash conferido.

## Regras de segurança e compatibilidade
- Não apagar/consolidar Turnip. Não reintroduzir seleção automática: driver e versão Turnip são manuais por decisão do mantenedor. Preservar o fallback fixo para contêineres antigos com versão vazia/`auto`.
- Não distribuir `Lossless.dll`, Discord Social SDK/AAR ou binários sem origem/licença verificada.
- Não fazer force-push/rebase destrutivo, alterar créditos upstream, excluir releases/tags ou mesclar PR #7 automaticamente.
- Não afirmar ganhos de FPS, RAM, suporte PANVK, estabilidade ou compatibilidade de aparelho sem testes físicos.
- Preservar atribuição a Rafaela (@amyroseee), brunodev85 e autores dos componentes.
- Não alterar a release principal ao criar PRs experimentais.

## Entrega por PR
Descrever: objetivo; GPU/SoC e Android; versões dos drivers; diferenças antes/depois; testes JVM/Android CI; testes em dispositivo (ou **pendentes**); riscos; fallback; plano de reversão. Preferir commits `feat(driver):`, `fix(gpu):`, `test(gpu):`, `perf(lsfg):` e `docs(driver):`.
