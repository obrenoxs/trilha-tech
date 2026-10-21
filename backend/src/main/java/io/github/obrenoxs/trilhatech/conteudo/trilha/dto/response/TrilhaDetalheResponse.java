package io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelEntrada;

import java.time.LocalDate;
import java.util.List;

public record TrilhaDetalheResponse(
        String slug,
        String nome,
        String resumo,
        AreaTrilhaResponse area,
        NivelEntrada nivelEntrada,
        String textoTrilha,
        String paraQuemE,
        int horasPorSemana,
        long duracaoTotalSemanas,
        String avisosSalario,
        LocalDate conteudoConferidoEm,
        List<EtapaResponse> etapas,
        List<FaixaSalarialResponse> faixasSalariais
) {}
