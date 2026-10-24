"use client";

import { useEffect, useId, useRef, useState, type KeyboardEvent } from "react";

import { focusRing } from "@/components/ui/states";

import { useAssistant } from "./assistant-provider";
import { MAX_MESSAGE_LENGTH } from "./history";
import { NOTICE_TEXT } from "./notices";
import type { ChatMessage } from "./types";

const FOCUSABLE = "button:not([disabled]), input:not([disabled]), a[href]";

// Campo e botão "Enviar" do Figma: sem sombra; desabilitado vira bg com borda e texto ink-soft.
const field =
  "min-w-0 flex-1 rounded-12 border-2 border-ink bg-bg py-[11px] pl-3.5 pr-2 font-body text-body-sm text-ink placeholder:text-ink-soft focus-visible:outline-3 focus-visible:outline-offset-2 focus-visible:outline-ink disabled:cursor-not-allowed disabled:border-ink-soft disabled:placeholder:text-ink-soft";
const sendButton = `shrink-0 rounded-12 border-3 border-ink bg-coral px-3.5 py-[11px] font-body text-nav text-ink md:px-4 ${focusRing} disabled:cursor-not-allowed disabled:border-ink-soft disabled:bg-bg disabled:text-ink-soft`;
const retryButton = `self-start rounded-12 border-3 border-ink bg-white px-4 py-2 font-body text-nav text-ink shadow-pressed ${focusRing}`;

const bubble =
  "rounded-14 border-2 border-ink px-3.5 py-2.5 font-body text-body-sm text-ink md:px-4 md:py-3";

function formatClock(totalSeconds: number) {
  return `${Math.floor(totalSeconds / 60)}:${String(totalSeconds % 60).padStart(2, "0")}`;
}

/** Contagem regressiva visual. É aria-hidden: o leitor de tela recebe um texto fixo, sem anunciar cada segundo. */
function Countdown({ until }: { until: number }) {
  const [now, setNow] = useState(() => Date.now());
  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(timer);
  }, []);
  return <>{formatClock(Math.max(0, Math.ceil((until - now) / 1000)))}</>;
}

function RateLimitHeading({ until }: { until: number }) {
  const [startSeconds] = useState(() => Math.max(1, Math.ceil((until - Date.now()) / 1000)));
  return (
    <>
      <span className="sr-only">
        Limite de mensagens atingido. O envio libera em cerca de {startSeconds} segundos.
      </span>
      <span aria-hidden="true">
        {NOTICE_TEXT["rate-limit"].heading} <Countdown until={until} />
      </span>
    </>
  );
}

function Message({ message, onRetry }: { message: ChatMessage; onRetry: (id: number) => void }) {
  if (message.role === "user") {
    return (
      <div className={`${bubble} bg-periwinkle`}>
        <p className="whitespace-pre-wrap [overflow-wrap:anywhere]">
          <span className="sr-only">Você: </span>
          {message.text}
        </p>
      </div>
    );
  }
  if (message.role === "assistant") {
    return (
      <div className={`${bubble} bg-bg`}>
        <p className="whitespace-pre-wrap [overflow-wrap:anywhere]">
          <span className="sr-only">Assistente: </span>
          {message.text}
        </p>
      </div>
    );
  }
  const text = NOTICE_TEXT[message.kind];
  const rateLimit = message.kind === "rate-limit" && message.retryUntil !== undefined;
  return (
    <div
      className={`flex flex-col gap-2 rounded-14 border-2 border-ink px-3.5 py-3 text-ink md:px-4 md:py-3.5 ${message.kind === "rate-limit" ? "bg-lemon" : "bg-coral"}`}
    >
      <p className="font-mono text-caption uppercase">
        {rateLimit ? <RateLimitHeading until={message.retryUntil!} /> : text.heading}
      </p>
      <p className="font-body text-body-sm">{text.body}</p>
      {message.retryable && (
        <button type="button" className={retryButton} onClick={() => onRetry(message.forId)}>
          Tentar de novo
        </button>
      )}
    </div>
  );
}

