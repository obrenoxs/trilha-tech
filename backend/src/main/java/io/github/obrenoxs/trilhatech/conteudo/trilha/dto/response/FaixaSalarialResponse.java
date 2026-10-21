package io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelSalarial;

import java.time.YearMonth;

public record FaixaSalarialResponse(
        NivelSalarial nivel,
        int minimo,
        int maximo,
        int valorReferencia,
        String tipoReferencia,
        String fonte,
        YearMonth dataReferencia,
        boolean dadosLimitados,
        String observacao
) {}
