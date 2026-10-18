# Decisões da Fase 1 (desenho) — Trilha Tech backend

Fonte de verdade do conteúdo: pasta `conteudo/` (JSON + PDFs). Contrato em `conteudo/docs/contrato-roadmap.md`.

## 1. Convenções do projeto
- Maven, Java, Spring Boot, API REST em JSON, PostgreSQL, Flyway (só estrutura das tabelas).
- groupId/pacote base: `io.github.obrenoxs.trilhatech`. artifactId: `trilha-tech-api`.
- Repositório único: `backend/`, `conteudo/`, `docs/`, README.
- Hospedagem: backend e PostgreSQL na Railway; front-end na Vercel.
- Sem login, sem Spring Security na v1. CORS por configuração (origem do front vinda de variável de ambiente).
- Erros no formato Problem Details (RFC 9457).
- Ids internos, `pendencias` e o mapa de pontuação do quiz nunca saem pela API.

## 2. Carga de conteúdo
- Um carregador em Java lê `catalogo.json`, `trilhas/<slug>.json` e `quiz.json` (empacotados no JAR) e sincroniza o banco nos dois sentidos: insere/atualiza o que está nos arquivos e apaga o que saiu.
- Chave natural: `slug` de área e de trilha. Filhos (etapas, recursos, faixas) são substituídos por inteiro a cada trilha, na mesma transação.
- Proteções: ler e validar tudo antes de tocar no banco (fail-fast); recusar catálogo com menos de 36 trilhas ou quiz fora de 12x4; transação única; bloqueio contra duas instâncias rodando a carga ao mesmo tempo.
- Ordem da carga: áreas, trilhas e filhos, quiz. Remoção na ordem inversa. Área referenciada pelo quiz não pode ser removida (a carga falha).
- Divisão da validação:
  - CI (`validar.py`): estrutura, catálogo, quiz (equilíbrio 6/6), hash do PDF (`pdfs/manifest.json`) e coerência dos dados. Não confere links.
  - Carregador Java: só o que protege o banco (estrutura, enums, referências entre arquivos, `acesso` coerente com `gratuito`, soma das semanas = `duracaoTotalSemanas`, `minimo <= maximo`, catálogo e quiz com as contagens esperadas).
- Sem limite máximo de etapas por trilha. Exige no mínimo 1 etapa e `ordem` sem buracos.
- Links não são verificados automaticamente na v1. A revalidação periódica fica para a fase 2 do produto.

## 3. Modelo de dados

| Tabela | Únicos / chave natural | Observações |
|---|---|---|
| area | slug, numero, ordem_desempate | ordem_exibicao = posição no catálogo; descricao; como_e_trabalhar |
| trilha | slug; (area_id, ordem_na_area) | resumo, texto_trilha, para_quem_e, horas_por_semana, avisos_salario, conteudo_conferido_em, nivel_entrada |
| etapa | (trilha_id, ordem) | nivel, duracao_semanas, descricao, projeto_pratico (nulo permitido) |
| recurso | (etapa_id, ordem) | titulo, url, tipo, idioma, acesso, observacao, verificado_em. Sem unicidade global de URL. Não guarda `gratuito`. |
| faixa_salarial | (trilha_id, nivel) | minimo, maximo, valor_referencia, tipo_referencia (texto), fonte (texto), data_referencia, dados_limitados, observacao. CHECK minimo <= maximo |
| pergunta | ordem | texto |
| alternativa | (pergunta_id, ordem) | texto, area_principal_id, area_secundaria_id. CHECK principal <> secundária |

- Enums gravados como texto, pelo nome da constante (ex.: DOCUMENTACAO): tipo de recurso (9 valores), nivel de entrada da trilha, nivel da etapa, nivel salarial, idioma, acesso (GRATUITO, PAGO, NAO_CONFIRMADO).
- Nível da trilha (Iniciante/Intermediário/Avançado) e nível da etapa (Básico/Intermediário/Avançado) são enums distintos.
- Duração total da trilha é derivada (soma das etapas), nunca gravada.
- FKs dos filhos da trilha com ON DELETE CASCADE. FKs para área sem cascade.
- Fora do banco: `pendencias`, resultado do quiz, histórico do chat, usuários. Tabela do contador de uso da IA fica para a Fase 6.

## 4. Endpoints da v1

