# Plano de construção do front-end

Regras gerais: uma fase por vez, sem pular. Em toda fase valem as regras do `frontend/CLAUDE.md`. "Pronto" sempre inclui: `lint`, `typecheck`, `test` e `build` verdes, **e** conferência contra o Figma no desktop (1280) e no mobile (390) com o checklist de fidelidade. No fim de cada fase, o Claude Code avisa que é um bom ponto para commitar e sugere a mensagem.

Frames do Figma (arquivo `ZkXYNG3Hur7RwxupOT0rHB`): Home `5:4` / `24:301`, Quiz pergunta `8:192` / `24:538`, Quiz resultado `8:252` / `24:595`, Área `9:238` / `24:653`, Trilha `9:333` / `24:738`, Chat `10:444` / `24:823`. Componentes: Botão `3:21`, Botão IA `3:32`, Ticket de área `4:180`, Botão pequeno `4:187`, Cabeçalho `4:188`, Rodapé `4:200`. Guia de estilo `2:27`.

## Pré-requisito de design (em paralelo, não trava as fases 1 a 5)

Quatro frames novos no Figma, **já desenhados** (IDs no `CLAUDE.md`): **(a)** Trilha com etapa expandida e salário com dados reais; **(b)** erro e 404; **(c)** chat com 429 (contagem do `Retry-After`) e 503; **(d)** seção "Como funciona" da Home. Carregamento usa skeleton com os componentes existentes.

| Frame novo | Bloqueia a fase |
|---|---|
| (d) Como funciona | 6 (Home) |
| (a) Trilha expandida e salário | 8 (Trilha) |
| (c) Chat 429 e 503 | 10 (Assistente) |
| (b) Erro e 404 | 11 (Estados) |

Quando cada frame existir, o dono passa o ID para entrar no `CLAUDE.md`. Até lá, o Claude Code **pergunta** em vez de improvisar.

---

## Fase 1: Setup, CI e variáveis

- **Objetivo:** projeto Next.js + TypeScript estrito na pasta `frontend/`, Tailwind, `lint`, `typecheck`, scripts e CI funcionando, sem nenhuma tela ainda.
- **Telas:** nenhuma.
- **Entregas:** projeto criado dentro de `frontend/` (mantendo o `CLAUDE.md`), `.env.example` com `NEXT_PUBLIC_API_URL`, scripts `dev`, `build`, `lint`, `typecheck`, `test`, `e2e`, `gen:api`, `.gitignore`, `README` curto do `frontend/`, Playwright instalado (com o navegador) para as capturas de tela da verificação visual, e um job de CI do GitHub Actions só para essa pasta (lint, tipos, testes, build).
- **Pronto quando:** `npm run dev` abre a página inicial padrão, os quatro comandos de verificação passam e o CI roda verde.
- **Como conferir:** não se aplica ao Figma. Conferir o CI no GitHub.
- **Commit sugerido:** `chore: scaffold Next.js frontend with CI`

## Fase 2: Tokens do Figma e guia de estilo

- **Objetivo:** cores, fontes, sombras, espaçamento e estilos de texto do Figma no tema do Tailwind, conferidos.
- **Frames:** Guia de estilo `2:27` (use `get_variable_defs` e `get_design_context`).
- **Entregas:** tema com os 8 tokens de cor, 8 estilos de texto, 4 sombras e as fontes via `next/font` (confirmadas no Figma), e uma rota interna `/styleguide` (só em desenvolvimento) que mostra tudo para comparar com o Figma.
- **Pronto quando:** a `/styleguide` bate com o frame `2:27` lado a lado, e nenhuma cor, fonte ou sombra existe no código sem existir no Figma.
- **Como conferir:** screenshot da `/styleguide` ao lado do frame do guia de estilo.
- **Commit sugerido:** `feat: add design tokens and style guide page`

## Fase 3: Componentes base

- **Objetivo:** componentes reutilizáveis, com todos os estados do Figma e com acessibilidade.
- **Frames:** página Componentes: Botão `3:21` (primário, secundário e escuro, em padrão e hover), Botão IA `3:32`, Ticket de área `4:180` (4 cores, padrão e hover), Botão pequeno `4:187`.
- **Entregas:** `Button`, `SmallButton`, `AssistantButton`, `AreaTicket`, o cartão de etapa e os selos (nível, grátis, pago, confirmar preço, dados limitados). Estados de foco, pressionado e desabilitado: **perguntar** se não estiverem no Figma.
- **Pronto quando:** cada componente aparece na `/styleguide` em todos os estados e bate com o Figma. Navegação por teclado e foco visível funcionam.
- **Como conferir:** a página `/styleguide` ao lado da página Componentes do Figma.
- **Commit sugerido:** `feat: add base UI components`

## Fase 4: Camada de API e tipos

