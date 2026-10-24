import { expect, test, type Page } from "@playwright/test";

// Movimento (fase 12B). Com prefers-reduced-motion: reduce (o padrão da config) não pode existir
// nenhuma animação nem transição; sem essa preferência, cada animação da lista tem que existir.

const animationCount = (page: Page) => page.evaluate(() => document.getAnimations().length);

/** Responde as 12 perguntas (sempre a primeira alternativa mostrada) e envia. */
async function finishQuiz(page: Page) {
  await page.goto("/quiz");
  for (let step = 1; step <= 12; step++) {
    await page.locator("label").first().click();
    await page.getByRole("button", { name: step === 12 ? "Ver resultado" : "Próxima" }).click();
  }
}

async function mockResult(page: Page) {
  await page.route("**/api/quiz/resultado", (route) =>
    route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify({
        recomendacoes: [
          { posicao: 1, slug: "dados-e-ia", nome: "Dados e Inteligência Artificial", barra: 100 },
          { posicao: 2, slug: "desenvolvimento-de-software", nome: "Desenvolvimento de Software", barra: 60 },
          { posicao: 3, slug: "ux-ui-design", nome: "UX/UI Design", barra: 25 },
        ],
      }),
    }),
  );
}

test.describe("com prefers-reduced-motion: reduce", () => {
  test("nenhum botão, cartão ou entrada anima", async ({ page }) => {
    await page.goto("/");
    await expect(page.getByRole("link", { name: "Ver os destinos" })).toHaveCSS("transition-duration", "0s");
    await expect(page.getByRole("link", { name: /^Área 01/ })).toHaveCSS("transition-duration", "0s");

    // Nada fica escondido esperando a animação de entrada.
    await page.getByRole("link", { name: /^Área 08/ }).scrollIntoViewIfNeeded();
    await expect(page.locator("[data-reveal]")).toHaveCount(0);
    await expect(page.getByRole("link", { name: /^Área 08/ })).toBeVisible();
    expect(await animationCount(page)).toBe(0);
  });

  test("etapa abre e fecha sem animação, com aria-expanded e foco", async ({ page }) => {
    await page.goto("/trilhas/java-e-spring");
    const step = page.locator("article h3 button").first();
    await step.focus();
    await page.keyboard.press("Enter");
    await expect(step).toHaveAttribute("aria-expanded", "true");
    await expect(step).toBeFocused();
    expect(await animationCount(page)).toBe(0);
    await page.keyboard.press("Enter");
    await expect(step).toHaveAttribute("aria-expanded", "false");
  });

  test("quiz: pergunta, progresso e resultado sem animação", async ({ page }) => {
    await page.goto("/quiz");
    await page.locator("label").first().click();
    await page.getByRole("button", { name: "Próxima" }).click();
    await expect(page.getByText("Pergunta 02 de 12")).toBeVisible();
    await expect(page.locator("fieldset")).toHaveCSS("animation-name", "none");
    expect(await animationCount(page)).toBe(0);

    await mockResult(page);
    await finishQuiz(page);
    await expect(page).toHaveURL(/\/quiz\/resultado$/, { timeout: 20_000 });
    await expect(page.locator(".motion-bar-grow").first()).toHaveCSS("animation-name", "none");
  });

  test("assistente abre e fecha sem animação", async ({ page }) => {
    await page.goto("/");
    await page.getByRole("button", { name: /Perguntar à IA/ }).click();
    const panel = page.getByRole("dialog", { name: "Assistente Trilha Tech" });
    await expect(panel).toBeVisible();
    await expect(panel).toHaveCSS("transition-duration", "0s");
    expect(await animationCount(page)).toBe(0);
  });
});

