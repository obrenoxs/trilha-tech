// Gera os tipos TypeScript a partir do esquema OpenAPI do backend (somente tipos, sem cliente).
import { spawnSync } from "node:child_process";

const url =
  process.env.OPENAPI_URL ??
  "https://trilha-tech-production.up.railway.app/v3/api-docs";

const result = spawnSync(
  "npx",
  ["openapi-typescript", url, "-o", "lib/api/schema.d.ts"],
  { stdio: "inherit", shell: true },
);

process.exit(result.status ?? 1);
