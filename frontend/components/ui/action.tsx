"use client";

import Link from "next/link";
import type { ComponentProps, ComponentPropsWithoutRef } from "react";

type ButtonOnlyProps = ComponentPropsWithoutRef<"button"> & { href?: undefined };
type LinkOnlyProps = Omit<ComponentProps<typeof Link>, "href"> & { href: string };

export type ActionProps = ButtonOnlyProps | LinkOnlyProps;

/**
 * Base dos botões: com `href` vira link (<a>), sem `href` vira <button>.
 * Assim a semântica certa vem de graça: navegar é link, agir é botão.
 * Link desabilitado usa aria-disabled (continua focável) e não navega ao clicar.
 */
export function Action(props: ActionProps) {
  if (props.href !== undefined) {
    const disabled = props["aria-disabled"] === true || props["aria-disabled"] === "true";
    return (
      <Link
        {...props}
        onClick={(event) => {
          if (disabled) {
            event.preventDefault();
            return;
          }
          props.onClick?.(event);
        }}
      />
    );
  }
  return <button type="button" {...props} />;
}
