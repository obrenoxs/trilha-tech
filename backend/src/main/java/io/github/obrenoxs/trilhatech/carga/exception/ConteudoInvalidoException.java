package io.github.obrenoxs.trilhatech.carga.exception;

import java.util.List;

public class ConteudoInvalidoException extends RuntimeException {

    private final List<String> problemas;

    public ConteudoInvalidoException(List<String> problemas) {
        super(montarMensagem(problemas));
        this.problemas = List.copyOf(problemas);
    }

    public List<String> getProblemas() {
        return problemas;
    }

    private static String montarMensagem(List<String> problemas) {
        return problemas.size() + " problema(s) encontrado(s):\n- "
                + String.join("\n- ", problemas);
    }
}
