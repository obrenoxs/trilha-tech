import type { MetadataRoute } from "next";

import { SITE_URL } from "@/lib/site";

// Libera tudo e aponta para o sitemap. O resultado do quiz fica de fora do sitemap e tem noindex na
// própria página; não é bloqueado aqui de propósito: se o robots.txt impedisse o acesso, o buscador
// não leria o noindex e a URL ainda poderia aparecer nos resultados.
export default function robots(): MetadataRoute.Robots {
  return {
    rules: { userAgent: "*", allow: "/" },
    sitemap: `${SITE_URL}/sitemap.xml`,
  };
}
