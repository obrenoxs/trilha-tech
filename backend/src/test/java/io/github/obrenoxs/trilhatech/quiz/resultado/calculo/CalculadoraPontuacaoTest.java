package io.github.obrenoxs.trilhatech.quiz.resultado.calculo;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculadoraPontuacaoTest {

    private final CalculadoraPontuacao calculadora = new CalculadoraPontuacao();

    @Test
    void devePontuarDoisParaPrincipalEUmParaSecundaria() {
        Map<String, Integer> ordens = Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
        List<Escolha> escolhas = List.of(new Escolha("a", "b"));

        List<Recomendacao> resultado = calculadora.calcular(escolhas, ordens);

        assertThat(resultado).extracting(Recomendacao::posicao).containsExactly(1, 2, 3);
        assertThat(resultado).extracting(Recomendacao::slug).containsExactly("a", "b", "c");
        assertThat(resultado).extracting(Recomendacao::barra).containsExactly(100, 50, 0);
    }

    @Test
    void deveDesempatarPorVezesComoPrincipalAntesDaOrdemFixa() {
        Map<String, Integer> ordens = Map.of("d", 1, "a", 2, "b", 3, "c", 4, "e", 5);
        List<Escolha> escolhas = List.of(
                new Escolha("a", "d"),
                new Escolha("b", "d")
        );

        List<Recomendacao> resultado = calculadora.calcular(escolhas, ordens);

        assertThat(resultado).extracting(Recomendacao::slug).containsExactly("a", "b", "d");
        assertThat(resultado).extracting(Recomendacao::barra).containsExactly(100, 100, 100);
    }

    @Test
    void deveDesempatarPelaOrdemFixaTambemNoTerceiroLugar() {
        Map<String, Integer> ordens = Map.of("b", 1, "a", 2, "c", 3, "d", 4, "e", 5);
        List<Escolha> escolhas = List.of(
                new Escolha("a", "c"),
                new Escolha("b", "d")
        );

        List<Recomendacao> resultado = calculadora.calcular(escolhas, ordens);

        assertThat(resultado).extracting(Recomendacao::posicao).containsExactly(1, 2, 3);
        assertThat(resultado).extracting(Recomendacao::slug).containsExactly("b", "a", "c");
        assertThat(resultado).extracting(Recomendacao::barra).containsExactly(100, 100, 50);
    }

    @Test
    void deveArredondarAsBarrasRelativasAoPrimeiro() {
        Map<String, Integer> ordens = Map.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5);
        List<Escolha> escolhas = List.of(
                new Escolha("a", "b"),
                new Escolha("a", "b"),
                new Escolha("a", "c"),
                new Escolha("c", "a")
        );

        List<Recomendacao> resultado = calculadora.calcular(escolhas, ordens);

        assertThat(resultado).extracting(Recomendacao::posicao).containsExactly(1, 2, 3);
        assertThat(resultado).extracting(Recomendacao::slug).containsExactly("a", "c", "b");
        assertThat(resultado).extracting(Recomendacao::barra).containsExactly(100, 43, 29);
    }

    @Test
    void deveDevolverBarrasZeroSemEscolhas() {
        Map<String, Integer> ordens = Map.of("d", 1, "a", 2, "b", 3, "c", 4, "e", 5);
        List<Escolha> escolhas = List.of();

        List<Recomendacao> resultado = calculadora.calcular(escolhas, ordens);

        assertThat(resultado).extracting(Recomendacao::posicao).containsExactly(1, 2, 3);
        assertThat(resultado).extracting(Recomendacao::slug).containsExactly("d", "a", "b");
        assertThat(resultado).extracting(Recomendacao::barra).containsExactly(0, 0, 0);
    }
}