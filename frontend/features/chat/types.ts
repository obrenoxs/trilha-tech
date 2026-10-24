export type NoticeKind =
  | "rate-limit"
  | "unavailable"
  | "too-long"
  | "bad-request"
  | "server"
  | "network";

/** Mensagens só existem em memória: nada da conversa vai para o navegador (sem localStorage). */
export type ChatMessage =
  | { id: number; role: "user"; text: string; status: "pending" | "sent" | "failed" }
  | { id: number; role: "assistant"; text: string }
  | {
      id: number;
      role: "notice";
      kind: NoticeKind;
      /** Mensagem do usuário que originou o aviso; usada por "Tentar de novo". */
      forId: number;
      /** Só no 429: instante (ms) em que o envio libera. */
      retryUntil?: number;
      retryable: boolean;
    };
