package io.github.obrenoxs.trilhatech.assistente.uso.service;


import io.github.obrenoxs.trilhatech.tests.RelogioMutavel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControleDeUsoServiceTest {

    @Mock
    private LimiteDiarioService limiteDiario;

    private RelogioMutavel relogio;

    @BeforeEach
    void preparar() {
        relogio = new RelogioMutavel(Instant.parse("2026-10-22T10:00:00Z"));
    }

    private ControleDeUsoService servico(int globalPorMinuto, int visitantePorMinuto, int visitantePorHora) {
        return new ControleDeUsoService(limiteDiario, relogio, globalPorMinuto, visitantePorMinuto, visitantePorHora);
    }

    @Test
    void devePermitirQuandoTodasAsBarreirasLiberam() {
        when(limiteDiario.reservarMensagem()).thenReturn(true);

        assertThat(servico(12, 5, 30).reservar("ip")).isEqualTo(ResultadoLimite.PERMITIDO);
    }

    @Test
    void deveBarrarOVisitanteQueExcedeOLimiteDoMinuto() {
        when(limiteDiario.reservarMensagem()).thenReturn(true);
        ControleDeUsoService servico = servico(12, 2, 30);

        servico.reservar("ip");
        servico.reservar("ip");

        assertThat(servico.reservar("ip")).isEqualTo(ResultadoLimite.VISITANTE_POR_MINUTO);
    }

    @Test
    void deveBarrarOVisitanteQueExcedeOLimiteDaHora() {
        when(limiteDiario.reservarMensagem()).thenReturn(true);
        ControleDeUsoService servico = servico(12, 2, 3);

        servico.reservar("ip");
        servico.reservar("ip");
        relogio.avancar(Duration.ofMinutes(1));
        servico.reservar("ip");

        assertThat(servico.reservar("ip")).isEqualTo(ResultadoLimite.VISITANTE_POR_HORA);
    }

    @Test
    void deveBarrarQuandoOGlobalDoMinutoEstoura() {
        when(limiteDiario.reservarMensagem()).thenReturn(true);
        ControleDeUsoService servico = servico(2, 5, 30);

        servico.reservar("a");
        servico.reservar("b");

        assertThat(servico.reservar("c")).isEqualTo(ResultadoLimite.GLOBAL_POR_MINUTO);
    }

    @Test
    void deveBarrarQuandoOTetoDiarioFoiAtingido() {
        when(limiteDiario.reservarMensagem()).thenReturn(false);

        assertThat(servico(12, 5, 30).reservar("ip")).isEqualTo(ResultadoLimite.TETO_DIARIO);
    }

    @Test
    void naoDeveConsultarOBancoQuandoOVisitanteJaFoiBarrado() {
        when(limiteDiario.reservarMensagem()).thenReturn(true);
        ControleDeUsoService servico = servico(12, 1, 30);

        servico.reservar("ip");
        servico.reservar("ip");

        verify(limiteDiario, times(1)).reservarMensagem();
    }
}
