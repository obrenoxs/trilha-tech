import type { MensagemHistoricoRequest } from "@/lib/api/types";

import type { ChatMessage } from "./types";

export const MAX_MESSAGE_LENGTH = 500;
export const HISTORY_LIMIT = 6;
const HISTORY_TEXT_MAX = 4000;

/**
 * Histórico enviado à API: no máximo as últimas 6 mensagens, da mais antiga para a mais recente.
 * Só entram falas que fizeram parte da conversa: avisos do sistema e mensagens que falharam
 * (sem resposta) ficam de fora. Como a conversa começa pela pessoa, uma fala inicial do
 * assistente sobra do corte e é descartada.
 */
export function buildHistory(messages: readonly ChatMessage[]): MensagemHistoricoRequest[] {
  const turns: MensagemHistoricoRequest[] = [];
  for (const message of messages) {
    if (message.role === "assistant") {
      turns.push({ papel: "ASSISTENTE", texto: message.text.slice(0, HISTORY_TEXT_MAX) });
    } else if (message.role === "user" && message.status === "sent") {
      turns.push({ papel: "USUARIO", texto: message.text.slice(0, HISTORY_TEXT_MAX) });
    }
  }
  const recent = turns.slice(-HISTORY_LIMIT);
  const firstUser = recent.findIndex((turn) => turn.papel === "USUARIO");
  return firstUser === -1 ? [] : recent.slice(firstUser);
}

export const isMessageTooLong = (text: string) => text.length > MAX_MESSAGE_LENGTH;
