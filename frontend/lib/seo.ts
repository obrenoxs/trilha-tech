import type { Metadata } from "next";

import { SITE_NAME } from "./site";

/**
 * Imagem de compartilhamento: o arquivo estático app/opengraph-image.png (1200x630, exportado do Figma,
 * frame 142:1110). O Next só o coloca nas tags sozinho quando a página NÃO define `openGraph`; como as
 * páginas definem (título e descrição), cada uma precisa repassar a imagem, senão ela se perde.
 * O texto alternativo é o mesmo de app/opengraph-image.alt.txt (há um teste que confere).
 */
export const OG_IMAGE = {
  url: "/opengraph-image.png",
  width: 1200,
  height: 630,
  alt: "Trilha Tech: sua passagem para a primeira vaga em TI.",
};

/** Tamanho máximo da descrição (meta description e Open Graph). Buscadores cortam por volta disso. */
export const MAX_DESCRIPTION_LENGTH = 155;

/**
 * Descrição curta a partir de um texto da API: junta espaços e quebras de linha e, se passar de `max`,
 * corta no fim da última frase que caiba (desde que não fique curta demais) ou na última palavra, com
 * reticências. Só reaproveita o texto recebido: nada é acrescentado nem inventado.
 */
export function shortDescription(text: string, max = MAX_DESCRIPTION_LENGTH): string {
  const clean = text.replace(/\s+/g, " ").trim();
  if (clean.length <= max) return clean;

  const window = clean.slice(0, max);
  const sentenceEnd = Math.max(window.lastIndexOf(". "), window.lastIndexOf("! "), window.lastIndexOf("? "));
  if (sentenceEnd >= 60) return window.slice(0, sentenceEnd + 1);

  const wordEnd = window.lastIndexOf(" ", max - 1);
  const cut = wordEnd > 0 ? window.slice(0, wordEnd) : window.slice(0, max - 1);
  return `${cut.replace(/[\s,;:.\-–—]+$/, "")}…`;
}

type PageMetadataInput = {
  /** Título da página, sem o nome do site (o layout acrescenta " | Trilha Tech"). */
  title: string;
  description: string;
  /** Caminho da página, por exemplo "/trilhas/java-e-spring". Vira canonical e og:url. */
  path: string;
};

/** Título, descrição, canonical, Open Graph e Twitter de uma página, sempre com o mesmo formato. */
export function pageMetadata({ title, description, path }: PageMetadataInput): Metadata {
  const fullTitle = `${title} | ${SITE_NAME}`;
  const text = shortDescription(description);
  return {
    title,
    description: text,
    alternates: { canonical: path },
    openGraph: {
      type: "website",
      siteName: SITE_NAME,
      locale: "pt_BR",
      url: path,
      title: fullTitle,
      description: text,
      images: [OG_IMAGE],
    },
    twitter: {
      card: "summary_large_image",
      title: fullTitle,
      description: text,
      images: [{ url: OG_IMAGE.url, alt: OG_IMAGE.alt }],
    },
  };
}
