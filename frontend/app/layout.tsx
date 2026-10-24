import type { Metadata } from "next";
import { Suspense } from "react";
import "./globals.css";
import { Footer } from "@/components/layout/footer";
import { Header } from "@/components/layout/header";
import { RouteFocus } from "@/components/layout/route-focus";
import { SkipLink } from "@/components/layout/skip-link";
import { Assistant } from "@/features/chat/assistant";
import { OG_IMAGE } from "@/lib/seo";
import { SITE_NAME, SITE_URL } from "@/lib/site";
import { fontVariables } from "./fonts";

// Base de todos os metadados. As páginas dão só o título (o template acrescenta o nome do site) e
// a descrição; canonical e Open Graph vêm de pageMetadata (lib/seo.ts).
export const metadata: Metadata = {
  metadataBase: new URL(SITE_URL),
  title: { default: "Trilha Tech: descubra qual área de TI seguir", template: `%s | ${SITE_NAME}` },
  description:
    "Descubra qual área de TI seguir. Compare 8 áreas e 36 trilhas, veja roadmaps com recursos de estudo e faixa salarial estimada. Sem cadastro.",
  applicationName: SITE_NAME,
  openGraph: { type: "website", siteName: SITE_NAME, locale: "pt_BR", images: [OG_IMAGE] },
  twitter: { card: "summary_large_image", images: [{ url: OG_IMAGE.url, alt: OG_IMAGE.alt }] },
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="pt-BR" className={`${fontVariables} h-full antialiased`}>
      <body className="min-h-full flex flex-col">
        <SkipLink />
        <Suspense fallback={null}>
          <RouteFocus />
        </Suspense>
        <Header />
        <main id="conteudo" tabIndex={-1} className="flex-1 focus:outline-none">
          {children}
        </main>
        <Footer />
        {/* Assistente de IA em todas as páginas. A conversa vive aqui, em memória, e não zera ao navegar. */}
        <Suspense fallback={null}>
          <Assistant />
        </Suspense>
      </body>
    </html>
  );
}
