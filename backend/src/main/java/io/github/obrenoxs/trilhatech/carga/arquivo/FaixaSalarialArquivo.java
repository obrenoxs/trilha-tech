package io.github.obrenoxs.trilhatech.carga.arquivo;

public record FaixaSalarialArquivo(
        String nivel,
        Integer minimo,
        Integer maximo,
        Integer valorReferencia,
        String tipoReferencia,
        String fonte,
        String dataReferencia,
        Boolean dadosLimitados,
        String observacao
) {}
