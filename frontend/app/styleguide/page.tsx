import { notFound } from "next/navigation";
import { AreaTicket } from "@/components/ui/area-ticket";
import { AssistantButton } from "@/components/ui/assistant-button";
import {
  Badge,
  ConfirmPriceBadge,
  FreeBadge,
  LevelBadge,
  LimitedDataBadge,
  PaidBadge,
} from "@/components/ui/badges";
import { Button } from "@/components/ui/button";
import { SmallButton } from "@/components/ui/small-button";
import { StepCard } from "@/components/ui/step-card";

// Para mostrar o estado hover lado a lado (como no Figma) sem precisar do mouse:
// as mesmas utilidades do :hover, aplicadas direto. O "!" vence a sombra padrão.
const forcedHover = "translate-x-1 translate-y-1 shadow-pressed!";
const forcedHoverCoral = "translate-x-1 translate-y-1 shadow-coral-pressed!";
const forcedHoverSmall = "translate-x-0.5 translate-y-0.5 shadow-pressed!";

const ticketColors = ["coral", "mint", "lemon", "periwinkle"] as const;

// Estados forçados (mesmas utilidades de :active e :focus-visible).
const focusForced = "outline-3 outline-offset-3 outline-ink";
const focusForcedDark = "outline-3 outline-offset-3 outline-white";

