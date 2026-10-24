# Pendências

Itens conhecidos que ficaram para fases futuras ou para o backend. Atualize ao resolver.

## Fase 12: responsividade e acessibilidade

- [ ] **Alternativa selecionada do quiz:** o estado aparece nos frames `137:760` (desktop) e `137:820` (mobile). Conferir se a tela do quiz segue esse estado.
- [ ] **Card "Ver meu resultado" na Home:** ainda não tem frame. Perguntar ao dono antes de refinar o visual.

## Fase 13: SEO e acabamento

- [ ] **Slug inexistente responde HTTP 200** (a página mostra o 404, mas o status não é 404). Investigar e corrigir junto dos metadados.
- [ ] **`generateMetadata`:** definir título e descrição por página. Medido na Fase 11: ele não atrasa o primeiro byte em produção, então não precisa de Suspense só por isso.

## Fase 14: deploy

- [ ] **Build depende da API:** `generateStaticParams` chama a API, e o build falha se ela estiver fora. O CLAUDE.md pede fallback para geração sob demanda.
- [ ] **CORS:** adicionar a origem do site da Vercel em `CORS_ORIGENS` na Railway. Previews da Vercel não chamam a API.

## Backend (o dono leva à conversa do backend)

- [ ] **`comoETrabalhar` obrigatório em `AreaDetalheResponse`:** hoje é opcional no schema, e o front só mostra o bloco quando o campo vem. Marcar com `@Schema(requiredMode = REQUIRED)`.
