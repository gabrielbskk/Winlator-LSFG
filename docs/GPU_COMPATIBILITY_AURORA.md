# Aurora Theme e compatibilidade de GPU — teste experimental

## Tema Aurora

Esta branch substitui o antigo destaque verde/teal pelo esquema Aurora azul/índigo em:
- Temas claro e escuro (superfícies, títulos, seleções e controles)
- Botões de confirmação e neutros, com estados de toque e desabilitado
- Diálogos e menus, com bordas arredondadas e cores adaptativas
- Linhas de tabelas, painel de configurações e cabeçalhos que já usam atributos do tema

O visual continua alternável entre claro e escuro em Configurações. As cores não alteram drivers, jogos nem desempenho.

## Compatibilidade, sem prometer ganhos de FPS

| GPU | Identificação por software | Padrão para contêineres novos | Seleção manual |
| --- | --- | --- | --- |
| Adreno 619 | `Adreno (TM) 619` | Vortek + Gladio | Turnip com versão escolhida pelo usuário; validar no aparelho |
| Adreno 642/642L | `Adreno 642L` | Vortek + Gladio | Turnip com versão escolhida pelo usuário; validar no aparelho |
| Mali G52 | `Mali-G52 MP2` | Vortek + Gladio | Driver Vulkan do sistema / VirGL; PANVK dedicado ainda não validado |

**Seleção manual (pós Experimental 6):** Aurora não escolhe Turnip automaticamente
nem calcula versões pela GPU. Quem precisa de Turnip seleciona-o explicitamente
nas opções Vulkan, abre Configurações e escolhe a versão. Contêineres antigos
com `version=auto` ou sem versão usam apenas um fallback fixo `DefaultVersion.TURNIP`
(26.1.0 atualmente) para continuar abrindo; não há classificação dinâmica.
Contêineres existentes com seleção manual continuam com seu driver salvo.
O app impede Turnip em uma sessão identificada como Mali e avisa sobre fallback,
sem apagar a configuração persistida. O Vortek pode não oferecer todos os recursos
necessários ao LSFG; teste a combinação selecionada antes de considerá-la estável.


Alterações concretas:

1. A consulta de GPU EGL agora cria uma superfície PBuffer GLES2 para compatibilidade com implementações que recusam contexto *surfaceless*, libera contexto e superfície, evita espera infinita e não armazena informações vazias em preferências.
2. Reconhece modelos Adreno com sufixo, por exemplo `642L`, e variantes de `Mali-G52` com testes JVM específicos.
3. Os contêineres novos usam o Vortek/Gladio conservador. O usuário escolhe Turnip e versão manualmente; Mali mantém o fallback de segurança se Turnip for selecionado.
4. Corrige Vortek: as extensões Vulkan gravadas com `:` eram lidas como se usassem outro separador. Também restaura corretamente a seleção de uma única extensão e trata listas de extensões indisponíveis.

**Importante:** drivers e recursos dependem de kernel, firmware, Vulkan do Android e versão do Wine/Turnip do dispositivo. Essas correções são de **robustez de seleção/compatibilidade**, não comprovam mais FPS, menos RAM ou execução de um jogo específico. Não foram alterados algoritmos de LSFG, nem DLLs proprietárias.

## Matriz de teste de aparelho

- Android, SoC/GPU, driver Vulkan, versão do Turnip/sistema e RAM
- Instalação/primeira execução do contêiner com 32-bit e 64-bit Windows
- Abrir e fechar jogo com DXVK, WineD3D e renderização OpenGL
- Alternar Vulkan Turnip/Vortek onde suportado, revisar extensões Vortek personalizadas
- Voltar do segundo plano, girar tela, criar nova janela e repetir execuções
- RPC, Steam cover e LSFG devem continuar funcionando sem regressões
- Medir FPS/frametime/RAM para conclusões de desempenho, em testes reproduzíveis

PR original de RPC: [#7](https://github.com/gabrielbskk/Winlator-LSFG/pull/7). O APK da release anterior continua inalterado até que essa nova branch passe no CI e em testes reais.
