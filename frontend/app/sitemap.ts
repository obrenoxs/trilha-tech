import type { MetadataRoute } from "next";
import { connection } from "next/server";

import { getArea, getAreas } from "@/lib/api";
import { SITE_URL } from "@/lib/site";

// Lista a Home, as áreas e as trilhas que a API devolve. Fora do sitemap, de propósito: /quiz e
// /quiz/resultado (o resultado é pessoal e tem noindex) e /styleguide (só existe em desenvolvimento).
//
// `connection()` faz o sitemap ser gerado a cada requisição (e não no build), então o build nunca
// depende da API para ele. As leituras abaixo usam o mesmo cache de 1 hora das páginas: uma
// requisição ao sitemap quase sempre não chama a API. Se a API estiver fora, devolve só a Home
// (sem guardar essa resposta incompleta) e o próximo acesso tenta de novo.
export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  await connection();
  const home: MetadataRoute.Sitemap[number] = { url: `${SITE_URL}/` };

  try {
    const areas = await getAreas();
    const details = await Promise.all(areas.map((area) => getArea(area.slug)));
    const trilhas = details.flatMap((area) => area?.trilhas ?? []);
    return [
      home,
      ...areas.map((area) => ({ url: `${SITE_URL}/areas/${area.slug}` })),
      ...trilhas.map((trilha) => ({ url: `${SITE_URL}/trilhas/${trilha.slug}` })),
    ];
  } catch {
    return [home];
  }
}
