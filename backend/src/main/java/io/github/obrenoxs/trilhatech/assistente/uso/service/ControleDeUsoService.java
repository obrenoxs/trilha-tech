package io.github.obrenoxs.trilhatech.assistente.uso.service;

import io.github.obrenoxs.trilhatech.assistente.uso.limitador.LimitadorJanelaFixa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;

@Service
public class ControleDeUsoService {

    private static final String CHAVE_GLOBAL = "global";

    private final LimitadorJanelaFixa visitantePorMinuto;
    private final LimitadorJanelaFixa visitantePorHora;
    private final LimitadorJanelaFixa globalPorMinuto;
    private final LimiteDiarioService limiteDiario;

    public ControleDeUsoService(
            LimiteDiarioService limiteDiario,
            Clock relogio,
            @Value("${trilhatech.assistente.limite.por-minuto-global}") int maximoGlobalPorMinuto,
            @Value("${trilhatech.assistente.limite.por-minuto-por-visitante}") int maximoVisitantePorMinuto,
            @Value("${trilhatech.assistente.limite.por-hora-por-visitante}") int maximoVisitantePorHora
    ) {
        this.limiteDiario = limiteDiario;
        this.visitantePorMinuto = new LimitadorJanelaFixa(Duration.ofMinutes(1), maximoVisitantePorMinuto, relogio);
        this.visitantePorHora = new LimitadorJanelaFixa(Duration.ofHours(1), maximoVisitantePorHora, relogio);
        this.globalPorMinuto = new LimitadorJanelaFixa(Duration.ofMinutes(1), maximoGlobalPorMinuto, relogio);
    }

    public ResultadoLimite reservar(String visitante) {
        if (!visitantePorMinuto.permitir(visitante)) {
            return ResultadoLimite.VISITANTE_POR_MINUTO;
        }
        if (!visitantePorHora.permitir(visitante)) {
            return ResultadoLimite.VISITANTE_POR_HORA;
        }
        if (!globalPorMinuto.permitir(CHAVE_GLOBAL)) {
            return ResultadoLimite.GLOBAL_POR_MINUTO;
        }
        if (!limiteDiario.reservarMensagem()) {
            return ResultadoLimite.TETO_DIARIO;
        }
        return ResultadoLimite.PERMITIDO;
    }
}
