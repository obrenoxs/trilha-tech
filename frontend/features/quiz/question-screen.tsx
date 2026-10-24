import { areaColors, colorForIndex } from "@/components/ui/area-colors";
import { Button } from "@/components/ui/button";
import type { Alternativa } from "@/lib/api/types";

type QuestionScreenProps = {
  /** Posição da pergunta, de 0 a total - 1. */
  index: number;
  total: number;
  /** `ordem` da pergunta; só identifica o grupo de rádios. */
  perguntaOrdem: number;
  texto: string;
  /** Alternativas já embaralhadas, na ordem de exibição. Vazio enquanto o embaralhamento não rodou. */
  alternativas: Alternativa[];
  /** `ordem` original da alternativa escolhida, se houver. */
  selected: number | undefined;
  submitting: boolean;
  /** Mensagem amigável de erro do envio, se houver. */
  error: string | null;
  onSelect: (alternativaOrdem: number) => void;
  onBack: () => void;
  onNext: () => void;
};

const pad = (n: number) => String(n).padStart(2, "0");

// Alternativa selecionada: usa a linguagem do botão escuro do Figma (fundo ink, texto lemon,
// sombra coral). O Figma não traz o estado selecionado; ver o relatório da fase.
const optionClass = `relative mb-1.5 flex min-h-[88px] cursor-pointer items-stretch gap-5 overflow-clip rounded-16 border-3 border-ink bg-white pr-5 font-body text-body-md text-ink shadow-md hover:translate-x-1 hover:translate-y-1 hover:shadow-pressed has-checked:bg-ink has-checked:text-lemon has-checked:shadow-coral-md has-checked:hover:shadow-coral-pressed has-focus-visible:outline-3 has-focus-visible:outline-offset-3 has-focus-visible:outline-ink md:min-h-[92px] md:gap-8 md:pr-8 md:text-body-lg`;

export function QuestionScreen({
  index,
  total,
  perguntaOrdem,
  texto,
  alternativas,
  selected,
  submitting,
  error,
  onSelect,
  onBack,
  onNext,
}: QuestionScreenProps) {
  const current = index + 1;

  return (
    <div className="flex flex-col items-start gap-[22px] pt-3 pb-12 md:gap-7 md:pt-8 md:pb-[72px]">
      <p aria-live="polite" className="font-mono text-label uppercase text-ink-soft">
        Pergunta {pad(current)} de {pad(total)}
      </p>
      <div
        role="progressbar"
        aria-label="Progresso do quiz"
        aria-valuemin={1}
        aria-valuemax={total}
        aria-valuenow={current}
        aria-valuetext={`Pergunta ${current} de ${total}`}
        className="flex w-full gap-1 md:gap-1.5"
      >
        {Array.from({ length: total }, (_, step) => (
          <span
            key={step}
            className={`h-3 flex-1 rounded-[4px] border-2 border-ink md:h-3.5 ${step <= index ? "bg-coral" : "bg-white"}`}
          />
        ))}
      </div>
      <fieldset aria-busy={alternativas.length === 0} className="flex w-full min-w-0 flex-col">
        <legend className="mb-[22px] font-display text-[20px] leading-[1.2] font-semibold tracking-[-0.025em] md:mb-7 md:text-h3">
          {texto}
        </legend>
        <div className="flex flex-col gap-3 md:gap-[18px]">
          {alternativas.map((alternativa, position) => (
            <label key={alternativa.ordem} className={optionClass}>
              <input
                type="radio"
                name={`pergunta-${perguntaOrdem}`}
                value={alternativa.ordem}
                checked={selected === alternativa.ordem}
                onChange={() => onSelect(alternativa.ordem)}
                disabled={submitting}
                className="sr-only"
              />
              <span
                aria-hidden="true"
                className={`flex w-[60px] shrink-0 items-center justify-center border-r-3 border-ink font-display text-[24px] leading-[1.2] font-bold tracking-[-0.03em] text-ink md:w-[72px] md:text-num ${areaColors[colorForIndex(position)]}`}
              >
                {String.fromCharCode(65 + position)}
              </span>
              <span className="min-w-0 flex-1 self-center py-2 [overflow-wrap:anywhere]">
                {alternativa.texto}
              </span>
            </label>
          ))}
        </div>
      </fieldset>
      {error && (
        <p role="alert" className="font-body text-body-md font-bold text-ink">
          {error}
        </p>
      )}
      <div className="flex gap-3.5 md:gap-[18px] md:pt-3">
        <Button variant="secondary" onClick={onBack} disabled={index === 0 || submitting}>
          Voltar
        </Button>
        <Button onClick={onNext} disabled={selected === undefined || submitting}>
          Próxima
        </Button>
      </div>
    </div>
  );
}
