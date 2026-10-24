// Classes de estado compartilhadas pelos componentes interativos.
// Ficam fora de action.tsx (que é "use client") para poder ser usadas em componentes de servidor.

// Foco: contorno de 3px com offset de 3px, em ink. Dentro de um ancestral com
// data-surface="dark" o contorno vira branco (variante on-dark, em globals.css).
export const focusRing =
  "focus-visible:outline-3 focus-visible:outline-offset-3 focus-visible:outline-ink on-dark:focus-visible:outline-white";

// Desabilitado: fundo bg, borda e texto em ink-soft, sem sombra e sem deslocamento.
// Vale para <button disabled> e para <a aria-disabled="true">. O "!" garante que vença
// o hover e o pressionado.
export const disabledLook =
  "disabled:cursor-not-allowed disabled:border-ink-soft! disabled:bg-bg! disabled:text-ink-soft! disabled:shadow-none! disabled:translate-x-0! disabled:translate-y-0! " +
  "aria-disabled:cursor-not-allowed aria-disabled:border-ink-soft! aria-disabled:bg-bg! aria-disabled:text-ink-soft! aria-disabled:shadow-none! aria-disabled:translate-x-0! aria-disabled:translate-y-0! " +
  "aria-disabled:focus-visible:outline-ink!";
