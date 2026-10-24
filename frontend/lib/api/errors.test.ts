import { describe, expect, it } from "vitest";
import { ApiError, DEFAULT_RETRY_AFTER_SECONDS, parseRetryAfter } from "./errors";

describe("ApiError", () => {
  it.each([
    [400, "bad-request"],
    [404, "not-found"],
    [413, "payload-too-large"],
    [429, "rate-limited"],
    [503, "unavailable"],
    [500, "server"],
    [502, "server"],
  ] as const)("mapeia o status %i para %s", (status, kind) => {
    const error = new ApiError(status);
    expect(error.kind).toBe(kind);
    expect(error.userMessage.length).toBeGreaterThan(0);
  });

  it("mapeia falha de rede", () => {
    const error = ApiError.network();
    expect(error.kind).toBe("network");
    expect(error.status).toBeNull();
  });

  it("nunca usa message/path/timestamp do backend no texto para a pessoa", () => {
    const error = new ApiError(500, {
      detail: {
        timestamp: "2026-10-23T19:05:44",
        status: 500,
        error: "Internal Server Error",
        message: "NullPointerException em AreaService",
        path: "/api/areas",
      },
    });
    expect(error.userMessage).not.toMatch(/NullPointer|\/api\/areas|2026/);
    expect(error.message).toBe(error.userMessage);
    expect(error.detail?.path).toBe("/api/areas");
  });

  it("só o 429 carrega retryAfterSeconds", () => {
    expect(new ApiError(429, { retryAfter: "17" }).retryAfterSeconds).toBe(17);
    expect(new ApiError(503, { retryAfter: "17" }).retryAfterSeconds).toBeNull();
  });

  it("fromResponse lê status, corpo e Retry-After", async () => {
    const response = new Response(JSON.stringify({
        timestamp: "2026-10-23T19:05:44",
        status: 429,
        error: "Too Many Requests",
        message: "x",
        path: "/api/assistente/mensagens",
      }), {
      status: 429,
      headers: { "Retry-After": "30" },
    });
    const error = await ApiError.fromResponse(response);
    expect(error.kind).toBe("rate-limited");
    expect(error.retryAfterSeconds).toBe(30);
    expect(error.detail?.status).toBe(429);
  });

  it("fromResponse tolera corpo que não é JSON", async () => {
    const error = await ApiError.fromResponse(new Response("<html>", { status: 500 }));
    expect(error.kind).toBe("server");
    expect(error.detail).toBeNull();
  });
});

describe("parseRetryAfter", () => {
  it("usa 60 s quando o cabeçalho falta ou é inválido", () => {
    expect(DEFAULT_RETRY_AFTER_SECONDS).toBe(60);
    expect(parseRetryAfter(null)).toBe(60);
    expect(parseRetryAfter("")).toBe(60);
    expect(parseRetryAfter("abc")).toBe(60);
    expect(parseRetryAfter("0")).toBe(60);
    expect(parseRetryAfter("-5")).toBe(60);
  });

  it("lê segundos e arredonda para cima", () => {
    expect(parseRetryAfter("42")).toBe(42);
    expect(parseRetryAfter(" 1.2 ")).toBe(2);
  });
});
