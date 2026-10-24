type NoticeTicketProps = {
  /** Texto da faixa colorida, em maiúsculas (ex.: "ROTA INEXISTENTE"). */
  title: string;
  /** Cor da faixa: limão para "não encontrado", coral para falha. */
  color: "lemon" | "coral";
  /** Inclinação do bilhete: o 404 gira para a direita, o erro para a esquerda. */
  tilt: "right" | "left";
  fields: { label: string; value: string }[];
};

const stripColors = { lemon: "bg-lemon", coral: "bg-coral" };
const tilts = { right: "rotate-4", left: "-rotate-3" };

// Bilhete inclinado das telas de 404 e de erro (Figma 124:623 e 124:669, mobile 124:756 e 124:796).
// Decorativo: o texto repete o que a página já diz, então fica fora da leitura de tela.
export function NoticeTicket({ title, color, tilt, fields }: NoticeTicketProps) {
  return (
    <div aria-hidden="true" className="flex w-full justify-center px-0 py-7 md:px-6">
      <div
        className={`${tilts[tilt]} w-[300px] overflow-clip rounded-[18px] border-3 border-ink bg-white shadow-md md:w-[400px] md:rounded-[20px] md:shadow-lg`}
      >
        <div
          className={`${stripColors[color]} border-b-2 border-dashed border-ink px-[18px] py-3.5 md:px-6 md:py-[18px]`}
        >
          <p className="break-words font-display text-[17px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card">
            {title}
          </p>
        </div>
        <dl className="flex flex-col gap-3 px-[18px] pt-[18px] pb-5 md:gap-3.5 md:px-6 md:pt-[22px] md:pb-[26px]">
          {fields.map((field) => (
            <div key={field.label} className="flex flex-col gap-0.5">
              <dt className="font-mono text-caption uppercase text-ink-soft">{field.label}</dt>
              <dd className="break-words font-display text-[17px] leading-[1.25] font-semibold tracking-[-0.025em] md:text-card">
                {field.value}
              </dd>
            </div>
          ))}
        </dl>
      </div>
    </div>
  );
}
