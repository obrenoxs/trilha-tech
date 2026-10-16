#!/usr/bin/env python3
"""Valida o conteúdo: estrutura dos JSONs, slugs únicos, PDF para cada trilha e catalogo.json em dia."""
import json, sys
from pathlib import Path
ROOT = Path(__file__).parent.parent
NIVEIS = {"Básico", "Intermediário", "Avançado"}
erros, slugs = [], set()
for f in sorted((ROOT / "trilhas").glob("*.json")):
    d = json.loads(f.read_text(encoding="utf-8"))
    t = d["trilha"]; s = t["slug"]
    if s != f.stem: erros.append(f"{f.name}: slug '{s}' diferente do nome do arquivo")
    if s in slugs: erros.append(f"{s}: slug repetido")
    slugs.add(s)
    if not (ROOT / "pdfs" / f"roadmap-{s}.pdf").exists(): erros.append(f"{s}: falta o PDF")
    if [e["ordem"] for e in d["etapas"]] != list(range(1, len(d["etapas"]) + 1)): erros.append(f"{s}: ordem das etapas")
    if t["duracaoTotalSemanas"] != sum(e["duracaoSemanas"] for e in d["etapas"]): erros.append(f"{s}: duração total não bate")
    if len(d["faixaSalarial"]) != 3: erros.append(f"{s}: faixaSalarial deve ter 3 níveis")
    for e in d["etapas"]:
        if e["nivel"] not in NIVEIS: erros.append(f"{s}: nível inválido na etapa {e['ordem']}")
        if not e["recursos"]: erros.append(f"{s}: etapa {e['ordem']} sem recursos")
        for r in e["recursos"]:
            if not r["url"].startswith("https://"): erros.append(f"{s}: url sem https ({r['titulo']})")
cat = json.loads((ROOT / "catalogo.json").read_text(encoding="utf-8"))
cat_slugs = {x["slug"] for a in cat["areas"] for x in a["trilhas"]}
if cat_slugs != slugs: erros.append("catalogo.json fora de sincronia com a pasta trilhas/")
print("\n".join(erros) if erros else f"OK: {len(slugs)} trilhas, {len(cat['areas'])} áreas")
sys.exit(1 if erros else 0)
