// Ciclo de cores do Figma para áreas e etapas: coral, menta, limão, lavanda.
// Ticket e cartão de etapa usam a mesma ordem; a posição na lista escolhe a cor (índice % 4).
export const areaColors = {
  coral: "bg-coral",
  mint: "bg-mint",
  lemon: "bg-lemon",
  periwinkle: "bg-periwinkle",
};

export type AreaColor = keyof typeof areaColors;

export const areaColorOrder: AreaColor[] = ["coral", "mint", "lemon", "periwinkle"];

export function colorForIndex(index: number): AreaColor {
  return areaColorOrder[((index % 4) + 4) % 4];
}
