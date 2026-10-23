package io.github.obrenoxs.trilhatech.quiz.pergunta.controller;

import io.github.obrenoxs.trilhatech.quiz.pergunta.dto.response.PerguntaResponse;
import io.github.obrenoxs.trilhatech.quiz.pergunta.service.PerguntaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Quiz", description = "O quiz que sugere áreas de TI")
@RestController
@RequestMapping(value = "/api/quiz")
public class PerguntaController {

    private final PerguntaService perguntaService;

    public PerguntaController(PerguntaService perguntaService) {
        this.perguntaService = perguntaService;
    }

    @Operation(summary = "Lista as perguntas do quiz",
            description = "Devolve as 12 perguntas, cada uma com 4 alternativas, na ordem do arquivo. "
                    + "A resposta não traz as áreas nem os pontos de cada alternativa. "
                    + "O front-end deve embaralhar as alternativas e identificar cada resposta pela 'ordem' "
                    + "da pergunta e da alternativa, e nunca pela posição na tela.")
    @ApiResponse(responseCode = "200", description = "As 12 perguntas")
    @GetMapping
    public ResponseEntity<List<PerguntaResponse>> findAll() {
        List<PerguntaResponse> perguntas = perguntaService.findAll();
        return ResponseEntity.ok().body(perguntas);
    }
}