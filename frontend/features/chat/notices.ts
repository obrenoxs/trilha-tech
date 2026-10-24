import { ApiError } from "@/lib/api/errors";

import type { NoticeKind } from "./types";

/** Textos próprios por tipo de erro: nada de status, código nem mensagem do servidor. */
export const NOTICE_TEXT: Record<NoticeKind, { heading: string; body: string }> = {
  "rate-limit": {
    heading: "Tente de novo em",
    body: "Você enviou várias mensagens em pouco tempo. Respire um instante: o envio libera sozinho quando o tempo acabar.",
  },
  unavailable: {
    heading: "Assistente fora do ar",
    body: "Não consegui responder agora. O resto do site continua funcionando. Tente de novo em alguns minutos.",
  },
  "too-long": {
    heading: "Mensagem muito longa",
    body: "Sua mensagem passou do limite de tamanho. Escreva algo mais curto e envie de novo.",
  },
  "bad-request": {
    heading: "Não foi possível enviar",
    body: "Não consegui entender essa mensagem. Confira o texto e tente de novo.",
  },
  server: {
    heading: "Algo deu errado",
    body: "Tive um problema do meu lado para responder agora. Tente de novo.",
  },
  network: {
    heading: "Sem conexão",
    body: "Não consegui conectar. Verifique sua internet e tente de novo.",
  },
};

export type NoticeInfo = { kind: NoticeKind; retryable: boolean; retryUntil?: number };

/** Converte o erro da chamada em aviso. `now` (ms) é injetável para os testes. */
export function noticeFromError(error: unknown, now: number = Date.now()): NoticeInfo {
  if (error instanceof ApiError) {
    switch (error.kind) {
      case "rate-limited":
        // Sem Retry-After legível, ApiError já usa 60 s.
        return {
          kind: "rate-limit",
          retryable: false,
          retryUntil: now + (error.retryAfterSeconds ?? 60) * 1000,
        };
      case "unavailable":
        return { kind: "unavailable", retryable: true };
      case "payload-too-large":
        return { kind: "too-long", retryable: false };
      case "bad-request":
        return { kind: "bad-request", retryable: false };
      case "network":
        return { kind: "network", retryable: true };
      default:
        return { kind: "server", retryable: true };
    }
  }
  return { kind: "server", retryable: true };
}
