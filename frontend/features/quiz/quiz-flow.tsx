"use client";

import { useRouter } from "next/navigation";
import { useReducer, useState, useSyncExternalStore } from "react";

import { postQuizResultado } from "@/lib/api/client";
import { ApiError } from "@/lib/api/errors";
import type { Pergunta } from "@/lib/api/types";
import { routes } from "@/lib/routes";

import { buildResultRequest, IncompleteAnswersError } from "./answers";
import { setFreshResult } from "./fresh-result";
import { QuestionScreen } from "./question-screen";
import { initialQuizState, quizReducer } from "./quiz-reducer";
import { saveSlugs } from "./saved-result";
import { shuffleAlternatives } from "./shuffle";

const subscribeNothing = () => () => {};

const BAD_REQUEST_MESSAGE =
  "Alguma pergunta ficou sem resposta. Volte e confira as respostas antes de enviar.";
const OTHER_ERROR_MESSAGE = "Não foi possível enviar suas respostas agora. Tente de novo.";

/**
 * O embaralhamento usa Math.random, então só pode rodar no navegador: se rodasse no servidor,
 * o HTML e a hidratação teriam ordens diferentes. Antes de hidratar mostramos a pergunta 1 sem
 * alternativas; depois o `QuizSession` monta e embaralha uma vez por visita.
 */
export function QuizFlow({ perguntas }: { perguntas: Pergunta[] }) {
  const isClient = useSyncExternalStore(
    subscribeNothing,
    () => true,
    () => false,
  );

  if (!isClient) {
    const first = perguntas[0];
    return (
      <QuestionScreen
        index={0}
        total={perguntas.length}
        perguntaOrdem={first.ordem}
        texto={first.texto}
        alternativas={[]}
        selected={undefined}
        submitting={false}
        error={null}
        onSelect={() => {}}
        onBack={() => {}}
        onNext={() => {}}
      />
    );
  }
  return <QuizSession perguntas={perguntas} />;
}

function QuizSession({ perguntas }: { perguntas: Pergunta[] }) {
  const router = useRouter();
  // Embaralha uma única vez; cada alternativa guarda a `ordem` original da API.
  const [shuffled] = useState(() => perguntas.map((pergunta) => shuffleAlternatives(pergunta)));
  const [state, dispatch] = useReducer(quizReducer, perguntas.length, initialQuizState);

  // Sentido da última troca de pergunta, só para a animação de entrada (nada no carregamento).
  const [enter, setEnter] = useState<"forward" | "back" | null>(null);

  const pergunta = shuffled[state.index];
  const isLast = state.index === state.total - 1;

  async function submit() {
    dispatch({ type: "submit" });
    try {
      const result = await postQuizResultado(buildResultRequest(perguntas, state.answers));
      // Resultado em memória (leva a "barra") e só os slugs no navegador, se ele deixar.
      setFreshResult(result);
      saveSlugs(
        [...result.recomendacoes].sort((a, b) => a.posicao - b.posicao).map((item) => item.slug),
      );
      router.push(routes.quizResultado);
    } catch (error) {
      const badRequest =
        error instanceof IncompleteAnswersError ||
        (error instanceof ApiError && error.kind === "bad-request");
      dispatch({ type: "submit-failed", errorKind: badRequest ? "bad-request" : "other" });
    }
  }

  let error: string | null = null;
  if (state.errorKind === "bad-request") error = BAD_REQUEST_MESSAGE;
  else if (state.errorKind === "other") error = OTHER_ERROR_MESSAGE;

  return (
    <QuestionScreen
      index={state.index}
      total={state.total}
      perguntaOrdem={pergunta.ordem}
      texto={pergunta.texto}
      alternativas={pergunta.alternativas}
      selected={state.answers[pergunta.ordem]}
      submitting={state.status === "submitting"}
      error={error}
      onSelect={(alternativa) =>
        dispatch({ type: "select", pergunta: pergunta.ordem, alternativa })
      }
      enter={enter}
      onBack={() => {
        setEnter("back");
        dispatch({ type: "back" });
      }}
      onNext={() => {
        if (isLast) return void submit();
        setEnter("forward");
        dispatch({ type: "next" });
      }}
    />
  );
}
