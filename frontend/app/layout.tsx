import type { Metadata } from "next";
import { Suspense } from "react";
import { DM_Mono, Manrope, Unbounded } from "next/font/google";
import "./globals.css";
import { Footer } from "@/components/layout/footer";
import { Header } from "@/components/layout/header";
import { SkipLink } from "@/components/layout/skip-link";
import { Assistant } from "@/features/chat/assistant";

// Pesos usados no Figma: Unbounded SemiBold/Bold, Manrope Medium/Bold/ExtraBold, DM Mono Medium.
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

export const metadata: Metadata = {
  title: "Trilha Tech",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="pt-BR"
      className={`${unbounded.variable} ${manrope.variable} ${dmMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col">
        <SkipLink />
        <Header />
        <main id="conteudo" tabIndex={-1} className="flex-1 focus:outline-none">
          {children}
        </main>
        <Footer />
        {/* Assistente de IA em todas as páginas. A conversa vive aqui, em memória, e não zera ao navegar. */}
        <Suspense fallback={null}>
          <Assistant />
        </Suspense>
      </body>
    </html>
  );
}
