import Image from "next/image";
import Link from "next/link";
import { routes } from "@/lib/routes";
import { SmallButton } from "@/components/ui/small-button";
import { focusRing } from "@/components/ui/states";

const navLink = `rounded-8 whitespace-nowrap font-body text-nav text-ink ${focusRing}`;

export function Header() {
  return (
    <header className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <div className="flex items-center justify-between py-5 md:py-7">
        <Link href={routes.home} className={`flex items-center gap-2.5 md:gap-3 ${focusRing} rounded-8`}>
          <Image src="/icons/ticket.svg" alt="" width={44} height={34} className="h-7 w-9 md:h-[34px] md:w-11" />
          <span className="whitespace-nowrap font-display text-[18px] leading-[1.2] font-bold tracking-[-0.02em] text-ink md:text-logo">
            Trilha Tech
          </span>
        </Link>
        {/* No mobile o Figma mostra só o botão; os dois links aparecem a partir de md. */}
        <nav aria-label="Principal" className="flex items-center gap-7">
          <Link href={routes.destinos} className={`hidden md:inline ${navLink}`}>
            Destinos
          </Link>
          <Link href={routes.comoFunciona} className={`hidden md:inline ${navLink}`}>
            Como funciona
          </Link>
          <SmallButton href={routes.quiz}>Fazer o quiz</SmallButton>
        </nav>
      </div>
    </header>
  );
}
