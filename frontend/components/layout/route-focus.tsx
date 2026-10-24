"use client";

import { usePathname } from "next/navigation";
import { useEffect, useRef } from "react";

// Ao mudar de página, leva o foco para o <main id="conteudo"> (que tem tabIndex -1 e não mostra
// contorno). Sem isto, depois de abrir um link com o Enter o foco cai no <body> e o próximo Tab
// pula para o fim da página; assim ele segue do começo do conteúdo novo.
// - `preventScroll`: não mexe na rolagem, então o botão voltar continua restaurando a posição.
// - Não roda no primeiro carregamento (a pessoa começa pelo início da página, como sempre).
// - Só reage à mudança de caminho: âncoras (#) e a mesma página não movem o foco.
export function RouteFocus() {
  const pathname = usePathname();
  const previous = useRef<string | null>(null);

  useEffect(() => {
    if (previous.current !== null && previous.current !== pathname) {
      document.getElementById("conteudo")?.focus({ preventScroll: true });
    }
    previous.current = pathname;
  }, [pathname]);

  return null;
}
