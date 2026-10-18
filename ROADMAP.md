# Aurora — próximos passos

O Aurora concentra o trabalho em RPC opcional e compatibilidade de aplicativos Windows no Android ARM64. Esta lista expressa prioridades; não promete suporte já disponível nem prazo de entrega.

## Disponível na RC8

- Instalação opcional de Endcord GUI, LegCord e Vesktop Windows, com tamanho e SHA-256 verificados.
- Download do instalador oficial Firefox ESR e atalhos de navegador.
- Início minimizado opcional do Vesktop e preset Electron.
- Seleção manual de drivers e configurações de LSFG-VK por contêiner.

Veja [versões](docs/RELEASES.md) para o código publicado e o alcance da validação.

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
