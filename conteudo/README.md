# Conteúdo do Trilha Tech

Conteúdo estático da plataforma **Trilha Tech** (orientação de carreira em TI para iniciantes): 8 áreas, 36 trilhas, roadmaps com recursos de estudo e faixa salarial estimada, e os PDFs para download.

Esta pasta **não tem código de servidor**. Ela guarda o conteúdo que o backend (Java/Spring Boot, na pasta `../backend`) vai consumir.

## Conteúdo

```
catalogo.json        áreas (com textos) e trilhas em ordem de exibição
quiz.json            as 12 perguntas do quiz
trilhas/<slug>.json  uma trilha completa (etapas, recursos, salários)
pdfs/roadmap-<slug>.pdf   PDF pronto de cada trilha
pdfs/manifest.json   hash do conteúdo de cada PDF (detecta PDF desatualizado)
docs/contrato-roadmap.md  descrição de cada campo do JSON
ferramentas/         validação e geração dos PDFs
```

### Áreas

| Nº | Área | Trilhas |
|---|---|---|
| 01 | Desenvolvimento de Software | 7 |
| 02 | Dados e IA | 5 |
| 03 | Infraestrutura, Cloud e DevOps | 4 |
| 04 | Segurança | 4 |
| 05 | Qualidade e Testes | 4 |
| 06 | Suporte Técnico e Redes | 3 |
| 07 | UX/UI Design | 4 |
| 08 | Gestão e Produtos de TI | 5 |

## Como o backend deve usar

1. Carregar `trilhas/*.json` no banco (migração Flyway) e servir `pdfs/` pelo slug.
2. O conteúdo vira dados no PostgreSQL; os JSONs continuam sendo a fonte editável.
3. Servir o PDF pelo `slug` da trilha.
4. **Não expor o campo `pendencias`** na API pública. É nota interna.

Detalhes dos campos: [docs/contrato-roadmap.md](docs/contrato-roadmap.md).

## Ferramentas

Requisitos: Python 3, Node.js e Playwright com Chromium (`pip install playwright`).

```bash
python3 ferramentas/validar.py                       # confere JSONs, slugs e PDFs

cd ferramentas && npm ci                             # só na primeira vez (fontes)
python3 gerar_pdf.py <slug>                          # refaz o PDF de uma trilha
```

Depois de mudar um JSON de trilha: refazer o PDF (`gerar_pdf.py <slug>`), rodar `python3 ferramentas/manifesto.py` e `python3 ferramentas/validar.py`. Se o nome, o nível de entrada ou a duração mudou, atualize também o `catalogo.json`.

## Regras

- Todo item pago é marcado (`gratuito: false`, observação começando com `Pago`).
- Salários são estimativas (Glassdoor). `dadosLimitados: true` indica poucos relatos.
- Os links foram conferidos em 16/10/2026. Páginas mudam: revisar de tempos em tempos.
