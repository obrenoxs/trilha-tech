"use client";

import { useRouter } from "next/navigation";
import { useEffect, useMemo, useSyncExternalStore } from "react";

import { areaColors, colorForIndex } from "@/components/ui/area-colors";
import { Button } from "@/components/ui/button";
import type { Area } from "@/lib/api/types";
import { requestOpenAssistant } from "@/features/chat/open-assistant";
import { routes } from "@/lib/routes";

import { clearFreshResult, getFreshResult } from "./fresh-result";
import {
  clearSavedSlugs,
  parseSavedSlugs,
  readRawSavedResult,
  subscribeSavedResult,
} from "./saved-result";

type ResultItem = {
  slug: string;
  nome: string;
  /** 0 a 100, relativo à primeira área. Só existe logo após fazer o quiz (não é salvo). */
  barra: number | null;
};

/**
 * Resultado do quiz. Fonte dos dados:
 * - logo após o quiz: o resultado em memória (com a "barra");
 * - depois de recarregar: os slugs salvos no navegador (sem "barra").
 * Em ambos os casos o slug é revalidado contra as áreas da API (`areas`), e o nome exibido
 * vem de lá. Sem nada válido, a pessoa volta para o quiz.
 */
export function QuizResult({ areas }: { areas: Area[] }) {
  const router = useRouter();
  // `undefined` no servidor e na hidratação; `null` quando não há nada salvo.
  const raw = useSyncExternalStore(subscribeSavedResult, readRawSavedResult, () => undefined);

  const items = useMemo<ResultItem[]>(() => {
    if (raw === undefined) return [];
    const byslug = new Map(areas.map((area) => [area.slug, area]));
    const fresh = getFreshResult();
    const candidates: { slug: string; barra: number | null }[] = fresh
      ? [...fresh.recomendacoes]
          .sort((a, b) => a.posicao - b.posicao)
          .map((item) => ({ slug: item.slug, barra: item.barra }))
      : (parseSavedSlugs(raw) ?? []).map((slug) => ({ slug, barra: null }));
    return candidates.flatMap(({ slug, barra }) => {
      const area = byslug.get(slug);
      return area ? [{ slug, nome: area.nome, barra }] : [];
    });
  }, [raw, areas]);

  const loading = raw === undefined;
  const empty = !loading && items.length === 0;

  useEffect(() => {
    if (!empty) return;
    // Valor salvo que não vale mais (inválido ou área removida): limpa e recomeça.
    if (raw !== null) clearSavedSlugs();
    router.replace(routes.quiz);
  }, [empty, raw, router]);

  if (loading || empty) return null;

  function restart() {
    clearFreshResult();
    clearSavedSlugs();
  }

  return (
    <div className="flex flex-col items-start gap-5 pt-3 pb-12 md:gap-7 md:pt-8 md:pb-[72px]">
      <p className="font-mono text-label uppercase text-ink-soft">
        Seu resultado · {items.length} {items.length === 1 ? "destino" : "destinos"}
        <span className="hidden md:inline"> sugerido{items.length === 1 ? "" : "s"}</span>
      </p>
      <h1 className="font-display text-[26px] leading-[1.1] font-bold tracking-[-0.04em] md:text-h2">
        Estas áreas combinam mais com o seu perfil.
      </h1>
      <p className="font-body text-body-md text-ink-soft md:text-body-lg">
        É uma sugestão para começar a explorar, não um teste vocacional.
        <span className="hidden md:inline">
          {" "}
          Veja as trilhas de cada área e decida com calma.
        </span>
      </p>
      <ol className="flex w-full flex-col gap-5 md:gap-6">
        {items.map((item, position) => {
          const color = colorForIndex(position);
          return (
            <li
              key={item.slug}
              className="mb-1.5 flex min-h-[120px] overflow-clip rounded-16 border-3 border-ink bg-white shadow-md md:min-h-[104px]"
            >
              <div
                className={`flex w-[60px] shrink-0 flex-col items-center justify-center gap-1 border-r-3 border-dashed border-ink md:w-[110px] ${areaColors[color]}`}
              >
                <span aria-hidden="true" className="hidden font-mono text-caption uppercase md:block">
                  Nº
                </span>
                <span className="sr-only">Posição </span>
                <span className="font-display text-[24px] leading-[1.2] font-bold tracking-[-0.03em] md:text-h2">
                  {position + 1}
                </span>
              </div>
              <div className="flex min-w-0 flex-1 flex-col gap-2.5 px-4 py-[18px] md:flex-row md:items-center md:gap-0 md:p-0 md:pl-7">
                <div className="flex min-w-0 flex-1 flex-col gap-2.5 md:gap-3.5 md:py-6">
                  <h2 className="font-display text-[16px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card">
                    {item.nome}
                  </h2>
                  {item.barra !== null && (
                    <div
                      aria-hidden="true"
                      className="h-3.5 overflow-clip rounded-[7px] border-2 border-ink bg-bg md:h-4 md:rounded-8"
                    >
                      <div
                        className={`h-full ${areaColors[color]}`}
                        style={{ width: `${Math.min(100, Math.max(0, item.barra))}%` }}
                      />
                    </div>
                  )}
                </div>
                <div className="md:pr-7">
                  <Button
                    href={`/areas/${item.slug}`}
                    variant="secondary"
                    aria-label={`Ver trilhas de ${item.nome}`}
                  >
                    Ver trilhas
                  </Button>
                </div>
              </div>
            </li>
          );
        })}
      </ol>
      <div className="flex flex-col items-start gap-3.5 md:flex-row md:gap-[18px] md:pt-3">
        <Button onClick={requestOpenAssistant}>Conversar com a IA</Button>
        <Button href={routes.quiz} variant="secondary" onClick={restart}>
          Refazer o quiz
        </Button>
      </div>
    </div>
  );
}
