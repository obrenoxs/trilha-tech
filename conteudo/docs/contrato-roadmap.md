# Contrato dos arquivos de trilha

Cada arquivo em `trilhas/<slug>.json` descreve **uma trilha**. O nome do arquivo é sempre igual ao `trilha.slug`.
O PDF correspondente fica em `pdfs/roadmap-<slug>.pdf`. O `catalogo.json` lista áreas e trilhas (sem as etapas) e serve de índice.

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
| `area.numero` | string | `"01"` a `"08"`. |
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
| `gratuito` | booleano | `false` = pago **ou** preço não confirmado. Veja `observacao`. |
| `verificadoEm` | string `AAAA-MM-DD` | Data em que o link foi conferido. |
| `observacao` | string, opcional | Se começa com `Pago`, é um item pago. Se `gratuito` é `false` e não começa com `Pago`, o preço ou a licença não foi confirmado. |

### `faixaSalarial[]` (sempre 3 itens: Júnior, Pleno, Sênior)

| Campo | Tipo | Observação |
|---|---|---|
| `nivel` | `"Júnior"` \| `"Pleno"` \| `"Sênior"` | |
| `minimo`, `maximo`, `valorReferencia` | inteiro (R$/mês) | Valores brutos, arredondados em milhares. |
| `tipoReferencia` | string | Ex.: `média do salário base`. |
| `fonte` | string | Ex.: `Glassdoor`. |
| `dataReferencia` | string `AAAA-MM` | |
| `dadosLimitados` | booleano | `true` = poucos relatos ou cargo parecido usado como aproximação. É um aviso, não um erro. |
| `observacao` | string | Explica a origem do número. |

## Regras de negócio que o conteúdo já garante

- O salário é **estimativa de mercado**; sempre mostrar `avisosSalario` ao lado.
- O app **recomenda áreas, não vagas**.
- Recursos pagos devem aparecer marcados como pagos.

## Como validar

`python3 ferramentas/validar.py` confere o formato, os slugs únicos, a existência dos PDFs e o `catalogo.json`.
