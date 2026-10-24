import { notFound } from "next/navigation";

// Rota interna: existe só em desenvolvimento, para comparar com o frame 2:27 do Figma.
const colors = [
  { name: "bg", hex: "#E6EEF2", className: "bg-bg" },
  { name: "ink", hex: "#14213D", className: "bg-ink" },
  { name: "ink-soft", hex: "#4B587A", className: "bg-ink-soft" },
  { name: "white", hex: "#FFFFFF", className: "bg-white" },
  { name: "coral", hex: "#FF8A6B", className: "bg-coral" },
  { name: "mint", hex: "#7EE0B8", className: "bg-mint" },
  { name: "lemon", hex: "#F6E27A", className: "bg-lemon" },
  { name: "periwinkle", hex: "#A9B8FF", className: "bg-periwinkle" },
];

const typography = [
  {
    name: "Display/H1",
    className: "font-display text-h1 whitespace-nowrap",
    sample: "Sua passagem para a primeira vaga",
  },
  {
    name: "Display/H2",
    className: "font-display text-h2",
    sample: "8 áreas, vários caminhos",
  },
  {
    name: "Display/H3",
    className: "font-display text-h3",
    sample: "Lógica de programação",
  },
  {
    name: "Display/Card",
    className: "font-display text-card",
    sample: "Desenvolvimento de Software",
  },
  {
    name: "Display/Num",
    className: "font-display text-num",
    sample: "36",
  },
  {
    name: "Display/Logo",
    className: "font-display text-logo",
    sample: "Trilha Tech",
  },
  {
    name: "Mono/Label",
    className: "font-mono text-label uppercase",
    sample: "Destinos disponíveis",
  },
  {
    name: "Corpo/Grande",
    className: "font-body text-body-lg",
    sample: "Escolha um destino entre 8 áreas e siga o roadmap até a primeira vaga.",
  },
  {
    name: "Corpo/Medio",
    className: "font-body text-body-md",
    sample: "Cada destino reúne trilhas por tecnologia.",
  },
  {
    name: "Corpo/Pequeno",
    className: "font-body text-body-sm",
    sample: "Cada destino reúne trilhas por tecnologia.",
  },
  {
    name: "Mono/Rodape",
    className: "font-mono text-footer",
    sample: "Trilha Tech",
  },
  {
    name: "UI/Nav",
    className: "font-body text-nav",
    sample: "Como funciona",
  },
  {
    name: "UI/Botao",
    className: "font-body text-button",
    sample: "Fazer o quiz de 12 perguntas",
  },
];

const shadows = [
  { name: "Sombra/Pressionado", className: "shadow-pressed" },
  { name: "Sombra/Pequena", className: "shadow-sm" },
  { name: "Sombra/Media", className: "shadow-md" },
  { name: "Sombra/Grande", className: "shadow-lg" },
];

export default function StyleguidePage() {
  if (process.env.NODE_ENV === "production") notFound();

  return (
    <main className="flex flex-col items-start gap-10 overflow-hidden p-12">
      <h1 className="font-display text-h3 whitespace-nowrap">
        Trilha Tech · Fundamentos (Bilhete)
      </h1>

      <section aria-label="Cores" className="flex items-start gap-4">
        {colors.map((color) => (
          <div key={color.name} className="flex flex-col items-start gap-2">
            <div
              className={`${color.className} h-[90px] w-[140px] rounded-12 border-3 border-ink shadow-sm`}
            />
            <p className="font-mono text-caption uppercase">{color.name}</p>
            <p className="font-mono text-caption uppercase text-ink-soft">{color.hex}</p>
          </div>
        ))}
      </section>

      <section aria-label="Tipografia" className="flex flex-col items-start gap-3.5">
        {typography.map((style) => (
          <div key={style.name} className="flex items-center gap-6">
            <p className="w-[160px] shrink-0 font-mono text-caption uppercase text-ink-soft">
              {style.name}
            </p>
            <p className={style.className}>{style.sample}</p>
          </div>
        ))}
      </section>

      <section aria-label="Sombras" className="flex items-start gap-8">
        {shadows.map((shadow) => (
          <div
            key={shadow.name}
            title={shadow.name}
            className={`${shadow.className} h-[80px] w-[150px] rounded-14 border-3 border-ink bg-white`}
          />
        ))}
      </section>
    </main>
  );
}
