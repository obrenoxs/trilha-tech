package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatRequest(

        @NotBlank
        @Size(max = 500)
        String mensagem,

        @Size(max = 6)
        List<@NotNull @Valid MensagemHistoricoRequest> historico,

        @Valid
        ContextoRequest contexto
) {
}
