import type { Metadata } from "next";
import { getAreas } from "@/lib/api";
import { QuizResult } from "@/features/quiz/quiz-result";

export const metadata: Metadata = {
  title: "Seu resultado do quiz",
  description: "Três áreas de TI sugeridas para você começar a explorar.",
  robots: { index: false },
};

export default async function QuizResultPage() {
  // As áreas vêm da API (cache de 1 hora) e servem para revalidar os slugs guardados no navegador.
  const areas = await getAreas();

  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <QuizResult areas={areas} />
    </div>
  );
}
