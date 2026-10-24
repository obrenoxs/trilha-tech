import type { Metadata } from "next";
import { Suspense } from "react";
import "./globals.css";
import { Footer } from "@/components/layout/footer";
import { Header } from "@/components/layout/header";
import { SkipLink } from "@/components/layout/skip-link";
import { Assistant } from "@/features/chat/assistant";
import { fontVariables } from "./fonts";

export const metadata: Metadata = {
  title: "Trilha Tech",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="pt-BR" className={`${fontVariables} h-full antialiased`}>
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
