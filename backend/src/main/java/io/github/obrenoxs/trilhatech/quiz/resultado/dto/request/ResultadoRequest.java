package io.github.obrenoxs.trilhatech.quiz.resultado.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ResultadoRequest(
        
        @NotNull
        @Size(min = 12, max = 12)
        List<@NotNull @Valid RespostaRequest> respostas
) {}
