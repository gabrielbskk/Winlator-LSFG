# Aurora 11.3 RC3 — candidato técnico (não Stable)

O código candidato usa `versionCode=41` e `versionName=11.3-aurora-rc.3`. **Ainda não foi publicada uma release 11.3; o build de desenvolvimento usa assinatura debug.**

## Alterações da RC3

- Build SDK-free não inicia nem exibe controles do Discord RPC; o SDK e a biblioteca nativa continuam ausentes.
- Preset Electron/Vesktop alinhado à base Intermediária que abriu corretamente no Galaxy S23, sem forçar `BOX64_LIBCEF`/`BOX64_MALLOC_HACK` que causaram tela preta nesse teste.
- Atalho local `Vesktop (Downloads)` é criado quando `vesktop.exe` já está extraído em uma pasta Vesktop dentro de Downloads; o binário não é incluído no APK.
- Em cada contêiner, um botão abre o Vesktop para o primeiro login/configuração e uma opção separada cria o início minimizado nas execuções seguintes. O usuário ainda precisa ativar Rich Presence via arRPC no próprio Vesktop; a opção fica desligada por padrão por causa do consumo adicional de RAM/CPU.
- Fontes CJK, emoji e símbolos disponíveis em `/system/fonts` são importadas para o prefixo Wine somente quando faltarem, reduzindo glifos quadrados sem redistribuir fontes.
- Validação física observada até aqui: interface abriu, desempenho melhorou com GPU ativa e ainda restaram alguns glifos ausentes. Testes de áudio, chamadas, notificações, RAM, reinicialização e outros aparelhos continuam pendentes.

## Correções incluídas

- Política de drivers inteiramente manual: novos contêineres iniciam Vortek/Gladio; Turnip e versão escolhidos no contêiner; valores legados `auto` usam fallback fixo sem alterar o salvo.
- Adreno A6xx/A7xx e Mali G52: melhorias diagnósticas, fallback e configurações; PANVK não está integrado/validado.
- Discord RPC Android, prévia/capas Steam, presets LSFG-VK, Wine/Box64 e recursos Windows x86/x64 preservados para verificação.
- `KeyValueSet` tolera campos importados inválidos sem perder a configuração intacta.
- `AtomicDownload` preserva o driver instalado quando a rede falha, o download é cancelado ou a transferência termina incompleta; substituição atômica e pool limitado de workers.
- Builds CI agora compilam os testes de instrumentação, além dos testes JVM. **Compilação de teste não equivale a execução no aparelho**.

## Gates obrigatórios antes da Stable

- [ ] Build SDK-free e AndroidTest, build nativo RPC JNI com SDK autorizado, testes JVM e inventário de drivers **no mesmo commit**.
- [ ] Verificação completa do APK ARM64, sem `Lossless.dll`, com recursos Windows `i386-windows`, `x86_64-windows`, `syswow64`, checksum e assinatura.
- [ ] Instalação limpa e atualização sem perder saves/contêineres, com rollback testado. APKs debug de CI podem usar assinatura diferente e não aceitar atualização.
- [ ] Hardware real: Adreno 619, 642/642L, representantes A7xx e Mali G52, conforme compatibilidade que a release alegar.
- [ ] Jogos Windows x86 e x64, DXVK/fallback, contêineres, drivers/versões manuais e restauro de configurações.
- [ ] LSFG ligado/desligado e 2x/3x/4x em caminhos suportados; FPS, p95/p99 frametime, travamentos e pico de RAM em três execuções comparáveis.
- [ ] Discord RPC ao vivo: opt-in, título, timer, imagem, troca/encerramento, rede offline/online.
- [ ] Assinatura de distribuição consistente, licenças de SDK e componentes verificadas, notas, SHA-256 e tag nova sem alterar releases existentes.

Para cada teste físico, registre dispositivo/SoC/GPU, Android/kernel, driver/versão, Wine/Box64/DXVK, jogo, configurações, passos, logs sem tokens e resultados. Registre falhas e regressões, não apenas sucessos.

A Stable só pode ser declarada depois do gate de qualidade em [STABLE_RELEASE_GATE.md](STABLE_RELEASE_GATE.md) e aprovação do responsável. Não alterar a Experimental 6 ou a release principal anterior.

## Hardening da RC2 — 17/10/2026

- PR #23: valida índice e caminho de download de Turnip, DXVK e demais componentes; rejeita caminhos relativos maliciosos e duplicatas.
- PR #24: ignora parâmetros importados inválidos de EnvVars e limita memória/cache/tipo de recurso Vortek antes de converter para JNI.
- PR #25: valida caminhos e links de ZIPs de drivers personalizados, valida metadados e preserva o driver anterior durante substituição com pasta temporária/rollback.
- Testes JVM e de instrumentação Android adicionais. O teste instrumental de extração de ZIP só é **compilado** no CI e não foi executado em dispositivo Android real.
- O workflow nativo deve gerar artifact `aurora-11.3-rc2-arm64-native-review` com APK debug e `SHA256SUMS.txt`, **somente se testes e validação do APK passarem no mesmo commit**.

**Ainda não é Stable:** dependências de assinatura release apropriada, instalação e atualização com backup real, jogos Windows 32/64, RPC em dispositivo, LSFG/frametime e GPUs Adreno 619/642 e Mali G52. PANVK permanece não integrado; Turnip sempre manual.

### CI/SDK externo

Em 17/10/2026 o MediaFire redirecionou o SDK para um host fora da regra antiga, bloqueando o build **antes da compilação**. O script atualizado permite apenas subdomínios HTTPS MediaFire, com rejeição de HTML, novas resoluções após falha e validação obrigatória do SHA-256 original. Nenhuma correção no código Android foi comprovada por essa falha externa. Sem SDK autorizado e verificável, **não declarar build nativo aprovado** e não publicar Stable.
