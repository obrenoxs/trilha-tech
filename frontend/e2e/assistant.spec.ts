import { expect, test, type Page, type Route } from "@playwright/test";

// Nenhum destes testes chama a API real do assistente (gasta cota de IA e o limite por IP):
// toda requisição a /api/assistente/** é interceptada. O que não tiver mock explícito é abortado.

type ChatBody = {
  mensagem: string;
  historico: { papel: string; texto: string }[];
  contexto?: { areaSlug?: string; trilhaSlug?: string; resultadoQuiz?: string[] };
};

const ASSISTANT_URL = "**/api/assistente/mensagens";

const jsonError = (status: number) => ({
  status,
  contentType: "application/json",
  body: JSON.stringify({
    timestamp: "2026-10-24T10:00:00",
    status,
    error: "ERRO TECNICO",
    message: "MENSAGEM TECNICA DO SERVIDOR",
    path: "/api/assistente/mensagens",
  }),
});

/** Registra os mocks e devolve as requisições recebidas. `handler` decide a resposta de cada uma. */
async function mockAssistant(page: Page, handler: (route: Route, call: number) => Promise<void>) {
  const bodies: ChatBody[] = [];
  await page.route("**/api/assistente/**", (route) => route.abort());
  await page.route(ASSISTANT_URL, async (route) => {
    bodies.push(route.request().postDataJSON() as ChatBody);
    await handler(route, bodies.length);
  });
  return bodies;
}

const reply = (route: Route, text: string) =>
  route.fulfill({
    status: 200,
    contentType: "application/json",
    body: JSON.stringify({ resposta: text }),
  });

async function openPanel(page: Page) {
  await page.getByRole("button", { name: "Perguntar à IA" }).click();
  const panel = page.getByRole("dialog", { name: "Assistente Trilha Tech" });
  await expect(panel).toBeVisible();
  return panel;
}

async function ask(page: Page, text: string) {
  await page.getByLabel("Sua mensagem").fill(text);
  await page.getByRole("button", { name: "Enviar" }).click();
}

test("429: mostra a contagem do Retry-After e bloqueia o envio até acabar", async ({ page }) => {
  const bodies = await mockAssistant(page, (route) =>
    // O backend expõe o Retry-After para o navegador (Access-Control-Expose-Headers).
    route.fulfill({
      ...jsonError(429),
      headers: { "Retry-After": "3", "Access-Control-Expose-Headers": "Retry-After" },
    }),
  );
  await page.goto("/trilhas/java-e-spring");
  const panel = await openPanel(page);
  // O foco vai para o campo de texto ao abrir.
  await expect(panel.getByLabel("Sua mensagem")).toBeFocused();

  await ask(page, "E qual o salário de quem trabalha com Spring?");

  await expect(panel.getByText(/Tente de novo em 0:0\d/)).toBeVisible();
  await expect(panel.getByText(/Você enviou várias mensagens/)).toBeVisible();
  await expect(page.getByLabel("Sua mensagem")).toBeDisabled();
  await expect(page.getByRole("button", { name: "Enviar" })).toBeDisabled();
  // Sem texto técnico, e a pergunta continua na tela.
  await expect(panel).not.toContainText("MENSAGEM TECNICA");
  await expect(panel).not.toContainText("429");
  await expect(panel).toContainText("E qual o salário de quem trabalha com Spring?");
  expect(bodies).toHaveLength(1);

  // Quando o tempo acaba, o envio libera sozinho e o aviso some.
  await expect(page.getByLabel("Sua mensagem")).toBeEnabled({ timeout: 8000 });
  await expect(panel.getByText(/Tente de novo em/)).toHaveCount(0);
  await expect(page.getByRole("button", { name: "Enviar" })).toBeEnabled();
  await expect(panel).toContainText("E qual o salário de quem trabalha com Spring?");
});

test("429 sem Retry-After legível usa 60 segundos", async ({ page }) => {
  await mockAssistant(page, (route) => route.fulfill(jsonError(429)));
  await page.goto("/");
  const panel = await openPanel(page);

  await ask(page, "oi");

  await expect(panel.getByText(/Tente de novo em 1:00|Tente de novo em 0:59/)).toBeVisible();
  await expect(page.getByLabel("Sua mensagem")).toBeDisabled();
});

