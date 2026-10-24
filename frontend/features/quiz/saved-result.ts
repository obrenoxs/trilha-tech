import { z } from "zod";

// Só os slugs das áreas recomendadas, da mais indicada para a menos. Nada de pontos nem respostas.
const KEY = "trilha-tech:quiz-resultado";
const CHANGE_EVENT = "trilha-tech:quiz-resultado-change";

// O conteúdo do localStorage é dado de fora do nosso controle: valida com Zod ao ler.
const savedSlugsSchema = z.array(z.string().min(1)).min(1).max(3);

/** Devolve o armazenamento ou `null` quando está bloqueado (acessá-lo pode lançar exceção). */
export type StorageGetter = () => Storage | null;

const browserStorage: StorageGetter = () => {
  try {
    return window.localStorage;
  } catch {
    return null;
  }
};

/** Texto cru salvo, ou `null` se vazio, bloqueado ou com erro de leitura. */
export function readRawSavedResult(getStorage: StorageGetter = browserStorage): string | null {
  try {
    return getStorage()?.getItem(KEY) ?? null;
  } catch {
    return null;
  }
}

/** Valida o texto cru. Qualquer coisa fora do formato esperado vira `null`. */
export function parseSavedSlugs(raw: string | null): string[] | null {
  if (raw === null) return null;
  try {
    const result = savedSlugsSchema.safeParse(JSON.parse(raw));
    return result.success ? result.data : null;
  } catch {
    return null;
  }
}

export function readSavedSlugs(getStorage: StorageGetter = browserStorage): string[] | null {
  return parseSavedSlugs(readRawSavedResult(getStorage));
}

function notifyChange() {
  try {
    window.dispatchEvent(new Event(CHANGE_EVENT));
  } catch {
    // sem window (testes em Node): nada a avisar
  }
}

/** Retorna `false` se não deu para salvar (bloqueado, cheio...). O fluxo segue sem o salvamento. */
export function saveSlugs(slugs: readonly string[], getStorage: StorageGetter = browserStorage): boolean {
  try {
    const storage = getStorage();
    if (!storage) return false;
    storage.setItem(KEY, JSON.stringify(slugs));
    notifyChange();
    return true;
  } catch {
    return false;
  }
}

export function clearSavedSlugs(getStorage: StorageGetter = browserStorage): void {
  try {
    getStorage()?.removeItem(KEY);
  } catch {
    // bloqueado: não há o que limpar
  }
  notifyChange();
}

/** Para `useSyncExternalStore`: avisa mudanças feitas nesta aba e em outras abas. */
export function subscribeSavedResult(onChange: () => void): () => void {
  window.addEventListener(CHANGE_EVENT, onChange);
  window.addEventListener("storage", onChange);
  return () => {
    window.removeEventListener(CHANGE_EVENT, onChange);
    window.removeEventListener("storage", onChange);
  };
}
