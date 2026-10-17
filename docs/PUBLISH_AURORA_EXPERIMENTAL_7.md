# Publicação controlada — Aurora 11.3 Experimental 7 (RC1)

**Não é Stable.** Este workflow só pode ser iniciado manualmente; ele publica uma pré-release, não altera a release principal.

## Fonte fixa e validação

- Código: `7e4f5a5b4c63a2ac2849025cafe89be713e28eb8` (PR #19).
- [CI nativo com Discord SDK e APK ARM64](https://github.com/gabrielbskk/Winlator-LSFG/actions/runs/39047623193).
- [Build Android SDK-free](https://github.com/gabrielbskk/Winlator-LSFG/actions/runs/39047627335).
- [Testes JVM e Steam/RPC](https://github.com/gabrielbskk/Winlator-LSFG/actions/runs/39047627315).
- Artefato: `aurora-11.3-rc1-arm64-native-review` (APK debug + `SHA256SUMS.txt`).
- Nova tag, se autorizada: `aurora-11.3-experimental-7-rc1`.

## Publicar pelo GitHub

Após integrar este workflow à branch `main`, abra **Actions → Publish Aurora 11.3 Experimental 7 (manual) → Run workflow**.
Selecione `main`, marque `confirm_publication` e execute. O workflow confirma o mesmo commit e três CI verdes, verifica SHA-256, integridade e bibliotecas nativas do APK, recusa tag preexistente e cria somente uma **pré-release**, nunca Latest.

Artefatos do GitHub Actions expiram, portanto o workflow deve ser executado enquanto o arquivo ainda está disponível. Caso expire, será preciso gerar novo artefato verificável do mesmo commit antes de publicar.

## Ainda bloqueado para Stable

Testes em dispositivos Adreno A6xx/A7xx e Mali G52, Windows x86/x64, LSFG-VK, RPC ao vivo, assinatura de distribuição e migração/rollback de contêineres.
Turnip e versão continuam manuais; PANVK não está integrado. `Lossless.dll` não é redistribuído. Faça backup dos saves antes de testes em APK debug.
