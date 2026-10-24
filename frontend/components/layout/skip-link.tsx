import { focusRing } from "@/components/ui/states";

// Primeiro item focável da página: fica fora da tela até receber foco.
export function SkipLink() {
  return (
    <a
      href="#conteudo"
      className={`absolute top-3 left-3 z-50 -translate-y-[200%] rounded-12 border-3 border-ink bg-white px-5 py-[11px] font-body text-nav text-ink focus:translate-y-0 ${focusRing}`}
    >
      Pular para o conteúdo
    </a>
  );
}
