// URL pública do site, usada em canonical, Open Graph, sitemap e robots.
// Vem de NEXT_PUBLIC_SITE_URL (sem barra no fim). Sem ela, tenta o domínio de produção que a Vercel
// informa (VERCEL_PROJECT_PRODUCTION_URL) e, por último, http://localhost:3000, que nunca quebra o
// build mas NÃO serve para produção: os links gerados apontariam para a máquina local.
function resolveSiteUrl(): string {
  const configured = process.env.NEXT_PUBLIC_SITE_URL?.trim();
  if (configured) return configured.replace(/\/+$/, "");

  const vercel = process.env.VERCEL_PROJECT_PRODUCTION_URL?.trim();
  if (vercel) return `https://${vercel.replace(/^https?:\/\//, "").replace(/\/+$/, "")}`;

  if (process.env.NODE_ENV === "production") {
    console.warn(
      "[seo] NEXT_PUBLIC_SITE_URL não está definida: sitemap, canonical e Open Graph usarão http://localhost:3000. Defina a variável antes de publicar.",
    );
  }
  return "http://localhost:3000";
}

export const SITE_URL = resolveSiteUrl();
export const SITE_NAME = "Trilha Tech";
