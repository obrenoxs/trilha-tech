import type { ReactNode } from "react";

const tones = {
  outline: "bg-white",
  mint: "bg-mint",
  coral: "bg-coral",
  lemon: "bg-lemon",
};

type BadgeProps = { tone?: keyof typeof tones; className?: string; children: ReactNode };

// Pílula do Figma: borda de 2px, raio total, texto mono em caixa alta.
export function Badge({ tone = "outline", className = "", children }: BadgeProps) {
  return (
    <span
      className={`inline-flex items-center whitespace-nowrap rounded-full border-2 border-ink px-2.5 py-[3px] font-mono text-caption uppercase text-ink ${tones[tone]} ${className}`}
    >
      {children}
    </span>
  );
}

export function FreeBadge() {
  return <Badge tone="mint">Grátis</Badge>;
}

export function PaidBadge() {
  return <Badge tone="coral">Pago</Badge>;
}

export function ConfirmPriceBadge() {
  return <Badge tone="lemon">Confirmar preço</Badge>;
}

export function LimitedDataBadge() {
  return <Badge>Dados limitados</Badge>;
}

// No Figma o nível da etapa é só texto mono em cinza-azulado, sem caixa.
export function LevelBadge({ level, className = "" }: { level: string; className?: string }) {
  return (
    <p className={`font-mono text-caption uppercase text-ink-soft ${className}`}>
      Nível · {level}
    </p>
  );
}
