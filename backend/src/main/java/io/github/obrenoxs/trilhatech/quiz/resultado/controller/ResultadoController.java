package io.github.obrenoxs.trilhatech.quiz.resultado.controller;

import io.github.obrenoxs.trilhatech.compartilhado.dto.response.ErroResponse;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.request.ResultadoRequest;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.response.ResultadoResponse;
import io.github.obrenoxs.trilhatech.quiz.resultado.service.ResultadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Quiz", description = "O quiz que sugere áreas de TI")
@RestController
@RequestMapping(value = "/api/quiz")
public class ResultadoController {

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @Operation(summary = "Calcula o resultado do quiz",
            description = "Recebe as 12 respostas de uma vez e devolve as 3 áreas mais indicadas, "
                    + "com uma barra de 0 a 100 relativa à primeira. Nada é gravado: o resultado deve ser "
                    + "guardado pelo front-end. Se houver problemas nas respostas (pergunta repetida, "
                    + "alternativa inexistente, pergunta sem resposta), todos voltam juntos em 'errors'.")
    @ApiResponse(responseCode = "200", description = "As três áreas mais indicadas")
    @ApiResponse(responseCode = "400", description = "Respostas inválidas, com a lista de problemas",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "413", description = "Corpo da requisição grande demais",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @PostMapping(value = "/resultado")
    public ResponseEntity<ResultadoResponse> calcular(@Valid @RequestBody ResultadoRequest request) {
        ResultadoResponse resultado = resultadoService.calcular(request);
        return ResponseEntity.ok().body(resultado);
    }
}