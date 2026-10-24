import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import { Reveal } from "@/components/ui/reveal";
import { focusRing } from "@/components/ui/states";
import { StepCard } from "@/components/ui/step-card";
import { PdfDownload } from "@/features/trail/pdf-download";
import { SalaryRanges } from "@/features/trail/salary-ranges";
import { StepDetails } from "@/features/trail/step-details";
import { getArea, getAreas, getTrilha, getTrilhaPdfUrl } from "@/lib/api";
import { routes } from "@/lib/routes";
import {
  entryLevelLabels,
  formatMonthYear,
  formatWeeks,
  stepLevelLabels,
} from "@/lib/trail-format";

// As 36 trilhas são geradas no build: a lista vem das 8 áreas. Com Cache Components a lista
// não pode ser vazia. Slug fora da lista é gerado na primeira visita (dynamicParams padrão).
export async function generateStaticParams() {
  const areas = await getAreas();
  const details = await Promise.all(areas.map((area) => getArea(area.slug)));
  return details.flatMap((area) => area?.trilhas.map((trilha) => ({ slug: trilha.slug })) ?? []);
}

// Slug que a API não conhece (getTrilha devolve null) vira a página "não encontrada".
async function loadTrilha(slug: string) {
  const trilha = await getTrilha(slug);
  if (!trilha) notFound();
  return trilha;
}

export async function generateMetadata({ params }: PageProps<"/trilhas/[slug]">): Promise<Metadata> {
  const { slug } = await params;
  const trilha = await loadTrilha(slug);
  return {
    title: `${trilha.nome} | Trilha Tech`,
    description: trilha.resumo,
  };
}

const label = "font-mono text-caption uppercase text-ink-soft md:text-label";
const infoCard =
  "flex min-w-0 flex-1 flex-col gap-2 rounded-16 border-3 border-ink bg-white px-5 pt-[18px] pb-5 shadow-sm md:px-[26px] md:pt-[22px] md:pb-6";

export default async function TrailPage({ params }: PageProps<"/trilhas/[slug]">) {
  const { slug } = await params;
  const trilha = await loadTrilha(slug);
  const steps = [...trilha.etapas].sort((a, b) => a.ordem - b.ordem);

  const metrics = [
    { label: "Nível de entrada", value: entryLevelLabels[trilha.nivelEntrada] },
    { label: "Por semana", value: `≈ ${trilha.horasPorSemana} h` },
    { label: "Duração total", value: `≈ ${formatWeeks(trilha.duracaoTotalSemanas)}` },
  ];

  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <div className="flex flex-col items-start gap-5 pt-3 pb-12 md:gap-6 md:pt-8 md:pb-[72px]">
        <nav aria-label="Trilha de navegação" className={label}>
          <Link href={routes.destinos} className={`rounded-8 hover:underline ${focusRing}`}>
            Destinos
          </Link>{" "}
          <span aria-hidden="true">/</span>{" "}
          <Link
            href={`/areas/${trilha.area.slug}`}
            className={`rounded-8 hover:underline ${focusRing}`}
          >
            {trilha.area.nome}
          </Link>
          {/* No mobile o Figma termina na área; no desktop mostra também a trilha. */}
          <span className="hidden md:inline">
            {" "}
            <span aria-hidden="true">/</span> <span aria-current="page">{trilha.nome}</span>
          </span>
        </nav>

        <h1 className="break-words font-display text-[34px] leading-[1.12] font-bold tracking-[-0.04em] md:text-h1">
          {trilha.nome}
        </h1>
        <p className="font-body text-body-md text-ink-soft md:text-body-lg">{trilha.resumo}</p>

        <div className="flex w-full flex-col gap-4 md:flex-row md:gap-6">
          <section aria-labelledby="titulo-sobre" className={infoCard}>
            <h2 id="titulo-sobre" className={label}>
              Sobre esta trilha
            </h2>
            <p className="break-words font-body text-body-sm text-ink md:text-body-md">
              {trilha.textoTrilha}
            </p>
          </section>
          <section aria-labelledby="titulo-para-quem" className={infoCard}>
            <h2 id="titulo-para-quem" className={label}>
              Para quem é
            </h2>
            <p className="break-words font-body text-body-sm text-ink md:text-body-md">
              {trilha.paraQuemE}
            </p>
          </section>
        </div>

        <dl className="flex flex-wrap gap-2.5 md:gap-3.5">
          {metrics.map((metric) => (
            <div
              key={metric.label}
              className="flex items-center gap-2 rounded-12 border-2 border-ink bg-white px-3 py-2 font-mono text-caption uppercase md:gap-2.5 md:px-4 md:py-2.5"
            >
              <dt className="text-ink-soft">{metric.label}</dt>
              <dd className="text-ink md:text-label">{metric.value}</dd>
            </div>
          ))}
        </dl>

        <PdfDownload href={getTrilhaPdfUrl(trilha.slug)} fileName={`roadmap-${trilha.slug}.pdf`} />

        <SalaryRanges ranges={trilha.faixasSalariais} notice={trilha.avisosSalario} />

        <h2 className={label}>
          Roadmap · {steps.length} {steps.length === 1 ? "etapa" : "etapas"}
        </h2>
        <ol className="flex w-full flex-col gap-5 md:gap-[22px]">
          {steps.map((step, index) => (
            <Reveal as="li" key={step.ordem}>
              <StepCard
                index={index}
                number={String(index + 1).padStart(2, "0")}
                title={step.titulo}
                summary={step.descricao}
                level={stepLevelLabels[step.nivel]}
              >
                <StepDetails step={step} />
              </StepCard>
            </Reveal>
          ))}
        </ol>

        <p className="font-body text-body-sm text-ink-soft">
          Recursos de estudo e fontes verificados em {formatMonthYear(trilha.conteudoConferidoEm)}.
        </p>
      </div>
    </div>
  );
}
