package io.github.obrenoxs.trilhatech.carga.leitura;

import io.github.obrenoxs.trilhatech.carga.arquivo.*;
import io.github.obrenoxs.trilhatech.carga.exception.FalhaLeituraException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LeitorConteudoTest {

    @Test
    void deveLerConteudoCompleto() {
        LeitorConteudo leitor = new LeitorConteudo();

        CatalogoArquivo arquivo = leitor.lerCatalogo();

        assertThat(arquivo.areas()).hasSize(8);

        int totalTrilhas = arquivo.areas().stream()
                .mapToInt(areas -> areas.trilhas().size())
                .sum();
        assertThat(totalTrilhas).isEqualTo(36);

        assertThat(arquivo.areas())
                .extracting(AreaCatalogoArquivo::numero)
                .containsExactly("01", "02", "03", "04", "05", "06", "07", "08");
    }

    @Test
    void deveLerTodasAsTrilhasDoCatalogo() {
        LeitorConteudo leitor = new LeitorConteudo();

        CatalogoArquivo arquivo = leitor.lerCatalogo();

        for (AreaCatalogoArquivo area : arquivo.areas()) {
            for (TrilhaCatalogoArquivo trilha : area.trilhas()) {
                TrilhaArquivo trilhaArquivo = leitor.lerTrilha(trilha.slug());
                assertThat(trilha.slug()).isEqualTo(trilhaArquivo.trilha().slug());
            }
        }
    }

    @Test
    void deveFalharQuandoTrilhaNaoExiste() {
        LeitorConteudo leitor = new LeitorConteudo();

        assertThatThrownBy(() -> leitor.lerTrilha("nao-existe"))
                .isInstanceOf(FalhaLeituraException.class)
                .hasMessageContaining("nao-existe");
    }

    @Test
    void deveLerQuizCompleto() {
        LeitorConteudo leitor = new LeitorConteudo();

        QuizArquivo quiz = leitor.lerQuiz();

        assertThat(quiz.perguntas()).hasSize(12);
        assertThat(quiz.perguntas())
                .extracting(PerguntaArquivo::ordem)
                .containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
    }

    @Test
    void deveLerQuatroAlternativasEmCadaPergunta() {
        LeitorConteudo leitor = new LeitorConteudo();

        QuizArquivo quiz = leitor.lerQuiz();

        for (PerguntaArquivo pergunta : quiz.perguntas()) {
            assertThat(pergunta.alternativas()).hasSize(4);
            assertThat(pergunta.alternativas())
                    .extracting(AlternativaArquivo::ordem)
                    .containsExactly(1, 2, 3, 4);
        }
    }

    @Test
    void deveLerOsCamposDeCadaAlternativa() {
        LeitorConteudo leitor = new LeitorConteudo();

        QuizArquivo quiz = leitor.lerQuiz();

        for (PerguntaArquivo pergunta : quiz.perguntas()) {
            assertThat(pergunta.texto()).isNotBlank();

            for (AlternativaArquivo alternativa : pergunta.alternativas()) {
                assertThat(alternativa.texto()).isNotBlank();
                assertThat(alternativa.areaPrincipal()).isNotBlank();
                assertThat(alternativa.areaSecundaria()).isNotBlank();
            }
        }
    }
}
