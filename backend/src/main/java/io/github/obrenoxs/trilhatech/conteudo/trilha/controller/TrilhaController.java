package io.github.obrenoxs.trilhatech.conteudo.trilha.controller;

import io.github.obrenoxs.trilhatech.compartilhado.dto.response.ErroResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.service.RoadmapPdfService;
import io.github.obrenoxs.trilhatech.conteudo.trilha.service.TrilhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Trilhas", description = "O roadmap de cada trilha e o PDF para baixar")
@RestController
@RequestMapping(value = "/api/trilhas")
public class TrilhaController {

    private final TrilhaService trilhaService;
    private final RoadmapPdfService roadmapPdfService;

    public TrilhaController(TrilhaService trilhaService, RoadmapPdfService roadmapPdfService) {
        this.trilhaService = trilhaService;
        this.roadmapPdfService = roadmapPdfService;
    }

    @Operation(summary = "Detalha uma trilha",
            description = "Devolve a trilha com as etapas em ordem, os recursos de estudo de cada etapa, "
                    + "as três faixas salariais (júnior, pleno e sênior) e o aviso sobre os salários. "
                    + "Os salários são estimativas, e cada faixa traz a fonte e o mês de referência.")
    @ApiResponse(responseCode = "200", description = "A trilha completa")
    @ApiResponse(responseCode = "404", description = "Não existe trilha com esse slug",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping(value = "/{slug}")
    public ResponseEntity<TrilhaDetalheResponse> findBySlug(
            @Parameter(description = "Slug da trilha, como vem em GET /api/areas/{slug}", example = "java-e-spring")
            @PathVariable String slug) {
        TrilhaDetalheResponse trilha = trilhaService.findBySlug(slug);
        return ResponseEntity.ok().body(trilha);
    }

    @Operation(summary = "Baixa o roadmap da trilha em PDF",
            description = "Devolve o arquivo com o cabeçalho Content-Disposition: attachment, "
                    + "para o navegador baixar em vez de abrir na aba.")
    @ApiResponse(responseCode = "200", description = "O PDF da trilha",
            content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "404", description = "Não existe trilha com esse slug",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @GetMapping(value = "/{slug}/pdf")
    public ResponseEntity<Resource> obterPDF(
            @Parameter(description = "Slug da trilha", example = "java-e-spring")
            @PathVariable String slug) {
        Resource pdf = roadmapPdfService.obter(slug);

        ContentDisposition disposicao = ContentDisposition.attachment()
                .filename(pdf.getFilename())
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicao.toString())
                .body(pdf);
    }
}