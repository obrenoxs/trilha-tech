import { describe, expect, it } from "vitest";
import {
  formatBrl,
  formatMonthYear,
  formatSalaryRange,
  formatWeeks,
  isSafeExternalUrl,
  sortSalaryRanges,
} from "./trail-format";

describe("formatSalaryRange", () => {
  it("mostra 'R$ mín – máx' com separador de milhar", () => {
    expect(formatSalaryRange(2800, 4200)).toBe("R$ 2.800 – 4.200");
    expect(formatSalaryRange(7050, 16076)).toBe("R$ 7.050 – 16.076");
  });
});

describe("formatBrl", () => {
  it("formata o valor de referência", () => {
    expect(formatBrl(3446)).toBe("R$ 3.446");
  });
});

describe("formatMonthYear", () => {
  it.each([
    ["2026-10-16", "10/2026"],
    ["2026-09", "09/2026"],
  ])("converte %s em %s", (input, expected) => {
    expect(formatMonthYear(input)).toBe(expected);
  });

  it("devolve o texto original se o formato for inesperado", () => {
    expect(formatMonthYear("outubro")).toBe("outubro");
  });
});

describe("formatWeeks", () => {
  it("usa singular e plural", () => {
    expect(formatWeeks(1)).toBe("1 semana");
    expect(formatWeeks(3)).toBe("3 semanas");
  });
});

describe("isSafeExternalUrl", () => {
  it("aceita só https", () => {
    expect(isSafeExternalUrl("https://exemplo.com/curso")).toBe(true);
    expect(isSafeExternalUrl("http://exemplo.com")).toBe(false);
    expect(isSafeExternalUrl("javascript:alert(1)")).toBe(false);
    expect(isSafeExternalUrl("//exemplo.com")).toBe(false);
  });
});

describe("sortSalaryRanges", () => {
  it("ordena Júnior, Pleno, Sênior sem alterar o original", () => {
    const base = { minimo: 0, maximo: 0, valorReferencia: 0, tipoReferencia: "", fonte: "", dataReferencia: "", dadosLimitados: false, observacao: "" };
    const input = [
      { ...base, nivel: "SENIOR" as const },
      { ...base, nivel: "JUNIOR" as const },
      { ...base, nivel: "PLENO" as const },
    ];
    expect(sortSalaryRanges(input).map((f) => f.nivel)).toEqual(["JUNIOR", "PLENO", "SENIOR"]);
    expect(input[0].nivel).toBe("SENIOR");
  });
});
