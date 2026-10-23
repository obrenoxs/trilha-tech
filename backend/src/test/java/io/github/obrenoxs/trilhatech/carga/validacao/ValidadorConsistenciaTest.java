package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.AreaTrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.DadosTrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.tests.FabricaConteudo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidadorConsistenciaTest {

    private static final String LOCAL = "Trilha 'trilha-1'";

    private final ValidadorConsistencia validador = new ValidadorConsistencia();

    private final AreaCatalogoArquivo area = new AreaCatalogoArquivo("01", "area-1", "Area 1", 1,
            "descricao", "como e", List.of());

    private final TrilhaCatalogoArquivo trilhaCatalogo = new TrilhaCatalogoArquivo(1, "trilha-1", "Nome",
            "Iniciante", 10);

    private AreaTrilhaArquivo areaIgual() {
        return FabricaConteudo.areaDaTrilha("01", "area-1", "Area 1");
    }

    private TrilhaArquivo arquivo(String slug, String nome, String nivelEntrada, Integer duracao, AreaTrilhaArquivo areaDoArquivo) {
        DadosTrilhaArquivo dados = new DadosTrilhaArquivo(slug, nome, "resumo", areaDoArquivo, nivelEntrada,
                "texto", "para quem", 8, duracao);
        return new TrilhaArquivo("1", "2026-10-16", dados, List.of(), List.of(), "aviso", List.of());
    }

    private TrilhaArquivo consistente() {
        return arquivo("trilha-1", "Nome", "Iniciante", 10, areaIgual());
    }

    private List<String> validar(TrilhaArquivo arquivo) {
        return validador.validar(area, trilhaCatalogo, arquivo);
    }

    @Test
    void deveAceitarUmArquivoConsistenteComOCatalogo() {
        assertThat(validar(consistente())).isEmpty();
    }

    @Test
    void naoDeveReclamarQuandoOArquivoNaoTemOsDadosDaTrilha() {
        TrilhaArquivo semDados = new TrilhaArquivo("1", "2026-10-16", null, List.of(), List.of(), "aviso", List.of());

        assertThat(validar(null)).isEmpty();
        assertThat(validar(semDados)).isEmpty();
    }

    @Test
    void naoDeveCompararAAreaQuandoOArquivoNaoTemOsDadosDaArea() {
        assertThat(validar(arquivo("trilha-1", "Nome", "Iniciante", 10, null))).isEmpty();
    }

    @Test
    void deveApontarSlugDiferente() {
        assertThat(validar(arquivo("outra", "Nome", "Iniciante", 10, areaIgual())))
                .containsExactly(LOCAL + ": slug diferente (catálogo: 'trilha-1', arquivo: 'outra')");
    }

    @Test
    void deveApontarNomeDiferente() {
        assertThat(validar(arquivo("trilha-1", "Outro", "Iniciante", 10, areaIgual())))
                .containsExactly(LOCAL + ": nome diferente (catálogo: 'Nome', arquivo: 'Outro')");
    }

    @Test
    void deveApontarNivelDeEntradaDiferente() {
        assertThat(validar(arquivo("trilha-1", "Nome", "Avançado", 10, areaIgual())))
                .containsExactly(LOCAL + ": nível de entrada diferente (catálogo: 'Iniciante', arquivo: 'Avançado')");
    }

    @Test
    void deveApontarDuracaoTotalDiferente() {
        assertThat(validar(arquivo("trilha-1", "Nome", "Iniciante", 12, areaIgual())))
                .containsExactly(LOCAL + ": duração total em semanas diferente (catálogo: '10', arquivo: '12')");
    }

    @Test
    void deveApontarCadaCampoDaAreaQueDiverge() {
        AreaTrilhaArquivo outraArea = FabricaConteudo.areaDaTrilha("09", "area-9", "Area 9");

        assertThat(validar(arquivo("trilha-1", "Nome", "Iniciante", 10, outraArea))).containsExactly(
                LOCAL + ": slug da área diferente (catálogo: 'area-1', arquivo: 'area-9')",
                LOCAL + ": número da área diferente (catálogo: '01', arquivo: '09')",
                LOCAL + ": nome da área diferente (catálogo: 'Area 1', arquivo: 'Area 9')");
    }

    @Test
    void deveMostrarNullQuandoUmDosLadosNaoTemOValor() {
        TrilhaCatalogoArquivo semNome = new TrilhaCatalogoArquivo(1, "trilha-1", null, "Iniciante", 10);

        assertThat(validador.validar(area, semNome, consistente()))
                .containsExactly(LOCAL + ": nome diferente (catálogo: 'null', arquivo: 'Nome')");
    }

    @Test
    void deveRelatarTodasAsDivergenciasJuntasNaOrdemDosCampos() {
        assertThat(validar(arquivo("outra", "Outro", "Iniciante", 10, areaIgual()))).containsExactly(
                LOCAL + ": slug diferente (catálogo: 'trilha-1', arquivo: 'outra')",
                LOCAL + ": nome diferente (catálogo: 'Nome', arquivo: 'Outro')");
    }
}
