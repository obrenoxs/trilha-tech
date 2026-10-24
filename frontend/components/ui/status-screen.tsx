import type { ComponentProps, ReactNode } from "react";
import { NoticeTicket } from "./notice-ticket";

type StatusScreenProps = {
  eyebrow: string;
  title: string;
  description: string;
  /** Botões e links de ação (o primeiro é o primário). */
  actions: ReactNode;
  ticket: ComponentProps<typeof NoticeTicket>;
};

// Tela de status em duas colunas (texto + bilhete). Serve ao 404 e às telas de erro.
// Desktop: texto de 520px à esquerda e bilhete à direita. Mobile: tudo em coluna, bilhete por último.
export function StatusScreen({ eyebrow, title, description, actions, ticket }: StatusScreenProps) {
  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-24">
      <div className="flex flex-col gap-5 pt-9 pb-8 md:flex-row md:items-center md:gap-10 md:pt-14 md:pb-24">
        <div className="flex flex-col items-start gap-5 md:w-[520px] md:shrink-0 md:gap-6">
          <p className="font-mono text-caption uppercase text-ink-soft md:text-label">{eyebrow}</p>
          <h1 className="break-words font-display text-[34px] leading-[1.12] font-bold tracking-[-0.04em] md:text-h1">
            {title}
          </h1>
          <p className="font-body text-body-md text-ink-soft md:text-body-lg">{description}</p>
          <div className="flex flex-col items-start gap-3.5 pt-1 md:flex-row md:gap-[18px] md:pt-2">
            {actions}
          </div>
        </div>
        <div className="md:min-w-0 md:flex-1">
          <NoticeTicket {...ticket} />
        </div>
      </div>
    </div>
  );
}
