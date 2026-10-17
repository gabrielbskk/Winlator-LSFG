# Winlator-LSFG RPC — experimental 1

APK ARM64 `11.2-rpc-experimental.1`, com Discord Social SDK 1.10.19337.

## Download

Baixe as duas partes abaixo para a mesma pasta:

- [Parte 1 — ZIP.001](https://raw.githubusercontent.com/gabrielbskk/Winlator-LSFG/downloads/rpc-experimental-1/Winlator-LSFG-RPC-experimental-1.zip.001)
- [Parte 2 — ZIP.002](https://raw.githubusercontent.com/gabrielbskk/Winlator-LSFG/downloads/rpc-experimental-1/Winlator-LSFG-RPC-experimental-1.zip.002)

Com as duas partes na mesma pasta, abra a `.zip.001` em um gerenciador que suporte ZIP dividido, como ZArchiver, e extraia o APK. Se o gerenciador pedir, use a opção de juntar partes antes de extrair. Não renomeie os arquivos.

No Linux/Termux, também é possível juntar assim:

```sh
cat Winlator-LSFG-RPC-experimental-1.zip.001 Winlator-LSFG-RPC-experimental-1.zip.002 > Winlator-LSFG-RPC-experimental-1.zip
unzip Winlator-LSFG-RPC-experimental-1.zip
sha256sum -c SHA256SUMS.txt
```

O APK excede o limite de 100 MiB por arquivo Git. O upload de anexos de release não está autorizado nesta sessão, e o GitHub não permite enviar novos objetos LFS neste fork; por isso esta distribuição usa duas partes Git comuns.

## Antes de testar

Faça backup dos contêineres/dados. O APK é de debug e sua assinatura pode diferir da instalação atual. Não desinstale uma instalação com dados importantes sem backup.

Em Configurações → Discord Rich Presence, informe seu Application ID público, habilite, salve e reabra o contêiner. Mantenha o Discord Android atualizado e conectado. Não informe tokens ou segredos.

Build, assinatura, integridade e dez testes JVM verificados. RPC e presença visível ainda não foram testados em aparelho; não há medições de desempenho/RAM nem garantia de compatibilidade por GPU. Fonte: branch `experimental/discord-rpc-android` e PR #1.
