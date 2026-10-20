package io.github.obrenoxs.trilhatech.conteudo.area.dto.response;

import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaResponse;

import java.util.List;

public record AreaDetalheResponse(
        String slug,
        String numero,
        String nome,
        String descricao,
        String comoETrabalhar,
        List<TrilhaResponse> trilhas
) {}
