# Critérios obrigatórios para Winlator LSFG Aurora Stable

**Estado em 2026-10-17: NÃO APROVADO PARA STABLE.** Este documento é um gate de qualidade,
não autorização para publicar nem prova de compatibilidade de GPUs. A Experimental 6
e o PR de consolidação são candidatos de desenvolvimento.

## Sequência de estabilização

1. Corrigir regressões reproduzíveis e preservar funcionalidades existentes, incluindo importações legadas malformadas em `KeyValueSet`.
2. Passar **JVM, inventário auditável Turnip, testes estáticos de recursos, build ARM64 SDK-free e build com o
   Discord SDK autorizado** para o MESMO commit/tag da candidata.
3. Inspecionar APK/JNI e conteúdo Windows `i386-windows`, `x86_64-windows`,
   `syswow64`, bibliotecas Vulkan, shaders e arquitetura `arm64-v8a`.
4. **Testes reais em Android**: ao menos Adreno 619 e 642/642L e Mali G52
   quando a release alegar compatibilidade específica com essas GPUs.
5. Testar instalação limpa, atualização da versão anterior, salvamento de
   contêineres, rollback e assinatura estável consistente (não chave debug).
6. Registrar resultados e liberar a versão Stable apenas quando os bloqueios
   críticos forem resolvidos e o responsável aprovar a publicação.

## Matriz de aceite em dispositivo (registrar cada execução)

| Teste | Adreno 619 | Adreno 642/642L | Mali G52 |
| --- | --- | --- | --- |
| Criar/abrir/fechar contêiner ARM64 e reiniciar app | Pendente | Pendente | Pendente |
| Definir driver manual e persistir ao reiniciar | Pendente | Pendente | Pendente |
| Abrir jogo Windows x86 e x64 de forma reproduzível | Pendente | Pendente | Pendente |
| DXVK e fallback WineD3D/VirGL, conforme suportado | Pendente | Pendente | Pendente |
| Trocar versões Turnip manualmente sem perda de config | Pendente | Pendente | N/A (Turnip é Qualcomm) |
| Vortek do sistema e diagnósticos Vulkan | Pendente | Pendente | Pendente |
| LSFG ligado/desligado, 2x/3x/4x em caminho suportado | Pendente | Pendente | Não afirmar suporte Vortek |
| RPC título, capa, timer, troca/fechamento e rede | Pendente | Pendente | Pendente |
| 3 execuções: crash, FPS/frametime e pico de RAM | Pendente | Pendente | Pendente |

PANVK é **experimental e NÃO integrado/validado** nesta versão; não apresentá-lo
como recurso Stable. Os testes acima são um alvo; quando um aparelho estiver
indisponível, a compatibilidade correspondente permanece não verificada.

## Comportamento manual que não pode regredir

- Novo contêiner: **Vortek/Gladio**, sem autodetecção para escolher Turnip.
- Quem deseja Turnip deve selecionar **Turnip** e depois a **versão** manualmente.
- Um contêiner existente com `turnip` + versão salva mantém a escolha anterior.
- Contêiner antigo com `version=auto` ou sem versão usa a versão Turnip
  incorporada como fallback fixo; não deve acionar uma nova seleção por GPU.
- Mali não deve carregar Turnip; a proteção de sessão existente deve avisar e
  usar sistema/Vortek sem apagar a seleção salva.
- Não remover versões instaladas/empacotadas sem comparação e plano de rollback.

## Build / distribuição

- APK Stable requer assinatura de distribuição apropriada e documentada, não
  reutilizar ou confundir a assinatura debug do candidato.
- Jamais versionar chaves, tokens, SDK Discord proprietário ou `Lossless.dll`.
- Registre SHA-256, changelog, referência do código, licença e instruções de backup.
- Preservar a release anterior e **não mover a tag existente**.
- Regressão crítica, falha de instalação, corrupção de save ou crash repetível:
  bloquear promoção até correção.

## Relatório mínimo por teste

Modelo/SoC/GPU, versão Android/kernel, driver+versão escolhida, jogo e API gráfica,
Wine/Box64, resolução, LSFG, taxa de FPS/percentis de frametime, RAM pico,
logcat sem tokens e passos exatos para reprodução. Para atualização, informar
origem da APK anterior, assinatura, backup e resultados do rollback.

**Acompanhamento:** [AURORA_STABLE_TRACKER.md](AURORA_STABLE_TRACKER.md) registra o que já foi integrado, os resultados de CI por commit e os bloqueios físicos pendentes.

**Repositório:** PR #19 (consolidação) e PR #7 (RPC) continuam sujeitos à revisão.
Compilações CI anteriores não substituem novos testes para o commit final.
