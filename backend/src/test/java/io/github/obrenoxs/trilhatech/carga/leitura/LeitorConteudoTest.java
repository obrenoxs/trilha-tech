package io.github.obrenoxs.trilhatech.carga.leitura;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.CatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaCatalogoArquivo;
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
}
