import { Action, type ActionProps } from "./action";
import { disabledLook, focusRing } from "./states";

// Sombra coral de 5px; pressionado desce 5px e a sombra some.
const base = `mb-1.5 mr-1.5 inline-flex whitespace-nowrap items-center justify-center gap-2.5 rounded-14 border-3 border-ink bg-ink px-6 py-4 font-body text-button text-lemon shadow-coral-md hover:translate-x-1 hover:translate-y-1 hover:shadow-coral-pressed active:translate-x-[5px] active:translate-y-[5px] active:shadow-none ${focusRing} ${disabledLook}`;

export function AssistantButton({ children, className = "", ...props }: ActionProps) {
  return (
    <Action {...props} className={`${base} ${className}`}>
      {/* Mesmo traçado do ícone do Figma, com currentColor para acompanhar o texto (inclusive desabilitado). */}
      <svg aria-hidden="true" width="22" height="22" viewBox="0 0 22 22" fill="none">
        <path
          d="M4 5H18V14H11L7 18V14H4V5Z"
          stroke="currentColor"
          strokeWidth="2.4"
          strokeLinecap="round"
          strokeLinejoin="round"
        />
      </svg>
      {children ?? "Perguntar à IA"}
    </Action>
  );
}
