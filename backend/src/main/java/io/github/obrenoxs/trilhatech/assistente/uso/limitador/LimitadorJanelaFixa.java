package io.github.obrenoxs.trilhatech.assistente.uso.limitador;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class LimitadorJanelaFixa {

    private static final int MAXIMO_DE_CHAVES = 10_000;

    private record Janela(long id, int contagem) {}

    private final ConcurrentHashMap<String, Janela> janelas = new ConcurrentHashMap<>();
    private final long duracaoEmMilissegundos;
    private final int maximo;
    private final Clock relogio;

    public LimitadorJanelaFixa(Duration duracao, int maximo, Clock relogio) {
        this.duracaoEmMilissegundos = duracao.toMillis();
        this.maximo = maximo;
        this.relogio = relogio;
    }

    public boolean permitir(String chave) {
        long idAtual = relogio.millis() / duracaoEmMilissegundos;

        Janela atual = janelas.merge(chave, new Janela(idAtual, 1),
                (existente, nova) -> existente.id() == idAtual
                        ? new Janela(idAtual, existente.contagem() + 1)
                        : nova);

        if (janelas.size() > MAXIMO_DE_CHAVES) {
            janelas.values().removeIf(janela -> janela.id() != idAtual);
        }

        return atual.contagem() <= maximo;
    }
}
