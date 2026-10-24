import { readFileSync, statSync } from "node:fs";
import { join } from "node:path";

import { describe, expect, it } from "vitest";

import { MAX_DESCRIPTION_LENGTH, OG_IMAGE, pageMetadata, shortDescription } from "./seo";

describe("shortDescription", () => {
  it("mantém textos curtos, só normalizando espaços e quebras de linha", () => {
    expect(shortDescription("  Back-end   robusto\npara sistemas.  ")).toBe("Back-end robusto para sistemas.");
  });

  it("corta no fim da última frase que cabe", () => {
    const text =
      "Desenvolvimento de software é a área de quem cria programas, sites e aplicativos. Você escreve código para transformar uma ideia em algo que as pessoas usam todos os dias.";
    const result = shortDescription(text);
    expect(result).toBe("Desenvolvimento de software é a área de quem cria programas, sites e aplicativos.");
    expect(result.length).toBeLessThanOrEqual(MAX_DESCRIPTION_LENGTH);
  });

  it("sem frase que caiba, corta na última palavra e põe reticências", () => {
    const text = "palavra ".repeat(40).trim();
    const result = shortDescription(text);
    expect(result.endsWith("palavra…")).toBe(true);
    expect(result.length).toBeLessThanOrEqual(MAX_DESCRIPTION_LENGTH);
  });

  it("não deixa vírgula ou hífen pendurado antes das reticências", () => {
    const text = `${"a".repeat(100)}, ${"b".repeat(100)}`;
    const result = shortDescription(text);
    expect(result).toBe(`${"a".repeat(100)}…`);
  });

  it("corta uma palavra gigante sem estourar o limite", () => {
    const result = shortDescription("x".repeat(400));
    expect(result.length).toBeLessThanOrEqual(MAX_DESCRIPTION_LENGTH);
    expect(result.endsWith("…")).toBe(true);
  });
});

describe("pageMetadata", () => {
  it("monta título, canonical, Open Graph e Twitter com o nome do site no título social", () => {
    const meta = pageMetadata({ title: "Java e Spring", description: "Back-end robusto.", path: "/trilhas/java-e-spring" });
    expect(meta.title).toBe("Java e Spring");
    expect(meta.description).toBe("Back-end robusto.");
    expect(meta.alternates?.canonical).toBe("/trilhas/java-e-spring");
    expect(meta.openGraph).toMatchObject({
      title: "Java e Spring | Trilha Tech",
      url: "/trilhas/java-e-spring",
      locale: "pt_BR",
      siteName: "Trilha Tech",
    });
    expect(meta.twitter).toMatchObject({ card: "summary_large_image", title: "Java e Spring | Trilha Tech" });
  });

  it("limita a descrição também no Open Graph", () => {
    const meta = pageMetadata({ title: "X", description: "palavra ".repeat(60), path: "/x" });
    expect((meta.description ?? "").length).toBeLessThanOrEqual(MAX_DESCRIPTION_LENGTH);
    expect(meta.openGraph?.description).toBe(meta.description);
  });
});

describe("imagem de compartilhamento", () => {
  const app = join(__dirname, "..", "app");

  it("o alt no código é igual ao de app/opengraph-image.alt.txt", () => {
    expect(OG_IMAGE.alt).toBe(readFileSync(join(app, "opengraph-image.alt.txt"), "utf-8").trim());
  });

  it("o arquivo existe, é PNG de 1200x630 e tem menos de 300 KB", () => {
    const file = join(app, "opengraph-image.png");
    const png = readFileSync(file);
    expect(png.subarray(0, 8).toString("hex")).toBe("89504e470d0a1a0a");
    expect(png.readUInt32BE(16)).toBe(OG_IMAGE.width);
    expect(png.readUInt32BE(20)).toBe(OG_IMAGE.height);
    expect(statSync(file).size).toBeLessThan(300 * 1024);
  });

  it("cada página repassa a imagem no Open Graph e no Twitter", () => {
    const meta = pageMetadata({ title: "X", description: "Y", path: "/x" });
    expect(meta.openGraph).toMatchObject({ images: [{ url: "/opengraph-image.png", width: 1200, height: 630 }] });
    expect(meta.twitter).toMatchObject({ images: [{ url: "/opengraph-image.png" }] });
  });
});
