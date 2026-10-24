import type { Metadata } from "next";
import { Button } from "@/components/ui/button";
import { StatusScreen } from "@/components/ui/status-screen";
import { routes } from "@/lib/routes";

export const metadata: Metadata = {
  title: "Página não encontrada | Trilha Tech",
};

// Slug inexistente (área ou trilha) e qualquer rota desconhecida caem aqui. Figma 124:623 e 124:756.
export default function NotFound() {
  return (
    <StatusScreen
      eyebrow="Destino não encontrado"
      title="Essa página não existe"
      description="O endereço pode ter mudado ou ter sido digitado errado. Volte aos destinos e escolha uma área para começar."
      actions={
        <>
          <Button href={routes.destinos}>Ver destinos</Button>
          <Button href={routes.home} variant="secondary">
            Voltar ao início
          </Button>
        </>
      }
      ticket={{
        title: "ROTA INEXISTENTE",
        color: "lemon",
        tilt: "right",
        fields: [
          { label: "De", value: "Você" },
          { label: "Para", value: "Destino desconhecido" },
          { label: "Situação", value: "Bilhete não encontrado" },
        ],
      }}
    />
  );
}
