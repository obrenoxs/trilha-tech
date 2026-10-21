package io.github.obrenoxs.trilhatech.quiz.pergunta.dto.response;

import java.util.List;

public record PerguntaResponse(
        int ordem,
        String texto,
        List<AlternativaResponse> alternativas
) {}
