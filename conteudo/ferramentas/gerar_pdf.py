#!/usr/bin/env python3
"""Gera o HTML e o PDF do roadmap a partir do JSON. Cores/fontes ficam em variáveis CSS (tema)."""
import base64, html, json, sys
from pathlib import Path
from urllib.parse import urlparse

TOOLS = Path(__file__).parent
ROOT = TOOLS.parent
SLUG = sys.argv[1] if len(sys.argv) > 1 else "java-e-spring"
data = json.loads((ROOT / "trilhas" / f"{SLUG}.json").read_text())
t, etapas, sal = data["trilha"], data["etapas"], data["faixaSalarial"]

def font(pkg, name, w):
    f = TOOLS / f"node_modules/@fontsource/{pkg}/files/{pkg}-latin-{w}-normal.woff2"
    return base64.b64encode(f.read_bytes()).decode()

faces = ""
for pkg, fam, ws in (("unbounded","Unbounded",(600,700)),("manrope","Manrope",(500,700,800)),("dm-mono","DM Mono",(500,))):
    for w in ws:
        faces += f"@font-face{{font-family:'{fam}';font-weight:{w};src:url(data:font/woff2;base64,{font(pkg,'x',w)}) format('woff2')}}\n"

esc = html.escape
def brl(v): return "R$ " + f"{v:,.0f}".replace(",", ".")
def dbr(iso):
    p = iso.split("-")
    return "/".join(reversed(p)) if len(p) == 3 else f"{p[1]}/{p[0]}"

NIVEL_COR = {"Básico": "var(--mint)", "Intermediário": "var(--peri)", "Avançado": "var(--coral)"}
CARD_COR = ["var(--coral)", "var(--mint)", "var(--lemon)", "var(--peri)"]

def recurso(r):
    dom = urlparse(r["url"]).netloc.replace("www.", "")
    obs = f'<div class="obs">{esc(r["observacao"])}</div>' if r.get("observacao") else ""
    grat = {"gratuito": '<span class="tag free">Grátis</span>', "pago": '<span class="tag paid">Pago</span>'}.get(r.get("acesso"), '<span class="tag paid">Confirmar preço</span>')
    return f'''<li class="rec"><a class="rtit" href="{esc(r["url"])}">{esc(r["titulo"])}</a>
      <div class="tags"><span class="tag">{esc(r["tipo"])}</span><span class="tag">{esc(r["idioma"])}</span>{grat}<span class="dom">{esc(dom)} · conferido em {dbr(r["verificadoEm"])}</span></div>{obs}</li>'''

PUB = {1:"publique no GitHub",2:"publique no GitHub ou no Kaggle",3:"publique no GitHub",4:"publique no GitHub",5:"publique no GitHub",6:"documente no GitHub ou em um portfólio",7:"monte um portfólio (Behance, Figma ou site pessoal)",8:"documente no seu portfólio (Notion, Google Docs ou PDF)"}
def publica(t):
    return PUB.get(int(t["area"]["numero"]),"publique no seu portfólio")

def etapa(e, i):
    c = CARD_COR[i % 4]
    recs = "".join(recurso(r) for r in e["recursos"]) or "<p style=\"font-size:8.5pt;margin:1mm 0\">Ainda não há um recurso verificado para esta etapa. Estamos buscando um.</p>"
    proj = f'<div class="proj"><b>Projeto prático</b>{esc(e["projetoPratico"])}</div>' if e.get("projetoPratico") else ""
    return f'''<div class="ew"><section class="etapa" style="--c:{c}">
  <div class="stub"><small>ETAPA</small><span class="num">{e["ordem"]:02d}</span></div>
  <div class="body">
    <h3>{esc(e["titulo"])}</h3>
    <div class="meta"><span class="pill" style="background:{NIVEL_COR[e["nivel"]]}">{esc(e["nivel"])}</span><span class="sem">{e["duracaoSemanas"]} semanas</span></div>
    <p class="desc">{esc(e["descricao"])}</p>
    {proj}
    <h4>Recursos de estudo</h4>
    <ul class="recs">{recs}</ul>
  </div>
</section></div>'''

resumo = "".join(
    f'<li><span class="rn" style="background:{CARD_COR[i%4]}">{e["ordem"]:02d}</span><span class="rt">{esc(e["titulo"])}</span><span class="rs">{e["duracaoSemanas"]} sem</span></li>'
    for i, e in enumerate(etapas))

