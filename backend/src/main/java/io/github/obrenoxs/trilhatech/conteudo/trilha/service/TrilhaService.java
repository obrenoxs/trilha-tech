package io.github.obrenoxs.trilhatech.conteudo.trilha.service;

import io.github.obrenoxs.trilhatech.compartilhado.exception.NaoEncontradoException;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.*;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Etapa;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.FaixaSalarial;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Recurso;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Trilha;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.EtapaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.FaixaSalarialRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.RecursoRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.TrilhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;

@Service
public class TrilhaService {

    private final TrilhaRepository trilhaRepository;
    private final EtapaRepository etapaRepository;
    private final RecursoRepository recursoRepository;
    private final FaixaSalarialRepository faixaSalarialRepository;

    public TrilhaService(TrilhaRepository trilhaRepository, EtapaRepository etapaRepository, RecursoRepository recursoRepository, FaixaSalarialRepository faixaSalarialRepository) {
        this.trilhaRepository = trilhaRepository;
        this.etapaRepository = etapaRepository;
        this.recursoRepository = recursoRepository;
        this.faixaSalarialRepository = faixaSalarialRepository;
    }

    @Transactional(readOnly = true)
    public TrilhaDetalheResponse findBySlug(String slug) {
        Trilha trilha = trilhaRepository.findBySlug(slug)
                .orElseThrow(() -> new NaoEncontradoException("Trilha não encontrada: " + slug));

        List<Etapa> etapas = etapaRepository.findAllByTrilhaIdOrderByOrdemAsc(trilha.getId());

        Map<UUID, List<RecursoResponse>> recursosPorEtapa = new HashMap<>();

        for (Recurso recurso : recursoRepository.findAllByTrilhaId(trilha.getId())) {
            RecursoResponse resposta = new RecursoResponse(
                    recurso.getOrdem(),
                    recurso.getTitulo(),
                    recurso.getUrl(),
                    recurso.getTipo(),
                    recurso.getIdioma(),
                    recurso.getAcesso(),
                    recurso.getObservacao(),
                    recurso.getVerificadoEm()
            );
            recursosPorEtapa
                    .computeIfAbsent(recurso.getEtapa().getId(), id -> new ArrayList<>())
                    .add(resposta);
        }

        List<EtapaResponse> etapasResponse = new ArrayList<>();
        long duracaoTotal = 0;

        for (Etapa etapa : etapas) {
            List<RecursoResponse> recursos = recursosPorEtapa.getOrDefault(etapa.getId(), List.of());

            etapasResponse.add(new EtapaResponse(
                    etapa.getOrdem(),
                    etapa.getTitulo(),
                    etapa.getNivel(),
                    etapa.getDuracaoSemanas(),
                    etapa.getDescricao(),
                    etapa.getProjetoPratico(),
                    recursos
            ));

            duracaoTotal += etapa.getDuracaoSemanas();
        }

        List<FaixaSalarial> faixas = new ArrayList<>(faixaSalarialRepository.findAllByTrilhaId(trilha.getId()));
        faixas.sort(Comparator.comparing(FaixaSalarial::getNivel));

        List<FaixaSalarialResponse> faixasResponse = new ArrayList<>();
        for (FaixaSalarial faixa : faixas) {
            faixasResponse.add(new FaixaSalarialResponse(
                    faixa.getNivel(),
                    faixa.getMinimo(),
                    faixa.getMaximo(),
                    faixa.getValorReferencia(),
                    faixa.getTipoReferencia(),
                    faixa.getFonte(),
                    YearMonth.from(faixa.getDataReferencia()),
                    faixa.isDadosLimitados(),
                    faixa.getObservacao()
            ));
        }

        Area area = trilha.getArea();
        AreaTrilhaResponse areaResponse = new AreaTrilhaResponse(area.getSlug(), area.getNumero(), area.getNome());

        return new TrilhaDetalheResponse(
                trilha.getSlug(),
                trilha.getNome(),
                trilha.getResumo(),
                areaResponse,
                trilha.getNivelEntrada(),
                trilha.getTextoTrilha(),
                trilha.getParaQuemE(),
                trilha.getHorasPorSemana(),
                duracaoTotal,
                trilha.getAvisosSalario(),
                trilha.getConteudoConferidoEm(),
                etapasResponse,
                faixasResponse
        );
    }
}
