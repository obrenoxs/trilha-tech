#!/usr/bin/env python3
"""Valida o conteúdo: trilhas, catálogo, quiz e PDFs. Sai com código 1 se achar erro (serve para o CI)."""
import json, sys
from collections import Counter
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent))
from manifesto import hash_trilha

ROOT = Path(__file__).parent.parent
NIVEIS = {"Básico", "Intermediário", "Avançado"}
ENTRADA = {"Iniciante", "Intermediário", "Avançado"}
TIPOS = {"documentação", "curso", "livro", "formação", "certificação", "tutorial", "guia", "vídeo", "ferramenta"}
ACESSOS = {"gratuito", "pago", "nao_confirmado"}
erros = []
def ler(p): return json.loads(Path(p).read_text(encoding="utf-8"))

# ---- catálogo
cat = ler(ROOT / "catalogo.json")
area_por_slug, trilha_area = {}, {}
for a in cat["areas"]:
    area_por_slug[a["slug"]] = a
    for k in ("numero", "slug", "nome", "ordemDesempate", "descricao", "comoETrabalhar"):
        if not a.get(k): erros.append(f"catalogo: área {a.get('slug')} sem {k}")
    if [t["ordem"] for t in a["trilhas"]] != list(range(1, len(a["trilhas"]) + 1)): erros.append(f"catalogo: ordem das trilhas em {a['slug']}")
    for t in a["trilhas"]: trilha_area[t["slug"]] = a
if len({a["ordemDesempate"] for a in cat["areas"]}) != len(cat["areas"]): erros.append("catalogo: ordemDesempate repetida")

# ---- trilhas
manifesto = ler(ROOT / "pdfs" / "manifest.json")["hashes"]
slugs = set()
for f in sorted((ROOT / "trilhas").glob("*.json")):
    d = ler(f); t = d["trilha"]; s = t["slug"]
    if s != f.stem: erros.append(f"{f.name}: slug '{s}' diferente do nome do arquivo")
    if s in slugs: erros.append(f"{s}: slug repetido")
    slugs.add(s)
    a = trilha_area.get(s)
    if not a: erros.append(f"{s}: não está no catalogo.json")
    else:
        if (t["area"]["slug"], t["area"]["numero"], t["area"]["nome"]) != (a["slug"], a["numero"], a["nome"]): erros.append(f"{s}: área diferente do catálogo")
        ct = next(x for x in a["trilhas"] if x["slug"] == s)
        if (ct["nome"], ct["nivelEntrada"], ct["duracaoTotalSemanas"]) != (t["nome"], t["nivelEntrada"], t["duracaoTotalSemanas"]): erros.append(f"{s}: catalogo.json fora de sincronia (nome, nível ou duração)")
    if not (30 <= len(t.get("resumo", "")) <= 110): erros.append(f"{s}: resumo deve ter entre 30 e 110 caracteres")
    if t["nivelEntrada"] not in ENTRADA: erros.append(f"{s}: nivelEntrada inválido")
    if not (ROOT / "pdfs" / f"roadmap-{s}.pdf").exists(): erros.append(f"{s}: falta o PDF")
    if manifesto.get(s) != hash_trilha(d): erros.append(f"{s}: o PDF está desatualizado em relação ao JSON (refaça o PDF e rode manifesto.py)")
    if [e["ordem"] for e in d["etapas"]] != list(range(1, len(d["etapas"]) + 1)): erros.append(f"{s}: ordem das etapas")
    if t["duracaoTotalSemanas"] != sum(e["duracaoSemanas"] for e in d["etapas"]): erros.append(f"{s}: duração total não bate")
    if [x["nivel"] for x in d["faixaSalarial"]] != ["Júnior", "Pleno", "Sênior"]: erros.append(f"{s}: faixaSalarial deve ser Júnior, Pleno, Sênior")
    for x in d["faixaSalarial"]:
        if not (x["minimo"] <= x["valorReferencia"] <= x["maximo"]): erros.append(f"{s}: faixa {x['nivel']} fora de ordem (min/ref/max)")
    for e in d["etapas"]:
        if e["nivel"] not in NIVEIS: erros.append(f"{s}: nível inválido na etapa {e['ordem']}")
        if not e["recursos"]: erros.append(f"{s}: etapa {e['ordem']} sem recursos")
        for r in e["recursos"]:
            if not r["url"].startswith("https://"): erros.append(f"{s}: url sem https ({r['titulo']})")
            if r["tipo"] not in TIPOS: erros.append(f"{s}: tipo inválido '{r['tipo']}'")
            if r["idioma"] not in ("PT", "EN"): erros.append(f"{s}: idioma inválido ({r['titulo']})")
            ac = r.get("acesso")
            if ac not in ACESSOS: erros.append(f"{s}: acesso inválido ({r['titulo']})")
            if (ac == "gratuito") != r["gratuito"]: erros.append(f"{s}: gratuito e acesso se contradizem ({r['titulo']})")
            if ac == "pago" and not str(r.get("observacao", "")).startswith("Pago"): erros.append(f"{s}: item pago sem observação 'Pago…' ({r['titulo']})")
            if ac != "pago" and str(r.get("observacao", "")).startswith("Pago"): erros.append(f"{s}: observação diz Pago, mas acesso é {ac} ({r['titulo']})")
if slugs != set(trilha_area): erros.append("catalogo.json e pasta trilhas/ têm trilhas diferentes")
for p in (ROOT / "pdfs").glob("roadmap-*.pdf"):
    if p.stem[len("roadmap-"):] not in slugs: erros.append(f"{p.name}: PDF sem trilha")

# ---- quiz
q = ler(ROOT / "quiz.json")
perg = q["perguntas"]
if len(perg) != 12: erros.append(f"quiz: esperadas 12 perguntas, achei {len(perg)}")
if [p["ordem"] for p in perg] != list(range(1, len(perg) + 1)): erros.append("quiz: ordem das perguntas")
prin, sec = Counter(), Counter()
for p in perg:
    if len(p["alternativas"]) != 4: erros.append(f"quiz: pergunta {p['ordem']} deve ter 4 alternativas")
    if len({a["areaPrincipal"] for a in p["alternativas"]}) != 4: erros.append(f"quiz: pergunta {p['ordem']} deve cobrir 4 áreas principais diferentes")
    for a in p["alternativas"]:
        for k in ("areaPrincipal", "areaSecundaria"):
            if a[k] not in area_por_slug: erros.append(f"quiz: área desconhecida '{a[k]}' na pergunta {p['ordem']}")
        if a["areaPrincipal"] == a["areaSecundaria"]: erros.append(f"quiz: principal = secundária na pergunta {p['ordem']}")
        prin[a["areaPrincipal"]] += 1; sec[a["areaSecundaria"]] += 1
for s in area_por_slug:
    if prin[s] != 6 or sec[s] != 6: erros.append(f"quiz: área {s} deve ser principal 6x e secundária 6x (achei {prin[s]} e {sec[s]})")

print("\n".join(erros) if erros else f"OK: {len(slugs)} trilhas, {len(cat['areas'])} áreas, {len(perg)} perguntas")
sys.exit(1 if erros else 0)
