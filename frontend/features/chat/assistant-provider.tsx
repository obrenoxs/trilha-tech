"use client";

import { usePathname } from "next/navigation";
import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useReducer,
  useRef,
  useSyncExternalStore,
  type ReactNode,
  type RefObject,
} from "react";

import { getFreshResult } from "@/features/quiz/fresh-result";
import {
  parseSavedSlugs,
  readRawSavedResult,
  subscribeSavedResult,
} from "@/features/quiz/saved-result";
import { postMensagemAssistente } from "@/lib/api/client";

import { chatReducer, initialChatState, type ChatState } from "./chat-reducer";
import { contextLabel, deriveContext, inputPlaceholder, type ContextNames } from "./context";
import { buildHistory, isMessageTooLong } from "./history";
import { noticeFromError } from "./notices";
import { OPEN_ASSISTANT_EVENT } from "./open-assistant";

type AssistantContextValue = {
  open: boolean;
  messages: ChatState["messages"];
  sending: boolean;
  /** Instante (ms) em que o envio libera após um 429, ou `null`. */
  cooldownUntil: number | null;
  /** Texto da faixa "CONTEXTO: ...", ou `null` quando a página não tem contexto. */
  contextText: string | null;
  placeholder: string;
  openPanel: () => void;
  closePanel: () => void;
  send: (text: string) => void;
  retry: (messageId: number) => void;
  launcherRef: RefObject<HTMLDivElement | null>;
  panelRef: RefObject<HTMLDivElement | null>;
};

const AssistantContext = createContext<AssistantContextValue | null>(null);

export function useAssistant(): AssistantContextValue {
  const value = useContext(AssistantContext);
  if (!value) throw new Error("useAssistant precisa estar dentro de AssistantProvider");
  return value;
}

/** Slugs das áreas do resultado do quiz: os salvos no navegador ou, se bloqueado, os da memória. */
function freshQuizSlugs(): string[] | null {
  const fresh = getFreshResult();
  if (!fresh) return null;
  return [...fresh.recomendacoes].sort((a, b) => a.posicao - b.posicao).map((item) => item.slug);
}

/**
 * Estado do assistente, no layout raiz. A conversa fica só em memória: sobrevive à navegação
 * entre páginas (o layout não remonta) e zera ao recarregar.
 */
export function AssistantProvider({
  names,
  children,
}: {
  names: ContextNames;
  children: ReactNode;
}) {
  const pathname = usePathname();
  const rawQuiz = useSyncExternalStore(subscribeSavedResult, readRawSavedResult, () => null);
  const [state, dispatch] = useReducer(chatReducer, initialChatState);

  const idRef = useRef(0);
  const launcherRef = useRef<HTMLDivElement>(null);
  const panelRef = useRef<HTMLDivElement>(null);
  const openerRef = useRef<HTMLElement | null>(null);
  const wasOpen = useRef(false);

  // O contexto vem sempre da página atual (só slugs) e do resultado do quiz, se existir.
  const context = useMemo(
    () => deriveContext(pathname, parseSavedSlugs(rawQuiz) ?? freshQuizSlugs()),
    [pathname, rawQuiz],
  );
  const contextText = useMemo(() => contextLabel(context, names), [context, names]);
  const placeholder = inputPlaceholder(context);

  const openPanel = useCallback(() => {
    // Guarda quem abriu (botão flutuante, "Conversar com a IA"...) para devolver o foco ao fechar.
    openerRef.current = document.activeElement instanceof HTMLElement ? document.activeElement : null;
    dispatch({ type: "open" });
  }, []);
  const closePanel = useCallback(() => dispatch({ type: "close" }), []);

  useEffect(() => {
    window.addEventListener(OPEN_ASSISTANT_EVENT, openPanel);
    return () => window.removeEventListener(OPEN_ASSISTANT_EVENT, openPanel);
  }, [openPanel]);

  // Foco: ao abrir, o próprio painel o leva ao campo de texto (assistant-panel.tsx). Ao fechar,
  // volta a quem abriu (ou ao botão flutuante).
  useEffect(() => {
    if (!state.open && wasOpen.current) {
      const opener = openerRef.current;
      const target = opener?.isConnected
        ? opener
        : launcherRef.current?.querySelector<HTMLElement>("button");
      target?.focus();
      openerRef.current = null;
    }
    wasOpen.current = state.open;
  }, [state.open]);

  // Libera o envio quando a contagem do 429 termina.
  useEffect(() => {
    if (state.cooldownUntil === null) return;
    const timer = setTimeout(
      () => dispatch({ type: "cooldown-end" }),
      Math.max(0, state.cooldownUntil - Date.now()),
    );
    return () => clearTimeout(timer);
  }, [state.cooldownUntil]);

  const nextId = () => ++idRef.current;

  async function deliver(id: number, text: string, isRetry: boolean) {
    const historico = buildHistory(state.messages);
    dispatch(isRetry ? { type: "retry", id } : { type: "user-sent", id, text });
    try {
      const response = await postMensagemAssistente({ mensagem: text, historico, contexto: context });
      dispatch({ type: "answered", id, replyId: nextId(), text: response.resposta });
    } catch (error) {
      dispatch({ type: "failed", id, noticeId: nextId(), ...noticeFromError(error) });
    }
  }

  // O timer acima zera `cooldownUntil` quando a espera termina.
  const coolingDown = state.cooldownUntil !== null;

  function send(rawText: string) {
    const text = rawText.trim();
    if (!text || isMessageTooLong(text) || state.sending || coolingDown) return;
    void deliver(nextId(), text, false);
  }

  function retry(messageId: number) {
    const message = state.messages.find((m) => m.id === messageId);
    if (message?.role !== "user" || state.sending || coolingDown) return;
    void deliver(message.id, message.text, true);
  }

  const value: AssistantContextValue = {
    open: state.open,
    messages: state.messages,
    sending: state.sending,
    cooldownUntil: state.cooldownUntil,
    contextText,
    placeholder,
    openPanel,
    closePanel,
    send,
    retry,
    launcherRef,
    panelRef,
  };

  return <AssistantContext.Provider value={value}>{children}</AssistantContext.Provider>;
}
