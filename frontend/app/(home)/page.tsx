import type { Metadata } from "next";
import { getAreas } from "@/lib/api";
import { Destinations } from "@/features/home/destinations";
import { Hero } from "@/features/home/hero";
import { HowItWorks } from "@/features/home/how-it-works";
import { SavedQuizResultShortcut } from "@/features/quiz/saved-result-shortcut";

export const metadata: Metadata = {
  title: "Trilha Tech: sua passagem para a primeira vaga em TI",
  description:
    "Descubra qual área de TI seguir. Compare 8 áreas e 36 trilhas, veja roadmaps com recursos de estudo e faixa salarial estimada. Sem cadastro.",
};

export default async function Home() {
  const areas = await getAreas();

  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <Hero />
      <SavedQuizResultShortcut areas={areas} />
      <Destinations areas={areas} />
      <HowItWorks />
    </div>
  );
}
