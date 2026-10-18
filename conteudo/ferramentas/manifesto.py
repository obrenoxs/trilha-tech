#!/usr/bin/env python3
"""Manifesto dos PDFs: guarda, para cada trilha, o hash do conteúdo que aparece no PDF.

Uso:
  python3 ferramentas/manifesto.py            # atualiza pdfs/manifest.json (depois de refazer PDFs)
  (a validação em validar.py compara o hash atual de cada JSON com o do manifesto)

O hash ignora campos que não aparecem no PDF (pendencias, versao, area.slug, resumo, acesso),
então mexer neles não obriga a refazer o PDF.
"""
import hashlib, json, sys
from pathlib import Path
ROOT = Path(__file__).parent.parent
IGNORAR_NA_RAIZ = {"pendencias", "versao"}

def hash_trilha(d):
    d = json.loads(json.dumps(d))
    for k in IGNORAR_NA_RAIZ: d.pop(k, None)
    d["trilha"]["area"].pop("slug", None)
    d["trilha"].pop("resumo", None)
    for e in d["etapas"]:
        for r in e["recursos"]: r.pop("acesso", None)
    canon = json.dumps(d, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
    return hashlib.sha256(canon.encode("utf-8")).hexdigest()

def calcular():
    out = {}
    for f in sorted((ROOT / "trilhas").glob("*.json")):
        d = json.loads(f.read_text(encoding="utf-8"))
        out[d["trilha"]["slug"]] = hash_trilha(d)
    return out

if __name__ == "__main__":
    m = {"versao": "1.0", "hashes": calcular()}
    (ROOT / "pdfs" / "manifest.json").write_text(json.dumps(m, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"manifest.json atualizado: {len(m['hashes'])} trilhas")
