import { describe, expect, it } from "vitest";

import { ApiError } from "@/lib/api/errors";

import { chatReducer, initialChatState } from "./chat-reducer";
import { noticeFromError } from "./notices";

describe("chatReducer", () => {
  it("guarda a pergunta, a resposta e libera o envio", () => {
    let state = chatReducer(initialChatState, { type: "user-sent", id: 1, text: "oi" });
    expect(state.sending).toBe(true);
    state = chatReducer(state, { type: "answered", id: 1, replyId: 2, text: "olá" });
    expect(state.sending).toBe(false);
    expect(state.messages).toEqual([
      { id: 1, role: "user", text: "oi", status: "sent" },
      { id: 2, role: "assistant", text: "olá" },
    ]);
  });

  it("no 429 guarda o aviso com a espera e zera ao terminar", () => {
    let state = chatReducer(initialChatState, { type: "user-sent", id: 1, text: "oi" });
    state = chatReducer(state, {
      type: "failed",
      id: 1,
      noticeId: 2,
      kind: "rate-limit",
      retryable: false,
      retryUntil: 5000,
    });
    expect(state.cooldownUntil).toBe(5000);
    expect(state.messages[0]).toMatchObject({ status: "failed" });
    state = chatReducer(state, { type: "cooldown-end" });
    expect(state.cooldownUntil).toBeNull();
    expect(state.messages.some((m) => m.role === "notice")).toBe(false);
    expect(state.messages).toHaveLength(1);
  });

  it("tentar de novo reaproveita a bolha e tira o aviso", () => {
    let state = chatReducer(initialChatState, { type: "user-sent", id: 1, text: "oi" });
    state = chatReducer(state, {
      type: "failed",
      id: 1,
      noticeId: 2,
      kind: "unavailable",
      retryable: true,
    });
    state = chatReducer(state, { type: "retry", id: 1 });
    expect(state.messages).toEqual([{ id: 1, role: "user", text: "oi", status: "pending" }]);
    expect(state.sending).toBe(true);
  });

  it("abrir e fechar o painel não mexe na conversa", () => {
    let state = chatReducer(initialChatState, { type: "user-sent", id: 1, text: "oi" });
    state = chatReducer(state, { type: "open" });
    state = chatReducer(state, { type: "close" });
    expect(state.messages).toHaveLength(1);
  });
});

describe("noticeFromError", () => {
  it("429 usa o Retry-After, ou 60 segundos quando o cabeçalho não vem", () => {
    expect(noticeFromError(new ApiError(429, { retryAfter: "42" }), 1000)).toEqual({
      kind: "rate-limit",
      retryable: false,
      retryUntil: 1000 + 42_000,
    });
    expect(noticeFromError(new ApiError(429), 1000).retryUntil).toBe(1000 + 60_000);
  });

  it("503 e falhas genéricas podem tentar de novo; 413 e 400 não", () => {
    expect(noticeFromError(new ApiError(503))).toMatchObject({ kind: "unavailable", retryable: true });
    expect(noticeFromError(new ApiError(500))).toMatchObject({ kind: "server", retryable: true });
    expect(noticeFromError(ApiError.network())).toMatchObject({ kind: "network", retryable: true });
    expect(noticeFromError(new ApiError(413))).toMatchObject({ kind: "too-long", retryable: false });
    expect(noticeFromError(new ApiError(400))).toMatchObject({ kind: "bad-request", retryable: false });
    expect(noticeFromError(new Error("boom"))).toMatchObject({ kind: "server" });
  });
});
