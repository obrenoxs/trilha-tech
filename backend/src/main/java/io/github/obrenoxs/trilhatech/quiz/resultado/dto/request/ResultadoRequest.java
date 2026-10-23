package io.github.obrenoxs.trilhatech.quiz.resultado.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ResultadoRequest(

        @Schema(description = "Exatamente 12 respostas, uma para cada pergunta")
        @NotNull
        @Size(min = 12, max = 12)
        List<@NotNull @Valid RespostaRequest> respostas
) {}
