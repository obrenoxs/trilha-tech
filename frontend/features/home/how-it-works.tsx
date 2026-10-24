import { areaColors, colorForIndex } from "@/components/ui/area-colors";

const steps = [
  {
    number: "01",
    title: "Explore os destinos",
    text: "Veja as 8 áreas de TI e as trilhas de cada uma. Cada destino mostra o que a área faz e por onde começar.",
  },
  {
    number: "02",
    title: "Siga o roadmap",
    text: "Cada trilha tem etapas em ordem, recursos de estudo e faixa salarial estimada. Se quiser, baixe tudo em PDF.",
  },
  {
    number: "03",
    title: "Quer ajuda? Faça o quiz",
    text: "São 12 perguntas, opcionais. O resultado sugere áreas para você explorar primeiro. Ele não indica vagas.",
  },
];

export function HowItWorks() {
  return (
    <section
      id="como-funciona"
      aria-labelledby="titulo-como-funciona"
      className="flex flex-col items-start gap-3 pt-9 pb-12 md:gap-7 md:py-[72px]"
    >
      <p className="font-mono text-caption uppercase text-ink-soft md:text-label">
        Como funciona
      </p>
      <h2
        id="titulo-como-funciona"
        className="font-display text-[30px] leading-[1.2] font-semibold tracking-[-0.025em] md:text-h2 md:leading-[1.1] md:font-bold md:tracking-[-0.04em]"
      >
        Três passos, sem cadastro
      </h2>
      <p className="font-body text-body-md text-ink-soft md:text-body-lg">
        Explore no seu ritmo. Não precisa criar conta para ver trilhas, roadmaps e faixas
        salariais.
      </p>
      <div className="flex w-full flex-col gap-5 pt-2 md:gap-6 md:pt-7">
        <ol className="grid grid-cols-1 gap-5 lg:grid-cols-3 md:gap-6">
          {steps.map((step, index) => (
            <li
              key={step.number}
              className="flex flex-col overflow-clip rounded-16 border-3 border-ink bg-white shadow-sm"
            >
              <p
                aria-hidden="true"
                className={`border-b-2 border-dashed border-ink px-[18px] py-3 font-display text-[28px] leading-[1.2] font-bold tracking-[-0.03em] md:px-6 md:py-[18px] md:text-num ${areaColors[colorForIndex(index)]}`}
              >
                {step.number}
              </p>
              <div className="flex flex-col gap-2 px-[18px] pt-[18px] pb-[22px] md:gap-2.5 md:px-6 md:pt-6 md:pb-7">
                <h3 className="font-display text-[18px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card">
                  {step.title}
                </h3>
                <p className="font-body text-body-sm text-ink-soft md:text-body-md">
                  {step.text}
                </p>
              </div>
            </li>
          ))}
        </ol>
        <aside className="flex flex-col gap-1.5 rounded-16 border-3 border-ink bg-periwinkle p-[18px] shadow-sm md:px-7 md:py-[22px]">
          <p className="font-mono text-caption uppercase md:text-label">
            Assistente de IA · em todas as páginas
          </p>
          <p className="font-body text-body-sm md:text-body-md">
            Ficou com dúvida? Pergunte. O assistente sabe em que página você está e responde
            só sobre TI e carreira. Não digite dados pessoais.
          </p>
        </aside>
      </div>
    </section>
  );
}
