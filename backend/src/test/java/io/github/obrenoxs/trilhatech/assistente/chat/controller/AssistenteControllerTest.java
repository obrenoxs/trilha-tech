package io.github.obrenoxs.trilhatech.assistente.chat.controller;

import io.github.obrenoxs.trilhatech.assistente.chat.dto.request.ChatRequest;
import io.github.obrenoxs.trilhatech.assistente.chat.dto.response.ChatResponse;
import io.github.obrenoxs.trilhatech.assistente.chat.service.AssistenteService;
import io.github.obrenoxs.trilhatech.compartilhado.exception.LimiteUsoExcedidoException;
import io.github.obrenoxs.trilhatech.compartilhado.exception.TratadorExcecoesGlobal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AssistenteControllerTest {

    private static final String URL = "/api/assistente/mensagens";

    @Mock
    private AssistenteService assistenteService;

    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AssistenteController(assistenteService))
                .setControllerAdvice(new TratadorExcecoesGlobal())
                .build();
    }

    private MockHttpServletRequestBuilder envio(String corpo) {
        return post(URL).contentType(MediaType.APPLICATION_JSON).content(corpo);
    }

    @Test
    void deveResponder200ComOTextoDoAssistente() throws Exception {
        when(assistenteService.responder(any(ChatRequest.class), anyString())).thenReturn(new ChatResponse("Ola!"));

        mockMvc.perform(envio("{\"mensagem\":\"Oi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta").value("Ola!"));
    }

    @Test
    void deveUsarOXRealIpComoVisitanteQuandoEleForValido() throws Exception {
        when(assistenteService.responder(any(ChatRequest.class), eq("203.0.113.7"))).thenReturn(new ChatResponse("ok"));

        mockMvc.perform(envio("{\"mensagem\":\"Oi\"}").header("X-Real-IP", "203.0.113.7"))
                .andExpect(status().isOk());
    }

    @Test
    void deveUsarOEnderecoDaConexaoQuandoOCabecalhoNaoForUmIp() throws Exception {
        when(assistenteService.responder(any(ChatRequest.class), eq("127.0.0.1"))).thenReturn(new ChatResponse("ok"));

        mockMvc.perform(envio("{\"mensagem\":\"Oi\"}").header("X-Real-IP", "evil.example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void deveRecusarUmaMensagemEmBrancoSemChamarOService() throws Exception {
        mockMvc.perform(envio("{\"mensagem\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("mensagem"));

        verifyNoInteractions(assistenteService);
    }

    @Test
    void deveRecusarUmJsonQuebradoComMensagemGenerica() throws Exception {
        mockMvc.perform(envio("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Requisição inválida"));

        verifyNoInteractions(assistenteService);
    }

    @Test
    void devePassarOLimiteDoVisitanteComoRespostaQuatroVinteENove() throws Exception {
        when(assistenteService.responder(any(ChatRequest.class), anyString()))
                .thenThrow(new LimiteUsoExcedidoException("interno", 60));

        mockMvc.perform(envio("{\"mensagem\":\"Oi\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"));
    }
}