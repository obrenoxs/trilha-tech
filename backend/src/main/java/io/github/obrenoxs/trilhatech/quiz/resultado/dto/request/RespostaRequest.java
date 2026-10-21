package io.github.obrenoxs.trilhatech.quiz.resultado.dto.request;

import jakarta.validation.constraints.NotNull;

public record RespostaRequest(

        @NotNull
        Integer pergunta,

        @NotNull
        Integer alternativa
) {}
