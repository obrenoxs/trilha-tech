package io.github.obrenoxs.trilhatech.integracao;


import io.github.obrenoxs.trilhatech.tests.IntegracaoBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OpenApiIT extends IntegracaoBase {

    @Test
    void deveExporOsSeteEndpointsDaApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Trilha Tech API"))
                .andExpect(jsonPath("$.paths['/api/areas'].get").exists())
                .andExpect(jsonPath("$.paths['/api/areas/{slug}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/trilhas/{slug}'].get").exists())
                .andExpect(jsonPath("$.paths['/api/trilhas/{slug}/pdf'].get").exists())
                .andExpect(jsonPath("$.paths['/api/quiz'].get").exists())
                .andExpect(jsonPath("$.paths['/api/quiz/resultado'].post").exists())
                .andExpect(jsonPath("$.paths['/api/assistente/mensagens'].post").exists());
    }

    @ParameterizedTest
    @CsvSource({
            "/api/areas, get, 200",
            "/api/areas/{slug}, get, 200",
            "/api/areas/{slug}, get, 404",
            "/api/trilhas/{slug}, get, 200",
            "/api/trilhas/{slug}, get, 404",
            "/api/trilhas/{slug}/pdf, get, 200",
            "/api/trilhas/{slug}/pdf, get, 404",
            "/api/quiz, get, 200",
            "/api/quiz/resultado, post, 200",
            "/api/quiz/resultado, post, 400",
            "/api/quiz/resultado, post, 413",
            "/api/assistente/mensagens, post, 200",
            "/api/assistente/mensagens, post, 400",
            "/api/assistente/mensagens, post, 413",
            "/api/assistente/mensagens, post, 429",
            "/api/assistente/mensagens, post, 503"})
    void deveDocumentarCadaRespostaDeCadaEndpoint(String caminho, String metodo, String status) throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['" + caminho + "']." + metodo + ".responses['" + status + "']").exists());
    }

    @Test
    void deveDocumentarOCabecalhoRetryAfterDo429() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['/api/assistente/mensagens'].post.responses['429'].headers['Retry-After']").exists());
    }

    @Test
    void deveDocumentarOPdfComoArquivoBinario() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['/api/trilhas/{slug}/pdf'].get.responses['200'].content['application/pdf']").exists());
    }

    @Test
    void naoDeveInventarRespostasQueNaoForamDocumentadas() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(jsonPath("$.paths['/api/areas'].get.responses.length()").value(1))
                .andExpect(jsonPath("$.paths['/api/quiz'].get.responses.length()").value(1));
    }

    @Test
    void deveServirASwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
