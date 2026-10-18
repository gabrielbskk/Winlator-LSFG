# Comece pelo Aurora

## Instalação

1. Faça backup dos contêineres e saves existentes.
2. Baixe o APK na [release RC8](https://github.com/gabrielbskk/Winlator-LSFG/releases/tag/aurora-11.3-rc8-discord-firefox). Requer Android 8.0+ ARM64; GPU, driver e aplicativo determinam a compatibilidade real.
3. Instale e crie um contêiner. A RC8 usa assinatura debug; uma APK com assinatura diferente pode não aceitar atualização direta.
4. Teste primeiro com LSFG desligado. Configure Wine/Box64 e gráficos conforme o aplicativo. Turnip e sua versão são escolhas manuais; novos contêineres usam Vortek/Gladio.

## Discord e RPC

**Aviso para quem baixou a RC8:** o mantenedor relatou que somente o Vesktop já instalado funcionou; os botões Endcord, LegCord e reinstalação Vesktop não tiveram resultado útil. **Não recomendamos esses botões na RC8.** Na RC9 de código (ainda sem APK pública), eles estão ocultos e o Vesktop existente é preservado. RPC ao vivo e consumo de RAM continuam sem validação.

O início minimizado do Vesktop é opcional por contêiner. Endcord é uma alternativa para testar menor consumo; o resultado no Winlator ainda não foi medido. A RC8 não ativa o RPC Android antigo com Application ID.

## Firefox e Discord Web

**O download/instalador Firefox da RC8 não teve funcionamento confirmado e não deve ser recomendado.** Na RC9 em desenvolvimento, a interface e o atalho gerado do instalador foram suspensos; arquivos que já existem não são apagados. Os atalhos de Discord Web para navegador Android e navegadores já instalados no Wine são rotas distintas, sem WebView embutido.

Detalhes: [Discord e navegadores](DISCORD_RPC_WINLATOR.md).

## Jogos e gráficos

Leia a [compatibilidade de GPU](GPU_COMPATIBILITY_AURORA.md). PANVK não está validado/integrado. Para LSFG-VK, forneça sua própria `Lossless.dll` compatível e compare os presets somente depois de confirmar que o jogo funciona sem geração de quadros. LSFG não funciona no caminho Vortek atual.

## Encontrou uma falha?

Registre versão, aparelho, Android, GPU, driver, Wine/Box64, aplicativo e passos para reproduzir. Consulte [como testar](TESTING.md) e [como contribuir](../CONTRIBUTING.md). Não publique tokens nem dados de login.
