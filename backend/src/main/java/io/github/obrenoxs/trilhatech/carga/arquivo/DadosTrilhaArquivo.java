package io.github.obrenoxs.trilhatech.carga.arquivo;

public record DadosTrilhaArquivo(
        String slug,
        String nome,
        String resumo,
        AreaTrilhaArquivo area,
        String nivelEntrada,
        String textoTrilha,
        String paraQuemE,
        Integer horasPorSemana,
        Integer duracaoTotalSemanas
) {}
