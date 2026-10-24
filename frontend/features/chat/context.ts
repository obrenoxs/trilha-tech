import type { ChatRequest } from "@/lib/api/types";

export type ChatContext = NonNullable<ChatRequest["contexto"]>;

/** Nomes para exibir na faixa "CONTEXTO", por slug. Só para exibição: o que vai para a API são slugs. */
export type ContextNames = {
  areas: Record<string, string>;
  trilhas: Record<string, string>;
};

const SLUG = /^[a-z0-9]+(?:-[a-z0-9]+)*$/;
const MAX_SLUG_LENGTH = 100;
const MAX_QUIZ_AREAS = 3;

export const isSlug = (value: string) => value.length <= MAX_SLUG_LENGTH && SLUG.test(value);

function decode(segment: string): string | null {
  try {
    return decodeURIComponent(segment);
  } catch {
    return null;
  }
}

/**
 * Contexto do assistente, derivado só dos segmentos da URL (`/areas/[slug]`, `/trilhas/[slug]`)
 * e das áreas do resultado do quiz, sempre como slugs válidos. Nunca a URL inteira, query,
 * hash nem texto da página. Sem nada reconhecível, devolve `undefined` (o campo é opcional).
 */
export function deriveContext(
  pathname: string,
  quizSlugs?: readonly string[] | null,
): ChatContext | undefined {
  const context: ChatContext = {};

  const path = pathname.split(/[?#]/)[0];
  const segments = path.split("/").filter(Boolean).map(decode);
  if (segments.length === 2) {
    const [section, slug] = segments;
    if (slug !== null && section !== null && isSlug(slug)) {
      if (section === "areas") context.areaSlug = slug;
      else if (section === "trilhas") context.trilhaSlug = slug;
    }
  }

  const quiz = [...new Set((quizSlugs ?? []).filter(isSlug))].slice(0, MAX_QUIZ_AREAS);
  if (quiz.length > 0) context.resultadoQuiz = quiz;

  return Object.keys(context).length > 0 ? context : undefined;
}

const humanize = (slug: string) => slug.replaceAll("-", " ");

/** Texto da faixa "CONTEXTO: ..." (a caixa alta vem do CSS). `null` quando não há contexto. */
export function contextLabel(
  context: ChatContext | undefined,
  names: ContextNames = { areas: {}, trilhas: {} },
): string | null {
  if (!context) return null;
  const parts: string[] = [];
  if (context.trilhaSlug) {
    parts.push(`Trilha ${names.trilhas[context.trilhaSlug] ?? humanize(context.trilhaSlug)}`);
  } else if (context.areaSlug) {
    parts.push(`Área ${names.areas[context.areaSlug] ?? humanize(context.areaSlug)}`);
  }
  if (context.resultadoQuiz) parts.push("Resultado do quiz");
  return parts.join(" · ");
}

/** Texto de apoio do campo, de acordo com a página. */
export function inputPlaceholder(context: ChatContext | undefined): string {
  if (context?.trilhaSlug) return "Pergunte sobre esta trilha…";
  if (context?.areaSlug) return "Pergunte sobre esta área…";
  return "Pergunte sobre TI e carreira…";
}
