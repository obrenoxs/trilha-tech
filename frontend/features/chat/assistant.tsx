import { getArea, getAreas } from "@/lib/api";

import { AssistantLauncher } from "./assistant-launcher";
import { AssistantPanel } from "./assistant-panel";
import { AssistantProvider } from "./assistant-provider";
import type { ContextNames } from "./context";

/**
 * Nomes de áreas e trilhas só para a faixa "CONTEXTO" (a API recebe slugs). Vêm do cache de 1 hora
 * da própria API. Se algo falhar, a faixa usa o slug no lugar do nome: o assistente nunca
 * derruba o resto do site.
 */
async function loadNames(): Promise<ContextNames> {
  const names: ContextNames = { areas: {}, trilhas: {} };
  try {
    const areas = await getAreas();
    await Promise.all(
      areas.map(async (area) => {
        names.areas[area.slug] = area.nome;
        try {
          const detail = await getArea(area.slug);
          for (const trilha of detail?.trilhas ?? []) names.trilhas[trilha.slug] = trilha.nome;
        } catch {
          // sem os nomes desta área: a faixa mostra o slug
        }
      }),
    );
  } catch {
    // API fora do ar: nomes vazios
  }
  return names;
}

/** Botão flutuante e painel do assistente, no layout raiz (presente em todas as páginas). */
export async function Assistant() {
  const names = await loadNames();
  return (
    <AssistantProvider names={names}>
      <AssistantLauncher />
      <AssistantPanel />
    </AssistantProvider>
  );
}
