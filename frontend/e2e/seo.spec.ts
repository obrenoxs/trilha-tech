import { expect, test, type Page } from "@playwright/test";

// SEO: metadados no HTML (o que um buscador ou uma prévia de link lê), sitemap e robots.

const metaContent = (page: Page, selector: string) =>
  page.locator(`head ${selector}`).first().getAttribute("content");

test("trilha: título, descrição, canonical, Open Graph e Twitter vêm dos dados da API", async ({ page, request }) => {
  const response = await request.get("/trilhas/java-e-spring");
  const html = await response.text();
  // Está no HTML que o servidor entrega (o "view-source"), e não só depois do JavaScript.
  expect(html).toContain("<title>Java e Spring: roadmap de estudo | Trilha Tech</title>");

  await page.goto("/trilhas/java-e-spring");
  await expect(page).toHaveTitle("Java e Spring: roadmap de estudo | Trilha Tech");

  const description = (await metaContent(page, 'meta[name="description"]'))!;
  expect(description.length).toBeGreaterThan(20);
  expect(description.length).toBeLessThanOrEqual(155);
  expect(description).not.toMatch(/pendenc|undefined|null|\[object/i);

  await expect(page.locator('head link[rel="canonical"]')).toHaveAttribute("href", /\/trilhas\/java-e-spring$/);
  await expect(page.locator('head meta[property="og:url"]')).toHaveAttribute("content", /\/trilhas\/java-e-spring$/);
  expect(await metaContent(page, 'meta[property="og:title"]')).toBe("Java e Spring: roadmap de estudo | Trilha Tech");
  expect(await metaContent(page, 'meta[property="og:description"]')).toBe(description);
  expect(await metaContent(page, 'meta[property="og:locale"]')).toBe("pt_BR");
  expect(await metaContent(page, 'meta[property="og:site_name"]')).toBe("Trilha Tech");
  expect(await metaContent(page, 'meta[name="twitter:card"]')).toBe("summary_large_image");
  await expect(page.locator('head meta[name="robots"]')).toHaveCount(0);
});

test("imagem de compartilhamento e ícones do site estão no HTML e carregam", async ({ page, request }) => {
  await page.goto("/trilhas/java-e-spring");

  // Open Graph e Twitter apontam para a imagem estática de 1200x630 (arquivo do Figma, sem gerar em código).
  const ogImage = (await metaContent(page, 'meta[property="og:image"]'))!;
  expect(ogImage).toMatch(/^https?:\/\/.+\/opengraph-image\.png/);
  expect(await metaContent(page, 'meta[property="og:image:width"]')).toBe("1200");
  expect(await metaContent(page, 'meta[property="og:image:height"]')).toBe("630");
  expect(await metaContent(page, 'meta[property="og:image:alt"]')).toBe(
    "Trilha Tech: sua passagem para a primeira vaga em TI.",
  );
  expect(await metaContent(page, 'meta[name="twitter:image"]')).toMatch(/opengraph-image\.png|twitter-image/);

  // Ícones: favicon em SVG e PNG, e o ícone da tela inicial do iOS (180x180).
  const icons = await page.locator("head link[rel='icon'], head link[rel='apple-touch-icon']").evaluateAll((links) =>
    links.map((link) => ({
      rel: link.getAttribute("rel"),
      type: link.getAttribute("type"),
      sizes: link.getAttribute("sizes"),
      href: link.getAttribute("href"),
    })),
  );
  expect(icons.some((icon) => icon.rel === "icon" && icon.type === "image/svg+xml")).toBe(true);
  expect(icons.some((icon) => icon.rel === "icon" && icon.type === "image/png")).toBe(true);
  expect(icons.some((icon) => icon.rel === "apple-touch-icon" && icon.sizes === "180x180")).toBe(true);

  // Os arquivos existem e têm o tipo certo; a imagem de compartilhamento fica abaixo de 300 KB.
  for (const icon of icons) {
    const response = await request.get(icon.href!);
    expect(response.status(), icon.href!).toBe(200);
    expect(response.headers()["content-type"]).toContain(icon.type ?? "image/png");
  }
  const og = await request.get(new URL(ogImage).pathname + new URL(ogImage).search);
  expect(og.status()).toBe(200);
  expect(og.headers()["content-type"]).toContain("image/png");
  expect((await og.body()).length).toBeLessThan(300 * 1024);

  // O favicon do scaffold do Next saiu.
  expect((await request.get("/favicon.ico")).status()).toBe(404);
});

test("área e Home têm título e descrição próprios", async ({ page }) => {
  await page.goto("/areas/dados-e-ia");
  await expect(page).toHaveTitle("Dados e Inteligência Artificial | Trilha Tech");
  const area = (await metaContent(page, 'meta[name="description"]'))!;
  expect(area.length).toBeLessThanOrEqual(155);
  await expect(page.locator('head link[rel="canonical"]')).toHaveAttribute("href", /\/areas\/dados-e-ia$/);

  await page.goto("/");
  await expect(page).toHaveTitle("Trilha Tech: descubra qual área de TI seguir");
  const home = (await metaContent(page, 'meta[name="description"]'))!;
  expect(home).not.toMatch(/vaga|garant/i);
});

test("resultado do quiz e página inexistente têm noindex", async ({ page }) => {
  await page.goto("/quiz/resultado");
  await expect(page.locator('head meta[name="robots"]')).toHaveAttribute("content", /noindex/);

  await page.goto("/trilhas/trilha-que-nao-existe");
  await expect(page.getByRole("heading", { level: 1 })).toHaveText("Essa página não existe");
  await expect(page).toHaveTitle("Página não encontrada | Trilha Tech");
  // O Next injeta o noindex da 404 transmitida em streaming; pode vir repetido, e é inofensivo.
  await expect(page.locator('head meta[name="robots"]').first()).toHaveAttribute("content", /noindex/);
});

test("sitemap.xml lista a Home, as 8 áreas e as 36 trilhas, e nada do quiz nem do styleguide", async ({ request }) => {
  const response = await request.get("/sitemap.xml");
  expect(response.status()).toBe(200);
  expect(response.headers()["content-type"]).toContain("xml");
  const urls = [...(await response.text()).matchAll(/<loc>([^<]+)<\/loc>/g)].map((match) => match[1]);

  expect(urls.filter((url) => /\/areas\/[^/]+$/.test(url))).toHaveLength(8);
  expect(urls.filter((url) => /\/trilhas\/[^/]+$/.test(url))).toHaveLength(36);
  expect(urls.filter((url) => /^https?:\/\/[^/]+\/$/.test(url))).toHaveLength(1);
  expect(urls).toHaveLength(45);
  expect(new Set(urls).size).toBe(urls.length);
  expect(urls.some((url) => /quiz|styleguide/.test(url))).toBe(false);
});

test("robots.txt libera o site e aponta para o sitemap", async ({ request }) => {
  const response = await request.get("/robots.txt");
  expect(response.status()).toBe(200);
  const text = await response.text();
  expect(text).toMatch(/User-Agent: \*/i);
  expect(text).toMatch(/Allow: \//);
  expect(text).toMatch(/Sitemap: https?:\/\/[^\s]+\/sitemap\.xml/);
});
