import { DM_Mono, Manrope, Unbounded } from "next/font/google";

// Pesos usados no Figma: Unbounded SemiBold/Bold, Manrope Medium/Bold/ExtraBold, DM Mono Medium.
// Ficam aqui (e não no layout) para o global-error, que substitui o layout raiz, usar as mesmas fontes.
const unbounded = Unbounded({
  variable: "--font-unbounded",
  subsets: ["latin"],
  weight: ["600", "700"],
});

const manrope = Manrope({
  variable: "--font-manrope",
  subsets: ["latin"],
  weight: ["500", "700", "800"],
});

const dmMono = DM_Mono({
  variable: "--font-dm-mono",
  subsets: ["latin"],
  weight: ["500"],
});

export const fontVariables = `${unbounded.variable} ${manrope.variable} ${dmMono.variable}`;
