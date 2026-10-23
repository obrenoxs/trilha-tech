package io.github.obrenoxs.trilhatech.assistente.chat.prompt;

import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaDetalheResponse;

import java.util.List;

public record ContextoPrompt(
        List<AreaResponse> areas,
        AreaDetalheResponse areaAberta,
        TrilhaDetalheResponse trilhaAberta,
        List<String> areasQuiz
) {}
