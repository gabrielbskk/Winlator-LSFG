# Aurora — próximos passos

O Aurora concentra o trabalho em RPC opcional e compatibilidade de aplicativos Windows no Android ARM64. Esta lista expressa prioridades; não promete suporte já disponível nem prazo de entrega.

## Introduzido na RC8 (experimental, não validado)

- Botões experimentais para Endcord GUI, LegCord e Vesktop Windows; **mantenedor relatou falhas e redundância**, por isso ocultados na RC9 de código.
- Download experimental do Firefox ESR, **sem funcionamento comprovado e suspenso na RC9**; atalhos de navegador já presente seguem independentes.
- Início minimizado opcional do Vesktop e preset Electron.
- Seleção manual de drivers e configurações de LSFG-VK por contêiner.

Veja [versões](docs/RELEASES.md) e [handoff do Codex](docs/CODEX_HANDOFF_RC9.md) para distinguir código RC9 da APK RC8 e as restrições de validação.

## 1. Validar RPC e reduzir o custo de uso

Comparar Endcord, LegCord e Vesktop no mesmo aparelho: login, RPC ao vivo, troca e encerramento de jogo, RAM, CPU e reabertura. Priorizar o cliente que funcionar com menor custo medido. Manter RPC opcional e sem pedir token ao Aurora.

## 2. Navegadores e ferramentas

Validar instalação, primeira janela, rede, fontes e downloads do Firefox no Wine. Reproduzir falhas de ferramentas Windows antes de ampliar presets. Avaliar WebView separadamente: a RC8 tem encaminhamento ao navegador, sem WebView embutido.

## 3. Compatibilidade e desempenho

Verificar jogos x86/x64, Wine/Box64 e gráficos em Adreno/Mali com configurações reproduzíveis. Turnip permanece manual; PANVK depende de pesquisa e hardware compatível. Medir RAM e frametime antes de afirmar melhorias. LSFG exige DLL fornecida pelo usuário e testes em caminhos suportados.

## 4. Caminho para Stable

Concluir instalação limpa, atualização, backup/rollback, assinatura de distribuição, jogos e RPC em aparelhos reais. Registrar resultados nos [critérios para Stable](docs/STABLE_RELEASE_GATE.md). CI aprovado é uma etapa; a RC8 continua experimental.

## Como participar

Siga [CONTRIBUTING.md](CONTRIBUTING.md). Relatos devem informar aparelho, Android, GPU/driver, versão Aurora, Wine/Box64, programa e passos para reproduzir. Preserve créditos upstream, tags e histórico.
