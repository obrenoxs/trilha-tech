import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import { focusRing } from "@/components/ui/states";
import { TrailCard } from "@/components/ui/trail-card";
import { getArea, getAreas } from "@/lib/api";
import { routes } from "@/lib/routes";

// As 8 áreas são geradas no build. Com Cache Components a lista não pode ser vazia.
export async function generateStaticParams() {
  const areas = await getAreas();
  return areas.map((area) => ({ slug: area.slug }));
}

// Slug que a API não conhece (getArea devolve null) vira a página "não encontrada".
// Outras falhas (API fora, rede) seguem como exceção e caem no error.tsx, sem ler o erro.
async function loadArea(slug: string) {
  const area = await getArea(slug);
  if (!area) notFound();
  return area;
}

export async function generateMetadata({ params }: PageProps<"/areas/[slug]">): Promise<Metadata> {
  const { slug } = await params;
  const area = await loadArea(slug);
  return {
    title: `${area.nome} | Trilha Tech`,
    description: area.descricao,
  };
}

const label = "font-mono text-caption uppercase text-ink-soft md:text-label";

export default async function AreaPage({ params }: PageProps<"/areas/[slug]">) {
  const { slug } = await params;
  const area = await loadArea(slug);
  const trailCount = area.trilhas.length;

  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <div className="flex flex-col items-start gap-5 pt-3 pb-12 md:gap-[26px] md:pt-8 md:pb-[72px]">
        <nav aria-label="Trilha de navegação" className={label}>
          <Link href={routes.destinos} className={`rounded-8 hover:underline ${focusRing}`}>
            Destinos
          </Link>{" "}
          <span aria-hidden="true">/</span>{" "}
          <span aria-current="page">Área {area.numero}</span>
        </nav>

        <h1 className="break-words font-display text-[30px] leading-[1.12] font-bold tracking-[-0.04em] md:text-h1">
          {area.nome}
        </h1>
        <p className="font-body text-body-md text-ink-soft md:text-body-lg">{area.descricao}</p>

        {area.comoETrabalhar && (
          <section
            aria-labelledby="titulo-como-e-trabalhar"
            className="flex w-full flex-col gap-2 rounded-16 border-3 border-ink bg-white px-5 pt-[18px] pb-5 shadow-sm md:gap-2.5 md:px-7 md:pt-[22px] md:pb-[26px]"
          >
            <h2 id="titulo-como-e-trabalhar" className={label}>
              Como é trabalhar nesta área
            </h2>
            <p className="font-body text-body-sm text-ink md:text-body-md">
              {area.comoETrabalhar}
            </p>
          </section>
        )}

        <h2 className={label}>
          {trailCount} {trailCount === 1 ? "trilha nesta área" : "trilhas nesta área"}
        </h2>
        <ul className="flex w-full flex-col gap-5 md:gap-[26px]">
          {area.trilhas.map((trilha, index) => (
            <li key={trilha.slug}>
              <TrailCard
                index={index}
                number={String(index + 1).padStart(2, "0")}
                name={trilha.nome}
                summary={trilha.resumo}
                href={`/trilhas/${trilha.slug}`}
              />
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
