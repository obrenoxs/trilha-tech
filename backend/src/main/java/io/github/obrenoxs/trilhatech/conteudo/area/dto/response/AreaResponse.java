package io.github.obrenoxs.trilhatech.conteudo.area.dto.response;

public record AreaResponse(
        String slug,
        String numero,
        String nome,
        String descricao,
        long quantidadeTrilhas
) {}
