import type { Alternativa, Pergunta } from "@/lib/api/types";

/** Fisher-Yates em uma cópia. `random` é injetável para os testes. */
export function shuffle<T>(items: readonly T[], random: () => number = Math.random): T[] {
  const copy = [...items];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}

/**
 * Embaralha só a ordem de exibição. Cada alternativa continua com a `ordem` original da API,
 * que é o que vai no POST. As letras A, B, C, D vêm da posição na tela, não da `ordem`.
 */
export function shuffleAlternatives(
  pergunta: Pergunta,
  random: () => number = Math.random,
): Pergunta {
  return { ...pergunta, alternativas: shuffle<Alternativa>(pergunta.alternativas, random) };
}
