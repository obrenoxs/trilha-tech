package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatRequest(

        @Schema(description = "A pergunta da pessoa, de até 500 caracteres", example = "Quanto ganha uma pessoa júnior nessa trilha?")
        @NotBlank
        @Size(max = 500)
        String mensagem,

        @Schema(description = "As últimas mensagens da conversa, da mais antiga para a mais recente (no máximo 6). Opcional")
        @Size(max = 6)
        List<@NotNull @Valid MensagemHistoricoRequest> historico,

        @Schema(description = "Onde a pessoa está no site. Opcional")
        @Valid
        ContextoRequest contexto
) {}
