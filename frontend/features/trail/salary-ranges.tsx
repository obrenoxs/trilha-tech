import { areaColors } from "@/components/ui/area-colors";
import { LimitedDataBadge } from "@/components/ui/badges";
import type { TrilhaDetalhe } from "@/lib/api";
import {
  formatBrl,
  formatMonthYear,
  formatSalaryRange,
  salaryLevelLabels,
  sortSalaryRanges,
} from "@/lib/trail-format";

type SalaryRangesProps = {
  ranges: TrilhaDetalhe["faixasSalariais"];
  /** `avisosSalario` da trilha: aparece sempre junto das faixas. */
  notice: string;
};

// A cor segue o nível (júnior coral, pleno menta, sênior limão), não a posição na lista.
const levelColors = { JUNIOR: "coral", PLENO: "mint", SENIOR: "lemon" } as const;

const caption = "font-mono text-caption uppercase";

// Bloco de salário (Figma 123:561, mobile 125:653). Regras de conteúdo:
// - sempre a faixa "R$ mín – máx", com valor de referência e fonte em letra pequena;
// - o rótulo do tipo de referência vem da API (tipoReferencia), nunca fixo aqui;
// - a observação da faixa (Corpo/Pequeno) aparece abaixo da fonte, só quando vier;
// - o selo "dados limitados" só aparece quando a faixa tem dadosLimitados;
// - o aviso (avisosSalario) fica dentro do mesmo cartão, sem exceção.
export function SalaryRanges({ ranges, notice }: SalaryRangesProps) {
  if (ranges.length === 0) return null;

  return (
    <section
      aria-labelledby="titulo-salario"
      className="flex w-full flex-col gap-3 rounded-[18px] border-3 border-ink bg-white p-[18px] shadow-lg md:gap-[18px] md:rounded-[20px] md:px-8 md:py-6"
    >
      <h2 id="titulo-salario" className={`${caption} text-ink-soft md:text-label`}>
        Faixa salarial estimada (mensal)
      </h2>

      <ul className="flex flex-col gap-3 md:flex-row md:gap-6">
        {sortSalaryRanges(ranges).map((range) => (
          <li
            key={range.nivel}
            className={`flex min-w-0 flex-col gap-1 rounded-12 border-3 border-ink px-4 py-3 shadow-sm md:flex-1 md:gap-1.5 md:rounded-14 md:px-5 md:py-[18px] ${areaColors[levelColors[range.nivel]]}`}
          >
            <div className="flex items-center gap-2">
              <h3 className={`min-w-0 flex-1 ${caption} text-ink`}>
                {salaryLevelLabels[range.nivel]}
              </h3>
              {range.dadosLimitados && <LimitedDataBadge />}
            </div>
            <p className="break-words font-display text-[19px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card">
              {formatSalaryRange(range.minimo, range.maximo)}
            </p>
            <p className="font-body text-body-sm text-ink">
              {capitalize(range.tipoReferencia)}: {formatBrl(range.valorReferencia)}
            </p>
            <p className={`${caption} text-ink-soft`}>
              {range.fonte} · {formatMonthYear(range.dataReferencia)}
            </p>
            {range.observacao && (
              <p className="break-words font-body text-body-sm text-ink">{range.observacao}</p>
            )}
          </li>
        ))}
      </ul>

      <div className="flex flex-col gap-1 rounded-12 border-2 border-ink bg-bg px-3.5 py-3 md:px-[18px] md:py-3.5">
        <p className={`${caption} text-ink-soft`}>Aviso</p>
        <p className="break-words font-body text-body-sm text-ink">{notice}</p>
      </div>
    </section>
  );
}

// "média do salário base" vem em minúsculas da API; no Figma o rótulo abre a frase ("Mediana: R$ ...").
// Só muda a primeira letra: o texto continua o da API.
function capitalize(text: string): string {
  return text.charAt(0).toLocaleUpperCase("pt-BR") + text.slice(1);
}
