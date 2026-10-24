import type { ReactNode } from "react";
import { areaColors, colorForIndex } from "./area-colors";

// Esqueletos de carregamento: repetem a casca dos componentes reais (borda, raio, sombra e
// canhoto colorido) e trocam só o texto por barras. Assim a página não "pula" quando os dados chegam.
// A pulsação só roda sem prefers-reduced-motion.

/** Barra cinza no lugar de um texto. `className` define largura e altura. */
export function SkeletonBar({ className = "" }: { className?: string }) {
  return (
    <div
      aria-hidden="true"
      className={`rounded-8 bg-ink/10 motion-safe:animate-pulse ${className}`}
    />
  );
}

/** Envolve um bloco de esqueletos: avisa leitores de tela uma vez só e esconde o resto. */
export function LoadingRegion({ children }: { children: ReactNode }) {
  return (
    <div role="status" aria-busy="true">
      <span className="sr-only">Carregando…</span>
      {children}
    </div>
  );
}

// Bilhete da Home (AreaTicket).
export function AreaTicketSkeleton({ index }: { index: number }) {
  return (
    <div
      aria-hidden="true"
      className="mb-1.5 mr-1.5 flex w-full overflow-clip rounded-16 border-3 border-ink bg-white shadow-md"
    >
      <div
        className={`w-[100px] shrink-0 border-r-3 border-dashed border-ink ${areaColors[colorForIndex(index)]}`}
      />
      <div className="flex min-h-[128px] min-w-0 flex-1 flex-col justify-between gap-4 px-6 py-5">
        <SkeletonBar className="h-6 w-3/4" />
        <SkeletonBar className="h-4 w-1/3" />
      </div>
    </div>
  );
}

// Cartão de trilha da página da área (TrailCard).
export function TrailCardSkeleton({ index }: { index: number }) {
  return (
    <div
      aria-hidden="true"
      className="flex min-h-[120px] overflow-clip rounded-16 border-3 border-ink bg-white shadow-md md:min-h-[104px] md:items-center"
    >
      <div
        className={`w-[60px] shrink-0 self-stretch border-r-3 border-dashed border-ink md:w-[90px] ${areaColors[colorForIndex(index)]}`}
      />
      <div className="flex min-w-0 flex-1 flex-col items-start gap-3 px-4 py-[18px] md:flex-row md:items-center md:gap-4 md:p-0 md:px-7">
        <div className="flex w-full min-w-0 flex-col gap-2.5 md:flex-1">
          <SkeletonBar className="h-5 w-2/3" />
          <SkeletonBar className="h-4 w-full" />
        </div>
        <SkeletonBar className="h-[52px] w-[140px] shrink-0 rounded-14" />
      </div>
    </div>
  );
}

// Cartão de etapa do roadmap (StepCard), fechado.
export function StepCardSkeleton({ index }: { index: number }) {
  return (
    <div
      aria-hidden="true"
      className="flex min-h-[96px] overflow-clip rounded-16 border-3 border-ink bg-white shadow-md md:shadow-sm"
    >
      <div
        className={`w-[60px] shrink-0 border-r-3 border-dashed border-ink md:w-20 ${areaColors[colorForIndex(index)]}`}
      />
      <div className="flex min-w-0 flex-1 items-center gap-4 px-4 py-[18px] md:pl-7 md:pr-7">
        <div className="flex min-w-0 flex-1 flex-col gap-2.5">
          <SkeletonBar className="h-5 w-1/2" />
          <SkeletonBar className="h-4 w-full" />
        </div>
        <SkeletonBar className="size-8 shrink-0" />
      </div>
    </div>
  );
}
