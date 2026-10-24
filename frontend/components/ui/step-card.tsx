"use client";

import { useEffect, useId, useRef, useState, type ReactNode } from "react";
import { areaColors, colorForIndex } from "./area-colors";
import { LevelBadge } from "./badges";
import { focusRing } from "./states";

type StepCardProps = {
  /** Posição na lista (começa em 0). Escolhe a cor do canhoto: índice % 4. */
  index: number;
  number: string;
  title: string;
  summary: string;
  /** Rótulo do nível já em texto de tela, por exemplo "Iniciante". */
  level: string;
  /** Conteúdo que aparece quando a etapa abre. Sem ele, o botão "+" não aparece. */
  children?: ReactNode;
  defaultExpanded?: boolean;
};

// Cartão da lista de etapas da trilha (Figma 9:333 e 123:553, mobile 125:640).
// A cor do canhoto segue a posição na lista (coral, menta, limão, lavanda, e repete).
// Fechado: canhoto + título + resumo (cortado em 2 linhas) + nível + "+". Aberto: o nível sobe para a linha do
// título, aparece o divisor tracejado e o conteúdo da etapa.
// No mobile o nível some do cartão fechado e fica abaixo do título no aberto.
export function StepCard({
  index,
  number,
  title,
  summary,
  level,
  children,
  defaultExpanded = false,
}: StepCardProps) {
  const [expanded, setExpanded] = useState(defaultExpanded);
  const panelId = useId();
  const toggleRef = useRef<HTMLButtonElement>(null);
  const restoreFocus = useRef(false);
  const hasDetails = children != null;
  const isOpen = hasDetails && expanded;

  // O botão muda de lugar na árvore quando a etapa abre ou fecha, e o React o recria.
  // Sem isto o foco do teclado se perderia a cada Enter/Espaço.
  useEffect(() => {
    if (restoreFocus.current) {
      toggleRef.current?.focus();
      restoreFocus.current = false;
    }
  }, [isOpen]);

  const toggle = hasDetails && (
    <button
      ref={toggleRef}
      type="button"
      aria-expanded={isOpen}
      aria-controls={isOpen ? panelId : undefined}
      aria-label={`${isOpen ? "Recolher" : "Expandir"} a etapa ${title}`}
      onClick={() => {
        restoreFocus.current = true;
        setExpanded((value) => !value);
      }}
      className={`flex size-8 shrink-0 items-center justify-center rounded-8 border-2 border-ink bg-white font-mono text-label text-ink ${focusRing}`}
    >
      <span aria-hidden="true">{isOpen ? "−" : "+"}</span>
    </button>
  );

  const summaryClass = "break-words font-body text-body-sm text-ink-soft md:text-body-md";

  return (
    <article className="flex overflow-clip rounded-16 border-3 border-ink bg-white shadow-md md:shadow-sm">
      <div
        aria-hidden="true"
        className={`flex w-[60px] shrink-0 items-center justify-center border-r-3 border-dashed border-ink ${areaColors[colorForIndex(index)]} font-display text-[24px] font-bold leading-[1.2] tracking-[-0.03em] text-ink md:w-20 md:text-num`}
      >
        {number}
      </div>

      {isOpen ? (
        <div className="flex min-w-0 flex-1 flex-col gap-3.5 px-4 pb-5 pt-[18px] md:gap-[18px] md:px-7 md:pb-[26px] md:pt-5">
          <div className="flex items-start gap-2.5 md:items-center md:gap-4">
            <h3 className="min-w-0 flex-1 break-words font-display text-[18px] font-semibold leading-[1.25] tracking-[-0.025em] md:text-card">
              {title}
            </h3>
            <LevelBadge level={level} className="hidden shrink-0 md:block" />
            {toggle}
          </div>
          <LevelBadge level={level} className="md:hidden" />
          <p className={summaryClass}>{summary}</p>
          <div aria-hidden="true" className="h-0.5 border-t-2 border-dashed border-ink" />
          <div id={panelId} className="flex flex-col gap-3.5 md:gap-[18px]">
            {children}
          </div>
        </div>
      ) : (
        <div className="flex min-w-0 flex-1 items-start md:items-center">
          <div className="flex min-w-0 flex-1 flex-col gap-2.5 px-4 py-[18px] md:gap-1.5 md:pl-7 md:pr-6">
            <h3 className="break-words font-display text-[16px] font-semibold leading-[1.25] tracking-[-0.025em] text-ink md:text-card">
              {title}
            </h3>
            <p className={`${summaryClass} line-clamp-2`}>{summary}</p>
          </div>
          <LevelBadge
            level={level}
            className={`hidden shrink-0 md:block ${hasDetails ? "md:pr-4" : "md:pr-7"}`}
          />
          {hasDetails && <div className="shrink-0 pr-3 pt-[18px] md:pr-7 md:pt-0">{toggle}</div>}
        </div>
      )}
    </article>
  );
}
