package io.github.obrenoxs.trilhatech.conteudo.area.controller;

import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.service.AreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public ResponseEntity<List<AreaResponse>> findAll() {
        List<AreaResponse> areas = areaService.findAll();
        return ResponseEntity.ok().body(areas);
    }

    @GetMapping(value = "/{slug}")
    public ResponseEntity<AreaDetalheResponse> findBySlug(@PathVariable String slug) {
        AreaDetalheResponse areaResponse = areaService.findBySlug(slug);
        return ResponseEntity.ok().body(areaResponse);
    }
}
