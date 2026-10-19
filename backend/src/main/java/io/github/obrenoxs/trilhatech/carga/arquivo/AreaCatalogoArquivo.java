package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record AreaCatalogoArquivo(
        String numero,
        String slug,
        String nome,
        Integer ordemDesempate,
        String descricao,
        String comoETrabalhar,
        List<TrilhaCatalogoArquivo> trilhas
) {}
