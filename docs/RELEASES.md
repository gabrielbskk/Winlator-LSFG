# Aurora — versões e downloads

## Pré-release atual: 11.3 RC8

**[Baixar o APK ARM64](https://github.com/gabrielbskk/Winlator-LSFG/releases/tag/aurora-11.3-rc8-discord-firefox)** · Experimental · Assinatura debug

A RC8 introduziu botões experimentais para instalar Endcord GUI, LegCord, Vesktop Windows e baixar Firefox ESR. **Aviso posterior (18/10): o mantenedor relatou que só o Vesktop funcionou, e já estava disponível; os demais botões não produziram clientes/navegador utilizáveis. Evite esses instaladores na RC8.** O PR #30 acompanha a correção. O RPC Android antigo com Application ID não está ativo nesta build SDK-free.

- Código publicado: `389ee86654133a152d7a759f4354331acaa7fba8`.
- APK: `app-debug.apk`, 172.017.335 bytes.
- SHA-256: `36e41e7e8886db48a1db7e5baa11aaefb978c64b66d62b8e1a6d9b534c953099`.
- [CI da RC8](https://github.com/gabrielbskk/Winlator-LSFG/actions/runs/39145337062): testes JVM, APK debug, compilação AndroidTest e verificações de recursos/ drivers aprovados.
- RPC ao vivo, Firefox, outros clientes e comparações de RAM ainda precisam de teste físico. Faça backup antes de instalar; assinaturas diferentes podem impedir atualização direta.

## Versões anteriores

As releases anteriores estão em rascunho, preservando tags, histórico e assets. Os downloads em rascunho só aparecem para administradores autorizados. A RC8 é a pré-release pública atual.

| Família | Referência histórica |
| --- | --- |
| 11.3 Experimental 7 / RC1 | `aurora-11.3-experimental-7-rc1` |
| 11.2 Experimental 6 | `aurora-experimental-6` |
| 11.2 RPC + capas | `rpc-android-experimental-5-full-rpc` |
| Experimental 1–5 | Builds anteriores do RPC Android e versões SDK-free |

O rótulo histórico “candidata a estável” não representa comprovação de estabilidade. O canal Stable depende dos [critérios de validação](STABLE_RELEASE_GATE.md).

<details>
<summary>Registro anterior da linha 11.2 (histórico)</summary>

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

</details>
