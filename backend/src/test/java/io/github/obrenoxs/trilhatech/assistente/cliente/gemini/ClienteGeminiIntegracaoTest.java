package io.github.obrenoxs.trilhatech.assistente.cliente.gemini;

import io.github.obrenoxs.trilhatech.assistente.cliente.MensagemIa;
import io.github.obrenoxs.trilhatech.assistente.cliente.PapelMensagem;
import io.github.obrenoxs.trilhatech.compartilhado.exception.IaIndisponivelException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClienteGeminiIntegracaoTest {

    private static final String URL_BASE = "https://generativelanguage.googleapis.com/v1beta";

    @Test
    void deveFalharSemChaveSemTentarARede() {
        ClienteGemini cliente = new ClienteGemini("", "gemini-3.5-flash-lite", URL_BASE, 30, 800);

        assertThrows(IaIndisponivelException.class, () -> cliente.responder(
                "Voce e o assistente do Trilha Tech.",
                List.of(new MensagemIa(PapelMensagem.USUARIO, "Oi"))));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
    void deveObterUmaRespostaRealDoGemini() {
        ClienteGemini cliente = new ClienteGemini(
                System.getenv("GEMINI_API_KEY"), "gemini-3.5-flash-lite", URL_BASE, 30, 800);

        String resposta = cliente.responder(
                "Voce e o assistente do Trilha Tech. Responda em portugues, em uma frase.",
                List.of(new MensagemIa(PapelMensagem.USUARIO, "O que faz uma pessoa que trabalha com dados?")));

        assertThat(resposta).isNotBlank();
    }
}
