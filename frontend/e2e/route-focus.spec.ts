import { expect, test, type Page } from "@playwright/test";

// Ao mudar de página o foco vai para o <main id="conteudo"> (sem rolar); no primeiro carregamento, não.

const activeId = (page: Page) =>
  page.evaluate(() =>
    document.activeElement === document.body
      ? "body"
      : document.activeElement?.id || document.activeElement?.tagName,
  );

test("no primeiro carregamento o foco não é movido", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
  expect(await activeId(page)).toBe("body");
  await page.keyboard.press("Tab");
  await expect(page.getByRole("link", { name: "Pular para o conteúdo" })).toBeFocused();
});

test("ao navegar com Enter o foco vai para o conteúdo e o Tab segue de dentro dele", async ({ page }) => {
  await page.goto("/");
  await page.getByRole("link", { name: /^Área 02/ }).focus();
  await page.keyboard.press("Enter");
  await expect(page).toHaveURL(/\/areas\/dados-e-ia$/);
  await expect.poll(() => activeId(page)).toBe("conteudo");
  // Espera o conteúdo real (e não o esqueleto de carregamento, que não tem nada focável).
  await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
  await page.keyboard.press("Tab");
  expect(
    await page.evaluate(() => document.getElementById("conteudo")!.contains(document.activeElement)),
  ).toBe(true);
});

test("o botão voltar mantém a rolagem restaurada e leva o foco ao conteúdo", async ({ page }) => {
  await page.goto("/");
  await page.evaluate(() => window.scrollTo(0, 900));
  await expect.poll(() => page.evaluate(() => Math.round(window.scrollY))).toBe(900);
  await page.getByRole("link", { name: /^Área 05/ }).click();
  await expect(page).toHaveURL(/\/areas\/qualidade-e-testes$/);
  await expect.poll(() => activeId(page)).toBe("conteudo");

  await page.goBack();
  await expect(page).toHaveURL(/\/$/);
  await expect.poll(() => page.evaluate(() => Math.abs(window.scrollY - 900) <= 40)).toBe(true);
  await expect.poll(() => activeId(page)).toBe("conteudo");
});

test("âncora na mesma página (#) não move o foco para o conteúdo", async ({ page }) => {
  await page.goto("/");
  await page.getByRole("link", { name: "Como funciona" }).first().click();
  await expect(page).toHaveURL(/#como-funciona$/);
  expect(await activeId(page)).not.toBe("conteudo");
});
