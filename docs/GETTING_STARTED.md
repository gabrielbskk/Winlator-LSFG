# Início rápido — Winlator LSFG Aurora

O Winlator LSFG Aurora é um fork experimental do [Winlator LSFG da Rafaela](https://github.com/amyroseee/Winlator-LSFG), derivado do Winlator original. O objetivo é testar geração de quadros LSFG-VK e compatibilidade Windows no Android.

## Requisitos gerais

- Dispositivo **Android ARM64 (64 bits)**. A configuração Android declara `minSdkVersion 26` (Android 8.0), mas uma versão de Android compatível não garante suporte a todas as GPUs ou jogos.
- Espaço livre para criar contêineres e instalar componentes, além de drivers/gráficos compatíveis.
- Compatibilidade de GPUs **Adreno/Mali varia por modelo, kernel, driver e jogo**. Não assuma que drivers Turnip são adequados às GPUs Mali.
- Os jogos/aplicativos Windows podem ser de 32 ou 64 bits se os componentes correspondentes estiverem presentes e funcionando. Isso é independente do APK Android ser ARM64.

## Começando

1. Consulte a página [Releases](../../releases) e leia as notas e limitações da versão escolhida. Releases `experimental` são voltadas a testes; podem conter regressões.
2. Faça backup dos contêineres/saves antes de atualizar. O APK experimental pode ter assinatura diferente e não atualizar diretamente uma instalação anterior.
3. Instale uma versão apropriada em seu próprio dispositivo e crie um contêiner com as configurações compatíveis com o SoC/GPU.
4. Para LSFG-VK, obtenha um `Lossless.dll` legítimo e compatível de forma independente; importe-o nas configurações do aplicativo e escolha o preset de LSFG por contêiner. **O fork não inclui `Lossless.dll`.**
5. Teste primeiro o jogo com frame generation desligado, depois compare presets 2x/3x/4x com as mesmas condições.
6. Para Discord RPC no build experimental, leia [o guia específico](DISCORD_RPC.md).

## Como reportar

Anote versão/commit, Android, SoC/GPU, driver, jogo, Wine/Box64, resolução e, quando possível, FPS, frame times e RAM; consulte [TESTING.md](TESTING.md). No momento, **GitHub Issues precisa ser habilitado nas configurações do repositório**. Até lá, não inclua dados privados em PRs públicos.

## Capturas existentes

O [README principal](../README.md#screenshots) já inclui imagens demonstrativas da interface, contador FPS e configurações.

## Código e atribuição

Código-fonte e documentação: [repositório principal](../README.md). Consulte o [arquivo LICENSE](../LICENSE) e as licenças específicas de bibliotecas e componentes antes de redistribuir um build. A presença de LGPL-2.1 na raiz não substitui os avisos próprios de terceiros.
