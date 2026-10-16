# Trilha Tech

> Plataforma web que ajuda quem está começando em TI a descobrir **qual área seguir**.

Muita gente quer entrar na área de tecnologia, mas não sabe por onde começar. O Trilha Tech mostra as áreas, as trilhas dentro de cada uma, o caminho de estudo etapa por etapa e quanto o mercado costuma pagar, sem exigir cadastro.

**Status:** em construção. O conteúdo (36 roadmaps) está pronto e o backend está sendo desenvolvido por fases.

## O que a plataforma faz (versão 1)

- Lista **8 áreas de TI** e **36 trilhas**, cada uma com seu texto.
- **Roadmap** de cada trilha: etapas em ordem, para quem parte do zero, com recursos de estudo (grátis e pagos, em português e inglês) e projeto prático por etapa.
- **Faixa salarial estimada** (Júnior, Pleno e Sênior), sempre com fonte e data.
- **Download do roadmap em PDF**.
- **Quiz opcional de 12 perguntas** que recomenda áreas (nunca vagas). O resultado fica só no navegador.
- **Assistente de IA** em todas as páginas, que recebe o contexto de onde a pessoa está.
- Sem login e sem cadastro.

## Como o projeto está organizado

```
backend/    API REST em Java e Spring Boot (em construção)
conteudo/   as 36 trilhas em JSON, os PDFs e as ferramentas de validação
docs/       decisões de projeto e regras do quiz
```

O front-end vai ficar em um repositório próprio.

## Tecnologias

| Parte | Tecnologia |
|---|---|
| Linguagem e framework | Java, Spring Boot, Maven |
| API | REST com JSON, documentada com Swagger/OpenAPI |
| Banco de dados | PostgreSQL, com migrações Flyway e Spring Data JPA |
| Testes | JUnit e Mockito |
| Entrega | Docker e GitHub Actions |
| IA do assistente | Gemini, atrás de uma interface própria para poder trocar de provedor |
| Hospedagem | Backend e banco na Railway; front-end na Vercel |

## Decisões que valem destacar

- **Recomendar áreas, não vagas.** O quiz e o assistente orientam o começo da exploração; não substituem orientação profissional.
- **Salário é estimativa.** Cada faixa mostra a fonte e o mês de referência, e trilhas com poucos dados são marcadas como "dados limitados".
- **A chave da IA nunca sai do servidor.** O prompt é montado só no backend e o contexto enviado pelo navegador é validado.
- **Uso limitado sem login.** Limite por IP e teto diário global para proteger o custo; se o teto estourar, só o assistente sai do ar.
- **Conteúdo versionado.** Cada recurso de estudo tem a data em que o link foi conferido.

Mais detalhes em [`docs/decisoes-fase-0.md`](docs/decisoes-fase-0.md) e nas regras do quiz em [`docs/quiz.md`](docs/quiz.md).

## Origem

O Trilha Tech nasceu como Projeto Integrador do curso de Análise e Desenvolvimento de Sistemas da Fatec Botucatu, feito em grupo (Breno, Esdras, Laura, Lorena e Salvador). A implementação do site real é um projeto de portfólio de [Breno](https://github.com/obrenoxs).

## Como rodar

Ainda não há o que rodar: o backend está na fase inicial. Quando o projeto subir, esta seção terá o passo a passo (Docker, variáveis de ambiente e comandos de teste).

Para validar o conteúdo hoje:

```bash
python3 conteudo/ferramentas/validar.py
```
