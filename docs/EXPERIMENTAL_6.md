# Winlator LSFG Aurora 11.2 — Experimental 6

Pré-release para testes do primeiro marco do trabalho Aurora. Não é a versão
11.3 estável e não substitui a release principal anterior.

## Alterações

- Integração isolada da main com o desenvolvimento RPC do PR #7, preservando
  ambos os históricos e os componentes Windows 32/64-bit.
- Paleta Aurora centralizada, contraste corrigido na prévia RPC, cabeçalhos,
  ajuda e armazenamento; diálogo LSFG com rolagem e largura adaptável.
- Identificação Adreno A6xx/A7xx incluindo A730, 642L e aliases FDxxx; Mali
  G52/G57/G68/G72/G76 mantém o caminho Vulkan do sistema.
- Consulta de identidade Vulkan com propriedades do driver quando disponíveis,
  fallback EGL e proteção contra zero dispositivos Vulkan.
- Política Turnip revisão 1 separa A6xx/A7xx, mantendo a versão 26.1.0 já
  existente como baseline provisória. Seleção automática/manual por configuração
  e motivo em log. Nenhuma versão alternativa foi removida.
- Proteção contra Turnip em sessão Mali; painel Mali com identificação e
  quantidade de extensões do sistema. PANVK não é declarado funcional.
- Persistência de seleção vazia de extensões, validação de configurações
  importadas e de manifesto vazio de driver, aviso de reinício corrigido.
- Tarefas pontuais de instalação encerram seus workers; removida alocação
  nativa sem liberação no caminho de carregamento Vortek.
- Prévia RPC evita baixar novamente a capa ao editar só o título, limita a
  fila de imagens e encerra o worker ao fechar o diálogo. Login, sessão,
  reconexão, Steam App ID e configuração de imagens existentes preservados.

## Validação e limites

Build ARM64 com Discord Social SDK 1.10 fornecido separadamente, testes JVM,
testes estáticos Aurora e integridade do APK. O workflow publica somente após
seus testes e build passarem. Consulte o run associado ao commit da tag.
SHA-256 acompanha o APK em `SHA256SUMS.txt`.

Cobertura automatizada de nomes/política: Adreno 619, 642/642L, 643, 650, 660,
680, 685, 690, 710, 720, 725, 730, 732, 735, 740, 750 e limites 699/799;
Mali G52/G57/G68/G72/G76 e fallback desconhecido. Isso não é teste físico.

**GPUs e drivers efetivamente testados em hardware nesta compilação: nenhum.**
Ainda faltam jogos Windows 32/64-bit, criação/encerramento de contêineres,
RPC/login/capas/reconexão offline, interface em diferentes telas e benchmarks
reproduzíveis de RAM/FPS/frametime. Não há promessa de ganho percentual.

PANVK depende de um pacote verificável e de compatibilidade kernel/firmware/ABI.
O link histórico LukeValen/panvk-mali-g52 respondeu 404 na auditoria. LSFG
continua indisponível via Vortek por limitações de swapchain; os presets
Performance/Balanced/Quality/Custom e 2x/3x/4x foram preservados. Não houve
alteração do algoritmo proprietário de Lossless.dll, que não está no APK.

## Atualização e relato de testes

Faça backup dos contêineres, configurações e saves antes de instalar. APK de
teste com assinatura debug: pode não atualizar diretamente uma instalação
assinada com outra chave. Se Android recusar a atualização, preserve os dados
e confirme o backup antes de considerar reinstalação.

Informe modelo do aparelho/GPU, Android, driver, Wine/DXVK, jogo e arquitetura,
LSFG ligado/desligado e passos para reproduzir. Para RPC, teste título, tempo,
capa, troca/fechamento de jogo e perda/retorno da conexão. Não envie tokens.

Créditos: Rafaela / amyroseee, brunodev85 e autores de Wine, Box64, Mesa/Turnip,
VirGL, DXVK, LSFG-VK, AdrenoTools e demais componentes. SDK Discord não foi
adicionado ao repositório; licenças e atribuições existentes foram preservadas.
