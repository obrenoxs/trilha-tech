// Escritas e interações, chamadas pelo navegador (componentes de cliente).
// Fica separado de `index.ts` porque lá há funções com "use cache", que só existem no servidor.
import { post } from "./http";
import type { ChatRequest, ChatResponse, ResultadoQuiz, ResultadoQuizRequest } from "./types";

export const postQuizResultado = (body: ResultadoQuizRequest) =>
  post<ResultadoQuiz>("/api/quiz/resultado", body);

export const postMensagemAssistente = (body: ChatRequest) =>
  post<ChatResponse>("/api/assistente/mensagens", body);
