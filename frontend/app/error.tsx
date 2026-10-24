"use client";

import { LoadErrorScreen } from "@/components/ui/load-error-screen";

// Falha ao carregar uma página (API fora, resposta inesperada, erro de rede). O erro em si
// não é lido nem mostrado: em Server Components ele chega sem classe e sem texto útil,
// e o texto da tela é sempre o mesmo. `retry` busca os dados e renderiza de novo.
export default function ErrorPage({ retry }: { error: unknown; retry: () => void }) {
  return <LoadErrorScreen onRetry={retry} />;
}
