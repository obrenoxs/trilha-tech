"use client";

import { useId, useState, type ReactNode } from "react";
import { areaColors, colorForIndex } from "./area-colors";
import { LevelBadge } from "./badges";

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

/**
 * Região que abre e fecha animando a altura (grid-template-rows 1fr <-> 0fr, 250 ms).
 * Fechada, fica `inert`: sai da ordem de Tab e da leitura de tela, mesmo estando no HTML.
 * Sem prefers-reduced-motion a transição some (globals.css) e a troca é instantânea.
 * `as="span"` para usar dentro do botão do cabeçalho (só conteúdo de texto cabe num botão).
 */
function Collapse({
  open,
  id,
  as: Tag = "div",
  children,
}: {
  open: boolean;
  id?: string;
  as?: "div" | "span";
  children: ReactNode;
}) {
  return (
    <Tag
      id={id}
      inert={!open}
      className={`grid transition-[grid-template-rows] duration-(--duration-base) ease-soft ${open ? "grid-rows-[1fr]" : "grid-rows-[0fr]"}`}
    >
      <Tag className="block min-h-0 overflow-hidden">{children}</Tag>
    </Tag>
  );
}

// Cartão da lista de etapas da trilha (Figma 9:333 e 123:553, mobile 125:640).
// A cor do canhoto segue a posição na lista (coral, menta, limão, lavanda, e repete).
// Fechado: canhoto + título + resumo (cortado em 2 linhas) + nível + "+". Aberto: o nível sobe para a linha do
// título, o resumo aparece inteiro, depois o divisor tracejado e o conteúdo da etapa.
// No mobile o nível some do cartão fechado e fica abaixo do título no aberto.
//
// Semântica: <h3><button>cabeçalho inteiro</button></h3>. O cabeçalho (título, resumo cortado, nível e o
// "+"/"−") é o botão, então clicar em qualquer parte dele abre e fecha. O conteúdo aberto (recursos,
// links) fica FORA do botão. O nome do botão é o título; resumo e nível entram como descrição
// (aria-describedby; o texto deles dentro do botão é aria-hidden, senão o nome teria que conter tudo o
// que se vê, regra WCAG 2.5.3). O "+" é só decoração (aria-hidden). O canhoto com o número também abre/fecha ao clique, como atalho do mouse:
// quem usa teclado ou leitor de tela usa o botão.
//
// A estrutura é uma só nos dois estados: o resumo cortado e o conteúdo aberto são duas regiões que
// trocam de altura ao mesmo tempo. O botão não é recriado, então o foco do teclado fica onde está.
// Os espaços que o Figma define diferentes em cada estado (topo, margem do "+", distância título/"+")
// mudam com `isOpen` e também transicionam (padding faz parte da altura da etapa), então os dois
// estados batem com os frames: aberta com o título a 20 px do topo no desktop; "+" a 12 px da borda
// fechado e a 16 px aberto no mobile; larguras de título 208 px (fechado) e 210 px (aberto) no mobile.
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
  const baseId = useId();
  const panelId = `${baseId}-panel`;
  const titleId = `${baseId}-title`;
  const summaryId = `${baseId}-summary`;
  const levelId = `${baseId}-level`;
  const hasDetails = children != null;
  const isOpen = hasDetails && expanded;
  const toggle = () => setExpanded((value) => !value);
  const summaryClass = "break-words font-body text-body-sm text-ink-soft md:text-body-md";
  const padMotion = "transition-[padding] duration-(--duration-base) ease-soft";

  // Linha do cabeçalho. Fechado: centrada na altura do texto (18 px acima e abaixo); aberto: 18 px no
  // mobile e 20 px no desktop acima, sem espaço abaixo (a região do conteúdo traz o seu).
  let rowPadding = "py-[18px] pr-3 md:pr-7";
  if (!hasDetails) rowPadding = "py-[18px] pr-0";
  else if (isOpen) rowPadding = "pt-[18px] pb-0 pr-4 md:pt-5 md:pr-7";
  // Espaço entre o título e o que vem à direita: 16/24 px fechado, 10/16 px aberto (mobile/desktop).
  const titlePadding = isOpen ? "pr-2.5 md:pr-4" : "pr-4 md:pr-6";

  const header = (
    <>
      <span className={`block min-w-0 flex-1 ${padMotion} ${titlePadding}`}>
        <span
          id={titleId}
          className="block break-words font-display text-[16px] font-semibold leading-[1.25] tracking-[-0.025em] text-ink md:text-card"
        >
          {title}
        </span>
        <Collapse as="span" open={!isOpen}>
          <span
            id={summaryId}
            aria-hidden="true"
            className={`${summaryClass} line-clamp-2 pt-2.5 md:pt-1.5`}
          >
            {summary}
          </span>
        </Collapse>
      </span>
      <LevelBadge
        as="span"
        id={levelId}
        hiddenFromReaders
        level={level}
        className={`hidden shrink-0 md:block ${hasDetails ? "md:pr-4" : "md:pr-7"}`}
      />
      {hasDetails && (
        <span
          aria-hidden="true"
          className="flex size-8 shrink-0 items-center justify-center rounded-8 border-2 border-ink bg-white font-mono text-label text-ink"
        >
          {isOpen ? "−" : "+"}
        </span>
      )}
    </>
  );
  const rowClass = `flex w-full items-start pl-4 text-left md:items-center md:pl-7 ${padMotion} ${rowPadding}`;

  return (
    <article className="flex overflow-clip rounded-16 border-3 border-ink bg-white shadow-md md:shadow-sm">
      {/* Atalho do mouse: o canhoto também abre/fecha. Não entra no Tab nem na leitura de tela. */}
      <div
        aria-hidden="true"
        onClick={hasDetails ? toggle : undefined}
        className={`flex w-[60px] shrink-0 items-center justify-center border-r-3 border-dashed border-ink ${areaColors[colorForIndex(index)]} font-display text-[24px] font-bold leading-[1.2] tracking-[-0.03em] text-ink md:w-20 md:text-num ${hasDetails ? "cursor-pointer" : ""}`}
      >
        {number}
      </div>

      <div className="flex min-w-0 flex-1 flex-col">
        <h3>
          {hasDetails ? (
            <button
              type="button"
              aria-expanded={isOpen}
              aria-controls={panelId}
              aria-labelledby={titleId}
              aria-describedby={`${summaryId} ${levelId}`}
              onClick={toggle}
              className={`${rowClass} cursor-pointer focus-visible:outline-3 focus-visible:-outline-offset-6 focus-visible:outline-ink`}
            >
              {header}
            </button>
          ) : (
            <span className={rowClass}>{header}</span>
          )}
        </h3>

        {hasDetails && (
          <Collapse open={isOpen} id={panelId}>
            <div className="flex flex-col gap-3.5 pt-3.5 pr-4 pb-5 pl-4 md:gap-[18px] md:pt-[18px] md:pr-7 md:pb-[26px] md:pl-7">
              <LevelBadge level={level} className="md:hidden" />
              <p className={summaryClass}>{summary}</p>
              <div aria-hidden="true" className="h-0.5 border-t-2 border-dashed border-ink" />
              <div className="flex flex-col gap-3.5 md:gap-[18px]">{children}</div>
            </div>
          </Collapse>
        )}
      </div>
    </article>
  );
}