| Método e rota | Resposta | Fase |
|---|---|---|
| GET /api/areas | 8 áreas: slug, número, nome, descrição, quantidade de trilhas | 3 |
| GET /api/areas/{slug} | Área com comoETrabalhar e trilhas em ordem (slug, nome, resumo, nível de entrada, duração total derivada) | 3 |
| GET /api/trilhas/{slug} | Trilha completa: textos, etapas com recursos, faixas salariais, avisosSalario, conteudoConferidoEm, duração total derivada | 3 |
| GET /api/trilhas/{slug}/pdf | PDF como download (Content-Disposition: attachment) | 5 |
| GET /api/quiz | 12 perguntas com ordem e texto das alternativas, sem áreas e sem pontos | 4 |
| POST /api/quiz/resultado | Entrada: 12 respostas (ordem da pergunta, ordem da alternativa). Saída: top 3 com posição, área (slug, nome) e barra 0-100. Sem pontos brutos. Não grava nada. | 4 |
| POST /api/assistente/mensagens | Desenho na Fase 6 | 6 |

- O PDF nunca é localizado pelo texto da URL: o slug é conferido no banco e só então se abre `pdfs/roadmap-<slug>.pdf` (evita path traversal).
- O front embaralha as alternativas do quiz.
- Health check para a Railway na Fase 2.

## 5. Regras de negócio
1. Todo salário sai com fonte, data de referência, `dadosLimitados` e `avisosSalario`.
2. Recurso pago ou não confirmado sai marcado pelo `acesso`.
3. O quiz recomenda áreas, nunca vagas, e não persiste nada.
4. Respostas do quiz: exatamente uma por pergunta, só alternativas existentes; senão 400 com os campos inválidos.
5. Pontuação: +2 área principal, +1 secundária. Resultado: top 3 por pontos brutos, barra relativa (primeira = 100).
6. Desempate (vale para qualquer posição do top 3): (1) mais pontos; (2) área principal em mais perguntas; (3) `ordem_desempate`.
7. Slug inexistente devolve 404.
8. Áreas recebidas como contexto do assistente são validadas contra o banco.

## 6. Estrutura de pacotes (por funcionalidade, camadas dentro de cada uma)
Base: `io.github.obrenoxs.trilhatech`

| Pacote | Conteúdo |
|---|---|
| conteudo | api (controllers, DTOs record), service, domain (entidades, enums, repositórios) |
| quiz | mesmas camadas; pontuação e desempate em classe pura, sem Spring |
| carga | leitura dos arquivos, validação de proteção, sincronização com o banco |
| assistente | Fase 6: interface do cliente de IA, implementação do Gemini separada, limite de uso |
| compartilhado | Problem Details, CORS, propriedades de configuração |

Dependências: `quiz` usa `conteudo`; `conteudo` nunca usa `quiz`; `carga` usa os dois; `assistente` usa `conteudo` e `quiz`. Sem ciclos. Entidade JPA nunca sai pela API.

## 7. Plano das fases
- **Fase 2 (Base):** projeto no Initializr em `backend/`; configuração por variáveis e perfis; PostgreSQL de desenvolvimento; migração Flyway; Problem Details; CORS; health check; cópia de `../conteudo` para o JAR.
- **Fase 3 (Conteúdo):** entidades e repositórios; carregador (leitura e validação, depois sincronização transacional com bloqueio); endpoints de áreas e trilhas; testes do carregador.
- **Fase 4 (Quiz):** entidades e carga; pontuação e desempate com testes primeiro; endpoints do quiz.
- **Fase 5 (PDF):** servir o PDF do JAR; conferir na carga que cada trilha tem PDF.
- **Fase 6 (IA):** chat, contexto validado, chave protegida, limite por IP + teto global diário (contador no PostgreSQL), tratamento do erro de cota.
- **Fase 7 (Qualidade):** testes unitários e de integração, cobertura, Swagger/OpenAPI.
- **Fase 8 (Entrega):** Docker, GitHub Actions (inclui `validar.py`), deploy na Railway.
- **Fase 9 (Front-end):** só depois do backend finalizado e com aval explícito.

## 8. Fora do escopo da v1 (candidatos à fase 2 do produto)
Login, favoritos, área administrativa, revalidação automática de links, cache de leitura.

## 9. Em aberto (não bloqueia a Fase 2)
- Valores do limite por IP e do teto global diário.
- Limites reais da chave do Gemini no AI Studio (chave em projeto sem faturamento).
- Direção visual final (votação do grupo) e atualização do Figma com os dados do JSON.
- Versões de Java e Spring Boot (decidimos no início da Fase 2).
