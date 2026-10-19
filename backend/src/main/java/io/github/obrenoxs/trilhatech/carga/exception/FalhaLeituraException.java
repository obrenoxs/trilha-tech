package io.github.obrenoxs.trilhatech.carga.exception;

public class FalhaLeituraException extends RuntimeException {

    public FalhaLeituraException(String message) {
        super(message);
    }

    public FalhaLeituraException(String message, Throwable cause) {
        super(message, cause);
    }
}
