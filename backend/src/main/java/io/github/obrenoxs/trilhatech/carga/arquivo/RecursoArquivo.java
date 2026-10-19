package io.github.obrenoxs.trilhatech.carga.arquivo;

public record RecursoArquivo(
        String titulo,
        String url,
        String tipo,
        String idioma,
        Boolean gratuito,
        String acesso,
        String verificadoEm,
        String observacao
) {}
