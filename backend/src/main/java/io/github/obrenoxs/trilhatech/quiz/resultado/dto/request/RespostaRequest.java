package io.github.obrenoxs.trilhatech.quiz.resultado.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RespostaRequest(

        @Schema(description = "Ordem da pergunta (1 a 12), a mesma que veio em GET /api/quiz. Não é um id", example = "1")
        @NotNull
        Integer pergunta,

        @Schema(description = "Ordem da alternativa escolhida (1 a 4), a mesma que veio em GET /api/quiz. "
                + "Não é a posição na tela, porque o front-end embaralha as alternativas", example = "3")
        @NotNull
        Integer alternativa
) {}
