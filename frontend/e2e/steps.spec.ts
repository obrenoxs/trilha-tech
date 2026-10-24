import { expect, test, type Locator, type Page } from "@playwright/test";

// Etapas da trilha: o cabeçalho inteiro (número, título, descrição, "+" ou "−") abre e fecha a etapa.
// Estrutura: <h3><button aria-expanded aria-controls>cabeçalho</button></h3>, com o conteúdo aberto
// (recursos, links) FORA do botão.

async function firstStep(page: Page) {
  await page.goto("/trilhas/java-e-spring");
  const article = page.locator("article").first();
  const button = article.locator("h3 button");
  const panel = page.locator(`[id="${await button.getAttribute("aria-controls")}"]`);
  return { article, button, panel };
}

async function expectOpen(button: Locator, panel: Locator, open: boolean) {
  await expect(button).toHaveAttribute("aria-expanded", String(open));
  if (open) await expect(panel).not.toHaveAttribute("inert");
  else await expect(panel).toHaveAttribute("inert");
}

test("clicar no título, na descrição, no número ou no ícone abre e fecha a etapa", async ({ page }) => {
  const { article, button, panel } = await firstStep(page);
  await expectOpen(button, panel, false);

  // Título.
  await article.locator("h3 button span[id$='-title']").click();
  await expectOpen(button, panel, true);
  // Ícone "−" (decorativo, dentro do botão).
  await button.locator("span[aria-hidden=true]:not([id])").click();
  await expectOpen(button, panel, false);
  // Descrição (o resumo cortado, que só aparece com a etapa fechada).
  await article.locator("h3 button span[id$='-summary']").click();
  await expectOpen(button, panel, true);
  // Número (o canhoto colorido).
  await article.getByText("01", { exact: true }).click();
  await expectOpen(button, panel, false);
  // Um ponto vazio do cabeçalho (a área do botão fora do texto).
  const box = (await button.boundingBox())!;
  await page.mouse.click(box.x + box.width - 60, box.y + 6);
  await expectOpen(button, panel, true);
});

test("o conteúdo aberto fica fora do botão: clicar nele ou em um link não fecha a etapa", async ({ page }) => {
  const { button, panel } = await firstStep(page);
  await button.click();
  await expectOpen(button, panel, true);

  // Nada do conteúdo aberto está dentro do botão.
  await expect(button.locator("a")).toHaveCount(0);
  expect(await panel.evaluate((el) => !!el.closest("button") || !!el.querySelector("button"))).toBe(false);

  // Texto do conteúdo (projeto prático): não fecha.
  await panel.getByText("Projeto prático").click();
  await expectOpen(button, panel, true);

  // Link de um recurso: o clique chega ao link e não fecha a etapa (a navegação é cancelada no teste).
  const link = panel.getByRole("link").first();
  await expect(link).toBeVisible();
  await link.evaluate((a) => a.addEventListener("click", (event) => event.preventDefault()));
  await link.click();
  await expectOpen(button, panel, true);
});

test("teclado: Enter e Espaço abrem e fecham; o foco fica no botão inteiro, com contorno visível", async ({ page }) => {
  const { button, panel } = await firstStep(page);
  await button.focus();
  await page.keyboard.press("Enter");
  await expectOpen(button, panel, true);
  await expect(button).toBeFocused();
  await page.keyboard.press("Space");
  await expectOpen(button, panel, false);
  await expect(button).toBeFocused();
  await page.keyboard.press("Space");
  await expectOpen(button, panel, true);

  // Foco chegando por Tab (focus-visible): contorno de 3 px em volta do botão inteiro.
  await page.goto("/trilhas/java-e-spring");
  const first = page.locator("article h3 button").first();
  await page.getByRole("link", { name: "Destinos" }).first().focus();
  for (let i = 0; i < 15; i++) {
    if (await first.evaluate((b) => b === document.activeElement)) break;
    await page.keyboard.press("Tab");
  }
  await expect(first).toBeFocused();
  await expect(first).toHaveCSS("outline-style", "solid");
  await expect(first).toHaveCSS("outline-width", "3px");
});

test("semântica: o nome do botão é o título; resumo e nível são a descrição; o ícone é decorativo", async ({ page }) => {
  const { article, button } = await firstStep(page);
  const title = (await article.locator("h3 button span[id$='-title']").innerText()).trim();
  await expect(article.getByRole("heading", { level: 3 })).toHaveCount(1);
  await expect(button).toHaveAccessibleName(title);
  await expect(button).toHaveAccessibleDescription(/Nível · /);
  await expect(button.locator("span[aria-hidden=true]:not([id])")).toHaveText("+");
  await expect(button).toHaveCSS("cursor", "pointer");
  await expect(article.getByText("01", { exact: true })).toHaveCSS("cursor", "pointer");
  // O botão é o único filho do h3 e o único controle do cabeçalho.
  await expect(article.locator("h3 > button")).toHaveCount(1);
});
