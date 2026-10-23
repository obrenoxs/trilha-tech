package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AlternativaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.PerguntaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.QuizArquivo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidadorQuizTest {

    private static final Set<String> AREAS = Set.of("a", "b", "c");

    private final ValidadorQuiz validador = new ValidadorQuiz();

    private AlternativaArquivo alternativa(int ordem) {
        return new AlternativaArquivo(ordem, "texto", "a", "b");
    }

    private List<AlternativaArquivo> quatroAlternativas() {
        return List.of(alternativa(1), alternativa(2), alternativa(3), alternativa(4));
    }

    private List<PerguntaArquivo> dozePerguntas() {
        List<PerguntaArquivo> perguntas = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            perguntas.add(new PerguntaArquivo(i, "texto", quatroAlternativas()));
        }
        return perguntas;
    }

    private QuizArquivo quizCom(List<PerguntaArquivo> perguntas) {
        return new QuizArquivo("1", perguntas);
    }

    @Test
    void deveAceitarUmQuizValido() {
        assertThat(validador.validar(quizCom(dozePerguntas()), AREAS)).isEmpty();
    }

    @Test
    void deveRecusarUmaPerguntaComOrdemErrada() {
        List<PerguntaArquivo> perguntas = dozePerguntas();
        perguntas.set(2, new PerguntaArquivo(5, "texto", quatroAlternativas()));

        assertThat(validador.validar(quizCom(perguntas), AREAS))
                .containsExactly("Pergunta nº 3: a ordem deveria ser 3, mas é 5");
    }

    @Test
    void deveRecusarUmQuizNulo() {
        assertThat(validador.validar(null, AREAS))
                .containsExactly("O quiz não tem a lista de perguntas");
    }

    @Test
    void deveRecusarUmQuizSemListaDePerguntas() {
        assertThat(validador.validar(new QuizArquivo("1", null), AREAS))
                .containsExactly("O quiz não tem a lista de perguntas");
    }

    @Test
    void deveRecusarUmQuizSemVersao() {
        assertThat(validador.validar(new QuizArquivo("  ", dozePerguntas()), AREAS))
                .containsExactly("Quiz: falta a versão");
    }

    @Test
    void deveRecusarUmQuizSemDozePerguntas() {
        List<PerguntaArquivo> onze = dozePerguntas().subList(0, 11);

        assertThat(validador.validar(quizCom(onze), AREAS))
                .containsExactly("Esperadas 12 perguntas, mas vieram 11");
    }

    @Test
    void deveRecusarUmaPerguntaSemOrdem() {
        List<PerguntaArquivo> perguntas = dozePerguntas();
        perguntas.set(0, new PerguntaArquivo(null, "texto", quatroAlternativas()));

        assertThat(validador.validar(quizCom(perguntas), AREAS))
                .containsExactly("Pergunta nº 1: falta a ordem");
    }

    @Test
    void deveRecusarUmaPerguntaSemTexto() {
        List<PerguntaArquivo> perguntas = dozePerguntas();
        perguntas.set(1, new PerguntaArquivo(2, "  ", quatroAlternativas()));

        assertThat(validador.validar(quizCom(perguntas), AREAS))
                .containsExactly("Pergunta nº 2: falta o texto");
    }

    @Test
    void deveRecusarUmaPerguntaNula() {
        List<PerguntaArquivo> perguntas = dozePerguntas();
        perguntas.set(0, null);

        assertThat(validador.validar(quizCom(perguntas), AREAS))
                .containsExactly("Pergunta nº 1: está vazia");
    }

    @Test
    void deveRecusarUmaPerguntaSemAlternativas() {
        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(null), AREAS))
                .containsExactly("Pergunta nº 1: não tem alternativas");
    }

    @Test
    void deveRecusarUmaPerguntaSemQuatroAlternativas() {
        List<AlternativaArquivo> tres = List.of(alternativa(1), alternativa(2), alternativa(3));

        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(tres), AREAS))
                .containsExactly("Pergunta nº 1: esperadas 4 alternativas, mas vieram 3");
    }

    @Test
    void deveRecusarUmaAlternativaComOrdemErrada() {
        List<AlternativaArquivo> alternativas = List.of(
                alternativa(1), alternativa(7), alternativa(3), alternativa(4));

        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(alternativas), AREAS))
                .containsExactly("Pergunta nº 1, alternativa nº 2: a ordem deveria ser 2, mas é 7");
    }

    @Test
    void deveRecusarUmaAlternativaSemTexto() {
        List<AlternativaArquivo> alternativas = alternativasComAPrimeira(
                new AlternativaArquivo(1, "  ", "a", "b"));

        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(alternativas), AREAS))
                .containsExactly("Pergunta nº 1, alternativa nº 1: falta o texto");
    }

    @Test
    void deveRecusarUmaAreaPrincipalQueNaoExisteNoCatalogo() {
        List<AlternativaArquivo> alternativas = alternativasComAPrimeira(
                new AlternativaArquivo(1, "texto", "zzz", "b"));

        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(alternativas), AREAS))
                .containsExactly("Pergunta nº 1, alternativa nº 1: a área principal 'zzz' não existe no catálogo");
    }

    @Test
    void deveRecusarUmaAreaSecundariaEmBranco() {
        List<AlternativaArquivo> alternativas = alternativasComAPrimeira(
                new AlternativaArquivo(1, "texto", "a", "  "));

        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(alternativas), AREAS))
                .containsExactly("Pergunta nº 1, alternativa nº 1: falta a área secundária");
    }

    @Test
    void deveRecusarAreaPrincipalIgualASecundaria() {
        List<AlternativaArquivo> alternativas = alternativasComAPrimeira(
                new AlternativaArquivo(1, "texto", "a", "a"));

        assertThat(validador.validar(quizComAlternativasNaPrimeiraPergunta(alternativas), AREAS))
                .containsExactly("Pergunta nº 1, alternativa nº 1: a área principal e a secundária são a mesma ('a')");
    }

    @Test
    void deveRelatarTodosOsProblemasJuntos() {
        List<PerguntaArquivo> perguntas = dozePerguntas();
        perguntas.set(0, new PerguntaArquivo(9, "texto", quatroAlternativas()));
        perguntas.set(3, new PerguntaArquivo(4, "  ", quatroAlternativas()));

        assertThat(validador.validar(quizCom(perguntas), AREAS)).containsExactly(
                "Pergunta nº 1: a ordem deveria ser 1, mas é 9",
                "Pergunta nº 4: falta o texto");
    }

    private QuizArquivo quizComAlternativasNaPrimeiraPergunta(List<AlternativaArquivo> alternativas) {
        List<PerguntaArquivo> perguntas = dozePerguntas();
        perguntas.set(0, new PerguntaArquivo(1, "texto", alternativas));
        return quizCom(perguntas);
    }

    private List<AlternativaArquivo> alternativasComAPrimeira(AlternativaArquivo primeira) {
        return List.of(primeira, alternativa(2), alternativa(3), alternativa(4));
    }
}
