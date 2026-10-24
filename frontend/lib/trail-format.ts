import type { TrilhaDetalhe } from "@/lib/api";

type Etapa = TrilhaDetalhe["etapas"][number];
type Recurso = Etapa["recursos"][number];
type Faixa = TrilhaDetalhe["faixasSalariais"][number];

// A API envia os enums em MAIÚSCULAS; aqui eles viram o rótulo da tela.
// Record<enum, string> faz o TypeScript avisar se a API ganhar um valor novo.
export const entryLevelLabels: Record<TrilhaDetalhe["nivelEntrada"], string> = {
  INICIANTE: "Iniciante",
  INTERMEDIARIO: "Intermediário",
  AVANCADO: "Avançado",
};

export const stepLevelLabels: Record<Etapa["nivel"], string> = {
  BASICO: "Básico",
  INTERMEDIARIO: "Intermediário",
  AVANCADO: "Avançado",
};

export const salaryLevelLabels: Record<Faixa["nivel"], string> = {
  JUNIOR: "Júnior",
  PLENO: "Pleno",
  SENIOR: "Sênior",
};

export const resourceTypeLabels: Record<Recurso["tipo"], string> = {
  DOCUMENTACAO: "Documentação",
  CURSO: "Curso",
  LIVRO: "Livro",
  FORMACAO: "Formação",
  CERTIFICACAO: "Certificação",
  TUTORIAL: "Tutorial",
  GUIA: "Guia",
  VIDEO: "Vídeo",
  FERRAMENTA: "Ferramenta",
};

export const languageLabels: Record<Recurso["idioma"], string> = {
  PT: "PT-BR",
  EN: "EN",
};

const salaryOrder: Faixa["nivel"][] = ["JUNIOR", "PLENO", "SENIOR"];

/** Júnior, Pleno, Sênior, independente da ordem em que a API devolver. */
export function sortSalaryRanges(ranges: Faixa[]): Faixa[] {
  return [...ranges].sort((a, b) => salaryOrder.indexOf(a.nivel) - salaryOrder.indexOf(b.nivel));
}

const number = new Intl.NumberFormat("pt-BR");

/** "R$ 2.800 – 4.200": o "R$" aparece só uma vez, como no Figma. */
export function formatSalaryRange(min: number, max: number): string {
  return `R$ ${number.format(min)} – ${number.format(max)}`;
}

export function formatBrl(value: number): string {
  return `R$ ${number.format(value)}`;
}

/** "2026-10-16" ou "2026-09" vira "10/2026" e "09/2026". Formato inesperado volta como veio. */
export function formatMonthYear(value: string): string {
  const match = /^(\d{4})-(\d{2})/.exec(value);
  return match ? `${match[2]}/${match[1]}` : value;
}

/** "≈ 1 semana" / "≈ 36 semanas". */
export function formatWeeks(weeks: number): string {
  return `${weeks} ${weeks === 1 ? "semana" : "semanas"}`;
}

/** Link externo só se começar com https:// (regra do projeto). */
export function isSafeExternalUrl(url: string): boolean {
  return url.startsWith("https://");
}
