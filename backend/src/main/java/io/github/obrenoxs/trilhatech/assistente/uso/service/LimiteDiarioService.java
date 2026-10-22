package io.github.obrenoxs.trilhatech.assistente.uso.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class LimiteDiarioService {

    private static final ZoneId FUSO_DA_COTA = ZoneId.of("America/Los_Angeles");

    private static final String RESERVAR_MENSAGEM = """
            INSERT INTO uso_assistente (dia, mensagens) VALUES (?, 1)
            ON CONFLICT (dia) DO UPDATE SET mensagens = uso_assistente.mensagens + 1
            WHERE uso_assistente.mensagens < ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final int limiteDiario;

    public LimiteDiarioService(
            JdbcTemplate jdbcTemplate,
            @Value("${trilhatech.assistente.limite.mensagens-por-dia}") int limiteDiario
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.limiteDiario = limiteDiario;
    }

    public boolean reservarMensagem() {
        if (limiteDiario <= 0) {
            return false;
        }

        int linhasAfetadas = jdbcTemplate.update(RESERVAR_MENSAGEM, diaDaCota(Instant.now()), limiteDiario);
        return linhasAfetadas == 1;
    }

    static LocalDate diaDaCota(Instant momento) {
        return momento.atZone(FUSO_DA_COTA).toLocalDate();
    }
}
