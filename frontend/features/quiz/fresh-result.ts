import type { ResultadoQuiz } from "@/lib/api/types";

// O resultado recém-calculado fica só em memória (some ao recarregar). Ele leva o campo "barra"
// até a tela de resultado, que não pode vir do localStorage (lá guardamos só os slugs), e faz o
// fluxo funcionar mesmo com o localStorage bloqueado.
let current: ResultadoQuiz | null = null;

export const getFreshResult = () => current;
export const setFreshResult = (result: ResultadoQuiz) => {
  current = result;
};
export const clearFreshResult = () => {
  current = null;
};
