package io.github.obrenoxs.trilhatech.assistente.cliente.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeminiResposta(List<Candidato> candidates) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidato(Conteudo content, String finishReason) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Conteudo(String role, List<Parte> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parte(String text) {}
}
