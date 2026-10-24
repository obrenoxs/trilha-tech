import { AreaTicket } from "@/components/ui/area-ticket";
import { colorForIndex } from "@/components/ui/area-colors";
import { Reveal } from "@/components/ui/reveal";
import type { Area } from "@/lib/api";

export function Destinations({ areas }: { areas: Area[] }) {
  return (
    <section
      id="destinos"
      aria-labelledby="titulo-destinos"
      className="flex flex-col items-start gap-3 pt-6 pb-8 md:pb-14"
    >
      <p className="font-mono text-caption uppercase text-ink-soft md:text-label">
        Destinos disponíveis
      </p>
      <h2
        id="titulo-destinos"
        className="font-display text-[28px] leading-[1.1] font-bold tracking-[-0.04em] md:text-h2"
      >
        {areas.length} áreas, vários caminhos
      </h2>
      <p className="font-body text-body-md text-ink-soft md:text-body-lg">
        Cada destino reúne trilhas por tecnologia, com roadmap, faixa salarial estimada e PDF
        para baixar.
      </p>
      <ul className="grid w-full grid-cols-1 gap-[18px] pt-3 lg:grid-cols-2 md:gap-6 md:pt-5">
        {areas.map((area, index) => (
          <Reveal as="li" key={area.slug} className="flex">
            <AreaTicket
              href={`/areas/${area.slug}`}
              color={colorForIndex(index)}
              number={area.numero}
              name={area.nome}
              trailCount={area.quantidadeTrilhas}
              className="w-full"
            />
          </Reveal>
        ))}
      </ul>
    </section>
  );
}
