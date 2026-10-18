# Contrato dos arquivos de trilha

O conteúdo tem três arquivos de dados e uma pasta de PDFs:

| Arquivo | O que é |
|---|---|
| `trilhas/<slug>.json` | **Uma trilha** completa. O nome do arquivo é sempre igual ao `trilha.slug`. |
| `catalogo.json` | As 8 áreas (com seus textos) e a lista ordenada de trilhas de cada uma. |
| `quiz.json` | As 12 perguntas do quiz. |
| `pdfs/roadmap-<slug>.pdf` | O PDF de cada trilha. |
| `pdfs/manifest.json` | Hash do conteúdo de cada trilha no momento em que o PDF foi feito. |

## catalogo.json

```json
{ "versao": "1.0", "areas": [ {
  "numero": "01", "slug": "desenvolvimento-de-software", "nome": "Desenvolvimento de Software",
  "ordemDesempate": 1,
  "descricao": "o que é a área",
  "comoETrabalhar": "como é o dia a dia",
  "trilhas": [ { "ordem": 1, "slug": "java-e-spring", "nome": "Java e Spring", "nivelEntrada": "Iniciante", "duracaoTotalSemanas": 36 } ]
} ] }
```

- A ordem das áreas no arquivo é a ordem de exibição. `ordemDesempate` é um campo separado (usado no desempate do quiz) e hoje coincide com o `numero`.
- `trilhas[].ordem` é a ordem de exibição das trilhas dentro da área.
- `nome`, `nivelEntrada` e `duracaoTotalSemanas` repetem o que está no arquivo da trilha e a validação confere que batem.

## quiz.json

```json
{ "versao": "1.0", "perguntas": [ { "ordem": 1, "texto": "…", "alternativas": [
  { "ordem": 1, "texto": "…", "areaPrincipal": "desenvolvimento-de-software", "areaSecundaria": "ux-ui-design" } ] } ] }
```

- 12 perguntas, 4 alternativas cada. `areaPrincipal` (+2) e `areaSecundaria` (+1) usam o `slug` da área. A pontuação é regra do código, não dado.
- Cada área é principal em exatamente 6 alternativas e secundária em exatamente 6, e cada pergunta cobre 4 áreas principais diferentes. A validação confere isso.
- A ordem das alternativas deve ser embaralhada a cada exibição pelo front. Perguntas e alternativas são identificadas por `ordem` (pergunta, alternativa), nunca por id do banco, que muda a cada carga.

## Estrutura

```json
{
  "versao": "1.0",
  "geradoEm": "2026-10-16",
  "trilha": { ... },
  "etapas": [ ... ],
  "faixaSalarial": [ ... ],
  "avisosSalario": "texto",
  "pendencias": [ "texto" ]
}
```

| Campo | Tipo | Observação |
|---|---|---|
| `versao` | string | Versão do formato. Mude só se o formato mudar. |
| `geradoEm` | string `AAAA-MM-DD` | Data em que o conteúdo foi conferido. |
| `avisosSalario` | string | Aviso que deve aparecer junto com qualquer faixa salarial. |
| `pendencias` | lista de string | **Uso interno** (notas de revisão da equipe). A API pública não deve expor. |

### `trilha`

| Campo | Tipo | Observação |
|---|---|---|
| `slug` | string | Identificador único, minúsculo com hífens. Usado nas URLs e no nome do PDF. |
| `nome` | string | Nome exibido. |
| `resumo` | string, 30 a 110 caracteres | Uma linha para a lista de trilhas da área. Não aparece no PDF. |
| `area.numero` | string | `"01"` a `"08"`. |
| `area.slug` | string | Identificador estável da área (ex.: `desenvolvimento-de-software`). Nunca muda, mesmo que o nome mude. |
| `area.nome` | string | Nome da área. |
| `nivelEntrada` | string | `Iniciante`, `Intermediário` ou `Avançado`. Machine Learning e SRE são `Avançado`. |
| `textoTrilha` | string | Descrição curta da trilha. |
| `paraQuemE` | string | Para quem a trilha serve. |
| `horasPorSemana` | inteiro | Estimativa usada para calcular as semanas. |
| `duracaoTotalSemanas` | inteiro | Soma de `duracaoSemanas` das etapas. |

### `etapas[]` (ordem importa)

| Campo | Tipo | Observação |
|---|---|---|
| `ordem` | inteiro | Começa em 1, sem buracos. |
| `titulo` | string | |
| `nivel` | `"Básico"` \| `"Intermediário"` \| `"Avançado"` | |
| `duracaoSemanas` | inteiro | |
| `descricao` | string | |
| `projetoPratico` | string | O que a pessoa deve construir na etapa. |
| `recursos` | lista | Nunca vazia. |

### `recursos[]`

| Campo | Tipo | Observação |
|---|---|---|
| `titulo` | string | |
| `url` | string | Sempre `https://`. |
| `tipo` | string | `documentação`, `curso`, `livro`, `formação`, `certificação`, `tutorial`, `guia`, `vídeo`, `ferramenta`. |
| `idioma` | `"PT"` \| `"EN"` | |
| `gratuito` | booleano | `true` só quando `acesso` é `gratuito`. Mantido por compatibilidade; leia `acesso`. |
| `acesso` | `"gratuito"` \| `"pago"` \| `"nao_confirmado"` | `nao_confirmado` = a página não informa o preço ou a licença. A validação garante que `acesso`, `gratuito` e a `observacao` não se contradizem. |
| `verificadoEm` | string `AAAA-MM-DD` | Data em que o link foi conferido. |
| `observacao` | string, opcional | Texto de apoio. Itens pagos começam com `Pago`. |

### `faixaSalarial[]` (sempre 3 itens: Júnior, Pleno, Sênior)

| Campo | Tipo | Observação |
|---|---|---|
| `nivel` | `"Júnior"` \| `"Pleno"` \| `"Sênior"` | |
| `minimo`, `maximo`, `valorReferencia` | inteiro (R$/mês) | Valores brutos, arredondados em milhares. |
| `tipoReferencia` | string, texto livre | Hoje: `média do salário base` (maioria), `média` ou `mediana`. |
| `fonte` | string, texto livre | Ex.: `Glassdoor` ou `Glassdoor (Analista de Testes Júnior)` quando um cargo parecido foi usado. Pode ser outra fonte (Indeed, Robert Half). |
| `dataReferencia` | string `AAAA-MM` | |
| `dadosLimitados` | booleano | `true` = poucos relatos ou cargo parecido usado como aproximação. É um aviso, não um erro. |
| `observacao` | string | Explica a origem do número. |

## Regras de negócio que o conteúdo já garante

- O salário é **estimativa de mercado**; sempre mostrar `avisosSalario` ao lado.
- O app **recomenda áreas, não vagas**.
- Recursos pagos devem aparecer marcados como pagos.
- Os salários não informam o regime de contratação (CLT ou PJ).
- Se o JSON de uma trilha mudar, o PDF dela precisa ser refeito: a validação compara o hash do conteúdo com `pdfs/manifest.json` e acusa PDF desatualizado.

## Como validar

`python3 ferramentas/validar.py` confere trilhas, catálogo, quiz, PDFs e o manifesto. Retorna erro (código 1) se algo estiver inconsistente, então serve no CI.
