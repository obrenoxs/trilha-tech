import { describe, expect, it } from "vitest";

import {
  clearSavedSlugs,
  parseSavedSlugs,
  readSavedSlugs,
  saveSlugs,
  type StorageGetter,
} from "./saved-result";

function fakeStorage(initial: Record<string, string> = {}): Storage {
  const data = new Map(Object.entries(initial));
  return {
    get length() {
      return data.size;
    },
    clear: () => data.clear(),
    getItem: (key) => data.get(key) ?? null,
    key: (index) => [...data.keys()][index] ?? null,
    removeItem: (key) => void data.delete(key),
    setItem: (key, value) => void data.set(key, value),
  };
}

const KEY = "trilha-tech:quiz-resultado";

// Navegador com o armazenamento bloqueado: até acessar `window.localStorage` lança exceção.
const blocked: StorageGetter = () => {
  throw new DOMException("bloqueado", "SecurityError");
};

// Armazenamento que existe mas falha em toda operação (cota cheia, modo privado antigo...).
const broken: StorageGetter = () => ({
  ...fakeStorage(),
  getItem: () => {
    throw new Error("falha");
  },
  setItem: () => {
    throw new DOMException("cheio", "QuotaExceededError");
  },
  removeItem: () => {
    throw new Error("falha");
  },
});

describe("readSavedSlugs", () => {
  it("lê os slugs salvos", () => {
    const storage = fakeStorage({ [KEY]: JSON.stringify(["dados-e-ia", "ux-ui-design"]) });
    expect(readSavedSlugs(() => storage)).toEqual(["dados-e-ia", "ux-ui-design"]);
  });

  it("devolve null quando está vazio", () => {
    expect(readSavedSlugs(() => fakeStorage())).toBeNull();
  });

  it("devolve null quando o armazenamento está bloqueado", () => {
    expect(readSavedSlugs(blocked)).toBeNull();
    expect(readSavedSlugs(broken)).toBeNull();
    expect(readSavedSlugs(() => null)).toBeNull();
  });

  it.each([
    ["JSON quebrado", "{nao-e-json"],
    ["não é lista", JSON.stringify({ slug: "dados" })],
    ["lista vazia", "[]"],
    ["itens que não são texto", JSON.stringify([1, 2])],
    ["texto vazio", JSON.stringify([""])],
    ["mais de 3 áreas", JSON.stringify(["a", "b", "c", "d"])],
    ["objetos com pontos", JSON.stringify([{ slug: "a", pontos: 10 }])],
  ])("devolve null para conteúdo inválido: %s", (_nome, conteudo) => {
    expect(readSavedSlugs(() => fakeStorage({ [KEY]: conteudo }))).toBeNull();
  });
});

describe("parseSavedSlugs", () => {
  it("trata null como vazio", () => {
    expect(parseSavedSlugs(null)).toBeNull();
  });
});

describe("saveSlugs e clearSavedSlugs", () => {
  it("guarda só a lista de slugs", () => {
    const storage = fakeStorage();
    expect(saveSlugs(["a", "b", "c"], () => storage)).toBe(true);
    expect(storage.getItem(KEY)).toBe('["a","b","c"]');
    expect(storage.length).toBe(1);
  });

  it("não lança nem quebra o fluxo quando não consegue salvar", () => {
    expect(saveSlugs(["a"], blocked)).toBe(false);
    expect(saveSlugs(["a"], broken)).toBe(false);
    expect(saveSlugs(["a"], () => null)).toBe(false);
  });

  it("limpa o resultado e tolera armazenamento bloqueado", () => {
    const storage = fakeStorage({ [KEY]: '["a"]' });
    clearSavedSlugs(() => storage);
    expect(storage.getItem(KEY)).toBeNull();
    expect(() => clearSavedSlugs(blocked)).not.toThrow();
    expect(() => clearSavedSlugs(broken)).not.toThrow();
  });
});
