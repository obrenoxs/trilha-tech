package io.github.obrenoxs.trilhatech.assistente.uso.limitador;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LimitadorJanelaFixaTest {

    class RelogioMutavel extends Clock {

        private Instant agora;

        RelogioMutavel(Instant inicio) {
            this.agora = inicio;
        }

        void avancar(Duration tempo) {
            agora = agora.plus(tempo);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return agora;
        }
    }

    @Test
    void deveLiberarAteOMaximoERecusarOSeguinte() {
        RelogioMutavel relogio = new RelogioMutavel(Instant.parse("2026-10-22T10:00:00Z"));
        LimitadorJanelaFixa limitador = new LimitadorJanelaFixa(Duration.ofMinutes(1), 3, relogio);

        assertThat(limitador.permitir("ip")).isTrue();
        assertThat(limitador.permitir("ip")).isTrue();
        assertThat(limitador.permitir("ip")).isTrue();
        assertThat(limitador.permitir("ip")).isFalse();
    }

    @Test
    void deveManterAsChavesIndependentes() {
        RelogioMutavel relogio = new RelogioMutavel(Instant.parse("2026-10-22T10:00:00Z"));
        LimitadorJanelaFixa limitador = new LimitadorJanelaFixa(Duration.ofMinutes(1), 3, relogio);

        assertThat(limitador.permitir("a")).isTrue();
        assertThat(limitador.permitir("a")).isTrue();
        assertThat(limitador.permitir("a")).isTrue();
        assertThat(limitador.permitir("a")).isFalse();

        assertThat(limitador.permitir("b")).isTrue();
    }

    @Test
    void naoDeveLiberarEnquantoAindaEstaNaMesmaJanela() {
        RelogioMutavel relogio = new RelogioMutavel(Instant.parse("2026-10-22T10:00:00Z"));
        LimitadorJanelaFixa limitador = new LimitadorJanelaFixa(Duration.ofMinutes(1), 3, relogio);

        limitador.permitir("ip");
        limitador.permitir("ip");
        limitador.permitir("ip");
        assertThat(limitador.permitir("ip")).isFalse();

        relogio.avancar(Duration.ofSeconds(30));

        assertThat(limitador.permitir("ip")).isFalse();
    }

    @Test
    void deveLiberarDeNovoQuandoComecaUmaNovaJanela() {
        RelogioMutavel relogio = new RelogioMutavel(Instant.parse("2026-10-22T10:00:00Z"));
        LimitadorJanelaFixa limitador = new LimitadorJanelaFixa(Duration.ofMinutes(1), 3, relogio);

        limitador.permitir("ip");
        limitador.permitir("ip");
        limitador.permitir("ip");
        assertThat(limitador.permitir("ip")).isFalse();

        relogio.avancar(Duration.ofMinutes(1));

        assertThat(limitador.permitir("ip")).isTrue();
    }

    @Test
    void deveRecusarTudoQuandoOMaximoEZero() {
        RelogioMutavel relogio = new RelogioMutavel(Instant.parse("2026-10-22T10:00:00Z"));
        LimitadorJanelaFixa limitador = new LimitadorJanelaFixa(Duration.ofMinutes(1), 0, relogio);

        assertThat(limitador.permitir("ip")).isFalse();
    }
}
