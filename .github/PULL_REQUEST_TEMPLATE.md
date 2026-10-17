## Resumo / Summary
Explique o que mudou e por quê. Se este PR substitui outro, indique o número. Mantenha uma única alteração lógica por PR.

## PRs e dependências relacionados
- PR relacionado / issue (se Issues estiver habilitado):
- Branch base: `main` ou `experimental/rpc-android-v4`
- Outros PRs necessários / substituídos:

## Escopo / Scope
- [ ] Documentação e manutenção do repositório
- [ ] Frame generation / LSFG
- [ ] GPU, driver ou RAM
- [ ] Android / ARM64 ou arquivos Windows 32/64 bits
- [ ] Discord RPC e capas de jogos
- [ ] Outro (explicar)

## Verificação / Verification
- [ ] Build concluído; ambiente, SDK/NDK e variante informados
- [ ] Testes automatizados / JVM executados (link do Actions)
- [ ] Teste em aparelho (modelo, Android, SoC, GPU/driver) documentado
- [ ] Ainda **não** testado em aparelho (descreva pendências)
- [ ] Regressões, Windows 32/64 bits e troca de jogos avaliados se aplicável
- [ ] JNI/SDK do Discord e permissões verificadas se alterar RPC
- [ ] Configurações e screenshots de UI conferidas quando necessário

## Desempenho / Performance, se aplicável
Informe FPS, tempo de frame, pico de RAM, temperaturas, driver, jogo,
resolução, comparação anterior/depois e repetição dos testes.
Se não mediu, escreva **não medido** (não alegar melhoria).

## Riscos, compatibilidade e reversão
Descreva limites conhecidos, privacidade, erros possíveis e como reverter.

## Checklist
- [ ] Nenhum AAR, ZIP fragmentado do SDK, APK binário, Lossless.dll, token ou arquivo proprietário adicionado
- [ ] Documentação / changelog / roadmap atualizados quando cabível
- [ ] Créditos à Rafaela, brunodev85 e terceiros mantidos
- [ ] PR em modo **draft** se ainda não estiver pronto para merge
