package io.github.obrenoxs.trilhatech.carga.conversao;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ConversorTextoTest {

    @ParameterizedTest
    @CsvSource({"Iniciante, INICIANTE", "Intermediário, INTERMEDIARIO", "Avançado, AVANCADO"})
    void deveTraduzirNivelEntrada(String texto, NivelEntrada esperado) {
        assertThat(ConversorTexto.traduzirNivelEntrada(texto)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({"Básico, BASICO", "Intermediário, INTERMEDIARIO", "Avançado, AVANCADO"})
    void deveTraduzirNivelEtapa(String texto, NivelEtapa esperado) {
        assertThat(ConversorTexto.traduzirNivelEtapa(texto)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({"Júnior, JUNIOR", "Pleno, PLENO", "Sênior, SENIOR"})
    void deveTraduzirNivelSalarial(String texto, NivelSalarial esperado) {
        assertThat(ConversorTexto.traduzirNivelSalarial(texto)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({"PT, PT", "EN, EN"})
    void deveTraduzirIdioma(String texto, Idioma esperado) {
        assertThat(ConversorTexto.traduzirIdioma(texto)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({"gratuito, GRATUITO", "pago, PAGO", "nao_confirmado, NAO_CONFIRMADO"})
    void deveTraduzirAcesso(String texto, Acesso esperado) {
        assertThat(ConversorTexto.traduzirAcesso(texto)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @CsvSource({
            "documentação, DOCUMENTACAO", "curso, CURSO", "livro, LIVRO",
            "formação, FORMACAO", "certificação, CERTIFICACAO", "tutorial, TUTORIAL",
            "guia, GUIA", "vídeo, VIDEO", "ferramenta, FERRAMENTA"})
    void deveTraduzirTipoRecurso(String texto, TipoRecurso esperado) {
        assertThat(ConversorTexto.traduzirTipoRecurso(texto)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"iniciante", "INICIANTE", " Iniciante", "Iniciante ", "Intermediario"})
    void deveSerRigorosoComMaiusculasEspacosEAcentos(String texto) {
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelEntrada(texto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível de entrada desconhecido: " + texto);
    }

    @Test
    void deveRecusarNivelEntradaNuloOuDesconhecido() {
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelEntrada(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível de entrada não informado");
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelEntrada("Expert"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível de entrada desconhecido: Expert");
    }

    @Test
    void deveTraduzirUmaData() {
        assertThat(ConversorTexto.traduzirData("2026-10-16")).isEqualTo(LocalDate.of(2026, 10, 16));
    }

    @ParameterizedTest
    @ValueSource(strings = {"16/10/2026", "2026-02-30", "", "ontem"})
    void deveRecusarUmaDataInvalida(String texto) {
        assertThatThrownBy(() -> ConversorTexto.traduzirData(texto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data inválida: " + texto)
                .hasCauseInstanceOf(DateTimeParseException.class);
    }

    @Test
    void deveRecusarDataNula() {
        assertThatThrownBy(() -> ConversorTexto.traduzirData(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data não informada");
    }

    @Test
    void deveTraduzirUmMesParaODiaUm() {
        assertThat(ConversorTexto.traduzirMes("2026-09")).isEqualTo(LocalDate.of(2026, 9, 1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-13", "2026-9", "2026-09-01", "setembro", ""})
    void deveRecusarUmMesInvalido(String texto) {
        assertThatThrownBy(() -> ConversorTexto.traduzirMes(texto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mês inválido: " + texto)
                .hasCauseInstanceOf(DateTimeParseException.class);
    }

    @Test
    void deveRecusarMesNulo() {
        assertThatThrownBy(() -> ConversorTexto.traduzirMes(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mês não informado");
    }

    @Test
    void deveRecusarNivelEtapaNuloOuDesconhecido() {
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelEtapa(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível de etapa não informado");
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelEtapa("Expert"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível de etapa desconhecido: Expert");
    }

    @Test
    void deveRecusarNivelSalarialNuloOuDesconhecido() {
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelSalarial(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível salarial não informado");
        assertThatThrownBy(() -> ConversorTexto.traduzirNivelSalarial("Estagiário"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nível salarial desconhecido: Estagiário");
    }

    @Test
    void deveRecusarIdiomaNuloOuDesconhecido() {
        assertThatThrownBy(() -> ConversorTexto.traduzirIdioma(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Idioma não informado");
        assertThatThrownBy(() -> ConversorTexto.traduzirIdioma("pt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Idioma desconhecido: pt");
    }

    @Test
    void deveRecusarAcessoNuloOuDesconhecido() {
        assertThatThrownBy(() -> ConversorTexto.traduzirAcesso(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Acesso não informado");
        assertThatThrownBy(() -> ConversorTexto.traduzirAcesso("Gratuito"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Acesso desconhecido: Gratuito");
    }

    @Test
    void deveRecusarTipoDeRecursoNuloOuDesconhecido() {
        assertThatThrownBy(() -> ConversorTexto.traduzirTipoRecurso(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tipo do recurso não informado");
        assertThatThrownBy(() -> ConversorTexto.traduzirTipoRecurso("podcast"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tipo do recurso desconhecido: podcast");
    }
}
