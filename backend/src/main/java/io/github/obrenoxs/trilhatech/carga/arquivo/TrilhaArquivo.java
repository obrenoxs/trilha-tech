package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record TrilhaArquivo(
        String versao,
        String geradoEm,
        DadosTrilhaArquivo trilha,
        List<EtapaArquivo> etapas,
        List<FaixaSalarialArquivo> faixaSalarial,
        String avisosSalario,
        List<String> pendencias
) {}
