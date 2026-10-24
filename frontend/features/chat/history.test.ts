import { describe, expect, it } from "vitest";

import { buildHistory, HISTORY_LIMIT, isMessageTooLong, MAX_MESSAGE_LENGTH } from "./history";
import type { ChatMessage } from "./types";

let id = 0;
const user = (text: string, status: "sent" | "failed" | "pending" = "sent"): ChatMessage => ({
  id: ++id,
  role: "user",
  text,
  status,
});
const assistant = (text: string): ChatMessage => ({ id: ++id, role: "assistant", text });
const notice = (): ChatMessage => ({
  id: ++id,
  role: "notice",
  kind: "unavailable",
  forId: 0,
  retryable: true,
});

describe("buildHistory", () => {
  it("devolve as falas na ordem, da mais antiga para a mais recente", () => {
    expect(buildHistory([user("oi"), assistant("olá")])).toEqual([
      { papel: "USUARIO", texto: "oi" },
      { papel: "ASSISTENTE", texto: "olá" },
    ]);
  });

  it("limita às 6 últimas mensagens", () => {
    const messages: ChatMessage[] = [];
    for (let i = 1; i <= 5; i++) messages.push(user(`p${i}`), assistant(`r${i}`));

    const history = buildHistory(messages);

    expect(HISTORY_LIMIT).toBe(6);
    expect(history).toHaveLength(6);
    expect(history[0]).toEqual({ papel: "USUARIO", texto: "p3" });
    expect(history.at(-1)).toEqual({ papel: "ASSISTENTE", texto: "r5" });
  });

  it("começa sempre por uma fala da pessoa, mesmo que o corte caia numa resposta", () => {
    const messages: ChatMessage[] = [];
    for (let i = 1; i <= 4; i++) messages.push(user(`p${i}`), assistant(`r${i}`));
    messages.splice(1, 1); // sem r1: a janela de 6 começaria numa resposta

    const history = buildHistory(messages);

    expect(history.length).toBeLessThanOrEqual(6);
    expect(history[0].papel).toBe("USUARIO");
  });

  it("ignora avisos do sistema e mensagens que falharam ou ainda esperam resposta", () => {
    const history = buildHistory([
      user("ok"),
      assistant("resposta"),
      user("falhou", "failed"),
      notice(),
      user("esperando", "pending"),
    ]);
    expect(history.map((turn) => turn.texto)).toEqual(["ok", "resposta"]);
  });

  it("devolve lista vazia sem conversa", () => {
    expect(buildHistory([])).toEqual([]);
    expect(buildHistory([assistant("sozinha")])).toEqual([]);
  });

  it("corta falas gigantes em 4000 caracteres", () => {
    const [turn] = buildHistory([user("a".repeat(5000))]);
    expect(turn.texto).toHaveLength(4000);
  });
});

describe("isMessageTooLong", () => {
  it("aceita até 500 caracteres e bloqueia acima", () => {
    expect(MAX_MESSAGE_LENGTH).toBe(500);
    expect(isMessageTooLong("a".repeat(500))).toBe(false);
    expect(isMessageTooLong("a".repeat(501))).toBe(true);
  });
});
