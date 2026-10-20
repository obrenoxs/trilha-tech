package io.github.obrenoxs.trilhatech.conteudo.area.controller;

import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.service.AreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    ResponseEntity<List<AreaResponse>> findAll() {
        List<AreaResponse> areas = areaService.findAll();
        return ResponseEntity.ok().body(areas);
    }
}
