# Discord Rich Presence no Android

**Estado:** experimental. O RPC depende de um build Android específico que inclua o Discord Social SDK oficial. O APK compilado sem esse SDK não fornece RPC.

## SDK oficial

1. Abra o [Discord Developer Portal](https://discord.com/developers/applications), selecione uma aplicação própria e habilite o **Discord Social SDK**, de acordo com os requisitos atuais do portal.
2. Na área **Discord Social SDK → Downloads**, obtenha o pacote oficial para **C++/Android**. O tamanho e a disposição dos arquivos podem mudar entre versões; não dependa de um tamanho fixo de ZIP.
3. Localize o arquivo `discord_partner_sdk.aar` no pacote oficial. Ele precisa incluir `prefab/modules/discord_partner_sdk/include/discordpp.h`, a biblioteca nativa ARM64 e as classes Java do SDK. É possível que a estrutura de diretórios do pacote mude.
4. Guarde o AAR apenas na máquina de compilação, por exemplo em `app/app/libs/discord_partner_sdk.aar`, ou informe um caminho absoluto na propriedade Gradle `discordSdkAar`. **Não adicione o AAR ao Git e não compartilhe SDKs ou tokens em Issues/PRs.**
5. Confirme que sua versão é compatível com o mínimo exigido no código experimental (**1.10+**). A simples presença dos arquivos no AAR **não prova** a versão do SDK.

## Compilar a versão experimental 4

No branch `experimental/rpc-android-v4`, com Android SDK 35, NDK 27.2.12479018, CMake 3.22.1, JDK 17, Python 3 e Zstd instalados:

```sh
bash scripts/build_rpc_v4.sh /caminho/absoluto/discord_partner_sdk.aar
```

Esse script tenta executar testes, compilar o APK de debug e validar componentes internos. O APK completo será gerado em `dist/rpc-experimental-4/` **somente se a compilação e as verificações passarem**. Sem AAR, a integração RPC é intencionalmente desativada nos builds comuns.

## Ativar RPC no aplicativo experimental

1. No Discord para Android, entre na sua conta e habilite o compartilhamento de atividade nas configurações disponíveis.
2. No Winlator experimental, abra **Configurações → Discord Rich Presence**.
3. Preencha o **Application ID público** correspondente à integração e marque a opção de atividade.
4. Reabra o contêiner, inicie um jogo e confira o estado do RPC. Verifique a presença também a partir de outra conta, porque a aceitação no SDK não garante exibição pública.

**Privacidade:** o recurso é opcional. Nunca use token de usuário, Client Secret ou senha do Discord no aplicativo, nos logs ou em um Pull Request. Recursos de voz ou de autenticação não são necessários para o RPC limitado descrito aqui; valide as permissões do manifesto final no build.

**Licenciamento:** o Discord permite determinadas formas de integração/distribuição *nos termos aplicáveis*; isso não torna o AAR software livre. Revise os [termos oficiais do Social SDK](https://support-dev.discord.com/hc/en-us/articles/30225844245271-Discord-Social-SDK-Terms) e a [configuração Android oficial](https://github.com/discord/discord-api-docs/blob/main/developers/discord-social-sdk/development-guides/account-linking-on-mobile.mdx) antes de distribuir um APK.

## Problemas conhecidos

- Se aparecer **SDK ausente**, o APK foi compilado sem o AAR/ponte RPC.
- Se aparecer **ID inválido**, confira o Application ID público e sua configuração.
- Se o Discord não exibir a atividade, confira se está conectado, a configuração de atividade e os logs de execução, sem publicar dados privados.
- RPC, consumo de RAM e desempenho precisam ser testados em dispositivos reais. Os testes JVM apenas verificam parte da lógica local.

Este é um fork não oficial. Créditos à [Rafaela (@amyroseee)](https://github.com/amyroseee/Winlator-LSFG), ao Winlator original e aos autores dos componentes.
