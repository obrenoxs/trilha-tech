import { describe, expect, it } from "vitest";
import { getAreas, getTrilha } from "./index";

// Chamadas reais à API de produção. Só rodam com `LIVE_API=1 npm run test`.
describe.skipIf(!process.env.LIVE_API)("API real", () => {
  it("GET /api/areas devolve as 8 áreas", async () => {
    const areas = await getAreas();
    expect(areas).toHaveLength(8);
    expect(areas[0]?.slug).toBe("desenvolvimento-de-software");
  });

  it("GET /api/trilhas/java-e-spring devolve a trilha completa", async () => {
    const trilha = await getTrilha("java-e-spring");
    expect(trilha.slug).toBe("java-e-spring");
    expect(trilha.etapas?.length).toBeGreaterThan(0);
    expect(trilha.faixasSalariais?.length).toBeGreaterThan(0);
  });

  it("slug inexistente vira ApiError 404", async () => {
    await expect(getTrilha("nao-existe")).rejects.toMatchObject({
      kind: "not-found",
      status: 404,
    });
  });
});
