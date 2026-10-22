package io.github.obrenoxs.trilhatech.assistente.uso.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LimiteDiarioServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void diaDaCotaViraAsSeteUtcComHorarioDeVerao() {
        assertThat(LimiteDiarioService.diaDaCota(Instant.parse("2026-10-22T06:59:59Z")))
                .isEqualTo(LocalDate.of(2026, 10, 21));
        assertThat(LimiteDiarioService.diaDaCota(Instant.parse("2026-10-22T07:00:00Z")))
                .isEqualTo(LocalDate.of(2026, 10, 22));
    }

    @Test
    void diaDaCotaViraAsOitoUtcSemHorarioDeVerao() {
        assertThat(LimiteDiarioService.diaDaCota(Instant.parse("2026-11-15T07:59:59Z")))
                .isEqualTo(LocalDate.of(2026, 11, 14));
        assertThat(LimiteDiarioService.diaDaCota(Instant.parse("2026-11-15T08:00:00Z")))
                .isEqualTo(LocalDate.of(2026, 11, 15));
    }

    @Test
    void deveReservarQuandoOBancoAfetaUmaLinha() {
        when(jdbcTemplate.update(anyString(), any(LocalDate.class), anyInt())).thenReturn(1);
        LimiteDiarioService servico = new LimiteDiarioService(jdbcTemplate, 400);

        assertThat(servico.reservarMensagem()).isTrue();
    }

    @Test
    void deveRecusarQuandoOTetoFoiAtingido() {
        when(jdbcTemplate.update(anyString(), any(LocalDate.class), anyInt())).thenReturn(0);
        LimiteDiarioService servico = new LimiteDiarioService(jdbcTemplate, 400);

        assertThat(servico.reservarMensagem()).isFalse();
    }

    @Test
    void deveRecusarSemConsultarOBancoQuandoOLimiteENaoPositivo() {
        LimiteDiarioService servico = new LimiteDiarioService(jdbcTemplate, 0);

        assertThat(servico.reservarMensagem()).isFalse();
        verifyNoInteractions(jdbcTemplate);
    }
}
