package io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelEtapa;

import java.util.List;

public record EtapaResponse(
        int ordem,
        String titulo,
        NivelEtapa nivel,
        int duracaoSemanas,
        String descricao,
        String projetoPratico,
        List<RecursoResponse> recursos
) {}
