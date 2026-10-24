"use client";

import { useMemo, useSyncExternalStore } from "react";

import { areaColors, colorForIndex } from "@/components/ui/area-colors";
import { SmallButton } from "@/components/ui/small-button";
import type { Area } from "@/lib/api/types";
import { routes } from "@/lib/routes";

import { parseSavedSlugs, readRawSavedResult, subscribeSavedResult } from "./saved-result";

const pad = (n: number) => String(n).padStart(2, "0");

/**
 * Card "Ver meu resultado" da Home (Figma 139:994 e 139:1248). Fica entre o hero e os destinos.
 * Só aparece se houver resultado válido no navegador: os slugs salvos são revalidados contra as
 * áreas da API, e os nomes vêm de lá, na ordem salva e sem a barra. Sem resultado (ou com o
 * armazenamento bloqueado), não renderiza nada.
 */
export function SavedQuizResultShortcut({ areas }: { areas: Area[] }) {
  const raw = useSyncExternalStore(subscribeSavedResult, readRawSavedResult, () => undefined);

  const saved = useMemo(() => {
    if (raw === undefined) return [];
    const byslug = new Map(areas.map((area) => [area.slug, area]));
    return (parseSavedSlugs(raw) ?? []).flatMap((slug) => byslug.get(slug) ?? []);
  }, [raw, areas]);

  if (saved.length === 0) return null;

  return (
    <section
      aria-labelledby="titulo-resultado-salvo"
      className="mb-6 flex w-full flex-col items-start gap-5 rounded-16 border-3 border-ink bg-white p-5 shadow-md md:flex-row md:items-center md:justify-between md:gap-6 md:px-8 md:py-7"
    >
      <div className="flex min-w-0 flex-col gap-2.5 md:flex-1">
        <p className="font-mono text-label uppercase text-ink-soft">Seu resultado do quiz</p>
        <h2
          id="titulo-resultado-salvo"
          className="font-display text-card md:text-h3"
        >
          Suas áreas recomendadas
        </h2>
        <ol className="flex flex-wrap gap-2.5">
          {saved.map((area, position) => (
            <li
              key={area.slug}
              className={`flex gap-2 rounded-12 border-2 border-ink px-3 py-1.5 font-mono text-caption uppercase text-ink ${areaColors[colorForIndex(position)]}`}
            >
              <span aria-hidden="true">{pad(position + 1)}</span>
              <span className="min-w-0 [overflow-wrap:anywhere]">{area.nome}</span>
            </li>
          ))}
        </ol>
      </div>
      <SmallButton href={routes.quizResultado} className="shrink-0">
        Ver meu resultado
      </SmallButton>
    </section>
  );
}
