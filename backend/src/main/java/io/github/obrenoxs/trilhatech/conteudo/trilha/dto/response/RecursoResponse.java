package io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.Acesso;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.Idioma;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.TipoRecurso;

import java.time.LocalDate;

public record RecursoResponse(
        int ordem,
        String titulo,
        String url,
        TipoRecurso tipo,
        Idioma idioma,
        Acesso acesso,
        String observacao,
        LocalDate verificadoEm
) {}
