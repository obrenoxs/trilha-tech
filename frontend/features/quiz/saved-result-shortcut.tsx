"use client";

import { useMemo, useSyncExternalStore } from "react";

import { SmallButton } from "@/components/ui/small-button";
import type { Area } from "@/lib/api/types";
import { routes } from "@/lib/routes";

import { parseSavedSlugs, readRawSavedResult, subscribeSavedResult } from "./saved-result";

/**
 * Atalho da Home para o resultado salvo. Só aparece se houver resultado válido no navegador,
 * e os slugs são revalidados contra as áreas da API. Sem resultado (ou com o armazenamento
 * bloqueado), não renderiza nada.
 */
export function SavedQuizResultShortcut({ areas }: { areas: Area[] }) {
  const raw = useSyncExternalStore(subscribeSavedResult, readRawSavedResult, () => undefined);

  const names = useMemo(() => {
    if (raw === undefined) return [];
    const byslug = new Map(areas.map((area) => [area.slug, area.nome]));
    return (parseSavedSlugs(raw) ?? []).flatMap((slug) => byslug.get(slug) ?? []);
  }, [raw, areas]);

  if (names.length === 0) return null;

  return (
    <div className="flex w-full flex-col items-start gap-3 rounded-16 border-3 border-ink bg-white p-5 shadow-sm md:flex-row md:items-center md:justify-between md:gap-6 md:px-6">
      <div className="flex flex-col gap-1">
        <p className="font-mono text-caption uppercase text-ink-soft">Seu resultado do quiz</p>
        <p className="font-body text-body-md font-bold text-ink">{names.join(" · ")}</p>
      </div>
      <SmallButton href={routes.quizResultado}>Ver meu resultado</SmallButton>
    </div>
  );
}
