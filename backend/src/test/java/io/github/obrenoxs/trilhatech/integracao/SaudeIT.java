package io.github.obrenoxs.trilhatech.integracao;

import io.github.obrenoxs.trilhatech.tests.IntegracaoBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SaudeIT.ObservadorDeProntidao.class)
class SaudeIT extends IntegracaoBase {

    static final AtomicReference<ReadinessState> ESTADO_DURANTE_OS_RUNNERS = new AtomicReference<>();

    @TestConfiguration
    static class ObservadorDeProntidao {

        @Bean
        ApplicationRunner observador(ApplicationAvailability disponibilidade) {
            return args -> ESTADO_DURANTE_OS_RUNNERS.set(disponibilidade.getReadinessState());
        }
    }

    @Autowired
    private ApplicationAvailability disponibilidade;

    @Test
    void deveRecusarTrafegoEnquantoOsRunnersExecutamEAceitarDepois() {
        assertThat(ESTADO_DURANTE_OS_RUNNERS.get()).isEqualTo(ReadinessState.REFUSING_TRAFFIC);
        assertThat(disponibilidade.getReadinessState()).isEqualTo(ReadinessState.ACCEPTING_TRAFFIC);
    }

    @Test
    void deveResponderUpNaVidaENaProntidao() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void naoDeveMostrarDetalhesDoBanco() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/env", "/actuator/beans", "/actuator/metrics",
            "/actuator/info", "/actuator/mappings", "/actuator/heapdump", "/actuator/configprops"})
    void deveExporSomenteARotaDeSaude(String caminho) throws Exception {
        mockMvc.perform(get(caminho))
                .andExpect(status().isNotFound());
    }
}
