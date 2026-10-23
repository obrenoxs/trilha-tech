package io.github.obrenoxs.trilhatech.quiz.resultado.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecomendacaoResponse(
        @Schema(description = "Posição no ranking, de 1 a 3", example = "1") int posicao,
        @Schema(description = "Slug da área, para usar em GET /api/areas/{slug}", example = "dados-e-ia") String slug,
        @Schema(description = "Nome da área, para exibir", example = "Dados e Inteligência Artificial") String nome,
        @Schema(description = "De 0 a 100, relativo à primeira área (a primeira é sempre 100). Não é um percentual de afinidade", example = "75") int barra
) {}
