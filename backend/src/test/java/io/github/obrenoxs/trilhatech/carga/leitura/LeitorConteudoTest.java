package io.github.obrenoxs.trilhatech.carga.leitura;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.CatalogoArquivo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LeitorConteudoTest {

    @Test
    void deveLerConteudoCompleto() {
        LeitorConteudo leitor = new LeitorConteudo();

        CatalogoArquivo arquivo = leitor.lerCatalogo();

        assertThat(arquivo.areas()).hasSize(8);

        int totalArquivos = arquivo.areas().stream()
                .mapToInt(areas -> areas.trilhas().size())
                .sum();
        assertThat(totalArquivos).isEqualTo(36);

        assertThat(arquivo.areas())
                .extracting(AreaCatalogoArquivo::numero)
                .containsExactly("01", "02", "03", "04", "05", "06", "07", "08");
    }
}
