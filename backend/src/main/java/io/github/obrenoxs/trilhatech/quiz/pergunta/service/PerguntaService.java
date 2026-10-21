package io.github.obrenoxs.trilhatech.quiz.pergunta.service;

import io.github.obrenoxs.trilhatech.quiz.pergunta.dto.response.AlternativaResponse;
import io.github.obrenoxs.trilhatech.quiz.pergunta.dto.response.PerguntaResponse;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Alternativa;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Pergunta;
import io.github.obrenoxs.trilhatech.quiz.pergunta.repository.AlternativaRepository;
import io.github.obrenoxs.trilhatech.quiz.pergunta.repository.PerguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PerguntaService {

    private final PerguntaRepository perguntaRepository;
    private final AlternativaRepository alternativaRepository;

    public PerguntaService(PerguntaRepository perguntaRepository, AlternativaRepository alternativaRepository) {
        this.perguntaRepository = perguntaRepository;
        this.alternativaRepository = alternativaRepository;
    }

    @Transactional(readOnly = true)
    public List<PerguntaResponse> findAll() {
        List<Pergunta> perguntas = perguntaRepository.findAllByOrderByOrdemAsc();
        List<Alternativa> alternativas = alternativaRepository.findAllOrdenadas();

        Map<UUID, List<AlternativaResponse>> alternativasPorPergunta = new HashMap<>();

        for (Alternativa alternativa : alternativas) {
            AlternativaResponse resposta = new AlternativaResponse(
                    alternativa.getOrdem(),
                    alternativa.getTexto()
            );
            alternativasPorPergunta
                    .computeIfAbsent(alternativa.getPergunta().getId(), id -> new ArrayList<>())
                    .add(resposta);
        }

        List<PerguntaResponse> perguntasResponse = new ArrayList<>();

        for (Pergunta pergunta : perguntas) {
            List<AlternativaResponse> daPergunta = alternativasPorPergunta.getOrDefault(pergunta.getId(), List.of());
            perguntasResponse.add(new PerguntaResponse(
                    pergunta.getOrdem(),
                    pergunta.getTexto(),
                    daPergunta
            ));
        }

        return perguntasResponse;
    }
}
