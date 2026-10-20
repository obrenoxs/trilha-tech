package io.github.obrenoxs.trilhatech.compartilhado.exception;

import io.github.obrenoxs.trilhatech.carga.exception.ConteudoInvalidoException;
import io.github.obrenoxs.trilhatech.carga.exception.FalhaLeituraException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class TratadorExcecoesGlobal {

    @ExceptionHandler(NaoEncontradoException.class)
    ResponseEntity<Map<String, Object>> tratarNaoEncontrado(NaoEncontradoException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
    }

    @ExceptionHandler(ConteudoInvalidoException.class)
    ResponseEntity<Map<String, Object>> tratarConteudoInvalido(ConteudoInvalidoException ex, WebRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), null, request);
    }

    @ExceptionHandler(FalhaLeituraException.class)
    ResponseEntity<Map<String, Object>> tratarFalhaLeitura(FalhaLeituraException ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null, request);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String message, List<Map<String, String>> errors, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        if (errors != null && !errors.isEmpty()) {
            body.put("errors", errors);
        }

        body.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(status).body(body);
    }
}
