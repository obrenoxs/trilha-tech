"use client";

import { useState, type MouseEvent } from "react";
import { Button } from "@/components/ui/button";

type PdfDownloadProps = {
  href: string;
  /** Nome do arquivo salvo, por exemplo "roadmap-backend-java.pdf". */
  fileName: string;
};

// Baixa o PDF buscando o arquivo em vez de navegar até ele: se a API responder com erro,
// a pessoa veria o JSON técnico do servidor numa página em branco. Sem JavaScript o <a>
// continua funcionando como link comum (o servidor envia `attachment`).
export function PdfDownload({ href, fileName }: PdfDownloadProps) {
  const [status, setStatus] = useState<"idle" | "loading" | "error">("idle");

  async function download(event: MouseEvent<HTMLAnchorElement>) {
    event.preventDefault();
    if (status === "loading") return;
    setStatus("loading");
    try {
      const response = await fetch(href);
      if (!response.ok) throw new Error("download failed");
      const url = URL.createObjectURL(await response.blob());
      const link = document.createElement("a");
      link.href = url;
      link.download = fileName;
      link.click();
      URL.revokeObjectURL(url);
      setStatus("idle");
    } catch {
      setStatus("error");
    }
  }

  return (
    <div className="flex flex-col items-start gap-2">
      <Button href={href} onClick={download} aria-disabled={status === "loading"}>
        {status === "loading" ? "Baixando…" : "Baixar roadmap em PDF"}
      </Button>
      {status === "error" && (
        <p role="alert" className="font-body text-body-sm text-ink">
          Não foi possível baixar o PDF agora. Tente de novo em instantes.
        </p>
      )}
    </div>
  );
}
