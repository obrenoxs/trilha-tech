// Gancho do assistente de IA. A fase 10 liga o painel a este evento (um listener no layout raiz).
// Até lá, chamar `requestOpenAssistant()` não faz nada visível.
export const OPEN_ASSISTANT_EVENT = "trilha-tech:open-assistant";

export function requestOpenAssistant() {
  window.dispatchEvent(new Event(OPEN_ASSISTANT_EVENT));
}
