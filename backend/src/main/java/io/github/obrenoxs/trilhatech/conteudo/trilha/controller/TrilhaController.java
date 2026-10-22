package io.github.obrenoxs.trilhatech.conteudo.trilha.controller;

import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.service.RoadmapPdfService;
import io.github.obrenoxs.trilhatech.conteudo.trilha.service.TrilhaService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/trilhas")
public class TrilhaController {

    private final TrilhaService trilhaService;
    private final RoadmapPdfService roadmapPdfService;

    public TrilhaController(TrilhaService trilhaService, RoadmapPdfService roadmapPdfService) {
        this.trilhaService = trilhaService;
        this.roadmapPdfService = roadmapPdfService;
    }

    @GetMapping(value = "/{slug}")
    public ResponseEntity<TrilhaDetalheResponse> findBySlug(@PathVariable String slug) {
        TrilhaDetalheResponse trilha = trilhaService.findBySlug(slug);
        return ResponseEntity.ok().body(trilha);
    }

    @GetMapping(value = "/{slug}/pdf")
    public ResponseEntity<Resource> obterPDF(@PathVariable String slug) {
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