const stateRows: {
  name: string;
  hover: string;
  pressed: string;
  focus: string;
  render: (className: string, disabled?: boolean) => React.ReactNode;
}[] = [
  {
    name: "Botão primário",
    hover: forcedHover,
    pressed: "translate-x-1.5 translate-y-1.5 shadow-none!",
    focus: focusForced,
    render: (className, disabled) => (
      <Button className={className} disabled={disabled}>
        Fazer o quiz
      </Button>
    ),
  },
  {
    name: "Botão secundário",
    hover: forcedHover,
    pressed: "translate-x-1.5 translate-y-1.5 shadow-none!",
    focus: focusForced,
    render: (className, disabled) => (
      <Button variant="secondary" className={className} disabled={disabled}>
        Fazer o quiz
      </Button>
    ),
  },
  {
    name: "Botão escuro",
    hover: forcedHoverCoral,
    pressed: "translate-x-[5px] translate-y-[5px] shadow-none!",
    focus: focusForced,
    render: (className, disabled) => (
      <Button variant="dark" className={className} disabled={disabled}>
        Fazer o quiz
      </Button>
    ),
  },
  {
    name: "Botão pequeno",
    hover: forcedHoverSmall,
    pressed: "translate-x-1 translate-y-1 shadow-none!",
    focus: focusForced,
    render: (className, disabled) => (
      <SmallButton className={className} disabled={disabled}>
        Fazer o quiz
      </SmallButton>
    ),
  },
  {
    name: "Botão IA",
    hover: "translate-x-1 translate-y-1 shadow-coral-pressed!",
    pressed: "translate-x-[5px] translate-y-[5px] shadow-none!",
    focus: focusForced,
    render: (className, disabled) => <AssistantButton className={className} disabled={disabled} />,
  },
];

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
    <div className="flex flex-col items-start gap-10 overflow-hidden p-12">
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

      <h2 className="font-display text-h3 whitespace-nowrap">Componentes</h2>

      <section aria-label="Botões" className="flex flex-col items-start gap-6">
        <p className="font-mono text-caption uppercase text-ink-soft">
          Botão · padrão (esquerda) e hover (direita)
        </p>
        <div className="flex items-start gap-16">
          <div className="flex flex-col items-start gap-6">
            <Button>Fazer o quiz</Button>
            <Button variant="secondary">Fazer o quiz</Button>
            <Button variant="dark">Fazer o quiz</Button>
          </div>
          <div className="flex flex-col items-start gap-6">
            <Button className={forcedHover}>Fazer o quiz</Button>
            <Button variant="secondary" className={forcedHover}>
              Fazer o quiz
            </Button>
            <Button variant="dark" className={forcedHoverCoral}>
              Fazer o quiz
            </Button>
          </div>
        </div>

        <p className="font-mono text-caption uppercase text-ink-soft">Botão IA</p>
        <div className="flex items-start gap-16">
          <AssistantButton />
          <AssistantButton className="translate-x-1 translate-y-1 shadow-coral-pressed!" />
        </div>

        <p className="font-mono text-caption uppercase text-ink-soft">Botão pequeno</p>
        <div className="flex items-start gap-16">
          <SmallButton>Fazer o quiz</SmallButton>
          <SmallButton className={forcedHoverSmall}>Fazer o quiz</SmallButton>
        </div>

        <p className="font-mono text-caption uppercase text-ink-soft">
          Como link (&lt;a&gt;), com href
        </p>
        <Button href="#styleguide-link">Fazer o quiz</Button>
      </section>

      <section aria-label="Estados" className="flex flex-col items-start gap-6">
        <p className="font-mono text-caption uppercase text-ink-soft">
          Estados · padrão, hover, pressionado, foco e desabilitado (hover, pressionado e foco
          forçados para comparar; o real vale com mouse, clique e Tab)
        </p>
        {stateRows.map((row) => (
          <div key={row.name} className="flex flex-col items-start gap-3">
            <p className="font-mono text-caption uppercase">{row.name}</p>
            <div className="flex items-start gap-8">
              {row.render("")}
              {row.render(row.hover)}
              {row.render(row.pressed)}
              {row.render(row.focus)}
              {row.render("", true)}
            </div>
          </div>
        ))}

        <p className="font-mono text-caption uppercase">Link desabilitado (aria-disabled)</p>
        <div className="flex items-start gap-10">
          <Button href="#styleguide-link" aria-disabled="true">
            Fazer o quiz
          </Button>
          <SmallButton href="#styleguide-link" aria-disabled="true">
            Fazer o quiz
          </SmallButton>
        </div>

        <p className="font-mono text-caption uppercase">Ticket · pressionado e foco</p>
        <div className="flex items-start gap-10">
          <div className="w-[560px]">
            <AreaTicket
              href="#styleguide-ticket"
              color="mint"
              number="02"
              name="Dados e IA"
              trailCount={5}
              className="translate-x-1.5 translate-y-1.5 shadow-none!"
            />
          </div>
          <div className="w-[560px] p-1">
            <AreaTicket
              href="#styleguide-ticket"
              color="lemon"
              number="03"
              name="Dados e IA"
              trailCount={1}
              className={focusForced}
            />
          </div>
        </div>

        <p className="font-mono text-caption uppercase">
          Foco em superfície escura (data-surface=&quot;dark&quot;): contorno branco
        </p>
        <div data-surface="dark" className="flex items-start gap-10 bg-ink p-8">
          <Button className={focusForcedDark}>Fazer o quiz</Button>
          <SmallButton className={focusForcedDark}>Fazer o quiz</SmallButton>
          <Button variant="dark" className={focusForcedDark}>
            Fazer o quiz
          </Button>
          <AssistantButton className={focusForcedDark} />
        </div>
      </section>

      <section aria-label="Ticket de área" className="flex flex-col items-start gap-6">
        <p className="font-mono text-caption uppercase text-ink-soft">
          Ticket de área · padrão (esquerda) e hover (direita)
        </p>
        <div className="flex items-start gap-8">
          <div className="flex w-[560px] flex-col gap-6">
            {ticketColors.map((color) => (
              <AreaTicket
                key={color}
                href="#styleguide-ticket"
                color={color}
                number="01"
                name="Desenvolvimento de Software"
                trailCount={7}
              />
            ))}
          </div>
          <div className="flex w-[560px] flex-col gap-6">
            {ticketColors.map((color) => (
              <AreaTicket
                key={color}
                href="#styleguide-ticket"
                color={color}
                number="01"
                name="Desenvolvimento de Software"
                trailCount={7}
                className={forcedHover}
              />
            ))}
          </div>
        </div>
      </section>

      <section aria-label="Selos" className="flex flex-col items-start gap-4">
        <p className="font-mono text-caption uppercase text-ink-soft">
          Selos · nível, tipo (documentação, curso), grátis, pago, confirmar preço, dados limitados
        </p>
        <div className="flex flex-wrap items-center gap-4">
          <LevelBadge level="Iniciante" />
          <Badge>Documentação</Badge>
          <Badge>Curso</Badge>
          <FreeBadge />
          <PaidBadge />
          <ConfirmPriceBadge />
          <LimitedDataBadge />
        </div>
      </section>

      <section aria-label="Cartão de etapa" className="flex w-[1088px] flex-col items-start gap-6">
        <p className="font-mono text-caption uppercase text-ink-soft">
          Cartão de etapa · fechado, com “+”, e aberto
        </p>
        <ol className="flex w-full flex-col gap-5">
          <li>
            <StepCard
              index={0}
              number="01"
              title="Lógica de programação"
              summary="Entenda variáveis, condições e laços antes de qualquer linguagem."
              level="Iniciante"
            />
          </li>
          <li>
            <StepCard
              index={1}
              number="02"
              title="Fundamentos de Java"
              summary="Sintaxe, tipos, classes e orientação a objetos."
              level="Iniciante"
            >
              <p className="font-body text-body-md">Conteúdo da etapa (exemplo).</p>
            </StepCard>
          </li>
          <li>
            <StepCard
              index={2}
              number="03"
              title="Spring Boot"
              summary="Crie sua primeira API REST com Spring Boot: rotas, controladores, validação de dados e tratamento de erros."
              level="Intermediário"
              defaultExpanded
            >
              <p className="font-body text-body-md">
                Conteúdo da etapa (exemplo): projeto prático, duração e recursos entram na fase da
                Trilha.
              </p>
            </StepCard>
          </li>
          <li>
            <StepCard
              index={3}
              number="04"
              title="SQL e bancos relacionais"
              summary="Modele tabelas e faça consultas."
              level="Iniciante"
            />
          </li>
        </ol>
      </section>
    </div>
  );
}
