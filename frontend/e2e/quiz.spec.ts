import { expect, test, type Page } from "@playwright/test";

const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "https://trilha-tech-production.up.railway.app";

type Pergunta = { ordem: number; texto: string; alternativas: { ordem: number; texto: string }[] };

/** Responde as 12 perguntas escolhendo sempre a alternativa mostrada na posição `position` (0 a 3). */
async function answerAll(page: Page, perguntas: Pergunta[], position = 0) {
  const picked: { pergunta: number; alternativa: number }[] = [];
  for (const [index, pergunta] of perguntas.entries()) {
    await expect(page.getByText(`Pergunta ${String(index + 1).padStart(2, "0")} de 12`)).toBeVisible();
    // Sem resposta marcada, "Próxima" fica desabilitada (a pergunta 1 pode já estar respondida).
    if ((await page.getByRole("radio", { checked: true }).count()) === 0) {
      await expect(
        page.getByRole("button", { name: index === 11 ? "Ver resultado" : "Próxima" }),
      ).toBeDisabled();
    }

    const radio = page.getByRole("radio").nth(position);
    const label = await radio.evaluate((el) => el.closest("label")!.innerText);
    // O texto do rótulo é "A\nTexto": a primeira linha é só a letra da posição.
    const texto = label.split("\n").slice(1).join(" ").trim();
    const alternativa = pergunta.alternativas.find((item) => item.texto === texto);
    expect(alternativa, `alternativa "${texto}" existe na API`).toBeDefined();
    picked.push({ pergunta: pergunta.ordem, alternativa: alternativa!.ordem });

    await page.locator("label").filter({ hasText: texto }).click();
    // Na última pergunta o botão que envia se chama "Ver resultado".
    const next = page.getByRole("button", { name: index === 11 ? "Ver resultado" : "Próxima" });
    await expect(next).toBeEnabled();
    await next.click();
  }
  return picked;
}

async function loadQuiz(request: Parameters<Parameters<typeof test>[2]>[0]["request"]) {
  const response = await request.get(`${API_URL}/api/quiz`);
  expect(response.ok()).toBe(true);
  return ((await response.json()) as Pergunta[]).sort((a, b) => a.ordem - b.ordem);
}

test("quiz completo: responde, vê o resultado, recarrega e refaz", async ({ page, request }) => {
  const perguntas = await loadQuiz(request);
  expect(perguntas).toHaveLength(12);

  await page.goto("/quiz");
  await expect(page.getByRole("button", { name: "Voltar" })).toBeDisabled();
  // As letras A, B, C, D são só a posição na tela.
  await expect(page.getByRole("radio")).toHaveCount(4);

  // Voltar mantém a resposta já dada.
  await page.locator("label").nth(1).click();
  await page.getByRole("button", { name: "Próxima" }).click();
  await page.getByRole("button", { name: "Voltar" }).click();
  await expect(page.getByRole("radio").nth(1)).toBeChecked();

  const posted = page.waitForRequest((req) => req.url().endsWith("/api/quiz/resultado"));
  const picked = await answerAll(page, perguntas, 1);

  // O corpo do POST leva as ordens originais, e não as posições na tela.
  const body = (await posted).postDataJSON() as { respostas: typeof picked };
  expect(body.respostas).toEqual(picked);
  expect(body.respostas).toHaveLength(12);

  await expect(page).toHaveURL(/\/quiz\/resultado$/);
  await expect(page.getByRole("heading", { level: 1 })).toContainText("Estas áreas combinam");
  await expect(page.getByText(/não um teste vocacional/)).toBeVisible();
  const items = page.getByRole("listitem").filter({ has: page.getByRole("link", { name: /Ver trilhas/ }) });
  await expect(items).toHaveCount(3);
  await expect(page.getByRole("button", { name: "Conversar com a IA" })).toBeVisible();

  // Só os slugs ficam no navegador.
  const stored = await page.evaluate(() => JSON.stringify({ ...localStorage }));
  expect(Object.values(JSON.parse(stored) as Record<string, string>)).toHaveLength(1);
  const [value] = Object.values(JSON.parse(stored) as Record<string, string>);
  const slugs = JSON.parse(value) as string[];
  expect(slugs).toHaveLength(3);
  expect(slugs.every((slug) => /^[a-z0-9-]+$/.test(slug))).toBe(true);

  // Recarregar mantém o resultado salvo, e a Home mostra o atalho.
  await page.reload();
  await expect(items).toHaveCount(3);
  await page.goto("/");
  await expect(page.getByText("Seu resultado do quiz")).toBeVisible();
  await page.getByRole("link", { name: "Ver meu resultado" }).click();
  await expect(items).toHaveCount(3);

  // "Ver trilhas" leva para a área.
  await items.first().getByRole("link", { name: /Ver trilhas/ }).click();
  await expect(page).toHaveURL(new RegExp(`/areas/${slugs[0]}$`));

  // "Refazer o quiz" limpa o resultado salvo.
  await page.goBack();
  await page.getByRole("link", { name: "Refazer o quiz" }).click();
  await expect(page).toHaveURL(/\/quiz$/);
  expect(await page.evaluate(() => localStorage.length)).toBe(0);
  await page.goto("/quiz/resultado");
  await expect(page).toHaveURL(/\/quiz$/);
  await page.goto("/");
  await expect(page.getByText("Seu resultado do quiz")).toHaveCount(0);
});

