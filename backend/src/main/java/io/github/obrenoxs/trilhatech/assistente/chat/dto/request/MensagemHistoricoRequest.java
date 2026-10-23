package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import io.github.obrenoxs.trilhatech.assistente.cliente.PapelMensagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MensagemHistoricoRequest(

        @NotNull
        PapelMensagem papel,

        @NotBlank
        @Size(max = 4000)
        String texto
) {}
