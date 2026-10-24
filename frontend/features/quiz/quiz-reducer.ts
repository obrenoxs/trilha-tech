import type { Answers } from "./answers";

export type QuizState = {
  index: number;
  total: number;
  answers: Answers;
  status: "idle" | "submitting" | "error";
  /** Tipo do erro do envio, para a tela escolher o texto. */
  errorKind: "bad-request" | "other" | null;
};

export type QuizAction =
  | { type: "select"; pergunta: number; alternativa: number }
  | { type: "next" }
  | { type: "back" }
  | { type: "submit" }
  | { type: "submit-failed"; errorKind: "bad-request" | "other" };

export const initialQuizState = (total: number): QuizState => ({
  index: 0,
  total,
  answers: {},
  status: "idle",
  errorKind: null,
});

export function quizReducer(state: QuizState, action: QuizAction): QuizState {
  if (state.status === "submitting" && action.type !== "submit-failed") return state;
  switch (action.type) {
    case "select":
      return {
        ...state,
        answers: { ...state.answers, [action.pergunta]: action.alternativa },
        status: "idle",
        errorKind: null,
      };
    case "next":
      return state.index < state.total - 1
        ? { ...state, index: state.index + 1, status: "idle", errorKind: null }
        : state;
    case "back":
      return state.index > 0
        ? { ...state, index: state.index - 1, status: "idle", errorKind: null }
        : state;
    case "submit":
      return { ...state, status: "submitting", errorKind: null };
    case "submit-failed":
      return { ...state, status: "error", errorKind: action.errorKind };
  }
}
