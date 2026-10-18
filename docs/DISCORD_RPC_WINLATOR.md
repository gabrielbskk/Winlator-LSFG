# Discord RPC no contêiner Windows

## Caminho recomendado para esta release

O caminho já integrado ao Aurora é executar o Vesktop Windows x64 dentro do contêiner Wine/Box64. A interface e a abertura foram observadas em dispositivo; o envio de RPC ao vivo no Winlator ainda precisa ser validado. O Aurora pode abrir o Vesktop manualmente e, se você marcar a opção do companion, iniciá-lo minimizado quando o contêiner iniciar. O companion fica desligado por padrão porque o cliente Electron aumenta o uso de RAM e CPU.

O executável não é distribuído no APK. Extraia uma versão confiável do Vesktop em uma pasta `Vesktop` dentro de Downloads; o Aurora cria `Vesktop (Downloads)` no menu Iniciar. Configure o preset Electron do Box64 se a abertura padrão falhar. Não há garantia de compatibilidade com todos os jogos ou aparelhos.

## Discord Web pelo navegador

A partir da RC4, se Chrome, Edge ou Firefox estiver instalado no prefixo Wine, o menu Iniciar ganha um atalho `Discord Web (...)` que abre `https://discord.com/app`. O navegador, o login e a extensão são responsabilidade do usuário; nenhum navegador é incluído no APK.

Para tentar Rich Presence no Discord Web:

1. Instale a extensão web do Vencord no Chrome/Chromium ou Firefox, usando as instruções oficiais do [Vencord](https://docs.vencord.dev/installing/).
2. Instale Node.js 18 ou superior no mesmo ambiente Windows e inicie o servidor arRPC conforme o [README oficial do arRPC](https://github.com/OpenAsar/arrpc#usage).
3. No Discord Web, habilite o plugin `WebRichPresence (arRPC)` do Vencord. Ele deve conseguir abrir `ws://127.0.0.1:1337` no mesmo ambiente de rede.
4. Teste com um jogo que publica RPC e confirme a atividade em outro cliente Discord.

Esta integração é experimental. O plugin WebRichPresence é identificado pelo Vencord como experimental, e o arRPC tem um relato aberto de vazamento de memória ao escanear processos no Windows. Portanto, não consideramos essa combinação uma alternativa leve validada no Winlator. O launcher do Aurora só abre o navegador; não instala Vencord/Node, não inicia arRPC e não garante que o servidor consiga detectar processos Wine.

## Endcord e Legcord

Há dois projetos não relacionados chamados Endcord. [rootpoii/Endcord](https://github.com/rootpoii/Endcord) é um mod injetado no Discord oficial para Windows; ele não substitui o cliente Electron. [sparklost/endcord](https://github.com/sparklost/endcord) é um cliente de terminal com RPC e detecção de jogos. Ele oferece binários Windows e recomenda a variante `endcord-gui` nessa plataforma; a release 1.5.4 informa que essa versão acrescenta 15 MB frente a terminais Linux comuns, medida que não representa o consumo no Winlator. Continua sendo um cliente completo de terceiros, e não um daemon RPC pequeno. O projeto alerta que clientes de terceiros podem violar os termos do Discord e resultar em banimento.

[Legcord](https://github.com/Legcord/Legcord) é um cliente Discord independente baseado em Electron com arRPC integrado. Ele pode simplificar a configuração, mas ainda executa um cliente completo, então não há evidência de que resolva o consumo de RAM do Vesktop. Na RC5, o Aurora cria o atalho `Endcord (Downloads)` se encontrar `endcord-gui.exe` (ou `endcord.exe`) em uma pasta Endcord já extraída em Downloads. Não baixa nem distribui o executável. Endcord, Legcord e Vesktop ainda não tiveram uso de RAM ou RPC ao vivo medidos comparativamente no Winlator. Considere o aviso de risco do Endcord antes de entrar na conta.

## Escolha

- **Quer usar o caminho já integrado:** Vesktop dentro do contêiner, com companion opcional. A abertura foi testada; o RPC ao vivo ainda está pendente. Espere uso extra de memória.
- **Quer testar o navegador:** Discord Web + Vencord + arRPC; use a RC4 apenas como atalho de abertura e considere o RPC experimental.
- **Quer testar um cliente com RPC integrado e mais leve segundo o projeto:** Endcord é a primeira opção para experimentar; extraia `endcord-gui.exe` em uma pasta Endcord dentro de Downloads e abra o atalho. A compatibilidade e a RAM no Winlator ainda estão pendentes, e há risco de conta por ser cliente de terceiros.
- **Discord oficial para Windows:** não oferece uma opção oficial apenas de RPC sem o cliente completo. O Social SDK Android é outro fluxo e não envia a presença do processo Windows do contêiner.

Nunca informe token, senha ou Client Secret em scripts, logs ou arquivos do projeto.
