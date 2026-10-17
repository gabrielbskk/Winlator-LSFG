# Imagem do jogo no Discord Rich Presence

## Como funciona

O Winlator LSFG Aurora já lê ícones Windows de arquivos `.exe` e salva versões PNG para os atalhos. Esta melhoria reaproveita o ícone no campo de visualização das **configurações do atalho**. Se o atalho não tiver uma imagem PNG, o leitor `PEParser.extractIcon` procura um ícone dentro do EXE em uma thread separada da interface.

**Limitação do Discord:** a imagem no Rich Presence deve ser uma **chave de asset** cadastrada no Discord Developer Portal da aplicação configurada ou uma **URL HTTPS pública** de imagem. Uma imagem local Android, bitmap, `file://`, `content://` ou caminho para `.exe` não é aceita diretamente pelo Discord Social SDK. A extração local não faz upload de nenhum arquivo, não publica o executável e não permite afirmar que qualquer ícone de EXE aparecerá automaticamente no perfil.

## Configurar uma imagem por jogo

1. No Winlator, abra a configuração do atalho Windows. Em **Imagem do jogo no Discord**, o ícone extraído do executável aparecerá na pré-visualização, quando disponível.
2. Selecione **Compartilhar PNG do ícone do jogo** para exportar o ícone através do seletor de compartilhamento do Android (o arquivo é preparado no armazenamento privado do aplicativo). Escolha onde guardar o PNG; o Winlator **não faz upload automático**.
3. Para usar **a mesma imagem** no seu Rich Presence, obtenha o PNG do atalho/ícone e **envie voluntariamente** essa imagem para a seção `Rich Presence → Art Assets` da sua aplicação no [Discord Developer Portal](https://discord.com/developers/applications). Recomenda-se uma imagem quadrada, preferencialmente 1024 × 1024. Informe a chave do asset no campo correspondente do Winlator. Como alternativa, informe uma URL HTTPS pública da imagem.
4. Salve o atalho, reinicie o jogo e confirme o Rich Presence a partir de outra conta, usando um APK compilado com o Discord Social SDK oficial.

A imagem definida vale apenas para o executável esperado do atalho. Se o launcher abrir outro programa/jogo, não será atribuída automaticamente a imagem do primeiro EXE ao segundo.

Deixe o campo vazio para mostrar o ícone padrão da aplicação Discord. Não envie informações privadas para um serviço público de hospedagem sem querer. **O recurso é opcional; nenhuma imagem é enviada automaticamente**.

## Sobre a versão

As alterações estão inicialmente no PR experimental de imagens, empilhado sobre `experimental/rpc-android-v4`. O APK **Experimental 4 SDK-free preview** já publicado **não possui RPC funcional** e não muda automaticamente com alterações no código-fonte: será necessária uma compilação posterior com o SDK oficial para exibir as imagens no Discord.

Referências oficiais: [Discord Social SDK - Setting Rich Presence](https://discord.com/developers/docs/social-sdk/) e [Rich Presence Art Assets](https://discord.com/developers/applications).
