import { cacheLife } from "next/cache";

import { ApiError } from "./errors";
import type { components } from "./schema";

export { ApiError, DEFAULT_RETRY_AFTER_SECONDS } from "./errors";
export type { ApiErrorKind } from "./errors";

type Schemas = components["schemas"];
export type Area = Schemas["AreaResponse"];
export type AreaDetalhe = Schemas["AreaDetalheResponse"];
export type TrilhaDetalhe = Schemas["TrilhaDetalheResponse"];
export type Pergunta = Schemas["PerguntaResponse"];
export type ResultadoQuizRequest = Schemas["ResultadoRequest"];
export type ResultadoQuiz = Schemas["ResultadoResponse"];
export type ChatRequest = Schemas["ChatRequest"];
export type ChatResponse = Schemas["ChatResponse"];

/** Chamadas direto do navegador (CORS); nada de proxy, para o limite por IP funcionar. */
export const API_URL = (
  process.env.NEXT_PUBLIC_API_URL ?? "https://trilha-tech-production.up.railway.app"
).replace(/\/+$/, "");

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, init);
  } catch {
    throw ApiError.network();
  }
  if (!response.ok) throw await ApiError.fromResponse(response);
  try {
    return (await response.json()) as T;
  } catch {
    throw new ApiError(500);
  }
}

/**
 * Leituras para componentes de servidor, com cache de 1 hora (perfil "hours":
 * revalidate 3600s). Só o valor resolvido é guardado: se `request` lançar um
 * ApiError, a exceção atravessa o `use cache` sem virar entrada de cache, e a
 * próxima chamada tenta a API de novo. O argumento (`path`) é uma string, logo
 * serializável, e também é a chave do cache.
 */
async function read<T>(path: string): Promise<T> {
  "use cache";
  cacheLife("hours");
  return request<T>(path);
}

/**
 * Igual a `read`, para recursos buscados por slug. Um 404 (ou 400, slug malformado) vira `null` em vez de exceção:
 * o erro perde a classe ao atravessar o `use cache` (chega como um Error genérico, só com
 * `digest`), então a página não conseguiria reconhecê-lo. O 404 é uma resposta estável e
 * pode ser cacheado; os demais erros continuam lançando e não entram no cache.
 */
async function readOrNull<T>(path: string): Promise<T | null> {
  "use cache";
  cacheLife("hours");
  try {
    return await request<T>(path);
  } catch (error) {
    // 400 também: a API recusa slug malformado (ex.: "%00") em vez de responder 404.
    if (error instanceof ApiError && (error.kind === "not-found" || error.kind === "bad-request"))
      return null;
    throw error;
  }
}

function post<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
    cache: "no-store",
  });
}

const enc = encodeURIComponent;

export const getAreas = () => read<Area[]>("/api/areas");
/** `null` quando o slug não existe (404). */
export const getArea = (slug: string) => readOrNull<AreaDetalhe>(`/api/areas/${enc(slug)}`);
/** `null` quando o slug não existe (404). */
export const getTrilha = (slug: string) =>
  readOrNull<TrilhaDetalhe>(`/api/trilhas/${enc(slug)}`);
export const getTrilhaPdfUrl = (slug: string) =>
  `${API_URL}/api/trilhas/${enc(slug)}/pdf`;

export const getQuiz = () => read<Pergunta[]>("/api/quiz");
export const postQuizResultado = (body: ResultadoQuizRequest) =>
  post<ResultadoQuiz>("/api/quiz/resultado", body);
export const postMensagemAssistente = (body: ChatRequest) =>
  post<ChatResponse>("/api/assistente/mensagens", body);
