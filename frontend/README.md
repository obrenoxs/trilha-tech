# Trilha Tech: front-end

Next.js (App Router) + TypeScript estrito + Tailwind CSS. Regras do projeto em [`CLAUDE.md`](./CLAUDE.md) e plano em [`../docs/plano-frontend.md`](../docs/plano-frontend.md).

## Como rodar

Requer Node 24 e npm.

```bash
cp .env.example .env.local   # ajuste NEXT_PUBLIC_API_URL se necessário
npm install
npx playwright install chromium   # só uma vez, para e2e e screenshots
npm run dev                       # http://localhost:3000
```

## Scripts

| Comando | O que faz |
|---|---|
| `npm run dev` | Servidor de desenvolvimento |
| `npm run build` | Build de produção |
| `npm run lint` | ESLint |
| `npm run typecheck` | TypeScript sem emitir arquivos |
| `npm run test` | Testes unitários (Vitest) |
| `npm run e2e` | Testes de ponta a ponta (Playwright) |
| `npm run gen:api` | Gera os tipos da API (ainda esqueleto; fase 4) |

## Variáveis de ambiente

| Variável | Uso |
|---|---|
| `NEXT_PUBLIC_API_URL` | URL base da API (pública, chamada direto do navegador) |

Nunca coloque segredos aqui: `.env*` (exceto `.env.example`) fica fora do git.
