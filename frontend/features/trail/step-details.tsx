import type { TrilhaDetalhe } from "@/lib/api";
import { formatWeeks } from "@/lib/trail-format";
import { ResourceItem } from "./resource-item";

type Etapa = TrilhaDetalhe["etapas"][number];

const caption = "font-mono text-caption uppercase text-ink-soft";
const bodyText = "break-words font-body text-body-sm text-ink md:text-body-md";

// Conteúdo que aparece quando a etapa abre (Figma 123:718, mobile 125:781):
// projeto prático e duração, depois os recursos de estudo.
// O projeto prático é opcional no schema: sem ele, o bloco some e fica só a duração.
export function StepDetails({ step }: { step: Etapa }) {
  return (
    <>
      <div className="flex flex-col gap-3.5 md:flex-row md:gap-6">
        {step.projetoPratico && (
          <div className="flex flex-col gap-1 md:min-w-0 md:flex-1 md:gap-1.5">
            <h4 className={caption}>Projeto prático</h4>
            <p className={bodyText}>{step.projetoPratico}</p>
          </div>
        )}
        <div className="flex flex-col gap-1 md:min-w-[180px] md:shrink-0 md:gap-1.5">
          <h4 className={caption}>Duração estimada</h4>
          <p className={bodyText}>{formatWeeks(step.duracaoSemanas)}</p>
        </div>
      </div>

      {step.recursos.length > 0 && (
        <>
          <h4 className={`${caption} md:text-label`}>Recursos de estudo · {step.recursos.length}</h4>
          <ul className="flex flex-col gap-2.5 md:gap-3">
            {step.recursos.map((resource) => (
              <ResourceItem key={resource.ordem} resource={resource} />
            ))}
          </ul>
        </>
      )}
    </>
  );
}
