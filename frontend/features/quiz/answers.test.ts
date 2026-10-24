import { describe, expect, it } from "vitest";

import type { Pergunta } from "@/lib/api/types";

import { buildResultRequest, IncompleteAnswersError } from "./answers";

const perguntas: Pergunta[] = Array.from({ length: 12 }, (_, i) => ({
  ordem: i + 1,
  texto: `Pergunta ${i + 1}`,
  alternativas: [1, 2, 3, 4].map((ordem) => ({ ordem, texto: `Alt ${ordem}` })),
}));

describe("buildResultRequest", () => {
  it("monta 12 pares com as ordens originais, na ordem das perguntas", () => {
    // As respostas chegam fora de ordem; o corpo sai ordenado pela pergunta.
    const answers = Object.fromEntries(
      [...perguntas].reverse().map((p) => [p.ordem, ((p.ordem + 1) % 4) + 1]),
    );

    const body = buildResultRequest([...perguntas].reverse(), answers);

    expect(body.respostas).toHaveLength(12);
    expect(body.respostas[0]).toEqual({ pergunta: 1, alternativa: 3 });
    expect(body.respostas[11]).toEqual({ pergunta: 12, alternativa: 2 });
    expect(body.respostas.map((r) => r.pergunta)).toEqual(perguntas.map((p) => p.ordem));
  });

  it("usa só os campos pergunta e alternativa", () => {
    const answers = Object.fromEntries(perguntas.map((p) => [p.ordem, 2]));
    const body = buildResultRequest(perguntas, answers);
    expect(Object.keys(body.respostas[0]).sort()).toEqual(["alternativa", "pergunta"]);
  });

  it("recusa quando falta uma resposta", () => {
    const answers = Object.fromEntries(perguntas.slice(1).map((p) => [p.ordem, 1]));
    expect(() => buildResultRequest(perguntas, answers)).toThrow(IncompleteAnswersError);
  });

  it("recusa uma alternativa que não pertence à pergunta", () => {
    const answers = Object.fromEntries(perguntas.map((p) => [p.ordem, 1]));
    answers[5] = 9;
    expect(() => buildResultRequest(perguntas, answers)).toThrow(IncompleteAnswersError);
  });
});
