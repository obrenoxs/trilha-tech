package io.github.obrenoxs.trilhatech.conteudo.area.service;

import io.github.obrenoxs.trilhatech.compartilhado.exception.NaoEncontradoException;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Trilha;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.EtapaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.TrilhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final TrilhaRepository trilhaRepository;
    private final EtapaRepository etapaRepository;

    public AreaService(AreaRepository areaRepository, TrilhaRepository trilhaRepository, EtapaRepository etapaRepository) {
        this.areaRepository = areaRepository;
        this.trilhaRepository = trilhaRepository;
        this.etapaRepository = etapaRepository;
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

    @Transactional(readOnly = true)
    public AreaDetalheResponse findBySlug(String slug) {
        Area area = areaRepository.findBySlug(slug)
                .orElseThrow(() -> new NaoEncontradoException("Área não encontrada: " + slug));


        List<Trilha> trilhas = trilhaRepository.findAllByAreaIdOrderByOrdemNaAreaAsc(area.getId());
        List<TrilhaResponse> trilhasResponse = new ArrayList<>();

        for (Trilha trilha : trilhas) {
            long duracao = etapaRepository.somarSemanasPorTrilha(trilha.getId());
            TrilhaResponse trilhaResponse = new TrilhaResponse(trilha.getSlug(), trilha.getNome(), trilha.getResumo(), trilha.getNivelEntrada(), duracao);
            trilhasResponse.add(trilhaResponse);
        }

        return new AreaDetalheResponse(area.getSlug(), area.getNumero(), area.getNome(), area.getDescricao(), area.getComoETrabalhar(), trilhasResponse);
    }
}
