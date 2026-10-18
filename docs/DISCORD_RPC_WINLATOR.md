# Discord RPC no contêiner Windows

## Instalação pelo Aurora

Em **Configurações → Clientes Discord Windows**, toque em **Instalar Endcord**, **Instalar LegCord** ou **Instalar Vesktop**. O Aurora baixa o ZIP Windows x64 da release oficial de cada projeto, confere tamanho e SHA-256, extrai na pasta privada do aplicativo e cria o atalho `<cliente> (Aurora)` no menu Iniciar quando o contêiner é aberto. Os clientes não vêm dentro do APK. Mantenha espaço livre para download e extração.

| Cliente | Versão fixada | ZIP | Tamanho do download | SHA-256 |
| --- | --- | --- | ---: | --- |
| [Endcord GUI](https://github.com/sparklost/endcord/releases/tag/1.5.4) | 1.5.4 | `endcord-gui-1.5.4-windows.zip` | 58 MB | `7c1ef34c18262ff87072de38afadc54d0c1daedf391d0c2b872cfce52db4d12f` |
| [LegCord](https://github.com/Legcord/Legcord/releases/tag/v1.3.1) | 1.3.1 | `Legcord-1.3.1-win-x64.zip` | 184 MB | `36937b53c9eb898041d790a48faf0d6058f8dbdee6b34dc844bf67c69cc14c2e` |
| [Vesktop](https://github.com/Vencord/Vesktop/releases/tag/v1.6.7) | 1.6.7 | `Vesktop-1.6.7-win.zip` | 144 MB | `ff733d40835a603239a43ec79d53131749dcebd8611b72d301f30cfb9b1fbfa3` |

Os atalhos antigos para arquivos extraídos manualmente em Downloads continuam disponíveis. O início minimizado opcional do Vesktop usa a versão instalada pelo Aurora quando disponível. A interface do Vesktop abriu em um Galaxy S23 num teste anterior; **RPC ao vivo, Endcord, LegCord e uso comparativo de RAM ainda não foram validados em aparelho**. Use preset Electron do Box64 se um cliente Electron não abrir. Endcord é a primeira opção para tentar reduzir RAM, sem promessa de resultado.

## Discord Web pelo navegador

O Aurora também oferece em Configurações o download verificado do instalador oficial Firefox ESR 153.0 para Windows x64. Depois do download, execute `Install Firefox ESR (Aurora)` no menu Iniciar do contêiner e siga o instalador. A abertura do navegador dentro do Wine ainda requer validação em aparelho.

O menu Iniciar oferece `Discord Web (Android Browser)`, que usa a integração existente do Winlator para abrir o link no navegador Android, além de atalhos separados para Chrome, Edge ou Firefox quando instalados no prefixo Wine. Isso é encaminhamento para o navegador Android, não um WebView embutido dentro da janela Windows. O navegador, o login e a extensão são responsabilidade do usuário; nenhum navegador é incluído no APK.

Para tentar Rich Presence no Discord Web:

1. Instale a extensão web do Vencord no Chrome/Chromium ou Firefox, usando as instruções oficiais do [Vencord](https://docs.vencord.dev/installing/).
2. Instale Node.js 18 ou superior no mesmo ambiente Windows e inicie o servidor arRPC conforme o [README oficial do arRPC](https://github.com/OpenAsar/arrpc#usage).
3. No Discord Web, habilite o plugin `WebRichPresence (arRPC)` do Vencord. Ele deve conseguir abrir `ws://127.0.0.1:1337` no mesmo ambiente de rede.
4. Teste com um jogo que publica RPC e confirme a atividade em outro cliente Discord.

Esta integração é experimental. O plugin WebRichPresence é identificado pelo Vencord como experimental, e o arRPC tem um relato aberto de vazamento de memória ao escanear processos no Windows. Portanto, não consideramos essa combinação uma alternativa leve validada no Winlator. Os atalhos do Aurora só abrem o navegador; não instala Vencord/Node, não inicia arRPC e não garante que o servidor consiga detectar processos Wine.

## Endcord e Legcord

Há dois projetos não relacionados chamados Endcord. [rootpoii/Endcord](https://github.com/rootpoii/Endcord) é um mod injetado no Discord oficial para Windows; ele não substitui o cliente Electron. [sparklost/endcord](https://github.com/sparklost/endcord) é um cliente de terminal com RPC e detecção de jogos. Ele oferece binários Windows e recomenda a variante `endcord-gui` nessa plataforma; a release 1.5.4 informa que essa versão acrescenta 15 MB frente a terminais Linux comuns, medida que não representa o consumo no Winlator. Continua sendo um cliente completo de terceiros, e não um daemon RPC pequeno. O projeto alerta que clientes de terceiros podem violar os termos do Discord e resultar em banimento.

[Legcord](https://github.com/Legcord/Legcord) é um cliente Discord independente baseado em Electron com arRPC integrado. Ele pode simplificar a configuração, mas ainda executa um cliente completo, então não há evidência de que resolva o consumo de RAM do Vesktop. O Aurora cria `Endcord (Downloads)` ao encontrar `endcord-gui.exe` (ou `endcord.exe`) em uma pasta Endcord extraída em Downloads. Também cria `LegCord (Downloads)` ao encontrar `Legcord.exe` em uma pasta Legcord extraída ali; use o pacote Windows x64 ZIP, não o instalador. Os executáveis não são distribuídos no APK. Endcord, Legcord e Vesktop ainda não tiveram uso de RAM ou RPC ao vivo medidos comparativamente no Winlator. Considere o aviso de risco do Endcord antes de entrar na conta.

O projeto separado [endcord-rpc](https://github.com/sparklost/endcord-rpc) oferece só Rich Presence e detecção de jogos, mas requer o token da conta no arquivo de configuração. Por isso o Aurora não instala nem integra esse processo. Os atalhos acima permitem testar clientes com login normal, sem pedir um token em scripts do Aurora.

## Escolha

- **Quer usar o caminho já integrado:** Vesktop dentro do contêiner, com início minimizado opcional. A abertura foi testada; o RPC ao vivo ainda está pendente. Espere uso extra de memória.
- **Quer testar o navegador:** Discord Web no navegador Android ou em Chrome/Edge/Firefox do contêiner + Vencord + arRPC; considere o RPC experimental.
- **Quer testar um cliente com RPC integrado e mais leve segundo o projeto:** Endcord é a primeira opção para experimentar; use o botão nas Configurações e abra o atalho do Aurora. A compatibilidade e a RAM no Winlator ainda estão pendentes, e há risco de conta por ser cliente de terceiros.
- **Quer testar outro cliente Windows com RPC integrado:** LegCord traz arRPC, mas continua baseado em Electron. Use o botão nas Configurações; a RAM e o RPC ainda precisam ser medidos no Winlator.
- **Discord oficial para Windows:** não oferece uma opção oficial apenas de RPC sem o cliente completo. A integração antiga pelo Social SDK Android não faz parte desta prévia pública.

Nunca informe token, senha ou Client Secret em scripts, logs ou arquivos do projeto.
