package io.github.obrenoxs.trilhatech.compartilhado.exception;

public class LimiteUsoExcedidoException extends RuntimeException {

    private final int segundos;

    public LimiteUsoExcedidoException(String message, int segundos) {
        super(message);
        this.segundos = segundos;
    }

    public int getSegundosParaTentarDeNovo() {
        return segundos;
    }
}