- **Objetivo:** o front falar com o backend com tipos corretos e erros tratados, antes de qualquer tela depender disso.
- **Frames:** nenhum.
- **Entregas:** `npm run gen:api` gerando os tipos de `/v3/api-docs`; módulo `lib/api` com funções para cada rota; classe de erro mapeando os status (400, 404, 413, 429 com `Retry-After`, 503, 500, rede) para textos amigáveis; funções de leitura cacheadas para os componentes de servidor. Testes do Vitest para o mapeamento de erros.
- **Pronto quando:** os tipos refletem a API real, não há `!` nem tipos escritos à mão, e os testes dos erros passam. Se campos vierem todos opcionais, **o dono é avisado** para ajustar o backend.
- **Como conferir:** chamadas reais contra a API em produção, em um script ou teste simples.
- **Commit sugerido:** `feat: add typed API client and error mapping`

## Fase 5: Layout raiz e navegação

- **Objetivo:** estrutura que todas as páginas compartilham.
- **Frames:** Cabeçalho `4:188`, Rodapé `4:200`, e o cabeçalho de cada frame mobile (menu no mobile: ver o frame; se não estiver claro, perguntar).
- **Entregas:** layout raiz com `Cabeçalho` ("Destinos", "Como funciona", "Fazer o quiz"), `Rodapé`, link de "pular para o conteúdo", `lang="pt-BR"`, e um espaço reservado para o assistente (que entra na fase 10).
- **Pronto quando:** o cabeçalho e o rodapé batem com o Figma nos dois tamanhos, e a navegação por teclado percorre tudo em ordem.
- **Como conferir:** desktop e mobile, lado a lado com os frames.
- **Commit sugerido:** `feat: add root layout with header and footer`

## Fase 6: Home

- **Objetivo:** a Home com dados reais.
- **Frames:** Home `5:4` / `24:301`; frame novo (d) "Como funciona".
- **Entregas:** hero com o ticket decorativo, seção "Destinos disponíveis" com os 8 tickets vindos de `GET /api/areas` (nome, número e quantidade de trilhas), seção "Como funciona" (quando o frame existir) e o espaço do atalho para o resultado salvo do quiz (ativado na fase 9).
- **Pronto quando:** a Home real bate com o Figma, as cores dos tickets alternam por posição (coral, menta, limão, lavanda) e o link "Como funciona" do cabeçalho rola até a seção.
- **Como conferir:** o checklist de fidelidade com os dois frames.
- **Commit sugerido:** `feat: add home page with area tickets`

## Fase 7: Página da área

- **Objetivo:** listar as trilhas de uma área.
- **Frames:** Área `9:238` / `24:653`.
- **Entregas:** rota `/areas/[slug]` com título, `descricao`, a contagem de trilhas e a lista de trilhas em ordem (nome, `resumo`, botão "Ver roadmap"). O `comoETrabalhar` vai no bloco "Como é trabalhar nesta área" dos frames novos `134:649` (desktop) e `134:747` (mobile), entre a descrição e a contagem de trilhas; só aparece se o campo vier (opcional no schema). Metadados de SEO por página. `404` para slug inexistente.
- **Pronto quando:** a página bate com o Figma, trata texto longo sem quebrar e mostra o 404 com um slug inválido.
- **Como conferir:** a página da área de Desenvolvimento de Software contra o frame, e uma área com textos mais longos.
- **Commit sugerido:** `feat: add area page with trail list`

## Fase 8: Página da trilha

- **Objetivo:** a página mais importante: roadmap completo com dados reais.
- **Frames:** Trilha `9:333` / `24:738`; frame novo (a) com etapa expandida e salário com dados reais.
- **Entregas:** rota `/trilhas/[slug]` com título, `resumo`/`textoTrilha`, `paraQuemE`, `nivelEntrada`, horas por semana e duração total; botão "Baixar roadmap em PDF" (link para o endpoint); bloco de salário com faixa, referência, fonte, selo de dados limitados e `avisosSalario` sempre visível; lista de etapas, cada uma expansível com descrição, projeto prático, duração e recursos (título, tipo, idioma, acesso); `conteudoConferidoEm`; geração das 36 páginas com `generateStaticParams`; metadados de SEO e breadcrumb a partir da área da trilha.
- **Pronto quando:** a trilha de Java e Spring e mais duas de áreas diferentes batem com o Figma, o `avisosSalario` aparece em todas, links externos só `https` e com `rel` correto, e acessíveis por teclado (etapa expansível como botão com `aria-expanded`).
- **Como conferir:** Java e Spring contra o frame; uma trilha com recursos pagos e uma com "dados limitados" para ver os selos.
- **Commit sugerido:** `feat: add trail page with roadmap and salary ranges`

## Fase 9: Quiz

