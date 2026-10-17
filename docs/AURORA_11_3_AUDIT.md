# Aurora 11.3: auditoria e execução progressiva

Auditoria de 17/10/2026, antes das correções funcionais. Referências:
`main` babca08, `experimental/rpc-android-v4` d9b574b, PR #7 aberto,
rascunho e com conflitos. A branch de integração preserva ambos os históricos;
não representa aprovação nem merge do PR #7 na main.

## Arquitetura e estado encontrado

- Android Java/XML com JNI/C/C++, APK ARM64; Wine/Box64, DXVK e WineD3D
  executam os hóspedes Windows. Assets i386-windows, x86_64-windows e
  syswow64 estão presentes nas duas branches: não precisam ser restaurados
  novamente. Execução de jogos 32/64-bit ainda requer dispositivos.
- GPUHelper/GpuCompatibility identificam a GPU via EGL; ainda não há a
  prioridade solicitada de propriedades Vulkan. GeneralComponents,
  ContainerDetailFragment e VortekConfigDialog controlam seleção e opções.
- AdrenoTools participa do carregamento Vulkan customizado; não é uma
  dependência morta. Turnip, Vortek e VirGL devem ser preservados. Os oito
  Turnip internos e três downloads inventariados têm hashes distintos;
  diferença de hash não comprova compatibilidade nem melhor desempenho.
- PANVK não tem carregamento dedicado validado. Importação genérica de ZIP
  não comprova suporte Mali/kernel/ABI. O endereço público fornecido para
  LukeValen/panvk-mali-g52 respondeu 404 nesta auditoria. Não criar uma opção
  que prometa suporte sem demonstrar o caminho de carregamento.
- LSFG tem controles de multiplicador e presets; alterações possíveis aqui
  são integração, sincronização, configuração e ciclo de vida. O algoritmo
  proprietário de Lossless.dll está fora deste trabalho.
- O RPC completo está no PR #7, com JNI Social SDK, detecção de sessão,
  reconexão, capas Steam e prévia por atalho. A main isolada não contém essa
  implementação. Preservar o funcionamento relatado pelo usuário; a auditoria
  não reproduziu uma sessão Discord em dispositivo.
- Aurora já existe em styles.xml e drawables nas duas branches. Não precisa
  ser refeito: precisa de recursos centralizados e correções de contraste.

## Problemas confirmados por leitura do código

- Prévia RPC tem fundo escuro fixo e texto escuro no modo claro; cabeçalho
  de tabela usa branco sobre accent claro no modo escuro; ícones de ajuda e
  texto de armazenamento têm baixo contraste no modo claro.
- Diálogo LSFG tem conteúdo fixo de 360dp, além das margens externas.
- Regex Adreno não aceita A730 e não identifica modelos antigos para fallback;
  seleção não possui tabela versionada de compatibilidade por geração.
- Vortek perde seleção de zero extensões ao reabrir; comparação de driver
  serializado com `vortek` não cobre `vortek,gladio`; números importados não
  validados podem provocar exceção. Manifesto vazio acessa índice zero.
- gpu_helper.c não trata enumeração Vulkan com zero dispositivos.
- Prévia Steam inicia downloads repetidos para o mesmo App ID; executores
  de operações em SettingsFragment/RootFSInstaller não encerram explicitamente.
- Workflows de publicação/promoção têm gatilhos push, incompatíveis com a
  autorização explícita agora exigida. A main referencia scripts RPC ausentes.

## Dependências, licenças e validação inicial

Gradle 8.14.5, AGP 8.4.2, JDK 17, Android SDK 35; NDK 24 para build sem SDK
Discord e NDK 27 para SDK nativo. AAR Discord fornecido pelo usuário fica
fora do Git. Lossless.dll não será alterada nem redistribuída. Arquivos SDK
removidos do snapshot ainda existem no histórico; não reescrever histórico
sem uma tarefa específica. Preservar LGPL e créditos Rafaela/amyroseee,
brunodev85 e componentes upstream.

Baseline local: main passou 4 testes JVM e assembleDebug. Branch RPC passou
27 testes JVM; build completo ainda em execução ao registrar este relatório.
Existem avisos C/C++ de qualificadores const e enums no código nativo.
Não foram executados testes de GPU física, FPS, frametime, RAM, conexão Discord
ou interface em diferentes telas. Compilar não comprova essas propriedades.

## Sequência e arquivos candidatos

1. Reconciliar main/PR #7 isoladamente; centralizar values/styles.xml e
   drawables Aurora, corrigir layouts RPC/armazenamento e diálogo LSFG.
2. GpuCompatibility/GPUHelper/gpu_helper.c: identificação robusta e fallback;
   tabela revisável, logs e sobrescrita manual antes de recomendações novas.
3. GeneralComponents/VortekConfigDialog: validar importações e persistência;
   manter Mali separado de Turnip e documentar bloqueios PANVK.
4. Corrigir recursos e tarefas de ciclo de vida comprovadamente abandonados;
   medir em dispositivo antes de alegar economia de RAM ou desempenho.
5. Revisar integração LSFG e regressões RPC sem reescrever algoritmos/SDK.
6. Testes de regressão, build SDK-free e SDK-backed, workflows de publicação
   somente manuais, documentação de matriz física pendente e PRs revisáveis.
7. Preparar candidata apenas após os gates aplicáveis. Nome/versão 11.3 e
   publicação dependem de confirmação; não substituir a release atual.

Esta lista é plano, não declaração de implementação. O registro de execução
deve separar cada correção efetiva dos bloqueios e testes pendentes.
