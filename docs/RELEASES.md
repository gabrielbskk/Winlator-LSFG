# Winlator LSFG Aurora — canal principal e arquivo

## Canal principal

**Winlator LSFG Aurora 11.2 — RPC + Capas (candidata a estável)**

- [Release principal e download do APK ARM64](https://github.com/gabrielbskk/Winlator-LSFG/releases/tag/rpc-android-experimental-5-full-rpc)
- APK: `Winlator-LSFG-Aurora-11.2-rpc-experimental.5-arm64-debug.apk` (176.187.610 bytes).
- Inclui bibliotecas nativas do Discord Social SDK e RPC JNI, entrada manual de Steam App ID/SteamDB, capas e prévia dos atalhos.
- Build Android ARM64, testes JVM, empacotamento e bibliotecas Windows 32-bit validados em [Actions run 39031134753](https://github.com/gabrielbskk/Winlator-LSFG/actions/runs/39031134753).
- **Aviso:** ainda não há confirmação em dispositivo físico para o RPC e as capas desta compilação. É uma **candidata a estável**, não garantia de estabilidade.
- Assinatura **debug**; faça backup antes de instalar. Instalações com outra assinatura podem não aceitar atualização direta.

## Histórico arquivado (sem exclusões)

As seguintes releases foram mantidas como **rascunho (draft)** para organizar a página pública. Administradores do repositório podem consultá-las e recuperar seus APKs e checksums; elas **não são visíveis ao público nem possuem download público enquanto forem rascunhos**. As tags e o histórico do Git continuam disponíveis.

| Tag | Conteúdo |
|---|---|
| `rpc-android-experimental-5-full-native` | APK nativo Experimental 5 (duplicata do candidato atual) |
| `rpc-android-experimental-5-sdkfree-preview` | Experimental 5 sem RPC nativo |
| `rpc-android-experimental-4-sdkfree-preview` | Experimental 4 sem RPC nativo |
| `rpc-android-experimental-3` | Implementação anterior de RPC básico |
| `rpc-android-experimental-2` | RPC experimental 2 |
| `rpc-android-experimental-1` | RPC experimental 1 |

Para recuperar uma release antiga, o mantenedor pode abrir [Releases](https://github.com/gabrielbskk/Winlator-LSFG/releases) enquanto estiver conectado com permissão de escrita e selecionar o rascunho, ou republicá-lo. As tags de código-fonte permanecem em [Tags](https://github.com/gabrielbskk/Winlator-LSFG/tags). **Não excluir releases nem tags** durante a organização.

## Nome da versão e evolução

O nome exibido pelo GitHub para a release principal pode mudar sem alterar o APK. O pacote Android dentro do arquivo continua com a versão `11.2-rpc-experimental.5` e a assinatura de debug original. Uma versão realmente estável exige validar RPC, imagens, compatibilidade 32/64-bit e assinatura de distribuição antes de lançar um APK novo.

Créditos: Winlator original de brunodev85, fork Winlator LSFG da Rafaela (@amyroseee) e seus respectivos projetos e dependências.
