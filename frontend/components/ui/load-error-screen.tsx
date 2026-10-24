import { Button } from "./button";
import { StatusScreen } from "./status-screen";
import { routes } from "@/lib/routes";

// Tela de "não foi possível carregar" (Figma 124:669 e 124:796), usada pelo error.tsx e pelo
// global-error.tsx. Não recebe o erro de propósito: o texto é sempre o mesmo, nunca técnico.
export function LoadErrorScreen({ onRetry }: { onRetry: () => void }) {
  return (
    <StatusScreen
      eyebrow="Viagem atrasada"
      title="Algo deu errado por aqui"
      description="Não conseguimos carregar este conteúdo agora. Tente de novo em instantes. Se continuar, volte aos destinos."
      actions={
        <>
          <Button onClick={onRetry}>Tentar de novo</Button>
          <Button href={routes.destinos} variant="secondary">
            Ver destinos
          </Button>
        </>
      }
      ticket={{
        title: "CONTEÚDO INDISPONÍVEL",
        color: "coral",
        tilt: "left",
        fields: [
          { label: "De", value: "Você" },
          { label: "Para", value: "Esta página" },
          { label: "Situação", value: "Tente de novo em instantes" },
        ],
      }}
    />
  );
}
