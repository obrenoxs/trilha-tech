package io.github.obrenoxs.trilhatech.conteudo.trilha.controller;

import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.service.TrilhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/trilhas")
public class TrilhaController {

    private final TrilhaService trilhaService;

    public TrilhaController(TrilhaService trilhaService) {
        this.trilhaService = trilhaService;
    }

    @GetMapping(value = "/{slug}")
    public ResponseEntity<TrilhaDetalheResponse> findBySlug(@PathVariable String slug) {
        TrilhaDetalheResponse trilha = trilhaService.findBySlug(slug);
        return ResponseEntity.ok().body(trilha);
    }
}