test("503: avisa que o assistente está fora do ar, o site segue e dá para tentar de novo", async ({
  page,
}) => {
  const bodies = await mockAssistant(page, (route, call) =>
    call === 1 ? route.fulfill(jsonError(503)) : reply(route, "Agora deu certo."),
  );
  await page.goto("/trilhas/java-e-spring");
  const panel = await openPanel(page);

  await ask(page, "Qual projeto posso fazer na etapa de Spring Boot?");

  await expect(panel.getByText("Assistente fora do ar")).toBeVisible();
  await expect(panel.getByText(/O resto do site continua funcionando/)).toBeVisible();
  await expect(panel).not.toContainText("MENSAGEM TECNICA");
  await expect(panel).not.toContainText("503");
  // O campo continua livre e o resto do site segue funcionando por trás do painel.
  await expect(page.getByLabel("Sua mensagem")).toBeEnabled();
  await expect(page.getByRole("heading", { level: 1 })).toBeVisible();

  await panel.getByRole("button", { name: "Tentar de novo" }).click();

  await expect(panel.getByText("Agora deu certo.")).toBeVisible();
  await expect(panel.getByText("Assistente fora do ar")).toHaveCount(0);
  // A bolha da pergunta é reaproveitada, e a nova tentativa reenvia o mesmo texto.
  await expect(panel.getByText("Qual projeto posso fazer na etapa de Spring Boot?")).toHaveCount(1);
  expect(bodies).toHaveLength(2);
  expect(bodies[1].mensagem).toBe(bodies[0].mensagem);
  expect(bodies[1].historico).toEqual([]);
});

test("a conversa sobrevive à navegação e o contexto muda de página para página", async ({
  page,
}) => {
  const bodies = await mockAssistant(page, (route, call) => reply(route, `resposta ${call}`));
  await page.goto("/areas/desenvolvimento-de-software");
  let panel = await openPanel(page);
  await expect(panel.getByText(/Contexto: Área Desenvolvimento de Software/i)).toBeVisible();

  await ask(page, "primeira pergunta");
  await expect(panel.getByText("resposta 1")).toBeVisible();
  expect(bodies[0].contexto).toEqual({ areaSlug: "desenvolvimento-de-software" });
  expect(bodies[0].historico).toEqual([]);

  // Esc fecha o painel e devolve o foco ao botão que o abriu.
  await page.keyboard.press("Escape");
  await expect(panel).toBeHidden();
  const launcher = page.getByRole("button", { name: "Perguntar à IA" });
  await expect(launcher).toBeFocused();

  // Navega (sem recarregar) para uma trilha da área.
  await page.getByRole("link", { name: /Ver roadmap de/ }).first().click();
  await expect(page).toHaveURL(/\/trilhas\/[a-z0-9-]+$/);
  const trilhaSlug = new URL(page.url()).pathname.split("/").pop()!;

  panel = await openPanel(page);
  await expect(panel).toContainText("primeira pergunta");
  await expect(panel).toContainText("resposta 1");
  await expect(panel.getByText(/Contexto: Trilha/i)).toBeVisible();

  await ask(page, "segunda pergunta");
  await expect(panel.getByText("resposta 2")).toBeVisible();
  expect(bodies[1].contexto).toEqual({ trilhaSlug });
  expect(bodies[1].historico).toEqual([
    { papel: "USUARIO", texto: "primeira pergunta" },
    { papel: "ASSISTENTE", texto: "resposta 1" },
  ]);

  // Recarregar zera a conversa (ela só existe em memória).
  await page.reload();
  panel = await openPanel(page);
  await expect(panel).not.toContainText("primeira pergunta");
  expect(await page.evaluate(() => JSON.stringify({ ...localStorage }))).not.toContain(
    "primeira pergunta",
  );
});

test("limite de 500 caracteres: contador e envio bloqueado acima disso", async ({ page }) => {
  const bodies = await mockAssistant(page, (route) => reply(route, "tudo certo"));
  await page.goto("/");
  const panel = await openPanel(page);
  const input = page.getByLabel("Sua mensagem");
  const send = page.getByRole("button", { name: "Enviar" });

  await input.fill("a".repeat(500));
  await expect(panel.getByText("500/500")).toBeVisible();
  await expect(send).toBeEnabled();

  await input.fill("a".repeat(501));
  await expect(panel.getByText(/501\/500/)).toBeVisible();
  await expect(panel.getByText(/limite excedido/)).toBeVisible();
  await expect(send).toBeDisabled();
  await input.press("Enter");
  expect(bodies).toHaveLength(0);
});

test("o botão do resultado do quiz abre o assistente com o contexto do quiz", async ({ page }) => {
  const bodies = await mockAssistant(page, (route) => reply(route, "tudo certo"));
  await page.addInitScript(() => {
    localStorage.setItem("trilha-tech:quiz-resultado", JSON.stringify(["dados-e-ia"]));
  });
  await page.goto("/quiz/resultado");

  await page.getByRole("button", { name: "Conversar com a IA" }).click();
  const panel = page.getByRole("dialog", { name: "Assistente Trilha Tech" });
  await expect(panel).toBeVisible();
  await expect(panel.getByLabel("Sua mensagem")).toBeFocused();
  await expect(panel.getByText(/Contexto: Resultado do quiz/i)).toBeVisible();

  await ask(page, "Qual dessas áreas é melhor para começar?");
  await expect(panel.getByText("tudo certo")).toBeVisible();
  expect(bodies[0].contexto).toEqual({ resultadoQuiz: ["dados-e-ia"] });

  // Ao fechar, o foco volta ao botão que abriu o painel.
  await page.keyboard.press("Escape");
  await expect(page.getByRole("button", { name: "Conversar com a IA" })).toBeFocused();
});