test("com o localStorage bloqueado o quiz funciona e o resultado não persiste", async ({
  page,
  request,
}) => {
  const perguntas = await loadQuiz(request);
  await page.addInitScript(() => {
    Object.defineProperty(window, "localStorage", {
      get() {
        throw new DOMException("bloqueado", "SecurityError");
      },
    });
  });

  await page.goto("/quiz");
  await answerAll(page, perguntas, 0);
  await expect(page).toHaveURL(/\/quiz\/resultado$/);
  await expect(page.getByRole("link", { name: /Ver trilhas/ })).toHaveCount(3);

  // Sem armazenamento não há o que recuperar: recarregar leva de volta ao quiz.
  await page.reload();
  await expect(page).toHaveURL(/\/quiz$/);
  await page.goto("/");
  await expect(page.getByText("Seu resultado do quiz")).toHaveCount(0);
});

test("erro 400 no envio mostra mensagem amigável e permite tentar de novo", async ({
  page,
  request,
}) => {
  const perguntas = await loadQuiz(request);
  await page.route("**/api/quiz/resultado", (route) =>
    route.fulfill({
      status: 400,
      contentType: "application/json",
      body: JSON.stringify({
        timestamp: "2026-10-24T10:00:00",
        status: 400,
        error: "Bad Request",
        message: "MENSAGEM TECNICA DO SERVIDOR",
        path: "/api/quiz/resultado",
      }),
    }),
  );

  await page.goto("/quiz");
  await answerAll(page, perguntas, 0);

  const alert = page.locator("p[role=alert]");
  await expect(alert).toContainText("sem resposta");
  await expect(page.locator("body")).not.toContainText("MENSAGEM TECNICA");
  await expect(page.locator("body")).not.toContainText("/api/quiz/resultado");
  await expect(page).toHaveURL(/\/quiz$/);

  // As respostas não se perdem: sem refazer o quiz, é só tentar de novo (agora com a API normal).
  await expect(page.getByRole("radio", { checked: true })).toHaveCount(1);
  await page.unroute("**/api/quiz/resultado");
  await page.getByRole("button", { name: "Ver resultado" }).click();
  await expect(page).toHaveURL(/\/quiz\/resultado$/);
  await expect(page.getByRole("link", { name: /Ver trilhas/ })).toHaveCount(3);
});

test("alternativas são rádios reais: setas do teclado trocam a escolha", async ({ page }) => {
  await page.goto("/quiz");
  const radios = page.getByRole("radio");
  await expect(radios).toHaveCount(4);

  // Foco no grupo e teclado nativo de rádios: espaço marca, setas trocam.
  await radios.first().focus();
  await page.keyboard.press("Space");
  await expect(radios.first()).toBeChecked();
  await page.keyboard.press("ArrowDown");
  await expect(radios.nth(1)).toBeChecked();
  await page.keyboard.press("ArrowUp");
  await expect(radios.first()).toBeChecked();
});
