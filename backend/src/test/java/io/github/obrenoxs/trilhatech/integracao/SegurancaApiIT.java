package io.github.obrenoxs.trilhatech.integracao;

import io.github.obrenoxs.trilhatech.tests.IntegracaoBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SegurancaApiIT extends IntegracaoBase {

    private static final String ORIGEM_DO_FRONT = "http://localhost:5173";

    private String corpoGrande() {
        return "{\"mensagem\":\"" + "a".repeat(150_000) + "\"}";
    }

    @Test
    void deveRecusarCorposGrandesComBadRequestPadronizadoNoChat() throws Exception {
        mockMvc.perform(post("/api/assistente/mensagens").contentType(MediaType.APPLICATION_JSON).content(corpoGrande()))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413));
    }

    @Test
    void deveRecusarCorposGrandesNoQuiz() throws Exception {
        mockMvc.perform(post("/api/quiz/resultado").contentType(MediaType.APPLICATION_JSON).content(corpoGrande()))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    void devePermitirOPreflightDoChatParaAOrigemDoFrontend() throws Exception {
        mockMvc.perform(options("/api/assistente/mensagens")
                        .header(HttpHeaders.ORIGIN, ORIGEM_DO_FRONT)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ORIGEM_DO_FRONT));
    }

    @Test
    void devePermitirALeituraDasAreasParaAOrigemDoFrontend() throws Exception {
        mockMvc.perform(get("/api/areas").header(HttpHeaders.ORIGIN, ORIGEM_DO_FRONT))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ORIGEM_DO_FRONT));
    }

    @Test
    void deveRecusarUmaOrigemDesconhecida() throws Exception {
        mockMvc.perform(options("/api/areas")
                        .header(HttpHeaders.ORIGIN, "https://malicioso.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveResponder404SemVazarDetalhesParaUmaRotaQueNaoExiste() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/api/nao-existe"))
                .andExpect(status().isNotFound())
                .andReturn();

        assertThat(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .doesNotContain("Exception", "io.github");
    }

    @Test
    void deveResponder405ParaUmMetodoQueARotaNaoAceita() throws Exception {
        mockMvc.perform(delete("/api/areas"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value("Requisição inválida"));
    }

    @Test
    void deveResponder415ParaUmTipoDeConteudoQueNaoESuportado() throws Exception {
        mockMvc.perform(post("/api/quiz/resultado").contentType(MediaType.TEXT_PLAIN).content("oi"))
                .andExpect(status().isUnsupportedMediaType());
    }
}