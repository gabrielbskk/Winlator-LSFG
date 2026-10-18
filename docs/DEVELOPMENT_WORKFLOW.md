# Fluxo de desenvolvimento — Winlator LSFG Aurora

> **Atualização 18/10/2026:** `main` contém código candidato **11.3 RC9** após PRs #33–#37, enquanto a APK pública continua RC8 experimental. Consulte [handoff do Codex](CODEX_HANDOFF_RC9.md) e [gates da Stable](STABLE_RELEASE_GATE.md); os parágrafos sobre 11.2/PR #7 abaixo são **históricos**.

## Branches e finalidade

| Branch / área | Finalidade | Política |
|---|---|---|
| `main` | Código-base, documentação, CI e identidade Aurora | Somente mudanças revisadas com PR |
| `experimental/rpc-android-v4` | Histórico Experimental 5: JNI/SDK, ícones EXE, capas SteamDB | **PR #7 já mesclado**; manter branch como referência, não reimportar |
| `ci/rpc-v5-mediafire-native` | Fonte exata do APK com RPC compilado e validado no CI | Preservar como referência do artefato publicado |
| Branches de `feat/`, `fix/`, `ci/`, `docs/`, `chore/` | Alterações pequenas, uma finalidade por PR | Squash na branch de destino quando validado |
| Branches históricas | Código de PRs fechados e/ou mesclados | Não apagar ou forçar push durante a organização |

**PRs #1, #4 e #5**: encerrados **sem merge**, substituídos pelo **PR #7**, que inclui as mudanças e a integração RPC mais atual. Os comentários, branches e commits históricos permanecem disponíveis.

**PRs #10 e #11**: mesclados na branch experimental (não diretamente na `main`). O PR #7 foi posteriormente mesclado; este registro documenta apenas o estado anterior da linha 11.2 e não constitui tarefa pendente para o Codex.

## Convenções de commits e PRs

Use mensagens claras, por exemplo:

- `feat(rpc): add per-game Steam artwork preview`
- `fix(android): handle missing game icon`
- `test(rpc): cover invalid Steam App IDs`
- `ci(android): verify native ARM64 APK contents`
- `docs(release): explain device validation requirements`
- `chore(repo): organize superseded PRs`

Um PR deve conter: motivação, comportamento alterado, arquivos principais, testes reproduzíveis, limitações, imagens da UI se necessário e dependências. Prefira abrir **draft** para protótipos. Evite criar vários PRs com os mesmos commits ou fazer merges de branches antigas que já foram substituídas.

Para manter o histórico auditável, **não fazer force-push, rebase ou squash retroativo da `main`**. Não renomear autores de commits anteriores, inclusive os de Rafaela e brunodev85. Mudanças novas entram em commits novos com PR; correções pontuais podem ser squash-merged.

## SDK privado e grandes binários

O Discord Social SDK é fornecido separadamente e tem condições próprias de licença. **Não versionar AAR, arquivos ZIP fragmentados do SDK, `Lossless.dll`, APKs gerados ou segredos.** Para builds, baixar o SDK autorizado no runner temporário e conferir um SHA-256 conhecido.

Em 2026-10-17, as seis partes `discord_partner_sdk.zip.001` a `.006` foram publicadas na `main` pelo commit `6aa6b4a42`. A manutenção remove essas partes **apenas da árvore atual da `main`**; elas **continuam recuperáveis no histórico Git e no commit original**. Isso reduz confusão para novos clones e PRs, mas **não é uma remoção permanente do histórico**. Uma eliminação completa exige um procedimento separado de reescrita e coordenação com colaboradores, refs, tags e forks; isso **não foi autorizado nem executado**.

## Processo de release

- A antiga [candidata 11.2](https://github.com/gabrielbskk/Winlator-LSFG/releases/tag/rpc-android-experimental-5-full-rpc) é **draft histórico**, não o canal principal público. A **RC8 experimental** é a APK pública existente; a RC9 é apenas código em `main`.
- **Ainda falta validação física** da versão exata: inicialização, login Discord, presença, contador, capas, troca de jogos, ausência de rede e desempenho.
- Não denominar uma versão *estável comprovada* sem esses testes; compilações debug são candidatas de teste.
- Não sobrescrever uma tag já publicada; novas builds devem receber versão e tag próprias, SHA-256 e release notes atualizadas.
- As seis releases antigas foram arquivadas como drafts, sem apagar assets ou tags. Detalhes em [histórico de releases](RELEASES.md).

## Permissões e revisões

O repositório possui templates em `.github/ISSUE_TEMPLATE/`, mas **Issues está desabilitado**. O proprietário pode ativar **Settings → General → Features → Issues**, se desejar. Até lá, use PRs e comentários para acompanhar decisões. O assistente não aparece como pessoa colaboradora no GitHub automaticamente: a conta e suas permissões precisam ser gerenciadas pelo proprietário.
