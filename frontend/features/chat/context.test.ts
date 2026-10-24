import { describe, expect, it } from "vitest";

import { contextLabel, deriveContext, inputPlaceholder, isSlug } from "./context";

describe("deriveContext", () => {
  it("lê o slug da área em /areas/[slug]", () => {
    expect(deriveContext("/areas/dados-e-ia")).toEqual({ areaSlug: "dados-e-ia" });
  });

  it("lê o slug da trilha em /trilhas/[slug]", () => {
    expect(deriveContext("/trilhas/java-e-spring")).toEqual({ trilhaSlug: "java-e-spring" });
  });

  it("não devolve contexto nas demais páginas", () => {
    for (const path of ["/", "/quiz", "/quiz/resultado", "/areas", "/trilhas", "/styleguide"]) {
      expect(deriveContext(path)).toBeUndefined();
    }
  });

  it("ignora query, hash e barra final, e nunca repassa a URL", () => {
    const context = deriveContext("/trilhas/python/?utm=1#topo");
    expect(context).toEqual({ trilhaSlug: "python" });
    expect(JSON.stringify(context)).not.toContain("utm");
    expect(JSON.stringify(context)).not.toContain("/");
  });

  it("descarta segmentos que não são slugs válidos", () => {
    expect(deriveContext("/areas/Dados E IA")).toBeUndefined();
    expect(deriveContext("/areas/%E0%A4%A")).toBeUndefined();
    expect(deriveContext("/trilhas/<script>")).toBeUndefined();
    expect(deriveContext("/trilhas/" + "a".repeat(101))).toBeUndefined();
  });

  it("não aceita caminhos mais longos que o esperado", () => {
    expect(deriveContext("/areas/dados-e-ia/extra")).toBeUndefined();
    expect(deriveContext("/outra/dados-e-ia")).toBeUndefined();
  });

  it("junta as áreas do resultado do quiz como slugs, no máximo 3 e sem repetir", () => {
    expect(deriveContext("/", ["dev", "dev", "dados-e-ia", "ux-ui-design", "gestao"])).toEqual({
      resultadoQuiz: ["dev", "dados-e-ia", "ux-ui-design"],
    });
  });

  it("filtra slugs inválidos do quiz (o localStorage é dado de fora)", () => {
    expect(deriveContext("/", ["ok-slug", "Nome Com Espaço", "", "x/y"])).toEqual({
      resultadoQuiz: ["ok-slug"],
    });
    expect(deriveContext("/", ["Nome Com Espaço"])).toBeUndefined();
    expect(deriveContext("/", null)).toBeUndefined();
  });

  it("combina a página e o quiz, e muda de página para página", () => {
    expect(deriveContext("/trilhas/python", ["dados-e-ia"])).toEqual({
      trilhaSlug: "python",
      resultadoQuiz: ["dados-e-ia"],
    });
    expect(deriveContext("/areas/dados-e-ia", ["dados-e-ia"])).toEqual({
      areaSlug: "dados-e-ia",
      resultadoQuiz: ["dados-e-ia"],
    });
  });
});

describe("isSlug", () => {
  it("aceita minúsculas, números e hífens entre palavras", () => {
    expect(isSlug("java-e-spring")).toBe(true);
    expect(isSlug("a1")).toBe(true);
    expect(isSlug("-a")).toBe(false);
    expect(isSlug("a--b")).toBe(false);
    expect(isSlug("A")).toBe(false);
  });
});

describe("contextLabel e inputPlaceholder", () => {
  const names = {
    areas: { "dados-e-ia": "Dados e Inteligência Artificial" },
    trilhas: { "java-e-spring": "Java e Spring" },
  };

  it("usa o nome da trilha ou da área, e cai no slug sem nome", () => {
    expect(contextLabel({ trilhaSlug: "java-e-spring" }, names)).toBe("Trilha Java e Spring");
    expect(contextLabel({ areaSlug: "dados-e-ia" }, names)).toBe(
      "Área Dados e Inteligência Artificial",
    );
    expect(contextLabel({ areaSlug: "ux-ui-design" })).toBe("Área ux ui design");
  });

  it("menciona o resultado do quiz e some sem contexto", () => {
    expect(contextLabel({ resultadoQuiz: ["a"] }, names)).toBe("Resultado do quiz");
    expect(contextLabel({ trilhaSlug: "java-e-spring", resultadoQuiz: ["a"] }, names)).toBe(
      "Trilha Java e Spring · Resultado do quiz",
    );
    expect(contextLabel(undefined, names)).toBeNull();
  });

  it("escolhe o texto do campo pela página", () => {
    expect(inputPlaceholder({ trilhaSlug: "a" })).toBe("Pergunte sobre esta trilha…");
    expect(inputPlaceholder({ areaSlug: "a" })).toBe("Pergunte sobre esta área…");
    expect(inputPlaceholder(undefined)).toBe("Pergunte sobre TI e carreira…");
  });
});
