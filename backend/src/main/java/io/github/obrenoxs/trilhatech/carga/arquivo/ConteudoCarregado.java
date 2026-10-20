package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record ConteudoCarregado(
        CatalogoArquivo catalogo,
        List<TrilhaArquivo> trilhas
) {}
