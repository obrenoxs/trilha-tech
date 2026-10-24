import { focusRing } from "@/components/ui/states";

// Primeiro item focável da página: fica fora da tela até receber foco.
export function SkipLink() {
  return (
    <a
      href="#conteudo"
      className={`sr-only rounded-12 border-3 border-ink bg-white px-5 py-[11px] font-body text-nav text-ink focus:not-sr-only focus:absolute focus:top-3 focus:left-3 focus:z-50 ${focusRing}`}
    >
      Pular para o conteúdo
    </a>
  );
}
