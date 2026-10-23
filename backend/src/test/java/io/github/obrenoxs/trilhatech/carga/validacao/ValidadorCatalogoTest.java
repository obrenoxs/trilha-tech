package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.CatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaCatalogoArquivo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidadorCatalogoTest {

    private final ValidadorCatalogo validador = new ValidadorCatalogo();

    private int quantidadeDeTrilhas(int posicaoDaArea) {
        return posicaoDaArea <= 4 ? 5 : 4;
    }

    private TrilhaCatalogoArquivo trilha(int posicaoDaArea, int ordem) {
        return new TrilhaCatalogoArquivo(ordem, "trilha-" + posicaoDaArea + "-" + ordem, "Trilha", "Iniciante", 10);
    }

    private List<TrilhaCatalogoArquivo> trilhas(int posicaoDaArea, int quantidade) {
        List<TrilhaCatalogoArquivo> trilhas = new ArrayList<>();
        for (int j = 1; j <= quantidade; j++) {
            trilhas.add(trilha(posicaoDaArea, j));
        }
        return trilhas;
    }

    private AreaCatalogoArquivo areaCom(int posicao, String numero, String slug, String nome,
                                        Integer ordemDesempate, String descricao, String comoETrabalhar) {
        return new AreaCatalogoArquivo(numero, slug, nome, ordemDesempate, descricao, comoETrabalhar,
                trilhas(posicao, quantidadeDeTrilhas(posicao)));
    }

    private AreaCatalogoArquivo area(int posicao) {
        return areaCom(posicao, String.format("%02d", posicao), "area-" + posicao, "Area " + posicao,
                posicao, "descricao", "como e");
    }

    private List<AreaCatalogoArquivo> oitoAreas() {
        List<AreaCatalogoArquivo> areas = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            areas.add(area(i));
        }
        return areas;
    }

    private CatalogoArquivo catalogoCom(List<AreaCatalogoArquivo> areas) {
        return new CatalogoArquivo("1", areas);
    }

    private CatalogoArquivo catalogoComArea(int posicao, AreaCatalogoArquivo nova) {
        List<AreaCatalogoArquivo> areas = oitoAreas();
        areas.set(posicao - 1, nova);
        return catalogoCom(areas);
    }

    private CatalogoArquivo catalogoComTrilha(int posicaoDaArea, int posicaoDaTrilha, TrilhaCatalogoArquivo nova) {
        List<AreaCatalogoArquivo> areas = oitoAreas();
        AreaCatalogoArquivo original = areas.get(posicaoDaArea - 1);
        List<TrilhaCatalogoArquivo> trilhas = new ArrayList<>(original.trilhas());
        trilhas.set(posicaoDaTrilha - 1, nova);
        areas.set(posicaoDaArea - 1, new AreaCatalogoArquivo(original.numero(), original.slug(), original.nome(),
                original.ordemDesempate(), original.descricao(), original.comoETrabalhar(), trilhas));
        return catalogoCom(areas);
    }

    @Test
    void deveAceitarUmCatalogoValido() {
        assertThat(validador.validar(catalogoCom(oitoAreas()))).isEmpty();
    }

    @Test
    void deveRecusarUmCatalogoSemListaDeAreas() {
        assertThat(validador.validar(null))
                .containsExactly("O catálogo não tem a lista de áreas");
        assertThat(validador.validar(new CatalogoArquivo("1", null)))
                .containsExactly("O catálogo não tem a lista de áreas");
    }

    @Test
    void deveRecusarUmCatalogoSemOitoAreas() {
        List<AreaCatalogoArquivo> sete = oitoAreas().subList(0, 7);

        assertThat(validador.validar(catalogoCom(sete))).containsExactly(
                "Esperadas 8 áreas, mas vieram 7",
                "Esperados 36 trilhas, mas vieram 32");
    }

    @Test
    void deveRecusarUmCatalogoSemTrintaESeisTrilhas() {
        AreaCatalogoArquivo comQuatro = new AreaCatalogoArquivo("01", "area-1", "Area 1", 1,
                "descricao", "como e", trilhas(1, 4));

        assertThat(validador.validar(catalogoComArea(1, comQuatro)))
                .containsExactly("Esperados 36 trilhas, mas vieram 35");
    }

    @Test
    void deveRecusarUmaAreaNula() {
        assertThat(validador.validar(catalogoComArea(1, null))).containsExactly(
                "Área nº 1: está vazia",
                "Esperados 36 trilhas, mas vieram 31");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaSemNumero(String numero) {
        assertThat(validador.validar(catalogoComArea(1, areaCom(1, numero, "area-1", "Area 1", 1, "descricao", "como e"))))
                .containsExactly("Área nº 1: falta o número");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaSemNome(String nome) {
        assertThat(validador.validar(catalogoComArea(1, areaCom(1, "01", "area-1", nome, 1, "descricao", "como e"))))
                .containsExactly("Área nº 1: falta o nome");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaSemSlug(String slug) {
        assertThat(validador.validar(catalogoComArea(1, areaCom(1, "01", slug, "Area 1", 1, "descricao", "como e"))))
                .containsExactly("Área nº 1: falta o slug");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaSemDescricao(String descricao) {
        assertThat(validador.validar(catalogoComArea(1, areaCom(1, "01", "area-1", "Area 1", 1, descricao, "como e"))))
                .containsExactly("Área nº 1: falta a descrição");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaSemComoETrabalhar(String comoETrabalhar) {
        assertThat(validador.validar(catalogoComArea(1, areaCom(1, "01", "area-1", "Area 1", 1, "descricao", comoETrabalhar))))
                .containsExactly("Área nº 1: falta Como é Trabalhar");
    }

    @Test
    void deveRecusarAreaSemOrdemDeDesempate() {
        assertThat(validador.validar(catalogoComArea(1, areaCom(1, "01", "area-1", "Area 1", null, "descricao", "como e"))))
                .containsExactly("Área nº 1: falta a ordem de desempate");
    }

    @Test
    void deveRecusarSlugDeAreaRepetido() {
        assertThat(validador.validar(catalogoComArea(2, areaCom(2, "02", "area-1", "Area 2", 2, "descricao", "como e"))))
                .containsExactly("Área nº 2: o slug 'area-1' já foi usado");
    }

    @Test
    void deveRecusarNumeroDeAreaRepetido() {
        assertThat(validador.validar(catalogoComArea(2, areaCom(2, "01", "area-2", "Area 2", 2, "descricao", "como e"))))
                .containsExactly("Área nº 2: o número '01' já foi usado");
    }

    @Test
    void deveRecusarOrdemDeDesempateRepetida() {
        assertThat(validador.validar(catalogoComArea(2, areaCom(2, "02", "area-2", "Area 2", 1, "descricao", "como e"))))
                .containsExactly("Área nº 2: a ordem de desempate 1 já foi usada");
    }

    @Test
    void deveRecusarAreaSemTrilhas() {
        AreaCatalogoArquivo semLista = new AreaCatalogoArquivo("01", "area-1", "Area 1", 1, "descricao", "como e", null);
        AreaCatalogoArquivo semItens = new AreaCatalogoArquivo("01", "area-1", "Area 1", 1, "descricao", "como e", List.of());

        assertThat(validador.validar(catalogoComArea(1, semLista))).containsExactly(
                "Área nº 1: não tem trilhas",
                "Esperados 36 trilhas, mas vieram 31");
        assertThat(validador.validar(catalogoComArea(1, semItens))).containsExactly(
                "Área nº 1: não tem trilhas",
                "Esperados 36 trilhas, mas vieram 31");
    }

    @Test
    void deveRecusarUmaTrilhaNula() {
        assertThat(validador.validar(catalogoComTrilha(1, 1, null)))
                .containsExactly("Área nº 1, trilha nº 1: está vazia");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarTrilhaSemSlug(String slug) {
        TrilhaCatalogoArquivo trilha = new TrilhaCatalogoArquivo(1, slug, "Trilha", "Iniciante", 10);

        assertThat(validador.validar(catalogoComTrilha(1, 1, trilha)))
                .containsExactly("Área nº 1, trilha nº 1: falta o slug");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarTrilhaSemNome(String nome) {
        TrilhaCatalogoArquivo trilha = new TrilhaCatalogoArquivo(1, "trilha-1-1", nome, "Iniciante", 10);

        assertThat(validador.validar(catalogoComTrilha(1, 1, trilha)))
                .containsExactly("Área nº 1, trilha nº 1: falta o nome");
    }

    @Test
    void deveRecusarTrilhaSemOrdem() {
        TrilhaCatalogoArquivo trilha = new TrilhaCatalogoArquivo(null, "trilha-1-1", "Trilha", "Iniciante", 10);

        assertThat(validador.validar(catalogoComTrilha(1, 1, trilha)))
                .containsExactly("Área nº 1, trilha nº 1: falta a ordem");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void deveRecusarTrilhaComDuracaoInvalida(Integer duracao) {
        TrilhaCatalogoArquivo trilha = new TrilhaCatalogoArquivo(1, "trilha-1-1", "Trilha", "Iniciante", duracao);

        assertThat(validador.validar(catalogoComTrilha(1, 1, trilha)))
                .containsExactly("Área nº 1, trilha nº 1: a duração total em semanas deve ser maior que zero");
    }

    @Test
    void deveRecusarTrilhaSemNivelDeEntrada() {
        TrilhaCatalogoArquivo trilha = new TrilhaCatalogoArquivo(1, "trilha-1-1", "Trilha", null, 10);

        assertThat(validador.validar(catalogoComTrilha(1, 1, trilha)))
                .containsExactly("Área nº 1, trilha nº 1: Nível de entrada não informado");
    }

    @Test
    void deveRecusarTrilhaComNivelDeEntradaDesconhecido() {
        TrilhaCatalogoArquivo trilha = new TrilhaCatalogoArquivo(1, "trilha-1-1", "Trilha", "Expert", 10);

        assertThat(validador.validar(catalogoComTrilha(1, 1, trilha)))
                .containsExactly("Área nº 1, trilha nº 1: Nível de entrada desconhecido: Expert");
    }

    @Test
    void deveRecusarSlugDeTrilhaRepetidoNaMesmaArea() {
        TrilhaCatalogoArquivo repetida = new TrilhaCatalogoArquivo(2, "trilha-1-1", "Trilha", "Iniciante", 10);

        assertThat(validador.validar(catalogoComTrilha(1, 2, repetida)))
                .containsExactly("Área nº 1, trilha nº 2: o slug 'trilha-1-1' já foi usado");
    }

    @Test
    void deveRecusarSlugDeTrilhaRepetidoEntreAreas() {
        TrilhaCatalogoArquivo repetida = new TrilhaCatalogoArquivo(1, "trilha-1-1", "Trilha", "Iniciante", 10);

        assertThat(validador.validar(catalogoComTrilha(2, 1, repetida)))
                .containsExactly("Área nº 2, trilha nº 1: o slug 'trilha-1-1' já foi usado");
    }

    @Test
    void deveRecusarOrdemDeTrilhaRepetidaNaMesmaArea() {
        TrilhaCatalogoArquivo repetida = new TrilhaCatalogoArquivo(1, "trilha-1-2", "Trilha", "Iniciante", 10);

        assertThat(validador.validar(catalogoComTrilha(1, 2, repetida)))
                .containsExactly("Área nº 1, trilha nº 2: a ordem 1 já foi usada nesta área");
    }

    @Test
    void deveRelatarTodosOsProblemasJuntos() {
        List<AreaCatalogoArquivo> areas = oitoAreas();
        areas.set(0, areaCom(1, "01", "area-1", null, 1, "descricao", "como e"));
        areas.set(2, areaCom(3, "03", "area-3", "Area 3", 3, null, "como e"));

        assertThat(validador.validar(catalogoCom(areas))).containsExactly(
                "Área nº 1: falta o nome",
                "Área nº 3: falta a descrição");
    }
}
