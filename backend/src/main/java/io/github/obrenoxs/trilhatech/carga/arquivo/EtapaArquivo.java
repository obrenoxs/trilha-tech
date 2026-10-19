package io.github.obrenoxs.trilhatech.carga.arquivo;

import java.util.List;

public record EtapaArquivo(
        Integer ordem,
        String titulo,
        String nivel,
        Integer duracaoSemanas,
        String descricao,
        String projetoPratico,
        List<RecursoArquivo> recursos
) {}
