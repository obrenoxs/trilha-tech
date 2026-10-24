# Decisões técnicas do Trilha Tech (backend)

> Atualizado em 23/10/2026, com o backend publicado. Este documento **substitui** `docs/decisoes-fase-1.md`: cobre todas as fases, registra o que mudou de rota durante a construção e lista os riscos conhecidos.

## Sumário

1. [Escopo da v1](#1-escopo-da-v1)
2. [Stack](#2-stack)
3. [Arquitetura](#3-arquitetura)
4. [Conteúdo e carga](#4-conteúdo-e-carga)
5. [Banco de dados](#5-banco-de-dados)
6. [API](#6-api)
7. [Quiz](#7-quiz)
8. [PDF dos roadmaps](#8-pdf-dos-roadmaps)
9. [Assistente de IA](#9-assistente-de-ia)
10. [Segurança](#10-segurança)
11. [Testes](#11-testes)
12. [CI/CD e deploy](#12-cicd-e-deploy)
13. [O que mudou em relação ao plano inicial](#13-o-que-mudou-em-relação-ao-plano-inicial)
14. [Riscos conhecidos e pendências](#14-riscos-conhecidos-e-pendências)
15. [Registro das verificações em produção](#15-registro-das-verificações-em-produção)

---

## 1. Escopo da v1

**Dentro do escopo**

- Catálogo de 8 áreas e 36 trilhas, com roadmap em etapas, recursos de estudo e três faixas salariais (júnior, pleno e sênior) por trilha, sempre com fonte e mês de referência.
- Quiz de 12 perguntas com top 3 de áreas.
- PDF do roadmap de cada trilha.
- Assistente de IA, com limites de uso.

**Fora do escopo**

- Login e contas de usuário, favoritos, painel de administração.
- Guardar o resultado do quiz ou o histórico do chat no servidor.

**Regra de trabalho:** um pedido fora do escopo é avisado e discutido antes de entrar. Ele só vai adiante como uma fase 2 do produto, se for decidido assim.

## 2. Stack

| Item | Decisão | Motivo e alternativas descartadas |
|---|---|---|
| Linguagem e framework | Java 25 e Spring Boot 4.1 | Stack-alvo do portfólio |
| Build | Maven, com o wrapper (`mvnw`) | Build reprodutível em qualquer máquina |
| Banco | PostgreSQL | Precisamos de `pg_advisory_xact_lock`, `INSERT ... ON CONFLICT` e `gen_random_uuid()`. H2 foi descartado por isso |
| Migrações | Flyway, **só para estrutura** | O conteúdo vem de arquivos e não de migrações |
| Persistência | Spring Data JPA e Hibernate, `ddl-auto: validate`, `open-in-view: false` | O esquema pertence ao Flyway. A validação derruba a subida se uma entidade divergir da tabela |
| Estilo de código | Sem Lombok. DTOs e arquivos de conteúdo são *records* | Código explícito e DTOs imutáveis |
| Segurança | Sem Spring Security | API pública, somente leitura e sem login. Os riscos são tratados por limites, validação e CORS (seção 10) |
| JSON | Jackson 3 (`tools.jackson`), com leitura estrita dos arquivos de conteúdo | `FAIL_ON_UNKNOWN_PROPERTIES` pega chave digitada errada |
| Documentação da API | springdoc-openapi 3.0.3 | A linha 3.x é a que atende o Spring Boot 4 |
| Observabilidade | Actuator, somente `health` | Necessário para o health check da plataforma. Nada mais é exposto |
| Empacotamento | Docker multi-stage (Temurin 25) | Imagem final sem Maven nem código-fonte, com usuário sem privilégios |
| CI | GitHub Actions | Roda no repositório, sem servidor próprio |
| Hospedagem | Railway (API e PostgreSQL). Front-end previsto na Vercel | Deploy direto do GitHub, com o *Wait for CI* |
| IA | Gemini, camada gratuita, sem faturamento ativado | Veja a seção 9 |

## 3. Arquitetura

- **Camadas:** `controller` → `service` → `repository`, com entidades e DTOs separados.
- **Pacotes:** `io.github.obrenoxs.trilhatech.<módulo>.<conceito>.{entity, enums, repository, service, controller, dto.request, dto.response}`, com nomes em português.
- **Módulos:** `conteudo` (área e trilha), `quiz` (pergunta e resultado), `assistente` (chat, cliente e uso), `carga` (arquivo, leitura, conversão, validação e sincronização) e `compartilhado` (config, exceções, filtro e DTO de erro).
- **Dependências em uma direção:** `compartilhado` não importa nenhum outro módulo, e por isso as exceções de erro ficam nele. O `assistente` usa services e DTOs do `conteudo`, e nunca o contrário.
- **Regras puras, sem Spring e sem banco:** `CalculadoraPontuacao`, os validadores, o `ConversorTexto`, o `LimitadorJanelaFixa` e o `IpDoCliente`. Elas são baratas de testar, e é onde um erro silencioso mais custaria.
- **Inversão de dependência:** o assistente conhece só a interface `ClienteIa`. O `ClienteGemini` é um detalhe trocável.
- **Sem N+1:** as consultas de listas aninhadas (etapas e recursos de uma trilha, alternativas do quiz) trazem tudo de uma vez e agrupam em memória, ou usam `join fetch`.

## 4. Conteúdo e carga

| Decisão | Motivo |
|---|---|
| A fonte de verdade é a pasta `conteudo/` (JSON e PDFs) no Git, e não um painel de administração | Versionado, revisável em Pull Request, validável no CI e sem precisar de login |
| Dupla validação | O `validar.py` (CI) confere a **qualidade do conteúdo**: estrutura, hashes e equilíbrio do quiz. O Java confere **o que o banco exige**. As duas não se repetem |
| A carga acontece na subida (`CarregadorConteudo`, um `ApplicationRunner`) | O banco sempre reflete o que está empacotado na versão em execução |
| Leitura estrita dos JSON | Uma chave desconhecida derruba a carga |
| Validação acumulada | Todos os problemas saem juntos, em uma `ConteudoInvalidoException`, com trilha, etapa e campo. Evita um ciclo de deploy por erro |
| Seis peças na carga | Leitor, validador do catálogo, validador de trilha, validador de consistência (catálogo contra arquivo), validador do quiz e sincronizador, orquestrados pelo `ValidadorConteudo` e pelo `CarregadorConteudo` |
| PDF obrigatório na carga | Se uma das 36 trilhas estiver sem PDF no pacote, a subida falha. O problema aparece no deploy e não em produção |
| Sincronização idempotente e transacional | Carregar duas vezes não duplica. A transação é única |
| Trava `pg_advisory_xact_lock` (chave fixa) | Duas instâncias não sincronizam ao mesmo tempo, o que acontece em um deploy com sobreposição. A trava solta sozinha no fim da transação |
| Áreas atualizadas pelo slug, com **id estável**. Trilhas, etapas, recursos e faixas **recriados** a cada carga (ids novos) | Nada fora do banco depende de id de trilha, e a API não expõe ids |
| Ordem do recurso = posição na lista do arquivo | Evita uma segunda fonte de ordem |
| O campo `gratuito` do JSON é validado, mas **não gravado** | É derivado do `acesso` (gratuito, pago ou não confirmado). Os dois têm de ser coerentes, e a observação de itens pagos começa com "Pago" |
| Quiz recriado a cada carga | A API o identifica pela ordem da pergunta e da alternativa, e não por id |
| Conteúdo empacotado no JAR pelo `<resources>` do Maven, **sem filtering** | O filtro alteraria os bytes dos PDFs. A igualdade byte a byte é testada |

## 5. Banco de dados

| Migração | Conteúdo |
|---|---|
| `V1` | `area` e `trilha`. A ordem da trilha é única por área. As chaves estrangeiras não têm cascata |
| `V2` | `etapa`, `recurso` e `faixa_salarial`, com `ON DELETE CASCADE` a partir da trilha. `data_referencia` é sempre o dia 1 do mês |
| `V3` | `pergunta` e `alternativa`. Cascata só a partir da pergunta. As áreas **não** têm cascata. `CHECK` impede área principal igual à secundária |
| `V4` | `uso_assistente`: uma linha por dia, com o contador. Não guarda IP nem texto |

- Chaves `UUID` (`gen_random_uuid()`), `TEXT` para textos e enums gravados como texto (`@Enumerated(STRING)`).
- Uma migração aplicada nunca é editada. Em desenvolvimento, recria-se o banco.
- Uma área com trilhas não pode ser apagada, e uma trilha leva etapas, recursos e faixas ao ser apagada. As duas regras são testadas.

## 6. API

- REST somente leitura, sob `/api`, com **slug** como identificador público. Nenhuma resposta expõe ids.
- Respostas em *records* no pacote `dto.response`. As listas aninhadas são agrupadas sem N+1.
- **Formato de erro próprio** (veja a seção 13): `timestamp`, `status`, `error`, `message`, `errors` (opcional, com `field` e `message`) e `path`. O `@RestControllerAdvice` estende o `ResponseEntityExceptionHandler`, e nenhuma mensagem de exceção chega ao usuário.
- **Status usados:** 200, 400, 404, 413, 429 (com `Retry-After`), 503 e, para o inesperado, 500 com mensagem fixa.
- **CORS** configurável por `CORS_ORIGENS`.
- **OpenAPI:** springdoc, com os erros documentados à mão (`ErroResponse` serve só à documentação) e `servers: "/"` para o *Try it out* funcionar atrás do proxy HTTPS da Railway. Um teste de integração garante que cada resposta documentada continua existindo.

## 7. Quiz

| Decisão | Detalhe |
|---|---|
| Formato | 12 perguntas, 4 alternativas cada |
| Pontuação | +2 para a área principal da alternativa e +1 para a secundária |
| Resultado | Top 3 áreas, com uma `barra` relativa: a primeira vale 100 e as outras são proporcionais, arredondadas. Os pontos brutos nunca saem da API |
| Desempate (vale também entre o 3º e o 4º) | 1) mais pontos, 2) a área escolhida como principal em mais perguntas, 3) menor `ordemDesempate` |
| Sem nenhuma escolha | Barras zeradas, sem erro de divisão |
| Identificação da resposta | Pela `ordem` da pergunta e da alternativa, nunca por id nem pela posição na tela. O front embaralha as alternativas |
| Sem estado | O servidor não persiste respostas nem resultado, e cada requisição é independente |
| `GET /api/quiz` | Não expõe áreas nem pontos, para ninguém "dirigir" o resultado |
| Validação | Exatamente 12 respostas. O service confere contra o banco: pergunta existente, uma por pergunta e alternativa existente naquela pergunta. Todos os problemas voltam juntos em um 400 |
| Equilíbrio do conteúdo | Cada área é principal em 6 alternativas e secundária em 6. Isso é conferido pelo `validar.py` e confirmado no banco |

## 8. PDF dos roadmaps

- Os 36 PDFs (cerca de 8 MB) ficam empacotados no JAR e são servidos a partir do classpath, com `Content-Disposition: attachment`. O atributo `download` de um link é ignorado entre domínios, e o cabeçalho resolve.
- **O nome do arquivo é montado com o slug que veio do banco**, nunca com o texto da URL. Um valor estranho não acha trilha, dá 404 e nem toca em arquivo.
- Nunca se chama `getFile()`, que não funciona dentro de um JAR.

## 9. Assistente de IA

| Decisão | Detalhe |
|---|---|
| Provedor | Gemini, atrás da interface `ClienteIa`. Modelo `gemini-3.5-flash-lite` (configurável), escolhido por ter a cota diária folgada na camada gratuita. Reserva: `gemini-3.1-flash-lite` |
| Chave | Em variável de ambiente, enviada no **cabeçalho** (nunca na URL). Projeto do Google **sem faturamento**: o pior caso é esgotar a cota, e não uma cobrança |
| Sem chave configurada | A aplicação sobe normalmente e só o assistente responde 503 |
| Cliente | `RestClient`, com 5 s para conectar e 30 s de leitura. Toda falha do provedor vira `IaIndisponivelException`, e o motivo real vai só para o log, sem conversa, IP nem chave |
| Prompt de sistema | Montado **só no backend**, a partir do conteúdo **do banco** (`CriadorPrompt`). Não há como a mensagem do usuário entrar nele |
| Estrutura do prompt | Regras no início, conteúdo do catálogo (limitado a 24 mil caracteres) e um lembrete final. As regras e o lembrete nunca são cortados |
| Contexto da página | Só **slugs** de área, trilha e áreas do quiz, validados no banco. A rota não é enviada. Um slug inexistente vira 400 |
| Histórico | No máximo 6 mensagens, só como turnos da conversa. Falas do assistente no início são descartadas. Cada fala é cortada em 2000 caracteres (o DTO aceita até 4000, porque as respostas do assistente são longas). A mensagem atual tem até 500 caracteres |
| Sem transação | A chamada ao provedor pode levar até 30 s, e uma transação aberta nesse tempo seguraria uma conexão do banco parada |

**Limites de uso**

| # | Barreira | Valor | Armazenamento |
|---|---|---|---|
| 1 | Por visitante, por minuto | 5 | Memória (janela fixa) |
| 2 | Por visitante, por hora | 30 | Memória (janela fixa) |
| 3 | Global, por minuto | 12 | Memória (janela fixa) |
| 4 | Global, por dia | 400 | PostgreSQL, com incremento atômico |

- A **ordem** vai da barreira mais barata para a mais cara. Só a última consome a cota de verdade, então um visitante barrado nem chega ao banco.
- Os valores seguem os limites da camada gratuita do modelo (15 por minuto e 500 por dia), com folga para testes e novas tentativas.
- O contador diário é uma **única instrução SQL** (`INSERT ... ON CONFLICT ... WHERE mensagens < teto`), segura com requisições simultâneas. Foi provada com 40 requisições disputando 5 vagas.
- O "dia" do contador segue o fuso `America/Los_Angeles`, o mesmo corte da cota do Google.
- Uma reserva não é desfeita se a chamada ao provedor falhar, porque o Google também conta as tentativas que falham.
- O IP vem do `X-Real-IP` (a plataforma o sobrescreve na borda), é validado (só caracteres de IP, no máximo 45, sem consulta de DNS) e, se faltar ou for inválido, usa-se o endereço da conexão. Fica **só em memória**, nunca em banco ou log.
- **Por que o limite por visitante é só complementar:** IP por cabeçalho é imperfeito (compartilhamento de rede, VPN). A defesa de verdade é o teto global.

## 10. Segurança

| Risco | Onde é tratado |
|---|---|
| Abuso e custo do assistente | Quatro barreiras de uso e chave sem faturamento |
| *Prompt injection* | Prompt só no servidor, histórico apenas como conversa, contexto validado no banco, regras no início e lembrete no fim. As camadas se somam, e nenhuma é infalível sozinha |
| Corpos gigantes | `LimiteCorpoFiltro`: recusa com 413, antes de ler, o que passa de 100 KB pelo `Content-Length` em `/api` |
| *Path traversal* | Nome do PDF vem do banco (seção 8) |
| Entradas malformadas | Bean Validation (tamanhos, padrões de slug, itens nulos em listas), com `@NotNull` dentro dos `<>` das listas |
| Vazamento de detalhes | Mensagens fixas. O motivo real, quando existe, vai só para o log |
| Segredos | Só em variáveis de ambiente. A chave nunca vai ao Git nem à imagem |
| Actuator | Só `health`, com `show-details: never`. `env`, `beans`, `metrics`, `heapdump` e outros respondem 404 (testado) |
| Contêiner | Usuário `app`, sem privilégios, e a JVM encerra o processo em falta de memória |
| Dados pessoais | O servidor não guarda o IP, a conversa nem o resultado do quiz. O prompt manda o assistente não pedir dados pessoais |

## 11. Testes

| Tipo | Convenção | Banco | Comando |
|---|---|---|---|
| Unitários | Classes `*Test` (Surefire) | Não | `./mvnw test` |
| Integração | Classes `*IT` (Failsafe) | PostgreSQL real | `./mvnw verify` |

- Em 23/10/2026: **318** testes unitários (1 ignorado: a chamada real ao Gemini, que só roda com a chave no ambiente) e **89** de integração.
- **Por que não Testcontainers:** o Docker do ambiente de desenvolvimento rodava em uma VM com NAT e redirecionamento de portas fixas, e o Testcontainers usa portas aleatórias. A solução foi um banco `trilhatech_test` no Postgres local e, no CI, um PostgreSQL como serviço do workflow. Os testes são os mesmos nos dois.
- **Trava dos testes de integração:** o contexto do Spring não sobe se a URL do banco não terminar em `_test`, e antes de qualquer limpeza o teste confere `current_database()`. A mensagem de erro mostra só o nome do banco, nunca a URL.
- **Conteúdo real nos testes de API:** os valores esperados vêm dos próprios arquivos, e não de números escritos à mão.
- **Cliente de IA falso** (`@MockitoBean`) nos testes do assistente: nenhuma chamada real e nenhuma cota gasta.
- Um teste documenta que a **prontidão** do health check só vira "pronta" depois dos `ApplicationRunner`, de que a garantia de não mandar tráfego a uma instância sem catálogo depende.
- Um teste de **contrato OpenAPI** garante que os 7 endpoints e cada resposta de erro documentada continuam existindo.
- O interruptor `trilhatech.carga.habilitada` (padrão `true`) desliga o carregador nos testes de integração.

## 12. CI/CD e deploy

**CI** (`.github/workflows/ci.yml`, em todo Pull Request e todo push no `main`):

| Job | O que faz |
|---|---|
| Validar conteúdo | `validar.py` |
| Testes do backend | `./mvnw -B verify` com PostgreSQL como serviço. Senha descartável no workflow, porque o banco só existe durante o job |
| Imagem Docker sobe e responde | Constrói a imagem, sobe o contêiner, espera a **prontidão** e confere as 8 áreas, o usuário sem privilégios, a trilha, o PDF, o `/v3/api-docs` e o `/actuator/env` fechado |

**Deploy (Railway)**

- O serviço da API usa o `Dockerfile` da **raiz**: o `pom.xml` copia `../conteudo`, que fica fora de `backend/`, então o contexto de build precisa ser a raiz.
- O `mvnw` precisa de fim de linha LF e de permissão de execução. O `.gitattributes` e o Git cuidam disso, e o workflow ainda faz `chmod +x`.
- A porta vem da variável `PORT` (`server.port: ${PORT:8080}`).
- O PostgreSQL é um serviço da Railway, acessado pela **rede privada**, sem acesso público.
- A `DB_URL` é montada à mão com o prefixo `jdbc:`, porque a Railway entrega a URL como `postgresql://`. Usuário e senha usam referências ao serviço do banco (`${{Postgres.PGUSER}}` e `${{Postgres.PGPASSWORD}}`), e nenhuma senha é copiada.
- O *health check* aponta para `/actuator/health/readiness`, que inclui o banco e só fica "UP" depois da carga. A plataforma consulta até receber 200 antes de mandar tráfego.
- **Wait for CI** ligado: o deploy só sai depois dos checks passarem.

## 13. O que mudou em relação ao plano inicial

| Planejado | Final | Por quê |
|---|---|---|
| Erros no formato Problem Details | Formato próprio (`timestamp`, `status`, `error`, `message`, `errors`, `path`) | Escolha do projeto: um formato simples e único |
| Health check na Fase 8, com um indicador próprio | Actuator com **prontidão** que espera os `ApplicationRunner`, sem indicador próprio | O Spring já oferece o comportamento. Um teste garante a dependência |
| Testcontainers para testes de integração | Banco `trilhatech_test` e PostgreSQL de serviço no CI, com trava pelo nome do banco | Docker em VM com NAT e portas aleatórias (seção 11) |
| Contexto do chat com rota, área, trilha e resultado do quiz | Só slugs de área, trilha e quiz, validados no banco | A rota livre acabaria dentro do prompt, e é a porta clássica de *prompt injection* |
| Limites do assistente "em aberto" | 5 e 30 por visitante, 12 por minuto e 400 por dia | Calculados a partir dos limites reais da chave (15 por minuto e 500 por dia) |
| Teste de pontuação "depois" | Escrito junto com a calculadora | É a regra mais delicada e o erro seria silencioso |
| Corpo grande barrado só pelo `@Size` | Filtro por `Content-Length` (100 KB) | O `@Size` só vale depois de o JSON inteiro ser lido |
| Documentos atualizados só no fim | Este documento e o README, ao fim da Fase 8 | Pedido do autor |
| Swagger com o servidor padrão | `servers: "/"` | Atrás do proxy, o Swagger montava `http://` e o navegador bloqueava o *Try it out* |

## 14. Riscos conhecidos e pendências

| Item | Estado e próximo passo |
|---|---|
| Usuário do banco em produção | A API usa o usuário padrão da Railway. A troca por um usuário restrito (sem superusuário, com `CONNECT` e `USAGE, CREATE` no schema `public`) é uma tarefa curta |
| `X-Real-IP` com duas redes | Testado contra IP forjado (a Railway sobrescreve o cabeçalho), mas **não** com duas redes diferentes. Se a aplicação enxergasse o mesmo IP para todos, o limite por visitante virar um limite global (incômodo, sem risco de custo) |
| Limitador de janela fixa | Aceita uma rajada de até o dobro do limite na virada da janela. As chaves de janelas antigas só são limpas quando o mapa passa de 10 mil entradas |
| Corpo sem `Content-Length` | O filtro de tamanho só olha o cabeçalho. Uma requisição em pedaços (*chunked*) não é pega. Contar bytes durante a leitura fica como melhoria |
| Links dos recursos | Conferidos manualmente (`verificadoEm`). A validação automática não verifica se ainda respondem |
| Dados salariais | São estimativas, de fontes públicas, com o mês de referência. Não distinguem CLT de PJ, e o aviso vai em todas as trilhas |
| Camada gratuita do Gemini | Tem cota e termos próprios (confira os termos vigentes sobre o uso do conteúdo enviado). O assistente fica indisponível ao atingir o teto, sem afetar o resto do site |
| Memória do contêiner | A JVM usa até 75% da memória do contêiner e encerra em falta de memória. Se a plataforma reiniciar a aplicação por esse motivo, ajustar a memória ou as opções da JVM |
| Documentação dos erros | O `ErroResponse` é só para documentação, e o tratador monta um `Map`. Os dois podem divergir. Uma refatoração faria o tratador devolver o próprio record |
| Refatorações pequenas | `SincronizadorConteudo` com 8 dependências (dividir em áreas, trilhas e quiz), `textoVazio` repetido nos validadores, constante da pasta de PDFs repetida e aviso do Mockito sobre o agente dinâmico (configuração do build) |
| Cobertura | O JaCoCo ainda não foi configurado |
| Metadados do projeto | `name`, `description`, `licenses` e `developers` do `pom.xml` estão vazios. A licença do repositório está por definir |

## 15. Registro das verificações em produção

Feitas em 23/10/2026, em `https://trilha-tech-production.up.railway.app`.

**Verificado**

- Prontidão `UP` e `200` em `/api/areas`.
- `/actuator/env` responde 404.
- Detalhe de uma trilha completo.
- PDF com o mesmo hash SHA-256 do arquivo do repositório.
- Quiz com o mesmo resultado do ambiente local (100, 75 e 42, com todas as respostas na primeira alternativa).
- Assistente respondendo em português, com a chave configurada na Railway.
- Limite por visitante: cinco respostas `200` e depois `429`.
- IP forjado por `X-Real-IP` continua contado como o mesmo visitante.
- Swagger UI funcionando, com o *Try it out* corrigido por `servers: "/"`.
- Deploy automático depois do CI (*Wait for CI*).

**Não verificado**

- Visitantes em redes diferentes sendo contados separadamente (precisa de duas redes).
- Troca do usuário do banco por um restrito.
