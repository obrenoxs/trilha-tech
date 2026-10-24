import {
  Badge,
  ConfirmPriceBadge,
  FreeBadge,
  PaidBadge,
} from "@/components/ui/badges";
import { focusRing } from "@/components/ui/states";
import type { TrilhaDetalhe } from "@/lib/api";
import { isSafeExternalUrl, languageLabels, resourceTypeLabels } from "@/lib/trail-format";

type Recurso = TrilhaDetalhe["etapas"][number]["recursos"][number];

function AccessBadge({ access }: { access: Recurso["acesso"] }) {
  if (access === "GRATUITO") return <FreeBadge />;
  if (access === "PAGO") return <PaidBadge />;
  return <ConfirmPriceBadge />;
}

const titleClass = "break-words font-body text-[16px] leading-[1.3] font-bold text-ink md:text-[17px]";

// Recurso de estudo (Figma 123:735, mobile 125:797).
// Uma só árvore de DOM nos dois tamanhos: no mobile é uma grade (tipo + idioma em cima, título,
// acesso embaixo); no desktop vira uma linha (tipo, título, idioma, acesso), na ordem do DOM.
export function ResourceItem({ resource }: { resource: Recurso }) {
  const external = isSafeExternalUrl(resource.url);

  return (
    <li className="grid grid-cols-[auto_1fr] items-center gap-2 rounded-12 border-2 border-ink bg-bg px-3 pt-2.5 pb-3 md:flex md:gap-3.5 md:px-4 md:py-3">
      <Badge className="col-start-1 row-start-1 justify-self-start bg-white md:min-w-32">
        {resourceTypeLabels[resource.tipo]}
      </Badge>
      <div className="col-span-2 row-start-2 flex min-w-0 flex-col gap-0.5 md:flex-1">
        {external ? (
          <a
            href={resource.url}
            target="_blank"
            rel="noopener noreferrer"
            className={`${titleClass} self-start rounded-8 underline decoration-from-font ${focusRing}`}
          >
            {resource.titulo}
            <span className="sr-only"> (abre em uma nova aba)</span>
          </a>
        ) : (
          // Endereço que não é https: mostra só o texto, sem link.
          <span className={titleClass}>{resource.titulo}</span>
        )}
        {resource.observacao && (
          <p className="break-words font-body text-body-sm text-ink-soft">{resource.observacao}</p>
        )}
      </div>
      <p className="col-start-2 row-start-1 justify-self-end font-mono text-caption uppercase text-ink-soft">
        <span className="sr-only">Idioma: </span>
        {languageLabels[resource.idioma]}
      </p>
      <div className="col-start-1 row-start-3 justify-self-start">
        <AccessBadge access={resource.acesso} />
      </div>
    </li>
  );
}
