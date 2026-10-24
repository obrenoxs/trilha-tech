# CLAUDE.md: front-end do Trilha Tech

Este arquivo vale para a pasta `frontend/` do monorepo `obrenoxs/trilha-tech`. Leia tudo antes de agir. As regras daqui valem em toda sessão.

## O projeto

Trilha Tech é uma plataforma web que orienta iniciantes e estudantes de TI a descobrir qual área seguir. Tem 8 áreas, 36 trilhas, roadmaps com etapas e recursos de estudo, faixa salarial estimada, download do roadmap em PDF, um quiz opcional de 12 perguntas (recomenda **áreas**, nunca vagas) e um assistente de IA presente em todas as páginas.

- Versão 1: **sem login e sem dados pessoais**.
- O backend (Java/Spring Boot) já está em produção e **não se altera sem necessidade**. API: `https://trilha-tech-production.up.railway.app`. Swagger: `/swagger-ui.html`. Esquema OpenAPI: `/v3/api-docs`.
- Monorepo: `backend/`, `conteudo/`, `docs/` e este `frontend/`. Documentação de decisões em `docs/`.
- O dono do projeto está **aprendendo**: ele quer entender o que foi construído, não só ter o resultado.

## Regras permanentes

1. **Fidelidade ao Figma é a prioridade máxima.** Leia o design direto do Figma (veja a seção "Figma"). Nunca adivinhe por descrição. Se algo for inviável, ambíguo ou faltar no design, **pare e pergunte**. Não decida sozinho e não invente telas, estados ou textos.
2. **Fases pequenas e testáveis.** Siga `docs/plano-frontend.md`, uma fase por vez, sem pular. Ao fim de cada fase, rode as verificações (abaixo), confira contra o Figma e avise que é um bom ponto para commitar, sugerindo a mensagem **em inglês, no padrão Conventional Commits** (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`), curta e no imperativo. Exemplo: `feat: add area listing page`. **Não faça commit sem o dono pedir.**
3. **Fora do escopo, avise antes.** Nada de login, cadastro, painel, favoritos, comentários, modo escuro, outros idiomas ou qualquer funcionalidade que não esteja no plano. Se o dono pedir algo fora disso, diga que está fora e espere a decisão.
4. **Nenhum segredo no repositório.** Configuração só por variáveis de ambiente, com um `.env.example` atualizado. A única variável pública prevista é `NEXT_PUBLIC_API_URL`. `.env*` (menos o `.env.example`) fica no `.gitignore`.
5. **Acessibilidade e responsividade desde o início**, em cada componente, e não como retoque no fim: HTML semântico, foco visível, contraste, navegação por teclado, `alt` e `aria` onde couber, respeito a `prefers-reduced-motion`.
6. **Explique as decisões importantes de forma curta** (2 a 4 linhas por fase: o que foi feito e por quê). Prefira ensinar o conceito a só entregar código.
7. **Responda em português, direto e objetivo.** Código, nomes de arquivos, variáveis e commits em inglês. Textos da interface em português do Brasil (`lang="pt-BR"`).

## Stack (decidida e aprovada: não troque sem perguntar)

- **Next.js (App Router) + TypeScript** em modo estrito. Use as versões estáveis atuais e **confira a documentação oficial** ao criar o projeto e ao usar recursos novos.
- **Tailwind CSS**, com os tokens do Figma (cores, fontes, sombras, espaçamento) definidos **uma vez** no tema. Padrões repetidos do design (borda grossa, sombra dura, "ticket") viram **componentes**, não classes copiadas.
- **Fontes** com `next/font`: Unbounded, Manrope e DM Mono (confirmadas no Figma).
- **Dados de leitura** (Home, Área, Trilha): `fetch` em **componentes de servidor**, com cache de 1 hora. Gerar as 36 trilhas no build com `generateStaticParams`; se a API estiver fora no build, cair para geração sob demanda.
- **Escritas e interações** (resultado do quiz, mensagens do chat): `fetch` em um módulo único e tipado, `lib/api`.
- **Sem TanStack Query, Redux ou Zustand na v1.** O quiz usa `useReducer`. O assistente usa Context no layout raiz.
- **Tipos da API**: gerados com `openapi-typescript` a partir de `/v3/api-docs` (script `npm run gen:api`). **Não escreva DTOs à mão** e não use `!` para forçar campos. Se os campos saírem todos opcionais, **avise o dono**: o backend precisa marcar `@Schema(requiredMode = REQUIRED)`.
- **Validação em runtime (Zod)** só para dado de fora do nosso controle, como o conteúdo do `localStorage`.
- **Gerenciador de pacotes: npm.**
- **Testes** na v1: Vitest para a lógica pura e 2 a 3 fluxos de Playwright. `axe` e o restante entram depois, sem travar as fases de tela.

## Regras da API que o front precisa respeitar

**Chamadas direto do navegador para a API (com CORS). Não use proxy da Vercel.** O backend limita o uso por visitante pelo IP (`X-Real-IP`), e um proxy faria todos parecerem o mesmo visitante.

| Rota | Uso |
|---|---|
| `GET /api/areas` | Home: lista das 8 áreas |
| `GET /api/areas/{slug}` | Página da área, com as trilhas em ordem |
| `GET /api/trilhas/{slug}` | Página da trilha completa |
| `GET /api/trilhas/{slug}/pdf` | Download do roadmap: o front baixa com `fetch` e mostra aviso amigável se falhar; o link simples (o servidor envia `attachment`) continua como fallback sem JavaScript |
| `GET /api/quiz` | As 12 perguntas, sem áreas nem pontos |
| `POST /api/quiz/resultado` | Corpo `{ "respostas": [{ "pergunta": 1, "alternativa": 3 }, ...] }` com **exatamente 12** |
| `POST /api/assistente/mensagens` | Corpo `{ "mensagem", "contexto", "historico" }`; devolve `{ "resposta" }` |

Os nomes exatos dos campos vêm **dos tipos gerados** do OpenAPI. Em caso de dúvida, o Swagger é a fonte de verdade.

**Valores de enum da API** (conferidos em `lib/api/schema.d.ts`; a API os envia em MAIÚSCULAS, e o front mapeia para o rótulo da tela):

| Campo | Valores |
|---|---|
| `acesso` (recurso) | `GRATUITO`, `PAGO`, `NAO_CONFIRMADO` |
| `tipo` (recurso) | `DOCUMENTACAO`, `CURSO`, `LIVRO`, `FORMACAO`, `CERTIFICACAO`, `TUTORIAL`, `GUIA`, `VIDEO`, `FERRAMENTA` |
| `idioma` (recurso) | `PT`, `EN` |
| `nivelEntrada` (trilha) | `INICIANTE`, `INTERMEDIARIO`, `AVANCADO` |
| `nivel` (etapa) | `BASICO`, `INTERMEDIARIO`, `AVANCADO` |
| `nivel` (faixa salarial) | `JUNIOR`, `PLENO`, `SENIOR` |
| `papel` (histórico do chat) | `USUARIO`, `ASSISTENTE` |

`tipoReferencia` (faixa salarial) **não é enum**: é texto livre vindo da API. Mostre-o como veio, sem mapear.

**Nunca faça chamada real a `POST /api/assistente/mensagens`: consome cota de IA do backend e o limite por IP. Teste o chat com mocks.**

**Quiz**
- `pergunta` e `alternativa` no POST são as **`ordem` originais** vindas do `GET /api/quiz`, e não ids, nem a posição na tela. O front **embaralha as alternativas ao exibir**, então guarde a `ordem` original de cada uma. As letras A, B, C, D são só a posição na tela.
- O botão de avançar fica **desabilitado** até haver uma resposta.
- O quiz é opcional, sem estado no servidor, e recomenda áreas, não vagas.
- **Resultado salvo no `localStorage`**, com `try/catch` em toda leitura e escrita (pode estar bloqueado ou vazio). Guarde **só os slugs** das áreas recomendadas e **revalide contra a API ao exibir**. A Home mostra um atalho para o resultado salvo.

**Assistente (em todas as páginas)**
- Fica no **layout raiz**. O estado da conversa fica **em memória**, sem `localStorage`: **não recomeça ao mudar de página**, e só zera ao recarregar.
- O **contexto** vem **apenas dos segmentos da URL** (`/areas/[slug]`, `/trilhas/[slug]`) e, se existir, das áreas do resultado do quiz, sempre como **slugs**. **Nunca** envie a URL inteira nem texto livre da página. O contexto enviado é sempre o da página atual.
- Histórico: no máximo as **últimas 6 mensagens**. Mensagem atual: no máximo **500 caracteres** (mostre contador e bloqueie o envio acima disso).
- O aviso "não digite dados pessoais" aparece como no design.
- Limites: 5 mensagens por minuto e 30 por hora por visitante. **429** traz `Retry-After`: mostre a contagem regressiva e desabilite o envio até acabar. **Atenção:** para o navegador ler `Retry-After` em chamada entre domínios, o backend precisa expô-lo (`Access-Control-Expose-Headers`). Se não vier, use 60 segundos como padrão e **avise o dono** para ajustar o backend.
- **503** significa assistente indisponível agora: mensagem amigável, e o resto do site segue funcionando.

**Erros.** O formato é `{ timestamp, status, error, message, errors?, path }`. **Nunca exiba** `message`, `path`, `timestamp` nem texto técnico ao usuário: use textos próprios, escolhidos pelo `status`:

| Status | Comportamento |
|---|---|
| 400 | Mensagem amigável; no quiz, indicar que falta responder |
| 404 | Página "não encontrada" |
| 413 | "Mensagem muito longa" |
| 429 | Aguarde X segundos (contagem) |
| 503 | Assistente indisponível agora |
| 500 e falha de rede | Erro genérico com opção de tentar de novo |

**Conteúdo e salário (regra de conteúdo, não detalhe visual)**
- Mostre **sempre a faixa "R$ mín – máx"**, com o valor de referência e a fonte em letra pequena. O rótulo do tipo de referência (média, mediana, média do salário base...) vem de `tipoReferencia`: **nunca fixo no código**.
- **`avisosSalario` fica visível junto das faixas em toda trilha.** Sem exceção.
- O selo discreto **"dados limitados"** aparece só quando `dadosLimitados` for `true`.
- Salários são estimativas de mercado e **não distinguem CLT de PJ**: nunca escreva que são CLT.
- Recurso de estudo: `acesso` = `GRATUITO` → "Grátis"; `PAGO` → "Pago"; `NAO_CONFIRMADO` → "Confirmar preço". Mostre também tipo e idioma.
- A trilha mostra `conteudoConferidoEm` ("Recursos e fontes verificados em MM/AAAA").
- Identificador público é sempre o **slug**. Nunca exiba ids internos. O campo `pendencias` não existe na API e nunca deve aparecer.
- Texto vindo da API é renderizado **como texto** (nada de `dangerouslySetInnerHTML`). Links externos usam `target="_blank"` com `rel="noopener noreferrer"` e só se começarem com `https://`.
- Dados reais são mais longos que os do mockup (títulos e resumos). Trate quebra de linha e `line-clamp` com cuidado e **pergunte** se o layout do Figma não comportar.

**CORS.** O backend aceita só a origem de produção e `localhost` (`http://localhost:3000` e `http://localhost:5173`). Previews da Vercel **não** chamam a API. Na fase de deploy, **lembre o dono de adicionar a origem do site na variável `CORS_ORIGENS` na Railway.**

## Figma

- Arquivo: `ZkXYNG3Hur7RwxupOT0rHB`. Páginas: **Telas**, **Fundamentos** e **Componentes**.
- Antes de qualquer leitura, chame `create_session` com a chave do arquivo e uma descrição curta da tarefa. Antes de usar `get_design_context`, carregue o skill `figma-design-to-code`. O resultado de `get_design_context` é uma **referência a adaptar** ao projeto (tokens, componentes e Tailwind), e não código final para colar.
- **Economize chamadas.** O plano do dono (Profissional, assento Full) permite até **200 chamadas de leitura por dia e 10 por minuto**. Peça o contexto de **um frame por vez**, reaproveite o que já foi lido na sessão, não repita a mesma chamada e, se bater o limite por minuto, espere em vez de insistir.
- Reaproveite componentes e tokens existentes. **Não crie cor, fonte ou sombra que não esteja no Figma.**
- Tokens (confirmar com `get_variable_defs` na fase 1): cores `bg #E6EEF2`, `ink #14213D`, `ink-soft #4B587A`, `white #FFFFFF`, `coral #FF8A6B`, `mint #7EE0B8`, `lemon #F6E27A`, `periwinkle #A9B8FF`; sombras duras (sem blur, cor `ink`): pressionado 2px, pequena 4px, média 6px, grande 8px. Fontes **confirmadas no Figma**: **Unbounded** (títulos e números), **Manrope** (corpo e botões) e **DM Mono** (rótulos). Estilos de texto: Display/H1 (Unbounded Bold 54, -4%), Display/H2 (Bold 46), Display/H3 (SemiBold 24), Display/Card (SemiBold 20), Display/Num (Bold 32), Display/Logo (Bold 22), Mono/Label (DM Mono Medium 15, +10%), Mono/Caption (13, +8%), Mono/Rodape (14), Corpo/Grande (Manrope Medium 20), Corpo/Medio (17), Corpo/Pequeno (14), UI/Botao (ExtraBold 18) e UI/Nav (Bold 17). No mobile, alguns títulos usam tamanho próprio (ex.: H1 34): leia do frame mobile.

Frames (desktop 1280 / mobile 390):

| Tela | Desktop | Mobile |
|---|---|---|
| Home | `5:4` | `24:301` |
| Quiz: pergunta | `8:192` | `24:538` |
| Quiz: resultado | `8:252` | `24:595` |
| Área | `9:238` | `24:653` |
| Trilha | `9:333` | `24:738` |
| Chat da IA aberto | `10:444` | `24:823` |

Componentes (página Componentes): Botão `3:21`, Botão IA `3:32`, Ticket de área `4:180`, Botão pequeno `4:187`, Cabeçalho `4:188`, Rodapé `4:200`. Guia de estilo (página Fundamentos): `2:27`.

Frames novos (página Telas, abaixo dos frames atuais). Estados e detalhes que só existem aqui:

| Tela | Desktop | Mobile |
|---|---|---|
| Home: seção "Como funciona" (3 passos + aviso da IA; vai depois dos destinos, o link do cabeçalho rola até ela) | `121:537` | `124:715` |
| Trilha: etapa expandida (projeto prático, duração, recursos com tipo, idioma e Grátis/Pago/Confirmar preço), salário com mín–máx, referência, fonte, selo "dados limitados" e aviso, blocos "Sobre esta trilha", "Para quem é" e métricas | `123:553` | `125:640` |
| Chat: limite atingido (429, com contagem do `Retry-After`, campo e botão desabilitados) e indisponível (503, com "Tentar de novo") | `124:571` (os dois estados lado a lado) | `125:838` (429) e `125:860` (503) |
| 404 | `124:623` | `124:756` |
| Erro ao carregar | `124:669` | `124:796` |

Detalhes dos frames novos: cada etapa do roadmap tem um botão "+" (aberta mostra "−") que expande o conteúdo; só uma etapa expandida aparece no mockup, mas o comportamento é abrir e fechar qualquer uma. O frame do chat 429/503 é uma folha de estados (as legendas "ESTADO · …" e o título não fazem parte da tela). Frames de erro e 404 usam o bilhete inclinado; o 404 não mostra o número do erro. Carregamento é skeleton com os componentes existentes (sem frame próprio). Os textos são exemplo.

Se algo não estiver em nenhum frame (outros erros do chat como 400, 413 e 500, estados de foco), **não improvise**: pergunte. Para 400, 413 e 500 do chat, use o mesmo aviso do 503 com texto próprio e amigável, e me avise.

Conteúdo do mockup (textos, etapas, salários) é **exemplo**. O front sempre usa o que a API devolve. O que a Área mostra de `comoETrabalhar` não está no frame atual: **pergunte onde fica**.

Na Área, o comoETrabalhar vai no bloco "Como é trabalhar nesta área", entre a descrição e "N TRILHAS NESTA ÁREA": frames 134:649 (desktop) e 134:747 (mobile). Mostre o bloco só se o campo vier (é opcional no schema).

## Estrutura de pastas (proposta; ajuste só explicando o motivo)

```
frontend/
  app/                    rotas (App Router)
    layout.tsx            layout raiz: cabeçalho, rodapé e assistente
    page.tsx              Home
    areas/[slug]/page.tsx
    trilhas/[slug]/page.tsx
    quiz/page.tsx         perguntas
    quiz/resultado/page.tsx
    not-found.tsx  error.tsx  loading.tsx
  components/ui/          Botão, Ticket, Cartão de etapa, selos
  components/layout/      Cabeçalho, Rodapé
  features/quiz/          lógica e telas do quiz
  features/chat/          assistente (contexto, painel, estados)
  lib/api/                cliente tipado, erros, tipos gerados
  e2e/                    testes do Playwright
```

## Como verificar ("pronto" de cada fase)

Rode e deixe verde antes de dizer que a fase terminou:

```
npm run lint
npm run typecheck
npm run test      # Vitest
npm run build
```

Os scripts (`dev`, `build`, `lint`, `typecheck`, `test`, `e2e`, `gen:api`) são criados na fase 1.

### Verificação visual contra o Figma (obrigatória em toda fase com tela)

1. Suba o app (`npm run dev`) e abra a rota da fase.
2. Tire screenshots da página com o Playwright em **1280 px** (desktop) e **390 px** (mobile) de largura, página inteira.
3. Peça o screenshot do frame correspondente no Figma (`get_screenshot`, com `maxDimension` suficiente para ver detalhes).
4. Compare os dois lado a lado e **liste cada diferença**: espaçamentos, tamanho e peso de fonte, cores, bordas, raios, sombras, alinhamentos, ordem e estados. Corrija e repita até não restar diferença que você consiga corrigir.
5. O **conteúdo** do mockup é exemplo (textos, números, quantidade de itens): compare **layout, estilo e estrutura**, não as palavras. Se o dado real quebrar o layout, pergunte.
6. Informe ao dono, com honestidade, o que ainda difere e por quê. **Não declare a fase concluída com diferença conhecida e escondida.** Depois, o dono usa o checklist de fidelidade (`docs/checklist-fidelidade.md`).

## Não faça

- Não invente telas, textos, estados, cores ou funcionalidades.
- Não use proxy da Vercel para a API, nem coloque a chave ou a URL de algo secreto no front.
- Não escreva tipos da API à mão, nem use `!` para forçar campos.
- Não guarde a conversa do chat nem dados pessoais no navegador.
- Não exiba mensagens técnicas de erro ao usuário.
- Não instale biblioteca nova sem explicar o motivo e esperar o OK do dono.
- Não faça commit, push ou deploy sem o dono pedir.
- Não mexa em `backend/`, `conteudo/` ou `docs/` sem avisar. Pedidos de ajuste no backend vão para o dono, que os leva à conversa do backend.
