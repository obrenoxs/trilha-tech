package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record QuizArquivo(
        String versao,
        List<PerguntaArquivo> perguntas
) {}
