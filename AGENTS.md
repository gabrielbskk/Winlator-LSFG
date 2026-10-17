# Instruções para agentes de código — Winlator LSFG Aurora

## Missão e limites
Trabalhe **somente no Aurora** (Android ARM64), mantendo compatibilidade de jogos Windows x86 e x64 dentro dos contêineres. Não integrar XenDroid/Xbox 360. Prioridades: estabilidade, drivers Adreno/Mali, LSFG-VK/frame pacing, memória e Discord RPC opcional.

## Antes de qualquer mudança
1. Leia `README.md`, `ROADMAP.md`, `docs/DEVELOPMENT_WORKFLOW.md`, `docs/GPU_COMPATIBILITY_AURORA.md` e `docs/DRIVER_PLAN.md`.
2. Consulte PRs abertos e a árvore da branch atual; **não duplique trabalho em andamento**.
3. Registre hipótese, dispositivos afetados, arquivos alterados, plano de teste e critério de reversão.
4. Crie branch própria e PR pequeno em draft quando depender de hardware ou bibliotecas externas.

## Ordem de implementação
1. **Inventário e testes de regressão**: mapear driver embutido vs instalável, versão, arquitetura, origem/licença e caminhos de seleção; documentar resultados sem alterar binários.
2. **Adreno A6xx/A7xx**: corrigir identificação/seleção e fallback de Turnip, preservar versões antigas enquanto a matriz de compatibilidade não justificar a remoção. AdrenoTools já está presente no código nativo; não remover nem ampliar seu uso sem demonstrar dependências e impacto.
3. **Mali G52 e PANVK**: analisar viabilidade real do driver PANVK no Android/Winlator (kernel, DRM, Vulkan ICD, distribuição e licenças). Não oferecer PANVK na UI se não houver pacote funcional e validação. Manter Vortek/Vulkan do sistema e fallback OpenGL.
4. **UI de drivers**: separar claramente seleção Vulkan/OpenGL, versões instaladas, GPU detectada, aviso de incompatibilidade e fallback seguro. Não aplicar Turnip automaticamente em Mali.
5. **LSFG-VK/frame generation**: instrumentar frame time, latência, RAM e stutter antes de alterar presets 2x/3x/4x; manter `Lossless.dll` fora do repositório.
6. **Memória/performance ARM64**: medir uso de heap, caches e inicialização por GPU e jogo; otimizar somente com comparações reproduzíveis.
7. **Discord RPC**: trabalhar a partir do PR #7 sem confundir código experimental com `main` ou APK publicado; SDK proprietário somente em CI autorizado, com hash conferido.

## Regras de segurança e compatibilidade
- Não apagar/consolidar Turnip nem mudar padrão de driver sem benchmarks e regressão por GPU.
- Não distribuir `Lossless.dll`, Discord Social SDK/AAR ou binários sem origem/licença verificada.
- Não fazer force-push/rebase destrutivo, alterar créditos upstream, excluir releases/tags ou mesclar PR #7 automaticamente.
- Não afirmar ganhos de FPS, RAM, suporte PANVK, estabilidade ou compatibilidade de aparelho sem testes físicos.
- Preservar atribuição a Rafaela (@amyroseee), brunodev85 e autores dos componentes.
- Não alterar a release principal ao criar PRs experimentais.

## Entrega por PR
Descrever: objetivo; GPU/SoC e Android; versões dos drivers; diferenças antes/depois; testes JVM/Android CI; testes em dispositivo (ou **pendentes**); riscos; fallback; plano de reversão. Preferir commits `feat(driver):`, `fix(gpu):`, `test(gpu):`, `perf(lsfg):` e `docs(driver):`.
