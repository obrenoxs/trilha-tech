import type { Pergunta, ResultadoQuizRequest } from "@/lib/api/types";

/** Respostas em memória: `ordem` da pergunta -> `ordem` da alternativa escolhida (as da API). */
export type Answers = Readonly<Record<number, number>>;

export class IncompleteAnswersError extends Error {
  constructor() {
    super("Há perguntas sem resposta.");
    this.name = "IncompleteAnswersError";
  }
}

/**
 * Monta o corpo de `POST /api/quiz/resultado`: um par `{ pergunta, alternativa }` por pergunta,
 * na ordem das perguntas, sempre com as `ordem` originais (nunca a posição na tela).
 */
export function buildResultRequest(
  perguntas: readonly Pergunta[],
  answers: Answers,
): ResultadoQuizRequest {
  const respostas = [...perguntas]
    .sort((a, b) => a.ordem - b.ordem)
    .map((pergunta) => {
      const alternativa = answers[pergunta.ordem];
      const valid = pergunta.alternativas.some((item) => item.ordem === alternativa);
      if (!valid) throw new IncompleteAnswersError();
      return { pergunta: pergunta.ordem, alternativa };
    });
  return { respostas };
}