- **Objetivo:** quiz completo, do início ao resultado salvo.
- **Frames:** Quiz pergunta `8:192` / `24:538`, Quiz resultado `8:252` / `24:595`.
- **Entregas:** rotas `/quiz` e `/quiz/resultado`; uma pergunta por tela com barra de 12 segmentos, Voltar e Próxima (desabilitado sem resposta); alternativas **embaralhadas guardando a `ordem` original**; envio dos 12 pares `{pergunta, alternativa}`; resultado com o top 3, barra de `barra`, "Ver trilhas", "Conversar com a IA" e "Refazer o quiz"; resultado salvo no `localStorage` (só slugs, com `try/catch`, revalidado na API); atalho na Home.
- **Testes:** Vitest para o embaralhamento mantendo a ordem, a montagem do POST e a leitura segura do `localStorage`; **Playwright: quiz completo**.
- **Pronto quando:** o fluxo completo funciona nos dois tamanhos, o quiz continua certo após recarregar e com `localStorage` bloqueado, e o erro 400 mostra mensagem amigável.
- **Como conferir:** os dois frames e o fluxo no navegador.
- **Commit sugerido:** `feat: add quiz flow with saved result`

## Fase 10: Assistente de IA

- **Objetivo:** o assistente em todas as páginas, com os estados de limite.
- **Frames:** Chat `10:444` / `24:823`; frame novo (c) com 429 e 503.
- **Entregas:** botão flutuante "Perguntar à IA" e painel no layout raiz; Context com a conversa **em memória**, que não zera ao navegar; contexto derivado só dos segmentos da URL e do resultado do quiz, como slugs; histórico limitado a 6 mensagens; contador de 500 caracteres; aviso de dados pessoais; estados de envio, 429 (contagem do `Retry-After`, ou 60 s se o cabeçalho não vier, avisando o dono), 503 e erro genérico.
- **Testes:** Vitest para a derivação do contexto e o limite de histórico; **Playwright: chat com 429 e chat com 503**, com a API simulada.
- **Pronto quando:** o painel bate com o Figma, a conversa sobrevive à navegação entre páginas, o contexto enviado muda com a página e o site continua funcionando quando o assistente falha.
- **Como conferir:** os frames e os dois cenários simulados.
- **Commit sugerido:** `feat: add AI assistant with rate limit states`

## Fase 11: Estados de carregamento e erro

- **Objetivo:** nenhum "branco" nem tela quebrada.
- **Frames:** frame novo (b) de erro e 404.
- **Entregas:** `loading.tsx` com skeletons usando os componentes existentes, `not-found.tsx`, `error.tsx` e erro global, falha amigável no download do PDF.
- **Pronto quando:** derrubar a API (ou usar um slug inexistente) mostra telas amigáveis sem texto técnico.
- **Como conferir:** simular cada falha e comparar com o frame (b).
- **Commit sugerido:** `feat: add loading, error and not-found states`

## Fase 12: Responsividade e acessibilidade

- **Objetivo:** revisar o que já foi feito com uma passada final nos dois pontos.
- **Entregas:** conferir tamanhos entre 390 e 1280 (layout fluido com largura máxima), tamanhos acima de 1280, navegação por teclado em todas as páginas, foco visível, contraste, `prefers-reduced-motion`, leitor de tela nos fluxos principais. Regras de lint de acessibilidade ativadas. `axe` automático pode entrar aqui, sem travar o plano.
- **Pronto quando:** nenhuma página quebra em larguras intermediárias, e tudo é usável só com teclado.
- **Como conferir:** o checklist de fidelidade e testes manuais em 3 larguras.
- **Commit sugerido:** `fix: improve responsive layout and accessibility`

## Fase 13: SEO e acabamento

- **Objetivo:** pronto para ser encontrado e compartilhado.
- **Frames:** logo e identidade no Figma (exportar o logo em SVG; favicon e imagem de compartilhamento: confirmar o que existe e perguntar o que faltar).
- **Entregas:** título e descrição por página, `sitemap.xml` e `robots.txt` a partir de áreas e trilhas, imagem de compartilhamento, favicon.
- **Pronto quando:** compartilhar um link de trilha mostra título e descrição corretos, e o sitemap lista as 36 trilhas.
- **Commit sugerido:** `feat: add SEO metadata, sitemap and favicon`

## Fase 14: Deploy

- **Objetivo:** site no ar na Vercel falando com a API.
- **Entregas:** projeto na Vercel com **diretório raiz `frontend/`**, variável `NEXT_PUBLIC_API_URL`, build verde, teste manual completo em produção.
- **LEMBRETE obrigatório do Claude Code ao dono:** adicionar a origem do site (domínio da Vercel) na variável `CORS_ORIGENS` na Railway, **sem isso o site não fala com a API**. Previews da Vercel não chamam a API a menos que a origem seja adicionada.
- **Pronto quando:** Home, uma área, uma trilha, o quiz, o chat e o download do PDF funcionam em produção, e o `Retry-After` é lido (ou o dono foi avisado do ajuste no backend).
- **Commit sugerido:** `docs: add frontend deployment notes`
