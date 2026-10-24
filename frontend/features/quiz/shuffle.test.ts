import { describe, expect, it } from "vitest";

import type { Pergunta } from "@/lib/api/types";

import { shuffle, shuffleAlternatives } from "./shuffle";

const pergunta: Pergunta = {
  ordem: 3,
  texto: "Pergunta de teste",
  alternativas: [
    { ordem: 1, texto: "um" },
    { ordem: 2, texto: "dois" },
    { ordem: 3, texto: "três" },
    { ordem: 4, texto: "quatro" },
  ],
};

// Gerador previsível: sequência fixa de valores entre 0 e 1.
const sequence = (values: number[]) => {
  let i = 0;
  return () => values[i++ % values.length];
};

describe("shuffleAlternatives", () => {
  it("muda a ordem de exibição mas mantém a ordem original de cada alternativa", () => {
    const result = shuffleAlternatives(pergunta, sequence([0, 0, 0]));

    // Com random sempre 0, Fisher-Yates leva o primeiro item para o fim a cada passo.
    expect(result.alternativas.map((a) => a.ordem)).toEqual([2, 3, 4, 1]);
    // Cada texto continua ligado à sua `ordem` original.
    for (const alternativa of result.alternativas) {
      const original = pergunta.alternativas.find((a) => a.ordem === alternativa.ordem);
      expect(alternativa).toEqual(original);
    }
  });

  it("não perde nem repete alternativas, em qualquer sorteio", () => {
    for (let round = 0; round < 50; round++) {
      const ordens = shuffleAlternatives(pergunta).alternativas.map((a) => a.ordem);
      expect([...ordens].sort()).toEqual([1, 2, 3, 4]);
    }
  });

  it("não altera a pergunta recebida e preserva os demais campos", () => {
    const before = structuredClone(pergunta);
    const result = shuffleAlternatives(pergunta, sequence([0.9, 0.1, 0.5]));

    expect(pergunta).toEqual(before);
    expect(result.ordem).toBe(3);
    expect(result.texto).toBe("Pergunta de teste");
  });
});

describe("shuffle", () => {
  it("devolve uma cópia", () => {
    const input = [1, 2, 3];
    const result = shuffle(input);
    expect(result).not.toBe(input);
    expect(input).toEqual([1, 2, 3]);
  });
});
