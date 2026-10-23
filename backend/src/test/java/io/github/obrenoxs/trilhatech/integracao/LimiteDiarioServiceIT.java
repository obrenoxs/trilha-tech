package io.github.obrenoxs.trilhatech.integracao;

import io.github.obrenoxs.trilhatech.assistente.uso.service.LimiteDiarioService;
import io.github.obrenoxs.trilhatech.tests.IntegracaoBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class LimiteDiarioServiceIT extends IntegracaoBase {

    @BeforeEach
    void preparar() {
        limparBanco();
    }

    private int mensagensGravadas() {
        Integer total = jdbcTemplate.queryForObject("select coalesce(sum(mensagens), 0) from uso_assistente", Integer.class);
        return total == null ? 0 : total;
    }

    @Test
    void deveReservarAteOLimiteERecusarDepois() {
        LimiteDiarioService servico = new LimiteDiarioService(jdbcTemplate, 3);

        assertThat(servico.reservarMensagem()).isTrue();
        assertThat(servico.reservarMensagem()).isTrue();
        assertThat(servico.reservarMensagem()).isTrue();
        assertThat(servico.reservarMensagem()).isFalse();
        assertThat(mensagensGravadas()).isEqualTo(3);
    }

    @Test
    void naoDeveGravarNadaQuandoOLimiteEZero() {
        LimiteDiarioService servico = new LimiteDiarioService(jdbcTemplate, 0);

        assertThat(servico.reservarMensagem()).isFalse();
        assertThat(contar("uso_assistente")).isZero();
    }

    @Test
    void naoDevePassarDoLimiteQuandoVariasRequisicoesChegamAoMesmoTempo() throws Exception {
        LimiteDiarioService servico = new LimiteDiarioService(jdbcTemplate, 5);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch largada = new CountDownLatch(1);
        List<Future<Boolean>> resultados = new ArrayList<>();

        try {
            for (int i = 0; i < 40; i++) {
                resultados.add(executor.submit(() -> {
                    largada.await();
                    return servico.reservarMensagem();
                }));
            }
            largada.countDown();

            int permitidas = 0;
            for (Future<Boolean> resultado : resultados) {
                if (resultado.get(30, TimeUnit.SECONDS)) {
                    permitidas++;
                }
            }

            assertThat(permitidas).isEqualTo(5);
            assertThat(mensagensGravadas()).isEqualTo(5);
        } finally {
            executor.shutdownNow();
        }
    }
}