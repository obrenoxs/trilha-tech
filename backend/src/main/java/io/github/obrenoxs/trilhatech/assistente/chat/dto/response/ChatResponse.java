package io.github.obrenoxs.trilhatech.assistente.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatResponse(
        @Schema(description = "A resposta do assistente, em texto simples") String resposta
) {}
