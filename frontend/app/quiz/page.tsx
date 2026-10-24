import type { Metadata } from "next";
import { getQuiz } from "@/lib/api";
import { QuizFlow } from "@/features/quiz/quiz-flow";

export const metadata: Metadata = {
  title: "Quiz: descubra por qual área de TI começar | Trilha Tech",
  description:
    "12 perguntas rápidas para sugerir áreas de TI para você explorar primeiro. O quiz recomenda áreas, não vagas.",
};

export default async function QuizPage() {
  const perguntas = [...(await getQuiz())].sort((a, b) => a.ordem - b.ordem);

  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-24">
      <h1 className="sr-only">Quiz de 12 perguntas</h1>
      <QuizFlow perguntas={perguntas} />
    </div>
  );
}