test.describe("sem a preferência de movimento reduzido", () => {
  test.use({ reducedMotion: "no-preference" });

  test("botões e cartões animam o hover e o pressionado em 150 ms", async ({ page }) => {
    await page.goto("/");
    for (const target of [
      page.getByRole("link", { name: "Ver os destinos" }),
      page.getByRole("link", { name: /^Área 01/ }),
    ]) {
      await expect(target).toHaveCSS("transition-property", "transform, box-shadow");
      await expect(target).toHaveCSS("transition-duration", "0.15s");
    }
  });

  test("cartões de área abaixo da dobra sobem e aparecem uma vez só", async ({ page }) => {
    await page.setViewportSize({ width: 1280, height: 720 });
    await page.goto("/");
    const last = page.locator("li", { has: page.getByRole("link", { name: /^Área 08/ }) });
    await expect(last).toHaveAttribute("data-reveal", "pending");
    await expect(last).toHaveCSS("opacity", "0");

    await last.scrollIntoViewIfNeeded();
    await expect(last).toHaveAttribute("data-reveal", "shown");
    await expect(last).toHaveCSS("opacity", "1");
    await expect(last).toHaveCSS("transition-duration", "0.25s, 0.25s");

    // Uma vez só: voltar ao topo e descer de novo não esconde nem repete.
    await page.evaluate(() => window.scrollTo(0, 0));
    await last.scrollIntoViewIfNeeded();
    await expect(last).toHaveAttribute("data-reveal", "shown");
    await expect(last).toHaveCSS("opacity", "1");
  });

  test("etapa anima a altura (grid-template-rows) e mantém foco e aria-expanded", async ({ page }) => {
    await page.goto("/trilhas/java-e-spring");
    const step = page.locator("article h3 button").first();
    await step.focus();
    const animatesHeight = await page.evaluate(() => {
      (document.activeElement as HTMLButtonElement).click();
      return new Promise<boolean>((resolve) =>
        requestAnimationFrame(() =>
          resolve(
            document
              .getAnimations()
              .some((a) => a instanceof CSSTransition && a.transitionProperty === "grid-template-rows"),
          ),
        ),
      );
    });
    expect(animatesHeight).toBe(true);

    await expect(step).toHaveAttribute("aria-expanded", "true");
    await expect(step).toBeFocused();
    // Aberta, a região do conteúdo é alcançável; fechada, fica inert (fora do Tab e da leitura de tela).
    const panel = page.locator(`[id="${await step.getAttribute("aria-controls")}"]`);
    await expect(panel).not.toHaveAttribute("inert");
    await expect(panel.getByRole("heading", { name: "Projeto prático" })).toBeVisible();
    await page.keyboard.press("Enter");
    await expect(step).toBeFocused();
    await expect(panel).toHaveAttribute("inert");
  });

  test("quiz: pergunta nova desliza e a barra de progresso preenche com transform", async ({ page }) => {
    await page.goto("/quiz");
    // No carregamento não há animação (não atrasa o conteúdo principal).
    await expect(page.locator("fieldset")).toHaveCSS("animation-name", "none");
    await page.locator("label").first().click();
    await page.getByRole("button", { name: "Próxima" }).click();
    await expect(page.getByText("Pergunta 02 de 12")).toBeVisible();
    await expect(page.locator("fieldset")).toHaveCSS("animation-name", "enter-x");
    await expect(page.locator("fieldset")).toHaveCSS("animation-duration", "0.25s");
    const fill = page.getByRole("progressbar").locator("span > span").nth(1);
    await expect(fill).toHaveCSS("transition-property", /transform/);
    await expect(fill).toHaveCSS("transition-duration", "0.4s");
  });

  test("resultado: as barras crescem ao entrar", async ({ page }) => {
    await mockResult(page);
    await finishQuiz(page);
    await expect(page).toHaveURL(/\/quiz\/resultado$/, { timeout: 20_000 });
    const bar = page.locator(".motion-bar-grow").first();
    await expect(bar).toHaveCSS("animation-name", "bar-grow");
    await expect(bar).toHaveCSS("animation-duration", "0.4s");
    // O valor final vem do estilo inline: 60% = scaleX(0.6).
    await expect(page.locator(".motion-bar-grow").nth(1)).toHaveCSS(
      "transform",
      "matrix(0.6, 0, 0, 1, 0, 0)",
    );
  });

  test("assistente abre e fecha com fade e deslocamento, sem perder foco nem rótulo", async ({ page }) => {
    await page.goto("/");
    const launcher = page.getByRole("button", { name: /Perguntar à IA/ });
    await launcher.click();
    const panel = page.getByRole("dialog", { name: "Assistente Trilha Tech" });
    await expect(panel).toBeVisible();
    await expect(panel).toHaveCSS("transition-property", /opacity.*translate/);
    await expect(panel).toHaveCSS("transition-duration", "0.25s");
    await expect(panel.getByLabel("Sua mensagem")).toBeFocused();
    await page.keyboard.press("Escape");
    await expect(panel).toBeHidden();
    await expect(launcher).toBeFocused();
  });

  test("navegar, voltar e avançar com a transição de página: URL, rolagem e conteúdo", async ({ page }) => {
    await page.goto("/");
    await page.evaluate(() => window.scrollTo(0, 900));
    await expect.poll(() => page.evaluate(() => Math.round(window.scrollY))).toBe(900);

    await page.getByRole("link", { name: /^Área 05/ }).click();
    await expect(page).toHaveURL(/\/areas\/qualidade-e-testes$/);
    await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
    await expect.poll(() => page.evaluate(() => Math.round(window.scrollY))).toBe(0);

    await page.goBack();
    await expect(page).toHaveURL(/\/$/);
    await expect(page.getByRole("heading", { level: 1 })).toContainText("Sua passagem");
    await expect.poll(() => page.evaluate(() => Math.abs(window.scrollY - 900) <= 40)).toBe(true);

    await page.goForward();
    await expect(page).toHaveURL(/\/areas\/qualidade-e-testes$/);
    await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
  });

  test("entrada ao rolar não atrapalha âncoras (#) nem a busca do navegador (Ctrl+F)", async ({ page }) => {
    await page.setViewportSize({ width: 1280, height: 720 });

    // Âncora para uma seção abaixo: o navegador rola até ela, e os cartões ficam visíveis.
    await page.goto("/#como-funciona");
    await expect
      .poll(() => page.evaluate(() => Math.round(document.getElementById("como-funciona")!.getBoundingClientRect().top)))
      .toBeLessThanOrEqual(30);
    await expect(page.getByRole("heading", { name: "Três passos, sem cadastro" })).toBeInViewport();

    await page.goto("/#destinos");
    await expect
      .poll(() => page.evaluate(() => Math.round(document.getElementById("destinos")!.getBoundingClientRect().top)))
      .toBeLessThanOrEqual(30);
    await expect(page.locator("li", { has: page.getByRole("link", { name: /^Área 01/ }) })).toHaveCSS("opacity", "1");

    // Busca do navegador: o texto de um cartão ainda escondido (opacity 0) é encontrado, e achar o
    // trecho rola até ele, o que revela o cartão.
    await page.goto("/");
    const last = page.locator("li", { has: page.getByRole("link", { name: /^Área 08/ }) });
    await expect(last).toHaveAttribute("data-reveal", "pending");
    const found = await page.evaluate(() => (window as unknown as { find: (text: string) => boolean }).find("Gestão e Produtos de TI"));
    expect(found).toBe(true);
    await expect(last).toHaveAttribute("data-reveal", "shown");
    await expect(last).toHaveCSS("opacity", "1");
  });
});
