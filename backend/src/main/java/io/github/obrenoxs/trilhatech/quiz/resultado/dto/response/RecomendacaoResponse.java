package io.github.obrenoxs.trilhatech.quiz.resultado.dto.response;

public record RecomendacaoResponse(
        int posicao,
        String slug,
        String nome,
        int barra
) {}
