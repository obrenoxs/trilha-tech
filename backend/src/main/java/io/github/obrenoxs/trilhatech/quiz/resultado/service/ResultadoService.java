package io.github.obrenoxs.trilhatech.quiz.resultado.service;

import io.github.obrenoxs.trilhatech.compartilhado.exception.RequisicaoInvalidaException;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Alternativa;
import io.github.obrenoxs.trilhatech.quiz.pergunta.repository.AlternativaRepository;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.CalculadoraPontuacao;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.Escolha;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.Recomendacao;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.request.RespostaRequest;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.request.ResultadoRequest;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.response.RecomendacaoResponse;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.response.ResultadoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ResultadoService {

    private final AlternativaRepository alternativaRepository;
    private final AreaRepository areaRepository;
    private final CalculadoraPontuacao calculadora;

    public ResultadoService(AlternativaRepository alternativaRepository,
                            AreaRepository areaRepository,
                            CalculadoraPontuacao calculadora) {
        this.alternativaRepository = alternativaRepository;
        this.areaRepository = areaRepository;
        this.calculadora = calculadora;
    }

    @Transactional(readOnly = true)
    public ResultadoResponse calcular(ResultadoRequest request) {
        List<Alternativa> alternativas = alternativaRepository.findAllComAreas();

        Map<Integer, Map<Integer, Alternativa>> alternativasPorPergunta = new HashMap<>();
        for (Alternativa alternativa : alternativas) {
            alternativasPorPergunta
                    .computeIfAbsent(alternativa.getPergunta().getOrdem(), ordem -> new HashMap<>())
                    .put(alternativa.getOrdem(), alternativa);
        }

        List<String> problemas = new ArrayList<>();
        Set<Integer> perguntasRespondidas = new HashSet<>();
        List<Escolha> escolhas = new ArrayList<>();

        for (RespostaRequest resposta : request.respostas()) {
            Map<Integer, Alternativa> daPergunta = alternativasPorPergunta.get(resposta.pergunta());

            if (daPergunta == null) {
                problemas.add("A pergunta " + resposta.pergunta() + " não existe");
                continue;
            }

            if (!perguntasRespondidas.add(resposta.pergunta())) {
                problemas.add("A pergunta " + resposta.pergunta() + " foi respondida mais de uma vez");
                continue;
            }

            Alternativa escolhida = daPergunta.get(resposta.alternativa());
            if (escolhida == null) {
                problemas.add("A alternativa " + resposta.alternativa()
                        + " não existe na pergunta " + resposta.pergunta());
                continue;
            }

            escolhas.add(new Escolha(
                    escolhida.getAreaPrincipal().getSlug(),
                    escolhida.getAreaSecundaria().getSlug()
            ));
        }

        for (Integer ordem : alternativasPorPergunta.keySet()) {
            if (!perguntasRespondidas.contains(ordem)) {
                problemas.add("Falta responder a pergunta " + ordem);
            }
        }

        if (!problemas.isEmpty()) {
            throw new RequisicaoInvalidaException(problemas);
        }

        Map<String, Integer> ordemDesempatePorArea = new HashMap<>();
        Map<String, String> nomePorArea = new HashMap<>();
        for (Area area : areaRepository.findAll()) {
            ordemDesempatePorArea.put(area.getSlug(), area.getOrdemDesempate());
            nomePorArea.put(area.getSlug(), area.getNome());
        }

        List<RecomendacaoResponse> recomendacoes = new ArrayList<>();
        for (Recomendacao recomendacao : calculadora.calcular(escolhas, ordemDesempatePorArea)) {
            recomendacoes.add(new RecomendacaoResponse(
                    recomendacao.posicao(),
                    recomendacao.slug(),
                    nomePorArea.get(recomendacao.slug()),
                    recomendacao.barra()
            ));
        }

        return new ResultadoResponse(recomendacoes);
    }
}