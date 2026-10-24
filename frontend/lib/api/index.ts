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

const ONE_HOUR = 60 * 60;

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

/** Leituras para componentes de servidor: cache de 1 hora. */
function read<T>(path: string): Promise<T> {
  return request<T>(path, { next: { revalidate: ONE_HOUR } });
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
export const getArea = (slug: string) => read<AreaDetalhe>(`/api/areas/${enc(slug)}`);
export const getTrilha = (slug: string) =>
  read<TrilhaDetalhe>(`/api/trilhas/${enc(slug)}`);
export const getTrilhaPdfUrl = (slug: string) =>
  `${API_URL}/api/trilhas/${enc(slug)}/pdf`;

export const getQuiz = () => read<Pergunta[]>("/api/quiz");
export const postQuizResultado = (body: ResultadoQuizRequest) =>
  post<ResultadoQuiz>("/api/quiz/resultado", body);
export const postMensagemAssistente = (body: ChatRequest) =>
  post<ChatResponse>("/api/assistente/mensagens", body);
