import { ApiError } from "./errors";

/** Chamadas direto do navegador (CORS); nada de proxy, para o limite por IP funcionar. */
export const API_URL = (
  process.env.NEXT_PUBLIC_API_URL ?? "https://trilha-tech-production.up.railway.app"
).replace(/\/+$/, "");

export async function request<T>(path: string, init?: RequestInit): Promise<T> {
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

export function post<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
    cache: "no-store",
  });
}
