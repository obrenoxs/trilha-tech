package io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelSalarial;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.YearMonth;

public record FaixaSalarialResponse(
        NivelSalarial nivel,
        int minimo,
        int maximo,
        int valorReferencia,
        String tipoReferencia,
        String fonte,

        @Schema(type = "string", example = "2026-09", description = "Mês de referência, no formato ano-mês")
        YearMonth dataReferencia,

        boolean dadosLimitados,
        String observacao
) {}
