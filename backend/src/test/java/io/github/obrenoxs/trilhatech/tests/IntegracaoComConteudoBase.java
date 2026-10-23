package io.github.obrenoxs.trilhatech.tests;

import io.github.obrenoxs.trilhatech.carga.arquivo.ConteudoCarregado;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegracaoComConteudoBase extends IntegracaoBase {

    protected ConteudoCarregado conteudo;

    @BeforeAll
    protected void carregarOConteudoUmaVezPorClasse() {
        limparBanco();
        conteudo = carregarConteudoReal();
    }
}