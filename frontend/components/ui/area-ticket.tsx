import Image from "next/image";
import Link from "next/link";
import { areaColors as colors, type AreaColor } from "./area-colors";
import { focusRing } from "./states";

type AreaTicketProps = {
  href: string;
  color: AreaColor;
  number: string;
  name: string;
  trailCount: number;
  className?: string;
};

// O bilhete inteiro é um link, com nome acessível único: "Área 01, <nome>, <N> trilhas".
// O aria-label substitui o conteúdo na leitura; canhoto e código de barras são decorativos.
export function AreaTicket({
  href,
  color,
  number,
  name,
  trailCount,
  className = "",
}: AreaTicketProps) {
  const trailsLabel = `${trailCount} ${trailCount === 1 ? "trilha" : "trilhas"}`;

  return (
    <Link
      href={href}
      aria-label={`Área ${number}, ${name}, ${trailsLabel}`}
      className={`relative mb-1.5 mr-1.5 flex overflow-clip rounded-16 border-3 border-ink bg-white shadow-md hover:translate-x-1 hover:translate-y-1 hover:shadow-pressed active:translate-x-1.5 active:translate-y-1.5 active:shadow-none ${focusRing} ${className}`}
    >
      <div
        aria-hidden="true"
        className={`flex w-[100px] shrink-0 flex-col items-center justify-center gap-1.5 border-r-3 border-dashed border-ink py-4 ${colors[color]}`}
      >
        <span className="font-mono text-caption uppercase">Área</span>
        <span className="font-display text-num">{number}</span>
      </div>
      <div className="flex min-h-[128px] min-w-0 flex-1 flex-col justify-between gap-4 px-6 py-5">
        <span className="font-display text-card">{name}</span>
        <span className="flex items-end justify-between gap-4">
          <span className="font-mono text-caption uppercase">{trailsLabel}</span>
          <Image src="/icons/barcode.svg" alt="" aria-hidden="true" width={72} height={22} />
        </span>
      </div>
      <Image
        src="/icons/notch.svg"
        alt=""
        width={26}
        height={26}
        className="absolute left-[84px] -top-[17px]"
      />
      <Image
        src="/icons/notch.svg"
        alt=""
        width={26}
        height={26}
        className="absolute -bottom-[17px] left-[84px]"
      />
    </Link>
  );
}
