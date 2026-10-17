# Winlator LSFG Aurora — RPC Experimental 5

**Estado: código-fonte em teste; ainda NÃO é um APK com Rich Presence validado.**

A Experimental 5 continua usando Android ARM64 e empacota os componentes Windows de 32 e 64 bits. O projeto conserva os créditos de Rafaela (@amyroseee), Winlator, LSFG-VK e demais dependências.

## Novidades

- Integração de `discordpp::ActivityAssets` ao RPC para apresentar uma imagem grande e o nome do jogo no Rich Presence.
- Pré-visualização do **ícone extraído do arquivo Windows .exe** ou PNG já armazenado no atalho.
- Botão **Compartilhar PNG do ícone do jogo**, que exporta o ícone somente quando o usuário solicita; o EXE completo não é compartilhado.
- Campo de imagem personalizado por atalho: **chave de asset do Discord Developer Portal** ou **URL de imagem HTTPS pública**.
- O jogo mostrado recebe apenas a imagem do executável esperado: um launcher que inicia outro programa não herda indevidamente a arte do primeiro jogo.
- Se a imagem for inválida ou estiver vazia, não envia uma imagem personalizada, permitindo a imagem padrão da aplicação Discord.
- Filtros para bloquear arquivos locais, URLs não HTTPS e endereços de rede privada na configuração.
- Conjunto de testes JVM para resolução de assets e URLs.
- `versionName=11.2-rpc-experimental.5` e `versionCode=37`.

## Limitação importante da API

O Discord Social SDK **não aceita um Bitmap Android, um `.exe`, caminho local `file://` ou `content://` como imagem do Rich Presence**. Ele exige um asset já cadastrado na aplicação ou uma URL pública. Por isso, a detecção/extração do ícone no Winlator é local e automática, mas a exibição de **essa mesma imagem no Discord requer que o usuário a cadastre ou hospede voluntariamente**. Não há upload automático de arquivos dos jogos.

Para seguir o processo: [Guia da imagem do jogo](RPC_GAME_ARTWORK.md).

## Compilação com Discord RPC

Dependências: Android SDK 35, NDK 27.2.12479018, CMake 3.22.1, JDK 17, Python 3, Zstd, e um `discord_partner_sdk.aar` obtido legalmente e de forma independente no Discord Developer Portal. O AAR **não deve ser publicado no repositório**.

```sh
bash scripts/build_rpc_v5.sh /caminho/para/discord_partner_sdk.aar
```

O script compila o APK de debug, executa os testes JVM, verifica a biblioteca JNI do RPC e os arquivos Windows 32-bit e gera um arquivo SHA-256, se todas as etapas tiverem sucesso. O status no CI público (sem AAR) verifica apenas código-fonte, recursos Android e testes Java, **não** o SDK nativo.

## Antes de publicar um APK com RPC

- [ ] Compilar com o AAR licenciado oficial, conferir versão SDK 1.10+ e assinaturas.
- [ ] Confirmar que `libwinlator_discord.so` e biblioteca `libdiscord_partner_sdk.so` estão presentes no pacote.
- [ ] Verificar no Discord Android a publicação do jogo, o tempo de sessão e a imagem cadastrada.
- [ ] Verificar alternância de jogos, PIP, rede instável, reinício do Discord e encerramento do jogo.
- [ ] Testar imagem por chave, URL HTTPS, URL inválida e fallback sem imagem.
- [ ] Testar jogos Windows 32-bit e 64-bit em dispositivos reais e manter backups.
- [ ] Publicar a release apenas com o APK testado, SHA-256 e notas honestas de limitações.

**A Experimental 4 SDK-free preview já publicada não oferece RPC funcional** e não inclui esta alteração; instalar a APK anterior não instala automaticamente a Experimental 5.
