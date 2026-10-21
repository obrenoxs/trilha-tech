package io.github.obrenoxs.trilhatech.quiz.pergunta.controller;

import io.github.obrenoxs.trilhatech.quiz.pergunta.dto.response.PerguntaResponse;
import io.github.obrenoxs.trilhatech.quiz.pergunta.service.PerguntaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/quiz")
public class PerguntaController {

    private final PerguntaService perguntaService;

    public PerguntaController(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @GetMapping
    public ResponseEntity<List<PerguntaResponse>> findAll() {
        List<PerguntaResponse> perguntas = perguntaService.findAll();
        return ResponseEntity.ok().body(perguntas);
    }
}
