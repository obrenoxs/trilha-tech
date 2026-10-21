package io.github.obrenoxs.trilhatech.quiz.resultado.controller;

import io.github.obrenoxs.trilhatech.quiz.resultado.dto.request.ResultadoRequest;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.response.ResultadoResponse;
import io.github.obrenoxs.trilhatech.quiz.resultado.service.ResultadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/quiz")
public class ResultadoController {

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @PostMapping(value = "/resultado")
    public ResponseEntity<ResultadoResponse> calcular(@Valid @RequestBody ResultadoRequest request) {
        ResultadoResponse resultado = resultadoService.calcular(request);
        return ResponseEntity.ok().body(resultado);
    }
}
