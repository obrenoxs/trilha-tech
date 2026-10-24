# Pendências

Itens conhecidos que ficaram para fases futuras ou para o backend. Atualize ao resolver.

## Fase 12: responsividade e acessibilidade

- [x] **Alternativa selecionada do quiz:** feita conforme os frames `137:760` (desktop) e `137:820` (mobile). Cartão na cor da aba, aba em ink com a letra na cor do cartão; borda e sombra iguais.
- [x] **Card "Ver meu resultado" na Home:** feito conforme `139:731` / `139:994` (desktop) e `139:1011` / `139:1248` (mobile), entre o hero e "Destinos disponíveis".
- [ ] **Leitor de tela real (NVDA):** testar os fluxos do quiz (perguntas, alternativa marcada, erro de envio, resultado) e do chat (abrir, enviar, resposta, limite 429 e indisponível 503, fechar). Na Fase 12 só foi conferida a árvore de acessibilidade, sem leitor de verdade.
- [ ] **Exceção do axe no contraste da fonte salarial:** `e2e/a11y.spec.ts` desliga a regra `color-contrast` só no teste da página da Trilha (`skipContrast`). Remover assim que o dono decidir a troca no Figma e o texto for ajustado.
- [ ] **Contraste vindo do Figma (aguardando confirmação do dono, não alterar ainda):** nas faixas salariais, o texto de fonte (`GLASSDOOR · 06/2026`) usa `ink-soft` sobre coral (3,06:1) e sobre menta (4,45:1); o mínimo AA é 4,5:1. Está nos nós `123:681` e `123:687`. Não foi alterado; o axe ignora só essa regra na página da Trilha (`e2e/a11y.spec.ts`). Decidir: usar `ink` nesses textos ou manter.
- [x] **Foco depois de navegar com o teclado:** resolvido. Ao mudar de rota, o foco vai para o `<main id="conteudo">` com `preventScroll` (`components/layout/route-focus.tsx`), sem rodar no primeiro carregamento. A rolagem restaurada pelo botão voltar continua funcionando (testes em `e2e/route-focus.spec.ts`).
- [ ] **Exceção do axe no nome do botão da etapa (WCAG 2.5.3):** o botão da etapa tem como nome só o título (decisão do dono); o resumo e o nível que aparecem dentro dele entram como descrição (`aria-describedby`) e ficam `aria-hidden`. O axe ainda acusa `label-content-name-mismatch` porque conta esse texto como visível, então `e2e/a11y.spec.ts` desliga só essa regra no teste da Trilha. Remover se o nome do botão passar a incluir resumo e nível, ou se o dono decidir que o rótulo visível deve ser só o título.
- [ ] **Hover/pressionado na etapa:** o cabeçalho da etapa ganhou `cursor: pointer`, mas nenhum hover novo (o Figma não define, e o cartão inteiro se mover com o conteúdo aberto não faz sentido). Se o dono quiser um estado de hover, precisa de decisão e, de preferência, frame.
- [ ] **Tablet (768 a 1279 px) sem frame:** a margem lateral é 40 px de 768 a 1023 e 96 px a partir de 1024; hero e telas de 404/erro empilham abaixo de 1280 e 1024 px; destinos, "Como funciona" e faixas salariais em colunas só a partir de 1024. Aguardando aprovação do dono.

## Fase 13: SEO e acabamento

- [x] **404 de verdade para slug inexistente (decidido pelo dono na Fase 13):** mantém HTTP 200 com a página "não encontrada" e `noindex` (o Next serve o shell da rota antes de checar os dados, então `notFound()` na página ou no layout não muda o status; só um `proxy.ts` mudaria, ao custo de 1 a 2 s na primeira visita por instância fria). Se um dia for preciso, o experimento do Proxy com lista de slugs funcionou: 404 correto nas áreas e trilhas inexistentes.
- [x] **Favicon e imagem de compartilhamento:** feitos a partir dos frames aprovados do Figma. Favicon `142:1102` em `app/icon.svg` (vetor do Figma), `app/icon.png` (512x512) e `app/apple-icon.png` (180x180), todos com fundo transparente; imagem de compartilhamento `142:1110` em `app/opengraph-image.png` (1200x630, 74 KB) com `opengraph-image.alt.txt`. O `favicon.ico` do scaffold foi removido. As páginas repassam a imagem no `openGraph`/`twitter` (`OG_IMAGE` em `lib/seo.ts`), porque o `openGraph` de uma página substitui o do layout inteiro, imagem incluída.
- [x] **Ícone da tela inicial do iOS (`apple-icon.png`) com fundo:** feito conforme o frame aprovado `149:1102` (180x180, fundo `#E6EEF2` opaco, sem canal alfa), renderizado a partir do vetor do Figma na posição do frame; a diferença para o screenshot do Figma é de 0,12 em média (só suavização de borda). Substituiu a versão transparente.
- [x] **Metadados por página:** título e descrição (até 155 caracteres, cortados em frase ou palavra) vêm da área ou da trilha da API; canonical, Open Graph e Twitter (`lib/seo.ts`). `generateMetadata` não precisa de Suspense (não atrasa o primeiro byte).
- [x] **`sitemap.xml` e `robots.txt`:** o sitemap lista Home, 8 áreas e 36 trilhas (45 URLs), sem quiz nem styleguide; é gerado a cada requisição (`connection()`), então o build não depende da API para ele. Com a API fora, devolve só a Home e não guarda essa resposta.
- [ ] **Definir `NEXT_PUBLIC_SITE_URL` antes de publicar:** sem ela o build avisa e usa `http://localhost:3000` (ou o domínio de produção da Vercel, se existir `VERCEL_PROJECT_PRODUCTION_URL`). Com o valor errado, canonical, Open Graph e sitemap apontam para o lugar errado. Lembrar na Fase 14.

## Fase 14: deploy

- [ ] **Build depende da API:** `generateStaticParams` chama a API, e o build falha se ela estiver fora. O CLAUDE.md pede fallback para geração sob demanda.
- [ ] **CORS:** adicionar a origem do site da Vercel em `CORS_ORIGENS` na Railway. Previews da Vercel não chamam a API.

## Backend (o dono leva à conversa do backend)

- [ ] **`comoETrabalhar` obrigatório em `AreaDetalheResponse`:** hoje é opcional no schema, e o front só mostra o bloco quando o campo vem. Marcar com `@Schema(requiredMode = REQUIRED)`.