def salario(s, i):
    c = ["var(--coral)", "var(--mint)", "var(--lemon)"][i]
    if s["minimo"] is not None:
        big = f'{brl(s["minimo"])} <span class="a">a</span> {brl(s["maximo"])}'
        sub = f'{s["tipoReferencia"].capitalize()} de referência: {brl(s["valorReferencia"])}'
    elif s["valorReferencia"] is None:
        big = "Sem dados confiáveis"
        sub = "Ainda não encontramos uma fonte com número suficiente"
    else:
        big = f'cerca de {brl(s["valorReferencia"])}'
        sub = f'{s["tipoReferencia"].capitalize()} (sem faixa mínimo–máximo confiável)'
    ref = dbr(s["dataReferencia"]) if s.get("dataReferencia") else "sem data informada"
    lim = '<div class="limit">Dados limitados</div>' if s["dadosLimitados"] else ""
    return f'''<div class="sal" style="--c:{c}"><div class="sn">{esc(s["nivel"])}</div>{lim}
      <div class="big">{big}</div><div class="sub">{esc(sub)}</div>
      <div class="fonte">Fonte: {esc(s["fonte"])} · {esc(ref)}</div><div class="so">{esc(s["observacao"])}</div></div>'''

nome = t["nome"]
HTML = f'''<!doctype html><html lang="pt-BR"><head><meta charset="utf-8"><title>Roadmap {esc(nome)} · Trilha Tech</title>
<style>
{faces}
:root{{
  /* TEMA B5 Bilhete */
  --bg:#E6EEF2; --ink:#14213D; --soft:#4B587A; --coral:#FF8A6B; --mint:#7EE0B8; --lemon:#F6E27A; --peri:#A9B8FF; --white:#FFFFFF;
  --font-display:'Unbounded',sans-serif; --font-body:'Manrope',sans-serif; --font-mono:'DM Mono',monospace;
  --bw:2.4px; --r:14px; --sh:4px;
}}
@page{{size:A4;margin:16mm 10mm 18mm;background:var(--bg)}}
*{{box-sizing:border-box}}
html{{-webkit-print-color-adjust:exact;print-color-adjust:exact}}
body{{margin:0;padding:0 4mm 0 4mm;background:var(--bg);color:var(--ink);font-family:var(--font-body);font-weight:500;font-size:10pt;line-height:1.5}}
a{{color:inherit;text-decoration:none}}
h1,h2,h3,.num,.big,.sn,.logo b{{font-family:var(--font-display);font-weight:700;letter-spacing:-.03em;margin:0}}
.mono{{font-family:var(--font-mono);font-weight:500;text-transform:uppercase;letter-spacing:.08em;font-size:7.5pt;color:var(--soft)}}
.logo{{display:flex;align-items:center;gap:10px}}
.logo i{{width:34px;height:26px;background:var(--coral);border:var(--bw) solid var(--ink);border-radius:6px;position:relative;display:block}}
.logo i:after{{content:"";position:absolute;left:9px;top:2px;bottom:2px;border-left:2px dashed var(--ink)}}
.logo b{{font-size:14pt}}
.cover h1{{font-size:28pt;line-height:1.1;margin-top:4mm}}
.cover h1 em{{font-style:normal;display:block}}
.lead{{margin-top:4mm;font-size:11pt;line-height:1.55;max-width:150mm;color:var(--soft)}}
.pass{{margin-top:6mm;display:flex;background:var(--white);border:var(--bw) solid var(--ink);border-radius:18px;box-shadow:5px 5px 0 var(--ink);overflow:hidden;transform:rotate(-1.2deg)}}
.pass .st{{flex:none;width:15mm;background:var(--lemon);border-right:var(--bw) dashed var(--ink);display:grid;place-items:center}}
.pass .st span{{writing-mode:vertical-rl;transform:rotate(180deg);font-family:var(--font-mono);font-weight:500;letter-spacing:.3em;font-size:8pt}}
.pass .mn{{flex:1;padding:5mm 6mm}}
.pass .top{{display:flex;justify-content:space-between}}
.pass .route{{font-family:var(--font-display);font-weight:600;font-size:13pt;margin:2mm 0 3mm;padding-bottom:3mm;border-bottom:2px dashed var(--ink)}}
.fields{{display:grid;grid-template-columns:1fr 1fr;gap:3mm 6mm}}
.fields b{{text-transform:none;letter-spacing:-.01em;color:var(--ink);display:block;font-family:var(--font-display);font-weight:600;font-size:9.5pt;margin-top:1px;line-height:1.25}}
.bars{{margin-top:3mm;height:8mm}}
.how{{margin-top:5mm;padding:12px 15px;background:var(--white);border:var(--bw) solid var(--ink);border-radius:var(--r);box-shadow:var(--sh) var(--sh) 0 var(--ink)}}
.how b{{font-family:var(--font-display);font-weight:600;font-size:10pt}}
.how ul{{margin:6px 0 0;padding-left:16px}}
h2{{font-size:15pt;margin-bottom:5mm}}
.resumo{{list-style:none;padding:0;margin:0;display:grid;grid-template-columns:1fr 1fr;gap:6px}}
.resumo li{{break-inside:avoid;display:flex;align-items:center;gap:10px;padding:5px 10px 5px 5px;background:var(--white);border:var(--bw) solid var(--ink);border-radius:10px}}
.rn{{font-family:var(--font-display);font-weight:700;width:28px;height:28px;border:var(--bw) solid var(--ink);border-radius:7px;display:grid;place-items:center;font-size:9pt;flex:none}}
.rt{{flex:1;font-weight:700;font-size:9pt;line-height:1.2}} .rs{{font-family:var(--font-mono);font-size:7.5pt}}
.ew{{break-inside:avoid;padding:12px 0 14px}}
.etapa{{display:flex;background:var(--white);border:var(--bw) solid var(--ink);border-radius:16px;box-shadow:var(--sh) var(--sh) 0 var(--ink);position:relative}}
.etapa .stub{{flex:none;width:22mm;background:var(--c);border-right:var(--bw) dashed var(--ink);border-radius:13px 0 0 13px;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:2px;position:relative}}
.etapa .stub small{{font-family:var(--font-mono);font-weight:500;font-size:7pt;letter-spacing:.12em}}
.etapa .stub:before,.etapa .stub:after{{content:"";position:absolute;right:-9px;width:16px;height:16px;border-radius:50%;background:var(--bg);border:var(--bw) solid var(--ink)}}
.etapa .stub:before{{top:-10px}} .etapa .stub:after{{bottom:-10px}}
.num{{font-size:18pt}}
.body{{flex:1;padding:12px 15px 13px 16px;min-width:0}}
.body h3{{font-size:11.5pt;line-height:1.2}}
.meta{{display:flex;gap:8px;align-items:center;margin-top:5px}}
.pill{{padding:1px 9px;border:2px solid var(--ink);border-radius:999px;font-weight:800;font-size:7.5pt}}
.sem{{font-family:var(--font-mono);font-size:8pt}}
.desc{{margin:9px 0 0}}
.proj{{margin-top:8px;padding:7px 10px;background:var(--bg);border:2px dashed var(--ink);border-radius:10px}}
.proj b{{display:block;font-family:var(--font-mono);font-weight:500;font-size:7.5pt;text-transform:uppercase;letter-spacing:.1em;margin-bottom:1px;color:var(--soft)}}
h4{{margin:10px 0 5px;font-family:var(--font-mono);font-weight:500;font-size:7.5pt;text-transform:uppercase;letter-spacing:.1em;color:var(--soft)}}
.recs{{list-style:none;margin:0;padding:0;display:grid;gap:6px}}
.rec{{padding:6px 10px;border:2px solid var(--ink);border-radius:9px;break-inside:avoid}}
.rtit{{font-weight:800;border-bottom:2px solid var(--c)}}
.tags{{display:flex;flex-wrap:wrap;gap:5px;align-items:center;margin-top:4px}}
.tag{{padding:0 8px;border:1.6px solid var(--ink);border-radius:999px;font-size:7.5pt;font-weight:700;background:var(--bg)}}
.tag.free{{background:var(--mint)}}
.tag.paid{{background:var(--lemon)}}
.dom{{font-family:var(--font-mono);font-size:7pt;color:var(--soft)}}
.obs{{font-size:8.5pt;margin-top:3px;color:var(--soft)}}
.salpage{{break-before:page}}
.sals{{display:grid;gap:5mm}}
.sal{{break-inside:avoid;padding:13px 16px;background:var(--c);border:var(--bw) solid var(--ink);border-radius:var(--r);box-shadow:var(--sh) var(--sh) 0 var(--ink)}}
.sn{{font-size:11pt}} .big{{font-size:16pt;line-height:1.15;margin-top:4px}} .a{{font-size:10pt;font-family:var(--font-body);font-weight:700}}
.sub{{font-weight:700;margin-top:2px}} .fonte{{margin-top:6px;font-size:8.5pt;font-weight:700}} .so{{font-size:8.5pt;margin-top:2px}}
.limit{{display:inline-block;margin-top:3px;padding:0 8px;border:2px solid var(--ink);border-radius:999px;background:var(--white);font-size:7.5pt;font-weight:800}}
.aviso{{margin-top:6mm;padding:11px 14px;border:var(--bw) solid var(--ink);border-radius:12px;background:var(--white);font-size:9pt}}
</style></head><body>

<section class="cover">
  <div class="logo"><i></i><b>Trilha Tech</b></div>
  <div class="mono" style="margin-top:6mm">Roadmap de estudos · Área {esc(t["area"]["numero"])}</div>
  <h1>Roadmap:<em>{esc(nome)}</em></h1>
  <p class="lead">{esc(t["textoTrilha"])}</p>
  <div class="pass"><div class="st"><span>EMBARQUE</span></div><div class="mn">
    <div class="top mono"><span>Bilhete nº {esc(t["area"]["numero"])}01</span><span>Trilha Tech</span></div>
    <div class="route">Do zero → 1ª vaga</div>
    <div class="fields">
      <div class="mono" style="grid-column:1/-1">Área<b>{esc(t["area"]["nome"])}</b></div>
      <div class="mono">Nível de entrada<b>{esc(t["nivelEntrada"])}</b></div>
      <div class="mono">Etapas<b>{len(etapas)} etapas</b></div>
      <div class="mono">Duração<b>≈ {t["duracaoTotalSemanas"]} semanas · {t["horasPorSemana"]} h/semana</b></div>
    </div>
    <svg class="bars" viewBox="0 0 130 30" preserveAspectRatio="none" width="130" height="30"><rect x="0" y="0" width="3" height="30" fill="#14213D"/><rect x="6" y="0" width="5" height="30" fill="#14213D"/><rect x="15" y="0" width="2" height="30" fill="#14213D"/><rect x="21" y="0" width="6" height="30" fill="#14213D"/><rect x="31" y="0" width="3" height="30" fill="#14213D"/><rect x="38" y="0" width="2" height="30" fill="#14213D"/><rect x="44" y="0" width="5" height="30" fill="#14213D"/><rect x="53" y="0" width="3" height="30" fill="#14213D"/><rect x="60" y="0" width="6" height="30" fill="#14213D"/><rect x="69" y="0" width="3" height="30" fill="#14213D"/><rect x="78" y="0" width="2" height="30" fill="#14213D"/><rect x="84" y="0" width="5" height="30" fill="#14213D"/><rect x="93" y="0" width="3" height="30" fill="#14213D"/><rect x="100" y="0" width="6" height="30" fill="#14213D"/><rect x="110" y="0" width="3" height="30" fill="#14213D"/><rect x="116" y="0" width="2" height="30" fill="#14213D"/><rect x="122" y="0" width="5" height="30" fill="#14213D"/></svg>
  </div></div>
  <div class="how"><b>Como usar este roadmap</b>
    <ul><li>Siga as etapas na ordem. Cada uma prepara a próxima.</li>
    <li>As semanas são uma estimativa para {t["horasPorSemana"]} h de estudo por semana. Seu ritmo pode ser outro.</li>
    <li>Faça o projeto prático de cada etapa e {publica(t)}.</li>
    <li>Os links foram conferidos em {dbr(data["geradoEm"])}. Páginas mudam: se algum não abrir, busque pelo título.</li></ul></div>
  <h2 style="margin-top:5mm;margin-bottom:3mm">Visão geral</h2>
  <ul class="resumo">{resumo}</ul>
</section>

<div style="break-before:page"></div>
<h2>Etapas</h2>
{"".join(etapa(e, i) for i, e in enumerate(etapas))}

<section class="salpage">
  <h2>Faixa salarial estimada</h2>
  <div class="sals">{"".join(salario(s, i) for i, s in enumerate(sal))}</div>
  <div class="aviso"><b>Leia com cuidado.</b> {esc(data["avisosSalario"])}</div>
</section>
</body></html>'''

(TOOLS / ".build").mkdir(exist_ok=True)
out_html = TOOLS / ".build" / f"roadmap-{SLUG}.html"
out_html.write_text(HTML, encoding="utf-8")

from playwright.sync_api import sync_playwright
with sync_playwright() as p:
    b = p.chromium.launch()
    pg = b.new_page()
    pg.goto(out_html.as_uri())
    pg.pdf(path=str(ROOT / "pdfs" / f"roadmap-{SLUG}.pdf"), prefer_css_page_size=True, print_background=True,
           display_header_footer=True, header_template="<span></span>",
           footer_template="<div style=\"width:100%;font-size:8px;font-family:monospace;color:#14213D;padding:0 14mm;display:flex;justify-content:space-between\"><span>Trilha Tech · Roadmap " + nome + "</span><span>Página <span class=\"pageNumber\"></span> de <span class=\"totalPages\"></span></span></div>")
    b.close()
print("ok")
