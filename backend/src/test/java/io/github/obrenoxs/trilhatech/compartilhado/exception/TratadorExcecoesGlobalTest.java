package io.github.obrenoxs.trilhatech.compartilhado.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TratadorExcecoesGlobalTest {

    @RestController
    static class ControladorFalso {

        @GetMapping("/falso/nao-encontrado")
        String naoEncontrado() {
            throw new NaoEncontradoException("Trilha x nao existe");
        }

        @GetMapping("/falso/limite")
        String limite() {
            throw new LimiteUsoExcedidoException("interno: limite do visitante", 60);
        }

        @GetMapping("/falso/ia")
        String ia() {
            throw new IaIndisponivelException("segredo interno");
        }

        @GetMapping("/falso/erro")
        String erro() {
            throw new IllegalStateException("segredo interno");
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ControladorFalso())
                .setControllerAdvice(new TratadorExcecoesGlobal())
                .build();
    }

    @Test
    void deveResponder404QuandoNaoEncontrado() throws Exception {
        mockMvc.perform(get("/falso/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveResponder429ComRetryAfterQuandoOLimiteDoVisitanteEstoura() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/falso/limite"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andReturn();

        assertThat(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .doesNotContain("interno");
    }

    @Test
    void deveResponder503SemVazarOMotivoQuandoAIaEstaIndisponivel() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/falso/ia"))
                .andExpect(status().isServiceUnavailable())
                .andReturn();

        assertThat(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .doesNotContain("segredo interno");
    }

    @Test
    void deveResponder500SemVazarOMotivoParaUmErroInesperado() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/falso/erro"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        assertThat(resultado.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .doesNotContain("segredo interno");
    }
}
