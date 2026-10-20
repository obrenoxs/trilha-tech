package io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelEntrada;

public record TrilhaResponse(
        String slug,
        String nome,
        String resumo,
        NivelEntrada nivelEntrada,
        long duracaoTotalSemanas
) {}
