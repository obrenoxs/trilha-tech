import { ViewTransition, type ReactNode } from "react";

// Transição entre páginas (View Transitions API, via <ViewTransition> do React 19.3): a página nova
// entra com fade de 250 ms por cima da antiga, só com opacidade (estilos em globals.css).
// Fica no template, e não no layout: o layout persiste entre navegações, o template é recriado,
// e é essa troca que dispara a saída e a entrada. Cabeçalho, rodapé e assistente ficam fora.
// Em navegadores sem suporte a página só troca, como antes.
export default function Template({ children }: { children: ReactNode }) {
  return (
    <ViewTransition enter="page-in" exit="page-out" default="none">
      {children}
    </ViewTransition>
  );
}
