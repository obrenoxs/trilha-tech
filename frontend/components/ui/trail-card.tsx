import { areaColors, colorForIndex } from "./area-colors";
import { Button } from "./button";

type TrailCardProps = {
  /** Posição na lista (começa em 0). Escolhe a cor do canhoto: índice % 4. */
  index: number;
  number: string;
  name: string;
  summary: string;
  href: string;
};

// Cartão da lista de trilhas da página da área (Figma 134:649, mobile 134:747).
// Desktop: canhoto + texto + botão na mesma linha. Mobile: o botão desce para baixo do texto.
// Título e resumo quebram linha (nada de nowrap): os textos reais são mais longos que o mockup.
export function TrailCard({ index, number, name, summary, href }: TrailCardProps) {
  return (
    <div className="flex min-h-[120px] overflow-clip rounded-16 border-3 border-ink bg-white shadow-md md:min-h-[104px] md:items-center">
      <div
        aria-hidden="true"
        className={`flex w-[60px] shrink-0 items-center justify-center self-stretch border-r-3 border-dashed border-ink ${areaColors[colorForIndex(index)]} font-display text-[24px] font-bold leading-[1.2] tracking-[-0.03em] md:w-[90px] md:text-num`}
      >
        {number}
      </div>
      <div className="flex min-w-0 flex-1 flex-col items-start gap-2.5 px-4 py-[18px] md:flex-row md:items-center md:gap-4 md:p-0">
        <div className="flex min-w-0 flex-col gap-2.5 md:flex-1 md:gap-1.5 md:py-[22px] md:pl-7">
          <h3 className="break-words font-display text-[16px] font-semibold leading-[1.25] tracking-[-0.025em] md:text-card">
            {name}
          </h3>
          <p className="break-words font-body text-body-sm text-ink-soft md:text-body-md">
            {summary}
          </p>
        </div>
        <div className="shrink-0 md:pr-7">
          <Button
            href={href}
            variant="secondary"
            aria-label={`Ver roadmap de ${name}`}
            className="whitespace-nowrap"
          >
            Ver roadmap
          </Button>
        </div>
      </div>
    </div>
  );
}
