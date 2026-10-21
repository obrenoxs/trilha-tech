package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.ConteudoCarregado;
import io.github.obrenoxs.trilhatech.carga.leitura.LeitorConteudo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidadorConteudoTest {

    @Test
    void deveCarregarEValidarOConteudoReal() {
        ValidadorConteudo validador = new ValidadorConteudo(
                new LeitorConteudo(),
                new ValidadorCatalogo(),
                new ValidadorTrilha(),
                new ValidadorConsistencia(),
                new ValidadorQuiz()
        );

        ConteudoCarregado conteudo = validador.carregarEValidar();

        assertThat(conteudo.catalogo().areas()).hasSize(8);
        assertThat(conteudo.trilhas()).hasSize(36);
        assertThat(conteudo.quiz().perguntas()).hasSize(12);
    }
}