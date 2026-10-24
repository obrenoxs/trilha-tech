import type { components } from "./schema";

type ErroResponse = components["schemas"]["ErroResponse"];

/** Tempo de espera padrão (segundos) quando o 429 vem sem `Retry-After` legível. */
export const DEFAULT_RETRY_AFTER_SECONDS = 60;

export type ApiErrorKind =
  | "bad-request"
  | "not-found"
  | "payload-too-large"
  | "rate-limited"
  | "unavailable"
  | "server"
  | "network";

type ErrorInfo = { kind: ApiErrorKind; userMessage: string };

const BY_STATUS: Record<number, ErrorInfo> = {
  400: {
    kind: "bad-request",
    userMessage: "Não foi possível processar o pedido. Confira os dados e tente de novo.",
  },
  404: { kind: "not-found", userMessage: "Não encontramos o que você procurava." },
  413: { kind: "payload-too-large", userMessage: "Mensagem muito longa." },
  429: {
    kind: "rate-limited",
    userMessage: "Você enviou mensagens demais. Aguarde um pouco para tentar de novo.",
  },
  503: {
    kind: "unavailable",
    userMessage: "O assistente está indisponível agora. O resto do site segue funcionando.",
  },
};

const GENERIC: ErrorInfo = {
  kind: "server",
  userMessage: "Algo deu errado do nosso lado. Tente de novo.",
};

const NETWORK: ErrorInfo = {
  kind: "network",
  userMessage: "Não conseguimos conectar. Verifique sua internet e tente de novo.",
};

/** Lê `Retry-After` (em segundos). Valor ausente, inválido ou não positivo cai no padrão. */
export function parseRetryAfter(value: string | null | undefined): number {
  if (value == null) return DEFAULT_RETRY_AFTER_SECONDS;
  const seconds = Number(value.trim());
  if (!Number.isFinite(seconds) || seconds <= 0) return DEFAULT_RETRY_AFTER_SECONDS;
  return Math.ceil(seconds);
}

/**
 * Erro da API. O texto para a pessoa (`userMessage`) depende só do status;
 * `message`, `path` e `timestamp` do backend ficam em `detail`, apenas para logs/depuração,
 * e nunca devem ser exibidos.
 */
export class ApiError extends Error {
  readonly status: number | null;
  readonly kind: ApiErrorKind;
  readonly userMessage: string;
  /** Só no 429: segundos até poder tentar de novo. */
  readonly retryAfterSeconds: number | null;
  /** Corpo de erro do backend, se houver. Não exibir ao usuário. */
  readonly detail: ErroResponse | null;

  constructor(
    status: number | null,
    options: { detail?: ErroResponse | null; retryAfter?: string | null } = {},
  ) {
    const info = status === null ? NETWORK : (BY_STATUS[status] ?? GENERIC);
    super(info.userMessage);
    this.name = "ApiError";
    this.status = status;
    this.kind = info.kind;
    this.userMessage = info.userMessage;
    this.detail = options.detail ?? null;
    this.retryAfterSeconds =
      status === 429 ? parseRetryAfter(options.retryAfter) : null;
  }

  static network(): ApiError {
    return new ApiError(null);
  }

  /** Monta o erro a partir de uma resposta HTTP não-OK, tolerando corpo vazio ou não-JSON. */
  static async fromResponse(response: Response): Promise<ApiError> {
    let detail: ErroResponse | null = null;
    try {
      const body: unknown = await response.json();
      if (body && typeof body === "object") detail = body as ErroResponse;
    } catch {
      // corpo vazio ou não-JSON: o status basta
    }
    return new ApiError(response.status, {
      detail,
      retryAfter: response.headers.get("Retry-After"),
    });
  }
}
