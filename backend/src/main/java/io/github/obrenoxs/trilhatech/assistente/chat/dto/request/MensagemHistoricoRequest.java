package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import io.github.obrenoxs.trilhatech.assistente.cliente.PapelMensagem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MensagemHistoricoRequest(

        @Schema(description = "Quem falou", example = "USUARIO")
        @NotNull
        PapelMensagem papel,

        @Schema(description = "O texto da fala, de até 4000 caracteres")
        @NotBlank
        @Size(max = 4000)
        String texto
) {}
