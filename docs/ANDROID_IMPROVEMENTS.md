# Android: Rich Presence, memória e Mali G52

## Estado desta alteração

Esta implementação adiciona uma integração **opcional** com o Discord Social SDK
no Android/Bionic. Ela não usa o Discord do Windows/Wine nem tokens de usuário.
Sem o AAR oficial, o aplicativo continua compilando e mostra explicitamente que o
RPC está indisponível. A ponte nativa só é compilada quando o AAR está presente.

O build padrão não contém o SDK proprietário. Para ativar o RPC, ainda são
necessários um Application ID próprio, o AAR Android do Social SDK **1.10 ou
superior** e validação em um aparelho com o Discord instalado e conectado.

## Preparar um build com RPC

1. Crie uma aplicação para o Winlator-LSFG no
   [Discord Developer Portal](https://discord.com/developers/applications).
   Habilite o Social SDK e baixe o pacote C++/Android oficial.
2. Copie o `discord_partner_sdk.aar` de release para
   `app/app/libs/discord_partner_sdk.aar`, ou passe um caminho absoluto pela
   propriedade `discordSdkAar`. O arquivo é ignorado pelo Git.
3. Instale as ferramentas Android correspondentes. O build com SDK usa por
   padrão NDK `27.2.12479018` (o Prefab do SDK usa NDK 27). É possível selecionar
   outra versão compatível pela propriedade `androidNdkVersion`.
4. Compile a partir de `app/`:

   ```sh
   ./gradlew :app:assembleDebug \
     -PdiscordSdkAar=/caminho/discord_partner_sdk.aar
   ```

5. No aplicativo, abra **Configurações → Discord Rich Presence**, informe o ID
   público da aplicação e ative a opção. Reabra o contêiner.
6. No Discord para Android, mantenha a conta conectada e habilite o
   compartilhamento de atividade. O menu de jogo **Discord Rich Presence** mostra
   o estado do envio. Uma confirmação do SDK indica aceitação da atualização,
   não comprova sua visibilidade para outra conta.

O RPC direto não requer OAuth, segredo de cliente, token de usuário ou um PC.
Não há chamadas de voz, gateway autenticado ou serviço de microfone nesta
integração. Permissões e componentes de voz/autenticação do AAR são removidos do
manifesto mesclado. Respeite os termos de distribuição do SDK ao publicar builds.

Referências oficiais:

- [RPC sem autenticação no Android, desde 1.10](https://docs.discord.com/developers/discord-social-sdk/development-guides/setting-rich-presence#rich-presence-without-authentication)
- [Configuração C++/Android e inicialização da Activity](https://docs.discord.com/developers/discord-social-sdk/development-guides/account-linking-on-mobile#android-setup)

## Sessão e custo de processamento

- Cruza snapshots completos de processos do WinHandler com janelas mapeadas do
  X server. A janela em foco tem prioridade. WM_CLASS identifica o executável
  quando o PID Unix da janela difere do PID Windows.
- Filtra processos comuns do Wine, gerenciador de arquivos e launchers conhecidos.
  Esta é uma heurística: launchers desconhecidos e jogos sem janela identificável
  precisam de validação específica. Não se afirma reconhecimento universal.
- Usa o nome do atalho somente quando seu executável corresponde ao processo;
  nos demais casos usa o nome do executável sem caminho e sem argumentos. Não
  publica títulos arbitrários de janelas ou diretórios locais.
- Mantém a mesma sessão se a janela for recriada e o processo continuar ativo;
  um processo novo inicia uma nova sessão. Trocar de jogo também reinicia o tempo.
- Suspensão limpa a presença e desconta o período suspenso. Picture-in-Picture
  acompanha o comportamento já existente de suspensão do contêiner.
- Consulta processos a cada cinco segundos, somente quando habilitado e ativo.
  Não faz trabalho de RPC por frame. O Discord renderiza o contador de tempo.
- Inicializa o cliente nativo e sua thread somente ao identificar o primeiro
  jogo; abrir apenas o desktop não aloca um cliente do SDK.
- Acesso ao SDK é serializado em uma HandlerThread. As tentativas têm timeout e
  intervalo crescente, limitado a 60 segundos. Após sucesso, reafirma a presença
  a cada 60 segundos para permitir recuperação quando o Discord é reiniciado,
  preservando o timestamp da sessão. Há limpeza ao encerrar a Activity.
- O listener do gerenciador de tarefas permanece independente dos observadores
  do RPC. Sem ID válido, SDK ou Discord instalado, não se cria cliente nativo nem
  thread de RPC.
- Se o helper Windows parar de responder por 15 segundos, limpa a presença em
  vez de continuar anunciando um jogo cujo processo não pode ser confirmado.

## Memória e estabilidade

- Remove uma referência adicional sem liberação correspondente de
  AHardwareBuffer na criação de EGLImage; valida falhas de alocação e lock.
- Adiciona o uso GPU_SAMPLED_IMAGE aos buffers usados como textura.
- Refaz render targets quando a resolução muda e elimina seus framebuffers,
  além das texturas. Um único efeito usa um render target; múltiplos efeitos
  usam dois. Remover todos os efeitos libera os buffers no próximo frame.
- Invalida recursos dos efeitos ao recriar o contexto EGL e descarta materiais
  removidos na thread GL.
- Mantém segmentos SHM excluídos rastreáveis enquanto ainda possuem anexos;
  desmapeia somente após o último detach. O encerramento libera os mapeamentos
  restantes depois de parar os consumidores em ordem inversa à inicialização.
- Corrige liberação parcial de imagens/memória na criação da swapchain Vortek.
- Substitui executores sem shutdown por threads de tarefa finita no setup e no
  WinHandler, e evita espera sem saída no encerramento do remetente.
- Corrige includes de funções C nos utilitários e no compressor de textura para
  permitir compilar também com o Clang mais estrito do NDK 27, usado com o SDK.

Estas correções não equivalem a uma medição de redução de RAM. Ainda é necessário
medir Java/native heaps, PSS dos processos e memória gráfica em sessões repetidas,
inclusive com o SDK oficial habilitado. O ciclo de vida global do SDK também deve
ser verificado na versão fornecida pelo desenvolvedor.

## Mali G52 e PanVK

O diálogo de configuração do Vortek mostra o link das
[releases indicadas](https://github.com/LukeValen/panvk-mali-g52/releases) ao
identificar Mali G52. O importador existente aceita o ZIP `meta.json` e conserva
as bibliotecas auxiliares. Agora verifica também `minApi` antes de importar.
O driver continua sendo uma escolha explícita, sem substituir o driver System.

O pacote examinado foi **PanVk V7 0.0.2-alpha**, destinado a Mali-G52 MC2, com
`minApi=28`. SHA-256 do ZIP:

```text
a5da75ae597b23337825c5a35d480c7a7cc0c8655d35850ca5fd25b37ac78289
```

O pacote inclui `libvulkan_panfrost.so`, `libc++_shared.so` e `libdrm.so`.
Não foi incluído no APK. A release declara que é experimental e não conforme ao
Vulkan. Android, kbase/kernel e recursos usados pelo jogo continuam determinando
a compatibilidade. Não há teste de carregamento/renderização desta versão nesta
sessão.

**LSFG é desativado no caminho Vortek atual**, com aviso e continuidade do jogo.
A swapchain desse backend sempre adquire índice 0 e usa o hardware buffer da
janela para suas imagens; isso não oferece a semântica exigida pelas aquisições
adicionais do LSFG. Instalar PanVK não corrige essa integração. A preferência do
contêiner é preservada, para poder usar LSFG ao selecionar um backend compatível.

As mudanças não alteram shaders da Lossless.dll, não reativam o caminho legado de
qualidade e não prometem ganhos de FPS. O pacing e o binário LSFG-VK permanecem
como estavam; otimizações desse pipeline dependem de medições em dispositivos.

## Validação

Nesta sessão, o build ARM64 sem o SDK e os dez testes JVM passaram. O APK dos
testes instrumentados foi compilado, mas não executado: não há aparelho Android
conectado. A ponte JNI foi compilada/linkada, inclusive pelo caminho Prefab/NDK 27,
contra um AAR de referência **1.9.16441**, usado somente para verificar a integração
do build. Essa versão é rejeitada em runtime pelo código; não serve para RPC
Android. O AAR não foi incluído no Git nem no APK de teste entregue.

A compilação e o funcionamento com o AAR oficial **1.10+** ainda precisam ser
validados quando ele estiver disponível. Não foram medidos FPS, RAM ou consumo
térmico em GPUs físicas.

```sh
cd app
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
```

Os testes JVM cobrem normalização, exclusão de processos auxiliares, correspondência
de janelas, troca/reinício de jogo, continuidade da sessão e desconto de suspensão.

Os testes instrumentados de SHM verificam mapeamentos em `/proc/self/maps`, anexos
simultâneos, exclusão antes do detach e cem ciclos de alocação/liberação. Para
executá-los, conecte um Android ARM64 e rode:

```sh
./gradlew :app:connectedDebugAndroidTest
```

O build dos testes não significa execução no dispositivo. Para aprovar o RPC,
teste ainda: Discord ausente/desconectado, aceitação e visibilidade por outra
conta, reinício do Discord, início/fim de jogo dentro do desktop, gerenciador de
tarefas aberto, launcher que inicia outro executável, pausa/retorno/PiP e saída
do contêiner. Confirme a liberação de clientes/threads e recursos da Activity.
Para memória/gráficos, repita screenshots, efeitos, resize e início/fim de jogos
em A6xx, A7xx e G52, registrando driver e versões exatas.
