import axe from "axe-core";
import { expect, test, type Page } from "@playwright/test";

// Varredura automática de acessibilidade (axe, WCAG 2.0/2.1/2.2 A e AA + boas práticas) nas páginas
// principais e no assistente, em desktop e mobile. O axe acha só parte dos problemas: teclado, ordem
// de foco e leitor de tela foram conferidos à mão (ver docs/pendencias.md e o relatório da Fase 12).

const SEEDED_RESULT = JSON.stringify(["dados-e-ia", "desenvolvimento-de-software", "ux-ui-design"]);

async function violations(page: Page, options: { skipRules?: string[] } = {}) {
  await page.evaluate(axe.source);
  return page.evaluate(async (skipRules) => {
    const result = await (
      window as unknown as { axe: typeof import("axe-core") }
    ).axe.run(
      { exclude: [["nextjs-portal"]] },
      {
        runOnly: { type: "tag", values: ["wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22aa", "best-practice"] },
        rules: Object.fromEntries(skipRules.map((rule) => [rule, { enabled: false }])),
      },
    );
    return result.violations.map((v) => `${v.id}: ${v.nodes.map((n) => n.target.join(" ")).join(" | ")}`);
  }, options.skipRules ?? []);
}

for (const [label, viewport] of [
  ["desktop", { width: 1280, height: 900 }],
  ["mobile", { width: 390, height: 844 }],
] as const) {
  test.describe(`axe em ${label}`, () => {
    test.use({ viewport });
    test.beforeEach(async ({ page }) => {
      await page.addInitScript((value) => localStorage.setItem("trilha-tech:quiz-resultado", value), SEEDED_RESULT);
    });

    test("Home, com o card do resultado salvo", async ({ page }) => {
      await page.goto("/");
      await expect(page.getByText("Seu resultado do quiz")).toBeVisible();
      expect(await violations(page)).toEqual([]);
    });

    test("Área", async ({ page }) => {
      await page.goto("/areas/desenvolvimento-de-software");
      await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
      expect(await violations(page)).toEqual([]);
    });

    test("Trilha, com uma etapa aberta", async ({ page }) => {
      await page.goto("/trilhas/java-e-spring");
      const step = page.locator("article h3 button").first();
      await step.click();
      await expect(step).toHaveAttribute("aria-expanded", "true");
      // Duas exceções, ambas em docs/pendencias.md:
      // - color-contrast: "ink-soft" sobre coral (3,06:1) e menta (4,45:1) nas faixas salariais vem do
      //   Figma (nós 123:681 e 123:687); fica fora até o dono decidir.
      // - label-content-name-mismatch (WCAG 2.5.3): o botão da etapa tem como nome só o título, e o
      //   resumo e o nível visíveis nele entram como descrição (decisão do dono).
      expect(
        await violations(page, { skipRules: ["color-contrast", "label-content-name-mismatch"] }),
      ).toEqual([]);
    });

    test("Quiz, com uma alternativa marcada", async ({ page }) => {
      await page.goto("/quiz");
      await page.locator("label").nth(1).click();
      await expect(page.getByRole("radio").nth(1)).toBeChecked();
      expect(await violations(page)).toEqual([]);
    });

    test("Resultado do quiz", async ({ page }) => {
      await page.goto("/quiz/resultado");
      await expect(page.getByRole("heading", { level: 1 })).toBeVisible();
      expect(await violations(page)).toEqual([]);
    });

    test("404", async ({ page }) => {
      await page.goto("/trilhas/trilha-que-nao-existe");
      await expect(page.getByRole("heading", { level: 1 })).toHaveText("Essa página não existe");
      expect(await violations(page)).toEqual([]);
    });

    test("Assistente aberto", async ({ page }) => {
      await page.goto("/");
      await page.getByRole("button", { name: /Perguntar à IA/ }).click();
      await expect(page.getByRole("dialog")).toBeVisible();
      expect(await violations(page)).toEqual([]);
    });
  });
}
