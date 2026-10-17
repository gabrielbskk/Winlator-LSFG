# Plano auditável de drivers — Aurora ARM64

**Estado:** planejamento técnico; não representa suporte PANVK implementado nem ganho de desempenho comprovado.

## Inventário confirmado na `main` em 2026-10-17

| Área | Observação verificada | Ação proposta |
| --- | --- | --- |
| Vulkan | `GraphicsDrivers.java` expõe `turnip` e `vortek` | Preservar opções existentes e adicionar detecção/fallback testáveis |
| OpenGL | `zink`, `virgl`, `gladio` | Evitar regressões de configuração por contêiner |
| Turnip empacotado | `app/app/src/main/assets/graphics_driver/` contém 8 arquivos `turnip-*.tzst` (24.3.4.1 até 26.2.1.1) | **Não remover** versões sem matriz de compatibilidade |
| Turnip instalável | `installable_components/turnip/` contém `index.txt` e três pacotes `turnip-*.tzst` | Diferenciar assets embarcados de downloads/instalação; conferir índice e atualização |
| Mali G52 | `GraphicsDrivers.getDefaultDriver()` seleciona Vortek em vez de Turnip quando GPU não é Adreno | Validar Vulkan do sistema e fallback Gladio em dispositivo |
| PANVK | Não consta como opção em `GraphicsDrivers.java` nem como pacote em `assets/graphics_driver/` | Investigar dependências reais e viabilidade antes de integrar |
| AdrenoTools | `app/app/src/main/cpp/libadrenotools/` já existe | Auditar chamadas e necessidade; não pressupor que removê-lo seja seguro |
| Configuração | `GraphicsDriverPicker.java` cria seletores Vulkan/OpenGL; `TurnipConfigDialog.java` guarda versão e flags | Manter compatibilidade com configurações persistidas |

## Trabalho simultâneo do Codex

O [PR #19](https://github.com/gabrielbskk/Winlator-LSFG/pull/19) já implementa o primeiro marco sobre a branch experimental de RPC: correções do tema, GPU, política Turnip por geração, Mali via driver do sistema, estabilização do RPC e testes. **Não repetir as mesmas mudanças.** Este documento e o script de inventário complementam o PR #19; reconcilie ambos antes de futuras alterações na `main`.

## Etapas e critérios de aceite

### A. Inventário de versões (primeiro PR de código)
- Criar teste ou script que compare `assets/graphics_driver`, `installable_components/turnip/index.txt` e os identificadores da UI.
- Documentar origem, hash, ABI, licença e mecanismo de seleção de cada pacote, onde for possível verificar.
- Testar configuração legada de contêiner e migração sem perda de dados.
- **Aceite:** inventário reproduzível, nenhuma versão removida, CI verde.

### B. Adreno A6xx/A7xx
- Cobrir identificação de Adreno 619, 642/642L e A7xx com testes unitários.
- Verificar escolha Turnip por padrão apenas quando compatível e fallback seguro quando ausente.
- Medir abertura do contêiner, renderização e consumo de RAM em aparelhos reais.
- **Aceite:** sem regressão em Vulkan/OpenGL; testes por modelo registrados.

### C. Mali G52 / PANVK
- Verificar se a pilha Android suporta PANVK na GPU alvo (kernel/DRM, ICD Vulkan, dependências e licença).
- Se inviável, manter Vortek e documentar claramente o bloqueio; não exibir opção não funcional.
- Se viável, integrar **em PR independente**, com pacote de origem verificável, seleção explícita e fallback.
- **Aceite:** inicialização e renderização comprovadas em Mali real, sem Turnip forçado.

### D. UI e presets
- Seletor de driver deve indicar GPU detectada, driver escolhido e fallback; configurações antigas continuam legíveis.
- Separar mudanças de driver das de LSFG-VK e Discord RPC para facilitar diagnóstico.

### E. Frame generation e memória
- Registrar baseline FPS, percentis de frametime, stutter, pico de RAM e latência com LSFG desligado e 2x/3x/4x.
- Otimizar apenas gargalos identificados; comparar mesmo jogo, resolução, preset e temperatura.
- **Aceite:** evidências reproduzíveis e ausência de regressões.

## Relação com outros trabalhos
- [PR #7 — Discord RPC Experimental 5](https://github.com/gabrielbskk/Winlator-LSFG/pull/7): draft; não mesclar automaticamente.
- [PR #18 — tema Aurora, Adreno 619/642 e Mali G52](https://github.com/gabrielbskk/Winlator-LSFG/pull/18): já integrado; preservar suas correções.
- [Workflow de desenvolvimento](DEVELOPMENT_WORKFLOW.md).

**Observação:** uma APK Android ARM64 pode conter recursos para jogos Windows 32 e 64 bits. Não confundir ABI do aplicativo com arquitetura do executável convidado.
