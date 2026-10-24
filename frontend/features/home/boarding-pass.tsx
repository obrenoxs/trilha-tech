import Image from "next/image";

const fieldLabel =
  "font-mono text-[11px] leading-[1.4] tracking-[0.08em] uppercase text-ink-soft md:text-caption";
const fieldValue =
  "font-display text-[14px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card";

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex min-w-0 flex-1 flex-col gap-0.5 md:gap-[3px]">
      <p className={fieldLabel}>{label}</p>
      <p className={fieldValue}>{value}</p>
    </div>
  );
}

// Cartão de embarque decorativo do hero. Todo o texto repete o que já está na página,
// então fica fora da leitura de tela (aria-hidden). No mobile é menor e sem código de barras.
export function BoardingPass() {
  return (
    <div aria-hidden="true" className="px-2 py-3.5 md:px-6 md:py-7">
      <div className="flex w-full rotate-2 overflow-clip rounded-[18px] border-3 border-ink bg-white shadow-lg md:rounded-[22px]">
        <div className="flex w-11 shrink-0 items-center justify-center self-stretch border-r-3 border-dashed border-ink bg-lemon md:w-16">
          <p className="rotate-180 font-mono text-[13px] leading-[1.4] tracking-[0.08em] whitespace-nowrap [writing-mode:vertical-rl] md:text-label md:tracking-[0.1em]">
            E M B A R Q U E
          </p>
        </div>
        <div className="flex min-w-0 flex-1 flex-col gap-3.5 p-[18px] md:gap-[18px] md:px-7 md:py-[26px]">
          <div className="flex items-start justify-between font-mono text-caption uppercase whitespace-nowrap text-ink-soft">
            <p>Bilhete nº 001</p>
            <p>Trilha Tech</p>
          </div>
          <p className="font-display text-[18px] leading-[1.2] font-semibold tracking-[-0.025em] md:text-h3">
            Indecisão → 1ª vaga
          </p>
          <Image
            src="/icons/ticket-dash.svg"
            alt=""
            width={374}
            height={2}
            className="hidden h-0.5 w-full md:block"
          />
          <Image
            src="/icons/ticket-dash-mobile.svg"
            alt=""
            width={248}
            height={2}
            className="h-0.5 w-full md:hidden"
          />
          <div className="flex flex-col gap-3 md:gap-[18px]">
            <div className="flex gap-3 md:gap-6">
              <Field label="Passageiro" value="Você" />
              <Field label="Quiz" value="12 perguntas" />
            </div>
            <div className="flex gap-3 md:gap-6">
              <Field label="Destinos" value="8 áreas" />
              <Field label="Roadmap" value="PDF incluso" />
            </div>
            <Field label="Cadastro" value="Não precisa" />
          </div>
          <Image
            src="/icons/ticket-barcode-lg.svg"
            alt=""
            width={130}
            height={30}
            className="hidden md:block"
          />
        </div>
      </div>
    </div>
  );
}
