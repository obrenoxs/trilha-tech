package io.github.obrenoxs.trilhatech.integracao;

import com.jayway.jsonpath.JsonPath;
import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.EtapaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.tests.IntegracaoComConteudoBase;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConteudoApiIT extends IntegracaoComConteudoBase {

    @Test
    void deveListarAsOitoAreasNaOrdemDoCatalogoComAsTrintaESeisTrilhas() throws Exception {
        String corpo = mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].numero").value(conteudo.catalogo().areas().get(0).numero()))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<String> slugs = JsonPath.read(corpo, "$[*].slug");
        List<Integer> quantidades = JsonPath.read(corpo, "$[*].quantidadeTrilhas");

        assertThat(slugs).containsExactlyElementsOf(
                conteudo.catalogo().areas().stream().map(AreaCatalogoArquivo::slug).toList());
        assertThat(quantidades.stream().mapToInt(Integer::intValue).sum()).isEqualTo(36);
    }

    @Test
    void deveDetalharUmaAreaComSuasTrilhas() throws Exception {
        AreaCatalogoArquivo area = conteudo.catalogo().areas().get(0);

        mockMvc.perform(get("/api/areas/{slug}", area.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(area.slug()))
                .andExpect(jsonPath("$.nome").value(area.nome()))
                .andExpect(jsonPath("$.comoETrabalhar").value(area.comoETrabalhar()))
                .andExpect(jsonPath("$.trilhas.length()").value(area.trilhas().size()))
                .andExpect(jsonPath("$.trilhas[0].slug").value(area.trilhas().get(0).slug()));
    }

    @Test
    void deveResponder404ParaUmaAreaQueNaoExiste() throws Exception {
        mockMvc.perform(get("/api/areas/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/areas/nao-existe"));
    }

    @Test
    void deveServirOsDetalhesDeCadaUmaDasTrintaESeisTrilhasIguaisAosArquivos() throws Exception {
        for (TrilhaArquivo trilha : conteudo.trilhas()) {
            String slug = trilha.trilha().slug();

            var requisicao = mockMvc.perform(get("/api/trilhas/{slug}", slug))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.slug").value(slug))
                    .andExpect(jsonPath("$.area.slug").value(trilha.trilha().area().slug()))
                    .andExpect(jsonPath("$.etapas.length()").value(trilha.etapas().size()))
                    .andExpect(jsonPath("$.faixasSalariais.length()").value(3));

            for (int i = 0; i < trilha.etapas().size(); i++) {
                EtapaArquivo etapa = trilha.etapas().get(i);
                requisicao
                        .andExpect(jsonPath("$.etapas[" + i + "].titulo").value(etapa.titulo()))
                        .andExpect(jsonPath("$.etapas[" + i + "].recursos.length()").value(etapa.recursos().size()))
                        .andExpect(jsonPath("$.etapas[" + i + "].recursos[0].url").value(etapa.recursos().get(0).url()));
            }

            String corpo = requisicao.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertThat(corpo).as(slug).doesNotContain("\"id\"");
        }
    }

    @Test
    void deveOrdenarAsFaixasSalariaisEFormatarOMesDeReferencia() throws Exception {
        String slug = conteudo.trilhas().get(0).trilha().slug();

        String corpo = mockMvc.perform(get("/api/trilhas/{slug}", slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faixasSalariais[0].dataReferencia", matchesPattern("\\d{4}-\\d{2}")))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<String> niveis = JsonPath.read(corpo, "$.faixasSalariais[*].nivel");
        assertThat(niveis).containsExactly("JUNIOR", "PLENO", "SENIOR");
    }

    @Test
    void deveResponder404ParaUmaTrilhaQueNaoExiste() throws Exception {
        mockMvc.perform(get("/api/trilhas/nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}