package io.github.obrenoxs.trilhatech.compartilhado.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class TratadorExcecoesGlobal extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TratadorExcecoesGlobal.class);

    @ExceptionHandler(NaoEncontradoException.class)
    ResponseEntity<Object> tratarNaoEncontrado(NaoEncontradoException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null, request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> tratarErroInesperado(Exception ex, WebRequest request) {
        log.error("Erro inesperado", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. Tente novamente mais tarde.", null, request);
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    ResponseEntity<Object> tratarRequisicaoInvalida(RequisicaoInvalidaException ex, WebRequest request) {
        List<Map<String, String>> erros = new ArrayList<>();
        for (String problema : ex.getProblemas()) {
            erros.add(Map.of("message", problema));
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", erros, request);
    }

    @ExceptionHandler(LimiteUsoExcedidoException.class)
    ResponseEntity<Object> tratarLimiteDeUsoExcedido(LimiteUsoExcedidoException ex, WebRequest request) {
        HttpHeaders cabecalhos = new HttpHeaders();
        cabecalhos.set(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getSegundosParaTentarDeNovo()));
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS,
                "Muitas mensagens em pouco tempo. Aguarde um instante e tente de novo.", null, cabecalhos, request);
    }

    @ExceptionHandler(IaIndisponivelException.class)
    ResponseEntity<Object> tratarIaIndisponivel(IaIndisponivelException ex, WebRequest request) {
        log.warn("Assistente indisponível: {}", ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "O assistente está indisponível no momento. O restante do site continua funcionando.", null, request);
    }

    @ExceptionHandler(CorpoRequisicaoException.class)
    ResponseEntity<Object> tratarCorpoRequisição(CorpoRequisicaoException ex, WebRequest request) {
        return buildResponse(HttpStatus.valueOf(413),
                "O corpo da requisição é grande demais.", null, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<Map<String, String>> erros = new ArrayList<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.add(Map.of(
                    "field", erro.getField(),
                    "message", String.valueOf(erro.getDefaultMessage())
            ));
        }
        return buildResponse(HttpStatus.BAD_REQUEST, "Requisição inválida", erros, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        String mensagem = status.is4xxClientError()
                ? "Requisição inválida"
                : "Erro interno. Tente novamente mais tarde.";

        return buildResponse(status, mensagem, null, request);
    }

    private ResponseEntity<Object> buildResponse(
            HttpStatus status, String message, List<Map<String, String>> errors, WebRequest request) {
        return buildResponse(status, message, errors, new HttpHeaders(), request);
    }

    private ResponseEntity<Object> buildResponse(
            HttpStatus status, String message, List<Map<String, String>> errors,
            HttpHeaders cabecalhos, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        if (errors != null && !errors.isEmpty()) {
            body.put("errors", errors);
        }

        body.put("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(status).headers(cabecalhos).body(body);
    }
}
