package io.github.obrenoxs.trilhatech.compartilhado.exception;

public class IaIndisponivelException extends RuntimeException {

    public IaIndisponivelException(String message) {
        super(message);
    }

    public IaIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