export function AssistantPanel() {
  const {
    open,
    messages,
    sending,
    cooldownUntil,
    contextText,
    placeholder,
    closePanel,
    send,
    retry,
    panelRef,
  } = useAssistant();
  const [draft, setDraft] = useState("");
  const logRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const titleId = useId();
  const counterId = useId();

  const cooling = cooldownUntil !== null;
  const tooLong = draft.length > MAX_MESSAGE_LENGTH;

  // Mantém a última mensagem à vista.
  useEffect(() => {
    const log = logRef.current;
    if (open && log) log.scrollTop = log.scrollHeight;
  }, [open, messages]);

  // Desabilitar o campo (espera do 429) tira o foco dele: leva o foco para o painel e, quando a
  // espera acaba, devolve ao campo. Assim o teclado nunca fica "solto" na página.
  useEffect(() => {
    const panel = panelRef.current;
    if (!open || !panel) return;
    const active = document.activeElement;
    const lost = active === document.body || active === panel;
    if (cooling && lost) panel.focus();
    else if (!cooling && active === panel && !sending) inputRef.current?.focus();
  }, [cooling, open, sending, panelRef]);

  // Esc fecha o painel quando o foco está nele ou em lugar nenhum (por exemplo, após o campo
  // ser desabilitado), sem interferir em quem digita em outro campo da página.
  useEffect(() => {
    if (!open) return;
    function onDocumentKeyDown(event: globalThis.KeyboardEvent) {
      if (event.key !== "Escape") return;
      const active = document.activeElement;
      if (active === document.body || panelRef.current?.contains(active)) closePanel();
    }
    document.addEventListener("keydown", onDocumentKeyDown);
    return () => document.removeEventListener("keydown", onDocumentKeyDown);
  }, [open, closePanel, panelRef]);

  function onKeyDown(event: KeyboardEvent<HTMLDivElement>) {
    // No celular o painel ocupa a tela toda: o Tab não pode escapar para a página atrás.
    if (event.key === "Tab" && window.matchMedia("(max-width: 767.98px)").matches) {
      const panel = panelRef.current;
      if (!panel) return;
      const items = [...panel.querySelectorAll<HTMLElement>(FOCUSABLE)];
      if (items.length === 0) return;
      const first = items[0];
      const last = items[items.length - 1];
      const active = document.activeElement;
      if (event.shiftKey && (active === first || active === panel)) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && active === last) {
        event.preventDefault();
        first.focus();
      }
    }
  }

  function onSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (sending || cooling || tooLong || draft.trim() === "") return;
    send(draft);
    setDraft("");
  }

  return (
    <div
      ref={panelRef}
      hidden={!open}
      role="dialog"
      aria-labelledby={titleId}
      tabIndex={-1}
      onKeyDown={onKeyDown}
      className="fixed inset-0 z-50 flex flex-col overflow-hidden bg-white outline-none md:inset-auto md:right-8 md:bottom-8 md:h-[560px] md:max-h-[calc(100dvh-4rem)] md:w-[420px] md:rounded-[20px] md:border-3 md:border-ink md:shadow-lg"
    >
      <div
        data-surface="dark"
        className="flex shrink-0 items-center justify-between gap-3 bg-ink px-5 pt-6 pb-4 text-white md:py-4"
      >
        <h2
          id={titleId}
          className="font-display text-[16px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card"
        >
          Assistente Trilha Tech
        </h2>
        <button
          type="button"
          aria-label="Fechar o assistente"
          onClick={closePanel}
          className={`rounded-8 px-1 font-body text-nav ${focusRing}`}
        >
          <span aria-hidden="true">✕</span>
        </button>
      </div>

      {contextText && (
        <p className="shrink-0 bg-lemon px-5 py-2 font-mono text-caption uppercase text-ink">
          Contexto: {contextText}
        </p>
      )}

      <div
        ref={logRef}
        role="log"
        aria-label="Conversa com o assistente"
        aria-busy={sending}
        className="flex min-h-0 flex-1 flex-col gap-3.5 overflow-y-auto p-5"
      >
        {messages.map((message) => (
          <Message key={message.id} message={message} onRetry={retry} />
        ))}
        {sending && (
          <div className={`${bubble} bg-bg`}>
            <p>Pensando…</p>
          </div>
        )}
        <p className="font-mono text-caption uppercase text-ink-soft">
          Não digite dados pessoais. A IA responde só sobre TI e carreira.
        </p>
      </div>

      <form onSubmit={onSubmit} className="shrink-0 border-t-3 border-ink px-4 pt-3.5 pb-7 md:pb-3.5">
        {draft.length > 0 && (
          <p
            id={counterId}
            className="mb-2 text-right font-mono text-caption uppercase text-ink-soft"
          >
            <span className={tooLong ? "font-bold text-ink" : undefined}>
              {draft.length}/{MAX_MESSAGE_LENGTH}
            </span>
            {tooLong && " · limite excedido"}
          </p>
        )}
        <div className="flex items-center gap-2.5">
          <input
            type="text"
            value={draft}
            onChange={(event) => setDraft(event.target.value)}
            ref={inputRef}
            disabled={cooling}
            readOnly={sending}
            placeholder={cooling ? "Aguarde para enviar de novo…" : placeholder}
            aria-label="Sua mensagem"
            aria-invalid={tooLong}
            aria-describedby={draft.length > 0 ? counterId : undefined}
            autoComplete="off"
            enterKeyHint="send"
            className={field}
          />
          <button type="submit" disabled={cooling || tooLong} className={sendButton}>
            Enviar
          </button>
        </div>
        <p className="sr-only" role="status">
          {tooLong
            ? `Mensagem acima de ${MAX_MESSAGE_LENGTH} caracteres. Apague ${draft.length - MAX_MESSAGE_LENGTH} para enviar.`
            : ""}
        </p>
      </form>
    </div>
  );
}
