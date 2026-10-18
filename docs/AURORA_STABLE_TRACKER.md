# Aurora — controle de preparação da Stable

**Data-base:** 17/10/2026 · **Canal:** desenvolvimento / candidata de testes · **Stable:** NÃO VALIDADA.

A publicação oficial depende de execução física, assinatura de distribuição e aprovação do mantenedor. Este documento acompanha progresso real por commit, sem prometer que CI significa GPU ou jogo funcionando.

## Alterações já incorporadas na branch `feat/aurora-consolidation`

- PR #19 mantém tema Aurora, identificação Adreno/Mali, escolha manual de driver/versão Turnip, configurações Vortek/Gladio, proteção Mali, RPC/capas e recursos Windows x86/x64.
- Merge `fffef037`: incorpora `main` (PRs #20/#21), auditoria de drivers, scripts e documentação; preserva histórico anterior e o PR #7 separado.
- Correção `8e43032f`: evita exceções quando um campo importado de `KeyValueSet` não contém `=`; três métodos de teste JVM verificam comportamento e manutenção da opção Turnip manual.
- CI de integração: testar recursos Aurora, inventário de pacotes, JVM/Gradle SDK-free e Android ARM64 com SDK Discord autorizado, mantendo builds experimentais e versões estáveis distintas.

## Gates automatizados (verificar no commit FINAL do candidato)

- [ ] `scripts/test_aurora_resources.py` — UI Aurora e seleção manual.
- [ ] `scripts/test_driver_inventory.py` e `scripts/audit_graphics_drivers.py` — índice/arquivos, hashes e declarações.
- [ ] `:app:testDebugUnitTest` — incluir `KeyValueSetMalformedConfigTest`, `TurnipVersionSelectionTest` e testes RPC.
- [ ] `:app:assembleDebug` (SDK-free) e inspeção do APK.
- [ ] Build ARM64 nativo com Discord SDK autorizado, JNI e recursos Windows x86/x64, no **mesmo commit**.
- [ ] Build de distribuição assinado com chave estável controlada pelo mantenedor (não chave debug), hash SHA-256 e verificação de atualização.

## Gates de hardware / bloqueios reais

- [ ] **Adreno 619:** contêiner, driver Turnip manual, versão persistida, DXVK/fallback e jogos x86/x64.
- [ ] **Adreno 642/642L:** mesmos testes, mudança entre versões Turnip e rollback de configuração.
- [ ] **Adreno A7xx:** validar separadamente os modelos disponíveis; não generalizar para a família toda.
- [ ] **Mali G52:** Vortek/Gladio, Vulkan de sistema, inicialização/retorno, jogos Windows; **PANVK não integrado**.
- [ ] **LSFG-VK:** ligado/desligado e multiplicadores 2x/3x/4x somente nas rotas suportadas; registrar FPS, frametime, stutter, RAM, temperatura e travamentos.
- [ ] **Discord RPC real:** ativação opcional, nome do jogo, capas Steam, contador, encerramento, troca de jogo, falta/volta de rede.
- [ ] **Instalação e dados:** instalação limpa, atualização, contêineres e saves existentes, assinatura e procedimento de rollback.
- [ ] **Interface:** modo claro/escuro, diálogos, dimensões e acessibilidade em aparelho.

Cada teste em hardware deve registrar modelo/SoC, Android/kernel, driver e versão, jogo x86/x64, Wine/Box64/DXVK, preset, logs sem credenciais e resultados objetivos. Problema reprodutível grave bloqueia a Stable.

## Próximas etapas de implementação

1. Manter PR #19 como integração de código até CI do último commit passar; não mesclar o PR #7 isoladamente.
2. Corrigir regressões confirmadas dos testes de dispositivos; preservar seleção manual de Turnip e todas as versões existentes.
3. Separar instrumentação de frametime e RAM de mudanças reais de LSFG ou driver; não anunciar melhorias não medidas.
4. Revisar viabilidade técnica de PANVK como projeto independente sem comprometer o fallback Mali.
5. Somente depois dos gates, preparar tag inédita, notas da versão, checksum e assinatura apropriada.

**Histórico:** não fazer force-push, apagar tags/releases ou reescrever créditos de Rafaela (@amyroseee), brunodev85 e outros autores.


## Situação após RC8 experimental (18/10/2026)

- [x] Consolidação Aurora (PR #19) integrada à `main` e compilação SDK-free CI aprovada em `14e85d7` (17/10).
- [x] Documentação da RC8 (PR #31) integrada à `main` em `7a7492f`, distinguindo a tag experimental do código-fonte ainda em PR.
- [x] RC8 experimental publicada na tag `aurora-11.3-rc8-discord-firefox`, commit `389ee866`; build SDK-free, testes JVM, compilação AndroidTest e inventário de drivers passaram no run `39145337062`. **Isso não valida o funcionamento em hardware.**
- [ ] Integrar com segurança o código da RC8 do PR #30 na `main`, incluindo histórico da RC2 sem mesclar as mesmas alterações duas vezes. Revisar possíveis conflitos com a documentação e a branch atual.
- [ ] Confirmar os testes automatizados no **commit de integração final** (não reutilizar o status verde de outro SHA).
- [ ] Testar em aparelho real Endcord, LegCord, Firefox ESR, Vesktop/RPC, LSFG e jogos Windows 32/64 bits. O relato anterior de abertura da interface do Vesktop em um Galaxy S23 não comprova os demais cenários.
- [ ] Validar assinatura de distribuição, instalação/atualização, backup de contêineres e rollback. A RC8 distribuída é debug, não Stable.

**Decisão de manutenção:** a falta de testes físicos não impede correções de documentação, testes unitários ou hardening de código em branches e PRs. Ela impede apenas afirmar suporte validado, compatibilidade, ganhos de desempenho ou promover a release para **Stable**. Não remover versões Turnip nem reintroduzir seleção automática.
