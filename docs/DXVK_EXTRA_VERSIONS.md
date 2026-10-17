# DXVK extra para o Aurora — importação manual (experimental)

O catálogo já oferece pacotes instaláveis DXVK 0.96, 1.4.2, 1.7.2, 2.2, 2.3.1, 2.5.2 e 2.6.1. As versões embarcadas são 1.10.3 e 2.4.1. **Nenhuma versão existente será removida ou substituída por padrão.**

## Novos pacotes opcionais
- **DXVK 2.6.2**, compilação upstream Wine: SHA-256 do TAR.GZ original `17761876556afd55736cb895d184f5a1c55d43350f1b1e3b129f8d28706d7992`.
- **DXVK 2.7.1**, compilação upstream Wine: SHA-256 do TAR.GZ original `d85ce7c79f57ecd765aaa1b9e7007cb875e6fde9f6d331df799bce73d513ce87`.

O script `scripts/package_dxvk_review.py` baixa releases oficiais de `doitsujin/dxvk`, valida SHA-256 e arquitetura PE das DLLs x86/x64, e transforma o conteúdo em um arquivo `.tzst` com manifesto compreendido pelo importador Winlator. Licenças/atribuições upstream ficam incluídas quando disponíveis no archive oficial.

**O workflow é de revisão** e não publica essas versões no catálogo por rede nem as instala automaticamente. A saída com `dxvk-2.6.2.tzst`, `dxvk-2.7.1.tzst` e `SHA256SUMS.txt` aparece em Actions → *Aurora DXVK optional packages (review only)* → Artifacts.

Para experimentar em dispositivo Android, faça backup dos contêineres, abra as configurações do DXVK dentro do contêiner, use o menu de importação de arquivo, escolha a versão desejada e reinicie o contêiner quando necessário. Se a versão não carregar, volte manualmente para a versão anterior.

## Compatibilidade e bloqueios
- DXVK moderno (2.x) requer drivers Vulkan compatíveis com Vulkan 1.3 e extensões/recursos necessários. A existência de um arquivo não prova funcionamento em Mali G52, Adreno 619, Adreno 642 ou qualquer jogo.
- DXVK 3.x exige Vulkan 1.4 e mais recursos. Não será adicionado automaticamente à configuração padrão dessas GPUs; é uma linha futura de testes.
- Os testes CI verificam integridade do arquivo, formato PE, layout das DLLs e manifesto. **Não executam jogos nem testam a importação em um celular**.
- Não alterar política Turnip: escolha de Turnip/versão continua manual.
- Não publicar Stable 11.3 sem aprovar `docs/STABLE_RELEASE_GATE.md` e os testes reais.
