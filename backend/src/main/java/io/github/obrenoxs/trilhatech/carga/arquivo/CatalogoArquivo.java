package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record CatalogoArquivo(
        String versao,
        List<AreaCatalogoArquivo> areas
) {}
