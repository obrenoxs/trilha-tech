package io.github.obrenoxs.trilhatech.assistente.cliente.gemini;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record GeminiRequisicao(
        Conteudo systemInstruction,
        List<Conteudo> contents,
        Geracao generationConfig
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Conteudo(String role, List<Parte> parts) {}

    public record Parte(String text) {}

    public record Geracao(Integer maxOutputTokens) {}
}
