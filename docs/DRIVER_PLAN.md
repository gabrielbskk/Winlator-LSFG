# Plano auditável de drivers — Aurora ARM64

**Estado:** planejamento técnico; não representa suporte PANVK implementado nem ganho de desempenho comprovado.

## Inventário confirmado na `main` em 2026-10-17

| Área | Observação verificada | Ação proposta |
| --- | --- | --- |
| Vulkan | `GraphicsDrivers.java` expõe `turnip` e `vortek` | Preservar opções existentes e adicionar detecção/fallback testáveis |
| OpenGL | `zink`, `virgl`, `gladio` | Evitar regressões de configuração por contêiner |
| Turnip empacotado | `app/app/src/main/assets/graphics_driver/` contém 8 arquivos `turnip-*.tzst` (24.3.4.1 até 26.2.1.1) | **Não remover** versões sem matriz de compatibilidade |
| Turnip instalável | `installable_components/turnip/` contém `index.txt` e três pacotes `turnip-*.tzst` | Diferenciar assets embarcados de downloads/instalação; conferir índice e atualização |
| Mali G52 | No PR #19, novos contêineres usam Vortek/Gladio independentemente da GPU | Validar Vulkan do sistema e fallback Gladio em dispositivo |
| PANVK | Não consta como opção em `GraphicsDrivers.java` nem como pacote em `assets/graphics_driver/` | Investigar dependências reais e viabilidade antes de integrar |
| AdrenoTools | `app/app/src/main/cpp/libadrenotools/` já existe | Auditar chamadas e necessidade; não pressupor que removê-lo seja seguro |
| Configuração | `GraphicsDriverPicker.java` cria seletores Vulkan/OpenGL; `TurnipConfigDialog.java` guarda versão e flags | Manter compatibilidade com configurações persistidas |

## Trabalho simultâneo do Codex

O [PR #19](https://github.com/gabrielbskk/Winlator-LSFG/pull/19) já implementa o primeiro marco sobre a branch experimental de RPC: correções de tema/GPU, Turnip manual, fallback Mali, RPC e testes. O merge `fffef037` incorporou os PRs #20/#21 e os testes de inventário sem apagar o histórico. **Não repetir essas implementações.** A validação em aparelhos ainda falta.

## Etapas e critérios de aceite

### A. Inventário de versões (primeiro PR de código)
- **Implementado:** `scripts/audit_graphics_drivers.py` e `scripts/test_driver_inventory.py` com inventário do pacote e comparação de índices; complementar a cobertura conforme mudanças nos drivers.
- Documentar origem, hash, ABI, licença e mecanismo de seleção de cada pacote, onde for possível verificar.
- Testar configuração legada de contêiner e migração sem perda de dados.
- **Aceite:** inventário reproduzível, nenhuma versão removida, CI verde.

### B. Adreno A6xx/A7xx
- Cobrir identificação de Adreno 619, 642/642L e A7xx com testes unitários.
- Verificar que novos contêineres começam com Vortek/Gladio e que o usuário escolhe Turnip e sua versão manualmente; não reintroduzir autodetecção como política de escolha.
- Testar contêineres antigos: `turnip` com versão explícita permanece intacto; versão vazia/`auto` usa fallback fixo empacotado.
- Medir abertura do contêiner, renderização e consumo de RAM em aparelhos reais.
- **Aceite:** sem regressão em Vulkan/OpenGL; testes por modelo registrados.

### C. Mali G52 / PANVK
- Verificar se a pilha Android suporta PANVK na GPU alvo (kernel/DRM, ICD Vulkan, dependências e licença).
- Se inviável, manter Vortek e documentar claramente o bloqueio; não exibir opção não funcional.
- Se viável, integrar **em PR independente**, com pacote de origem verificável, seleção explícita e fallback.
- **Aceite:** inicialização e renderização comprovadas em Mali real, sem Turnip forçado.

### D. UI e presets
- Seletor de driver deve indicar GPU detectada **para diagnóstico**, driver escolhido manualmente e fallback; configurações antigas continuam legíveis.
- Separar mudanças de driver das de LSFG-VK e Discord RPC para facilitar diagnóstico.

### E. Frame generation e memória
- Registrar baseline FPS, percentis de frametime, stutter, pico de RAM e latência com LSFG desligado e 2x/3x/4x.
- Otimizar apenas gargalos identificados; comparar mesmo jogo, resolução, preset e temperatura.
- **Aceite:** evidências reproduzíveis e ausência de regressões.

### F. Aplicativos Electron/Vesktop (experimental)
- Hipótese: falhas antes da primeira janela podem vir da integração CEF/alocador do Box64, independentemente do driver Vulkan.
- Dispositivo inicial: Galaxy S23, Snapdragon 8 Gen 2/Adreno 740, Android 16; Mali G52 permanece sem validação física.
- Resultado no Galaxy S23: o preset com `BOX64_LIBCEF=1`, `BOX64_MALLOC_HACK=2` e dynarec conservador abriu uma janela preta. O preset Intermediário, com GPU ativa e `--no-sandbox --disable-gpu-sandbox`, abriu e apresentou desempenho melhor; ainda requer medições reproduzíveis.
- Implementação RC3: o preset Electron usa a base Intermediária testada e não força os overrides CEF/alocador. Uma instalação extraída do Vesktop em Downloads é detectada para criar um atalho local no menu Iniciar, sem redistribuir o aplicativo no APK.
- Companion RPC opt-in por contêiner: botão para primeiro login/configuração e atalho de inicialização com `--start-minimized`; requer habilitar o arRPC no Vesktop e não garante detecção de jogos que não publicam RPC.
- RC5: cria atalho opcional para Endcord Windows já extraído em Downloads; RPC integrado, compatibilidade e RAM pendentes no Winlator.
- RC4: cria atalhos para Discord Web em Chrome/Edge/Firefox já instalados no prefixo. Vencord Web + arRPC depende de instalação manual e está sem validação no Winlator; consulte `docs/DISCORD_RPC_WINLATOR.md`.
- Fontes: fontes Unicode/CJK/símbolos existentes no Android são copiadas, somente quando ausentes, para o prefixo Wine. O APK não redistribui esses arquivos.
- Teste: comparar preset Intermediário e Electron com Vesktop Windows x64, registrar primeira janela, login, renderização, áudio, reabertura, RAM e logs.
- Reversão: selecionar outro preset e remover os argumentos do atalho; nenhum contêiner existente é migrado.
- Limite: flags `--no-sandbox` reduzem o isolamento dentro do contêiner e não comprovam compatibilidade sem execução física.

## Relação com outros trabalhos
- [PR #7 — Discord RPC Experimental 5](https://github.com/gabrielbskk/Winlator-LSFG/pull/7): draft; não mesclar automaticamente.
- [PR #18 — tema Aurora, Adreno 619/642 e Mali G52](https://github.com/gabrielbskk/Winlator-LSFG/pull/18): já integrado; preservar suas correções.
- [Workflow de desenvolvimento](DEVELOPMENT_WORKFLOW.md).

**Decisão do mantenedor (pós Experimental 6):** remover completamente o controle automático de Turnip. O PR #19 deve manter driver/versão manual e incluir testes. Não promover Stable antes dos gates de `docs/STABLE_RELEASE_GATE.md` na branch de integração.

**Observação:** uma APK Android ARM64 pode conter recursos para jogos Windows 32 e 64 bits. Não confundir ABI do aplicativo com arquitetura do executável convidado.
