package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record PerguntaArquivo(
        Integer ordem,
        String texto,
        List<AlternativaArquivo> alternativas
) {}
