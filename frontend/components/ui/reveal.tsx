"use client";

import { useEffect, useRef, type ReactNode } from "react";

type RevealProps = {
  /** Elemento HTML: "li" dentro de listas, "div" nos demais casos. */
  as?: "li" | "div";
  className?: string;
  children: ReactNode;
};

// Entrada ao rolar: leve subida + fade, uma vez só (estilos em globals.css, [data-reveal]).
// A animação é só acabamento: o HTML chega com tudo visível, e nada fica escondido sem JavaScript.
// Só o que está abaixo da dobra é escondido (depois da hidratação) e revelado ao aparecer na tela;
// o que já está à vista no carregamento fica como está, para não atrasar o conteúdo principal (LCP).
// Com prefers-reduced-motion não faz nada.
export function Reveal({ as: Tag = "div", className, children }: RevealProps) {
  const ref = useRef<HTMLLIElement & HTMLDivElement>(null);

  useEffect(() => {
    const element = ref.current;
    if (!element) return;
    if (window.matchMedia("(prefers-reduced-motion: reduce)").matches) return;
    if (!("IntersectionObserver" in window)) return;
    if (element.getBoundingClientRect().top < window.innerHeight) return;

    element.dataset.reveal = "pending";
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (!entry.isIntersecting) return;
        element.dataset.reveal = "shown";
        observer.disconnect();
      },
      { rootMargin: "0px 0px -8% 0px" },
    );
    observer.observe(element);
    return () => {
      observer.disconnect();
      if (element.dataset.reveal === "pending") element.dataset.reveal = "shown";
    };
  }, []);

  return (
    <Tag ref={ref} className={className}>
      {children}
    </Tag>
  );
}
