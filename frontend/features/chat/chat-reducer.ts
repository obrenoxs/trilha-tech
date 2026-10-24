import type { ChatMessage, NoticeKind } from "./types";

export type ChatState = {
  open: boolean;
  messages: ChatMessage[];
  sending: boolean;
  /** Instante (ms) em que o envio libera após um 429; `null` quando não há espera. */
  cooldownUntil: number | null;
};

export type ChatAction =
  | { type: "open" }
  | { type: "close" }
  | { type: "user-sent"; id: number; text: string }
  | { type: "retry"; id: number }
  | { type: "answered"; id: number; replyId: number; text: string }
  | {
      type: "failed";
      id: number;
      noticeId: number;
      kind: NoticeKind;
      retryable: boolean;
      retryUntil?: number;
    }
  | { type: "cooldown-end" };

export const initialChatState: ChatState = {
  open: false,
  messages: [],
  sending: false,
  cooldownUntil: null,
};

function setUserStatus(
  messages: ChatMessage[],
  id: number,
  status: "pending" | "sent" | "failed",
): ChatMessage[] {
  return messages.map((message) =>
    message.id === id && message.role === "user" ? { ...message, status } : message,
  );
}

export function chatReducer(state: ChatState, action: ChatAction): ChatState {
  switch (action.type) {
    case "open":
      return { ...state, open: true };
    case "close":
      return { ...state, open: false };
    case "user-sent":
      return {
        ...state,
        sending: true,
        messages: [
          ...state.messages,
          { id: action.id, role: "user", text: action.text, status: "pending" },
        ],
      };
    case "retry":
      // Reaproveita a bolha da pergunta e tira o aviso de falha dela.
      return {
        ...state,
        sending: true,
        messages: setUserStatus(
          state.messages.filter((m) => !(m.role === "notice" && m.forId === action.id)),
          action.id,
          "pending",
        ),
      };
    case "answered":
      return {
        ...state,
        sending: false,
        messages: [
          ...setUserStatus(state.messages, action.id, "sent"),
          { id: action.replyId, role: "assistant", text: action.text },
        ],
      };
    case "failed":
      return {
        ...state,
        sending: false,
        cooldownUntil: action.retryUntil ?? state.cooldownUntil,
        messages: [
          ...setUserStatus(state.messages, action.id, "failed"),
          {
            id: action.noticeId,
            role: "notice",
            kind: action.kind,
            forId: action.id,
            retryable: action.retryable,
            retryUntil: action.retryUntil,
          },
        ],
      };
    case "cooldown-end":
      return {
        ...state,
        cooldownUntil: null,
        messages: state.messages.filter((m) => !(m.role === "notice" && m.kind === "rate-limit")),
      };
  }
}
