import { describe, expect, it } from "vitest";

import { initialQuizState, quizReducer } from "./quiz-reducer";

describe("quizReducer", () => {
  it("guarda a resposta pela ordem da pergunta e da alternativa", () => {
    const state = quizReducer(initialQuizState(12), { type: "select", pergunta: 1, alternativa: 3 });
    expect(state.answers).toEqual({ 1: 3 });
  });

  it("avança e volta, sem sair dos limites", () => {
    let state = initialQuizState(2);
    state = quizReducer(state, { type: "back" });
    expect(state.index).toBe(0);
    state = quizReducer(state, { type: "next" });
    expect(state.index).toBe(1);
    state = quizReducer(state, { type: "next" });
    expect(state.index).toBe(1);
    state = quizReducer(state, { type: "back" });
    expect(state.index).toBe(0);
  });

  it("mantém as respostas ao voltar", () => {
    let state = initialQuizState(12);
    state = quizReducer(state, { type: "select", pergunta: 1, alternativa: 2 });
    state = quizReducer(state, { type: "next" });
    state = quizReducer(state, { type: "back" });
    expect(state.answers[1]).toBe(2);
  });

  it("ignora ações durante o envio, exceto a falha", () => {
    let state = quizReducer(initialQuizState(12), { type: "submit" });
    expect(quizReducer(state, { type: "next" })).toBe(state);
    state = quizReducer(state, { type: "submit-failed", errorKind: "bad-request" });
    expect(state).toMatchObject({ status: "error", errorKind: "bad-request" });
  });
});
