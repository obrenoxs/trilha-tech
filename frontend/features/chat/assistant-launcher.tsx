"use client";

import { AssistantButton } from "@/components/ui/assistant-button";

import { useAssistant } from "./assistant-provider";

/** Botão flutuante "Perguntar à IA". Some enquanto o painel está aberto (o painel cobre o canto). */
export function AssistantLauncher() {
  const { open, openPanel, launcherRef } = useAssistant();

  return (
    <div ref={launcherRef} className="fixed right-3.5 bottom-4 z-40 md:right-8 md:bottom-8">
      {!open && (
        <AssistantButton aria-haspopup="dialog" onClick={openPanel}>
          Perguntar à IA
        </AssistantButton>
      )}
    </div>
  );
}
