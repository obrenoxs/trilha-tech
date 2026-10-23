package io.github.obrenoxs.trilhatech.integracao;

import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.tests.IntegracaoComConteudoBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PdfApiIT extends IntegracaoComConteudoBase {

    private byte[] bytesDoArquivo(String slug) throws IOException {
        try (InputStream entrada = new ClassPathResource("conteudo/pdfs/roadmap-" + slug + ".pdf").getInputStream()) {
            return entrada.readAllBytes();
        }
    }

    @Test
    void deveBaixarOPdfDeCadaUmaDasTrilhasSemAlterarUmByte() throws Exception {
        for (TrilhaArquivo trilha : conteudo.trilhas()) {
            String slug = trilha.trilha().slug();

            MvcResult resultado = mockMvc.perform(get("/api/trilhas/{slug}/pdf", slug))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                    .andReturn();

            assertThat(resultado.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION))
                    .as(slug)
                    .startsWith("attachment")
                    .contains("roadmap-" + slug + ".pdf");
            assertThat(resultado.getResponse().getContentAsByteArray())
                    .as(slug)
                    .isEqualTo(bytesDoArquivo(slug));
        }
    }

    @Test
    void deveEntregarUmArquivoQueComecaComoPdf() throws Exception {
        String slug = conteudo.trilhas().get(0).trilha().slug();

        byte[] corpo = mockMvc.perform(get("/api/trilhas/{slug}/pdf", slug))
                .andReturn().getResponse().getContentAsByteArray();

        assertThat(new String(corpo, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }

    @Test
    void deveResponder404ParaUmaTrilhaQueNaoExiste() throws Exception {
        mockMvc.perform(get("/api/trilhas/nao-existe/pdf"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"..%2F..%2Fapplication", "%2e%2e%2f%2e%2e%2fapplication", "java-e-spring%2F..%2Fx"})
    void naoDeveMontarCaminhoDeArquivoAPartirDoTextoDaUrl(String tentativa) throws Exception {
        mockMvc.perform(get(URI.create("/api/trilhas/" + tentativa + "/pdf")))
                .andExpect(status().is4xxClientError());
    }
}