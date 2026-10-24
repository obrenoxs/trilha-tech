import { Action, type ActionProps } from "./action";
import { disabledLook, focusRing } from "./states";

type ButtonProps = ActionProps & { variant?: "primary" | "secondary" | "dark" };

// A margem de 6px reserva o espaço da sombra (como o quadro do Figma). No hover o botão
// desce 4px e a sombra encolhe para 2px. Pressionado: desce o tamanho da sombra e ela some.
const base = `mb-1.5 mr-1.5 inline-flex items-center justify-center rounded-14 border-3 border-ink px-[30px] py-[18px] text-center font-body text-button hover:translate-x-1 hover:translate-y-1 ${focusRing} ${disabledLook}`;

const variants = {
  primary:
    "bg-coral text-ink shadow-md hover:shadow-pressed active:translate-x-1.5 active:translate-y-1.5 active:shadow-none",
  secondary:
    "bg-white text-ink shadow-md hover:shadow-pressed active:translate-x-1.5 active:translate-y-1.5 active:shadow-none",
  // sombra coral de 5px
  dark: "bg-ink text-lemon shadow-coral-md hover:shadow-coral-pressed active:translate-x-[5px] active:translate-y-[5px] active:shadow-none",
};

export function Button({ variant = "primary", className = "", ...props }: ButtonProps) {
  return <Action {...props} className={`${base} ${variants[variant]} ${className}`} />;
}
