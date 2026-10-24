"use client";

import "./globals.css";
import { LoadErrorScreen } from "@/components/ui/load-error-screen";
import { fontVariables } from "./fonts";

// Último recurso: falha no layout raiz, que o error.tsx não alcança. Este arquivo substitui o
// layout, então traz o próprio <html>, os estilos e as fontes. Sem cabeçalho, rodapé nem assistente.
export default function GlobalError({ retry }: { error: unknown; retry: () => void }) {
  return (
    <html lang="pt-BR" className={`${fontVariables} h-full antialiased`}>
      <head>
        <title>Algo deu errado | Trilha Tech</title>
      </head>
      <body className="min-h-full">
        <main>
          <LoadErrorScreen onRetry={retry} />
        </main>
      </body>
    </html>
  );
}
