package io.github.obrenoxs.trilhatech.assistente.cliente.gemini;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClienteGeminiTest {

    private GeminiResposta respostaCom(String... textos) {
        List<GeminiResposta.Parte> partes = new ArrayList<>();
        for (String texto : textos) {
            partes.add(new GeminiResposta.Parte(texto));
        }
        return new GeminiResposta(List.of(
                new GeminiResposta.Candidato(new GeminiResposta.Conteudo("model", partes), "STOP")));
    }

    @Test
    void deveJuntarSoOsPedacosQueTemTexto() {
        GeminiResposta resposta = respostaCom("Ola, ", null, "mundo");

        assertThat(ClienteGemini.extrairTexto(resposta)).isEqualTo("Ola, mundo");
    }

    @Test
    void deveTirarOsEspacosDasPontas() {
        assertThat(ClienteGemini.extrairTexto(respostaCom("  oi  "))).isEqualTo("oi");
    }

    @Test
    void deveDevolverVazioQuandoARespostaENula() {
        assertThat(ClienteGemini.extrairTexto(null)).isEmpty();
    }

    @Test
    void deveDevolverVazioQuandoNaoHaCandidatos() {
        assertThat(ClienteGemini.extrairTexto(new GeminiResposta(null))).isEmpty();
        assertThat(ClienteGemini.extrairTexto(new GeminiResposta(List.of()))).isEmpty();
    }

    @Test
    void deveDevolverVazioQuandoOCandidatoNaoTemConteudo() {
        GeminiResposta bloqueada = new GeminiResposta(List.of(
                new GeminiResposta.Candidato(null, "SAFETY")));

        assertThat(ClienteGemini.extrairTexto(bloqueada)).isEmpty();
    }
}
