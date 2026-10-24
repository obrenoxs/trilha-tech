import { defineConfig, devices } from "@playwright/test";

// Por padrão sobe o `npm run dev`. Com E2E_BASE_URL (por exemplo, um `npm run start` depois do
// `npm run build` em http://localhost:5173) testa o site como ele vai para produção. A API só aceita
// as origens localhost:3000 e localhost:5173 (CORS), então use uma dessas portas.
const baseURL = process.env.E2E_BASE_URL ?? "http://localhost:3000";

export default defineConfig({
  testDir: "./e2e",
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  reporter: "list",
  use: {
    baseURL,
    trace: "on-first-retry",
    // Sem animações por padrão: testes mais estáveis (nada se mexe enquanto o Playwright clica).
    // e2e/motion.spec.ts liga o movimento de volta onde precisa conferir que ele existe.
    reducedMotion: "reduce",
  },
  projects: [{ name: "chromium", use: { ...devices["Desktop Chrome"] } }],
  webServer: process.env.E2E_BASE_URL
    ? undefined
    : {
        command: "npm run dev",
        url: "http://localhost:3000",
        reuseExistingServer: !process.env.CI,
      },
});
