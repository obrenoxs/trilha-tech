package io.github.obrenoxs.trilhatech.compartilhado.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Formato padrão de todos os erros da API")
public record ErroResponse(
        @Schema(example = "2026-10-23T19:05:44") String timestamp,
        @Schema(example = "400") int status,
        @Schema(example = "Bad Request") String error,
        @Schema(example = "Requisição inválida") String message,
        @Schema(description = "Só aparece nos erros de validação") List<Detalhe> errors,
        @Schema(example = "/api/quiz/resultado") String path
) {
    public record Detalhe(
            @Schema(description = "Caminho do campo. Ausente nos problemas que não são de um campo", example = "respostas[0]") String field,
            @Schema(example = "não deve ser nulo") String message
    ) {}
}
