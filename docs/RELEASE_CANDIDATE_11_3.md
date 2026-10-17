# Aurora 11.3 RC1 — candidato técnico (não Stable)

O código de `feat/aurora-consolidation` / PR #19 usa `versionCode=39` e
`versionName=11.3-aurora-rc.1`. A pré-release de testes
[Aurora 11.3 Experimental 7 (RC1)](https://github.com/gabrielbskk/Winlator-LSFG/releases/tag/aurora-11.3-experimental-7-rc1)
foi publicada a partir do commit `7e4f5a5`. Ela usa assinatura debug e não é
uma Stable.

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
