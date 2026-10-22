package io.github.obrenoxs.trilhatech.quiz.resultado.service;

import io.github.obrenoxs.trilhatech.compartilhado.exception.RequisicaoInvalidaException;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Alternativa;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Pergunta;
import io.github.obrenoxs.trilhatech.quiz.pergunta.repository.AlternativaRepository;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.CalculadoraPontuacao;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.request.RespostaRequest;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.request.ResultadoRequest;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.response.RecomendacaoResponse;
import io.github.obrenoxs.trilhatech.quiz.resultado.dto.response.ResultadoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultadoServiceTest {

    @Mock
    private AlternativaRepository alternativaRepository;

    @Mock
    private AreaRepository areaRepository;

    private ResultadoService service;

    @BeforeEach
    void preparar() {
        service = new ResultadoService(alternativaRepository, areaRepository, new CalculadoraPontuacao());
    }

    @Test
    void deveCalcularORankingQuandoAsRespostasSaoValidas() {
        prepararQuizPequeno();
        ResultadoRequest request = new ResultadoRequest(List.of(
                new RespostaRequest(1, 1),
                new RespostaRequest(2, 1)
        ));

        ResultadoResponse resultado = service.calcular(request);

        assertThat(resultado.recomendacoes()).extracting(RecomendacaoResponse::slug)
                .containsExactly("a", "c", "b");
        assertThat(resultado.recomendacoes()).extracting(RecomendacaoResponse::nome)
                .containsExactly("Area A", "Area C", "Area B");
        assertThat(resultado.recomendacoes()).extracting(RecomendacaoResponse::barra)
                .containsExactly(100, 67, 33);
    }

    @Test
    void deveRecusarPerguntaQueNaoExiste() {
        prepararQuizPequeno();
        ResultadoRequest request = new ResultadoRequest(List.of(
                new RespostaRequest(1, 1),
                new RespostaRequest(5, 1)
        ));

        RequisicaoInvalidaException ex = assertThrows(RequisicaoInvalidaException.class,
                () -> service.calcular(request));

        assertThat(ex.getProblemas()).containsExactly(
                "A pergunta 5 não existe",
                "Falta responder a pergunta 2"
        );
    }

    @Test
    void deveRecusarPerguntaRespondidaMaisDeUmaVez() {
        prepararQuizPequeno();
        ResultadoRequest request = new ResultadoRequest(List.of(
                new RespostaRequest(1, 1),
                new RespostaRequest(1, 2)
        ));

        RequisicaoInvalidaException ex = assertThrows(RequisicaoInvalidaException.class,
                () -> service.calcular(request));

        assertThat(ex.getProblemas()).containsExactly(
                "A pergunta 1 foi respondida mais de uma vez",
                "Falta responder a pergunta 2"
        );
    }

    @Test
    void deveRecusarAlternativaQueNaoExiste() {
        prepararQuizPequeno();
        ResultadoRequest request = new ResultadoRequest(List.of(
                new RespostaRequest(1, 9),
                new RespostaRequest(2, 1)
        ));

        RequisicaoInvalidaException ex = assertThrows(RequisicaoInvalidaException.class,
                () -> service.calcular(request));

        assertThat(ex.getProblemas()).containsExactly(
                "A alternativa 9 não existe na pergunta 1"
        );
    }

    @Test
    void deveRecusarQuandoFaltaResponderUmaPergunta() {
        prepararQuizPequeno();
        ResultadoRequest request = new ResultadoRequest(List.of(
                new RespostaRequest(1, 1)
        ));

        RequisicaoInvalidaException ex = assertThrows(RequisicaoInvalidaException.class,
                () -> service.calcular(request));

        assertThat(ex.getProblemas()).containsExactly(
                "Falta responder a pergunta 2"
        );
    }

    private void prepararQuizPequeno() {
        Area a = new Area("a", "01", "Area A", "d", "c", 1, 1);
        Area b = new Area("b", "02", "Area B", "d", "c", 2, 2);
        Area c = new Area("c", "03", "Area C", "d", "c", 3, 3);

        Pergunta p1 = new Pergunta(1, "P1");
        Pergunta p2 = new Pergunta(2, "P2");

        List<Alternativa> alternativas = List.of(
                new Alternativa(p1, 1, "p1a1", a, b),
                new Alternativa(p1, 2, "p1a2", b, c),
                new Alternativa(p2, 1, "p2a1", c, a),
                new Alternativa(p2, 2, "p2a2", a, c)
        );

        when(alternativaRepository.findAllComAreas()).thenReturn(alternativas);
        lenient().when(areaRepository.findAll()).thenReturn(List.of(a, b, c));
    }
}
