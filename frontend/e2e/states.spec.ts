import { expect, test } from "@playwright/test";

test("slug de área inexistente mostra a página não encontrada", async ({ page }) => {
  await page.goto("/areas/area-que-nao-existe");
  await expect(page.getByRole("heading", { level: 1 })).toHaveText("Essa página não existe");
  await expect(page.getByRole("link", { name: "Ver destinos" })).toBeVisible();
  await page.getByRole("link", { name: "Voltar ao início" }).click();
  await expect(page).toHaveURL(/\/$/);
});

test("slug de trilha inexistente mostra a página não encontrada", async ({ page }) => {
  await page.goto("/trilhas/trilha-que-nao-existe");
  await expect(page.getByRole("heading", { level: 1 })).toHaveText("Essa página não existe");
  await expect(page.locator("body")).not.toContainText("trilha-que-nao-existe");
});

test("falha no download do PDF mostra aviso amigável, sem texto técnico", async ({ page }) => {
  await page.route("**/api/trilhas/*/pdf", (route) =>
    route.fulfill({
      status: 500,
      contentType: "application/json",
      body: JSON.stringify({ status: 500, message: "MENSAGEM TECNICA DO SERVIDOR" }),
    }),
  );
  await page.goto("/areas/desenvolvimento-de-software");
  await page.getByRole("link", { name: /Ver roadmap/ }).first().click();
  await page.getByRole("link", { name: "Baixar roadmap em PDF" }).click();
  await expect(page.locator("p[role=alert]")).toContainText("Não foi possível baixar o PDF");
  await expect(page.locator("body")).not.toContainText("MENSAGEM TECNICA");
  // O botão continua disponível para tentar de novo.
  await expect(page.getByRole("link", { name: "Baixar roadmap em PDF" })).toBeVisible();
});
