package io.github.obrenoxs.trilhatech.assistente.chat.service;

import io.github.obrenoxs.trilhatech.assistente.chat.dto.request.ChatRequest;
import io.github.obrenoxs.trilhatech.assistente.chat.dto.request.ContextoRequest;
import io.github.obrenoxs.trilhatech.assistente.chat.dto.request.MensagemHistoricoRequest;
import io.github.obrenoxs.trilhatech.assistente.chat.dto.response.ChatResponse;
import io.github.obrenoxs.trilhatech.assistente.chat.prompt.ContextoPrompt;
import io.github.obrenoxs.trilhatech.assistente.chat.prompt.CriadorPrompt;
import io.github.obrenoxs.trilhatech.assistente.cliente.ClienteIa;
import io.github.obrenoxs.trilhatech.assistente.cliente.MensagemIa;
import io.github.obrenoxs.trilhatech.assistente.cliente.PapelMensagem;
import io.github.obrenoxs.trilhatech.assistente.uso.service.ControleDeUsoService;
import io.github.obrenoxs.trilhatech.assistente.uso.service.ResultadoLimite;
import io.github.obrenoxs.trilhatech.compartilhado.exception.IaIndisponivelException;
import io.github.obrenoxs.trilhatech.compartilhado.exception.LimiteUsoExcedidoException;
import io.github.obrenoxs.trilhatech.compartilhado.exception.NaoEncontradoException;
import io.github.obrenoxs.trilhatech.compartilhado.exception.RequisicaoInvalidaException;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.service.AreaService;
import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.TrilhaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.service.TrilhaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssistenteService {

    private static final int TAMANHO_MAXIMO_DO_HISTORICO = 6;
    private static final int TAMANHO_MAXIMO_DE_UMA_MENSAGEM_DO_HISTORICO = 2000;

    private final ControleDeUsoService controleDeUso;
    private final AreaService areaService;
    private final TrilhaService trilhaService;
    private final CriadorPrompt criadorPrompt;
    private final ClienteIa clienteIa;

    public AssistenteService(
            ControleDeUsoService controleDeUso,
            AreaService areaService,
            TrilhaService trilhaService,
            CriadorPrompt criadorPrompt,
            ClienteIa clienteIa
    ) {
        this.controleDeUso = controleDeUso;
        this.areaService = areaService;
        this.trilhaService = trilhaService;
        this.criadorPrompt = criadorPrompt;
        this.clienteIa = clienteIa;
    }

    public ChatResponse responder(ChatRequest requisicao, String visitante) {
        reservarUso(visitante);

        ContextoPrompt contexto = montarContexto(requisicao.contexto());
        String prompt = criadorPrompt.montar(contexto);
        List<MensagemIa> conversa = montarConversa(requisicao.historico(), requisicao.mensagem().trim());

        String resposta = clienteIa.responder(prompt, conversa);
        return new ChatResponse(resposta);
    }

    private void reservarUso(String visitante) {
        ResultadoLimite resultado = controleDeUso.reservar(visitante);
        switch (resultado) {
            case PERMITIDO -> { }
            case VISITANTE_POR_MINUTO ->
                    throw new LimiteUsoExcedidoException("Limite por minuto do visitante atingido", 60);
            case VISITANTE_POR_HORA ->
                    throw new LimiteUsoExcedidoException("Limite por hora do visitante atingido", 3600);
            case GLOBAL_POR_MINUTO ->
                    throw new IaIndisponivelException("Limite global por minuto atingido");
            case TETO_DIARIO ->
                    throw new IaIndisponivelException("Teto diário de mensagens atingido");
        }
    }

    private ContextoPrompt montarContexto(@Valid ContextoRequest contexto) {
        List<AreaResponse> areas = areaService.findAll();

        String areaSlug = contexto == null ? null : contexto.areaSlug();
        String trilhaSlug = contexto == null ? null : contexto.trilhaSlug();
        List<String> slugsDoQuiz = contexto == null ? null : contexto.resultadoQuiz();

        AreaDetalheResponse areaAberta = null;
        TrilhaDetalheResponse trilhaAberta = null;

        try {
            if (trilhaSlug != null) {
                trilhaAberta = trilhaService.findBySlug(trilhaSlug);
            } else if (areaSlug != null) {
                areaAberta = areaService.findBySlug(areaSlug);
            }
        } catch (NaoEncontradoException e) {
            throw new RequisicaoInvalidaException(List.of("O contexto da página não existe"));
        }

        return new ContextoPrompt(areas, areaAberta, trilhaAberta, nomesDasAreas(slugsDoQuiz, areas));
    }

    private List<String> nomesDasAreas(List<String> slugs, List<AreaResponse> areas) {
        if (slugs == null || slugs.isEmpty()) {
            return List.of();
        }

        Map<String, String> nomePorSlug = new HashMap<>();
        for (AreaResponse area : areas) {
            nomePorSlug.put(area.slug(), area.nome());
        }

        List<String> nomes = new ArrayList<>();
        for (String slug : slugs) {
            String nome = nomePorSlug.get(slug);
            if (nome == null) {
                throw new RequisicaoInvalidaException(List.of("O resultado do quiz tem uma área que não existe"));
            }
            nomes.add(nome);
        }
        return nomes;
    }

    private List<MensagemIa> montarConversa(@Size(max = 6) List<@NotNull @Valid MensagemHistoricoRequest> historico, String mensagem) {
        List<MensagemIa> conversa = new ArrayList<>();

        if (historico != null) {
            int inicio = Math.max(0, historico.size() - TAMANHO_MAXIMO_DO_HISTORICO);
            for (MensagemHistoricoRequest item : historico.subList(inicio, historico.size())) {
                if (conversa.isEmpty() && item.papel() == PapelMensagem.ASSISTENTE) {
                    continue;
                }
                conversa.add(new MensagemIa(item.papel(), cortar(item.texto())));
            }
        }

        conversa.add(new MensagemIa(PapelMensagem.USUARIO, mensagem));
        return conversa;
    }

    private String cortar(String texto) {
        if (texto.length() <= TAMANHO_MAXIMO_DE_UMA_MENSAGEM_DO_HISTORICO) {
            return texto;
        }
        return texto.substring(0, TAMANHO_MAXIMO_DE_UMA_MENSAGEM_DO_HISTORICO);
    }
}
