package io.github.obrenoxs.trilhatech.quiz.resultado.dto.response;

import java.util.List;

public record ResultadoResponse(
        List<RecomendacaoResponse> recomendacoes
) {}
