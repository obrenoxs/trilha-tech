import { Button } from "@/components/ui/button";
import { routes } from "@/lib/routes";
import { BoardingPass } from "./boarding-pass";

export function Hero() {
  return (
    <section
      aria-labelledby="titulo-hero"
      className="flex flex-col gap-[22px] pt-4 pb-10 md:flex-row md:items-center md:gap-10 md:pt-12 md:pb-[88px]"
    >
      <div className="flex flex-col items-start gap-[22px] md:w-[500px] md:shrink-0 md:gap-7">
        <p className="font-mono text-caption uppercase text-ink-soft md:text-label">
          Orientação de carreira em TI
        </p>
        <h1
          id="titulo-hero"
          className="font-display text-[36px] leading-[1.12] font-bold tracking-[-0.04em] md:text-h1"
        >
          Sua passagem para a primeira vaga em TI.
        </h1>
        <p className="font-body text-body-md text-ink-soft md:text-body-lg">
          Escolha um destino entre 8 áreas, compare trilhas de estudo e siga o roadmap até a
          primeira vaga. Sem cadastro.
        </p>
        <div className="flex flex-col items-start gap-3.5 md:gap-4">
          <Button href={routes.quiz}>Fazer o quiz de 12 perguntas</Button>
          <Button href={routes.destinos} variant="secondary">
            Ver os destinos
          </Button>
        </div>
      </div>
      <div className="md:min-w-0 md:flex-1">
        <BoardingPass />
      </div>
    </section>
  );
}
