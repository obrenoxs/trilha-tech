import { Action, type ActionProps } from "./action";
import { disabledLook, focusRing } from "./states";

// Sombra de 4px; no hover desce 2px e a sombra vai para 2px; pressionado desce 4px sem sombra.
const base = `mb-1 mr-1 inline-flex items-center justify-center rounded-12 border-3 border-ink bg-coral px-5 py-[11px] text-center font-body text-nav text-ink shadow-sm hover:translate-x-0.5 hover:translate-y-0.5 hover:shadow-pressed active:translate-x-1 active:translate-y-1 active:shadow-none ${focusRing} ${disabledLook}`;

export function SmallButton({ className = "", ...props }: ActionProps) {
  return <Action {...props} className={`${base} ${className}`} />;
}
