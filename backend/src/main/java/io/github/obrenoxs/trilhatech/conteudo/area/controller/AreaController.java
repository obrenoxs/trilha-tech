package io.github.obrenoxs.trilhatech.conteudo.area.controller;

import io.github.obrenoxs.trilhatech.compartilhado.dto.response.ErroResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Áreas", description = "As oito áreas de TI do Trilha Tech e as trilhas de cada uma")
@RestController
@RequestMapping(value = "/api/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @Operation(summary = "Lista as áreas",
            description = "Devolve as oito áreas na ordem de exibição, com a quantidade de trilhas de cada uma.")
    @ApiResponse(responseCode = "200", description = "As oito áreas")
    @GetMapping
    public ResponseEntity<List<AreaResponse>> findAll() {
        List<AreaResponse> areas = areaService.findAll();
        return ResponseEntity.ok().body(areas);
    }

    @Operation(summary = "Detalha uma área",
            description = "Devolve a área, como é trabalhar nela e a lista de trilhas, na ordem da área.")
    @ApiResponse(responseCode = "200", description = "A área e as trilhas dela")
    @ApiResponse(responseCode = "404", description = "Não existe área com esse slug",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping(value = "/{slug}")
    public ResponseEntity<AreaDetalheResponse> findBySlug(
            @Parameter(description = "Slug da área, como vem em GET /api/areas", example = "dados-e-ia")
            @PathVariable String slug) {
        AreaDetalheResponse area = areaService.findBySlug(slug);
        return ResponseEntity.ok().body(area);
    }
}