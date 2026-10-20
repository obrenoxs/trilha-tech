package io.github.obrenoxs.trilhatech.conteudo.area.service;

import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.TrilhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final TrilhaRepository trilhaRepository;

    public AreaService(AreaRepository areaRepository, TrilhaRepository trilhaRepository) {
        this.areaRepository = areaRepository;
        this.trilhaRepository = trilhaRepository;
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> findAll() {
        List<Area> areas = areaRepository.findAllByOrderByOrdemExibicaoAsc();
        List<AreaResponse> areasResponse = new ArrayList<>();
        for (Area area : areas) {
            long qntdTrilha = trilhaRepository.countByAreaId(area.getId());
            areasResponse.add(new AreaResponse(area.getSlug(), area.getNumero(), area.getNome(), area.getDescricao(), qntdTrilha));
        }

        return areasResponse;
    }
}
