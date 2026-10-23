package io.github.obrenoxs.trilhatech.integracao;

import com.jayway.jsonpath.JsonPath;
import io.github.obrenoxs.trilhatech.carga.arquivo.AlternativaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.PerguntaArquivo;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.CalculadoraPontuacao;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.Escolha;
import io.github.obrenoxs.trilhatech.quiz.resultado.calculo.Recomendacao;
import io.github.obrenoxs.trilhatech.tests.IntegracaoComConteudoBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuizApiIT extends IntegracaoComConteudoBase {

    private static final String URL_RESULTADO = "/api/quiz/resultado";

    private MockHttpServletRequestBuilder envio(String json) {
        return post(URL_RESULTADO).contentType(MediaType.APPLICATION_JSON).content(json);
    }

    private int escolha(int padrao, int pergunta) {
        return switch (padrao) {
            case 0, 1, 2, 3 -> padrao + 1;
            case 4 -> (pergunta % 4) + 1;
            default -> ((pergunta * 3) % 4) + 1;
        };
    }

    private String corpoDasRespostas(IntUnaryOperator alternativaDaPergunta) {
        String itens = IntStream.rangeClosed(1, 12)
                .mapToObj(p -> "{\"pergunta\":" + p + ",\"alternativa\":" + alternativaDaPergunta.applyAsInt(p) + "}")
                .collect(Collectors.joining(","));
        return "{\"respostas\":[" + itens + "]}";
    }

    private List<Recomendacao> esperadoPara(int padrao) {
        List<Escolha> escolhas = new ArrayList<>();
        for (PerguntaArquivo pergunta : conteudo.quiz().perguntas()) {
            AlternativaArquivo escolhida = pergunta.alternativas().get(escolha(padrao, pergunta.ordem()) - 1);
            escolhas.add(new Escolha(escolhida.areaPrincipal(), escolhida.areaSecundaria()));
        }
        Map<String, Integer> ordens = new HashMap<>();
        for (AreaCatalogoArquivo area : conteudo.catalogo().areas()) {
            ordens.put(area.slug(), area.ordemDesempate());
        }
        return new CalculadoraPontuacao().calcular(escolhas, ordens);
    }

    @Test
    void deveListarAsDozePerguntasSemExporAsAreasNemOsPontos() throws Exception {
        String corpo = mockMvc.perform(get("/api/quiz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[0].ordem").value(1))
                .andExpect(jsonPath("$[11].ordem").value(12))
                .andExpect(jsonPath("$[0].alternativas.length()").value(4))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<Integer> ordensDasAlternativas = JsonPath.read(corpo, "$[*].alternativas[*].ordem");
        assertThat(ordensDasAlternativas).hasSize(48);

        assertThat(corpo).doesNotContain("areaPrincipal", "areaSecundaria", "\"id\"", "pontos");
        for (AreaCatalogoArquivo area : conteudo.catalogo().areas()) {
            assertThat(corpo).as(area.slug()).doesNotContain(area.slug());
        }
    }

    @Test
    void deveCalcularOResultadoConhecidoQuandoTodasAsRespostasSaoAPrimeiraAlternativa() throws Exception {
        // "foto" do quiz atual: se você mudar as alternativas do quiz.json, atualize estes valores
        mockMvc.perform(envio(corpoDasRespostas(p -> 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recomendacoes[0].slug").value("desenvolvimento-de-software"))
                .andExpect(jsonPath("$.recomendacoes[0].barra").value(100))
                .andExpect(jsonPath("$.recomendacoes[1].slug").value("dados-e-ia"))
                .andExpect(jsonPath("$.recomendacoes[1].barra").value(75))
                .andExpect(jsonPath("$.recomendacoes[2].slug").value("infraestrutura-cloud-devops"))
                .andExpect(jsonPath("$.recomendacoes[2].barra").value(42));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void deveDevolverOMesmoRankingQueACalculadoraProduzParaOsMesmosDados(int padrao) throws Exception {
        List<Recomendacao> esperado = esperadoPara(padrao);

        String corpo = mockMvc.perform(envio(corpoDasRespostas(p -> escolha(padrao, p))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<String> slugs = JsonPath.read(corpo, "$.recomendacoes[*].slug");
        List<Integer> barras = JsonPath.read(corpo, "$.recomendacoes[*].barra");
        List<Integer> posicoes = JsonPath.read(corpo, "$.recomendacoes[*].posicao");

        assertThat(slugs).containsExactlyElementsOf(esperado.stream().map(Recomendacao::slug).toList());
        assertThat(barras).containsExactlyElementsOf(esperado.stream().map(Recomendacao::barra).toList());
        assertThat(posicoes).containsExactly(1, 2, 3);
        assertThat(corpo).doesNotContain("pontos");
    }

    @Test
    void deveRelatarTodosOsProblemasDeUmaRequisicaoInvalidaJuntos() throws Exception {
        String itens = IntStream.rangeClosed(3, 12)
                .mapToObj(p -> "{\"pergunta\":" + p + ",\"alternativa\":1}")
                .collect(Collectors.joining(","));
        String json = "{\"respostas\":[{\"pergunta\":1,\"alternativa\":9},{\"pergunta\":1,\"alternativa\":1}," + itens + "]}";

        String corpo = mockMvc.perform(envio(json))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<String> mensagens = JsonPath.read(corpo, "$.errors[*].message");
        assertThat(mensagens).containsExactly(
                "A alternativa 9 não existe na pergunta 1",
                "A pergunta 1 foi respondida mais de uma vez",
                "Falta responder a pergunta 2");
    }

    @Test
    void deveRecusarUmaPerguntaQueNaoExiste() throws Exception {
        String itens = IntStream.rangeClosed(1, 11)
                .mapToObj(p -> "{\"pergunta\":" + p + ",\"alternativa\":1}")
                .collect(Collectors.joining(","));
        String json = "{\"respostas\":[" + itens + ",{\"pergunta\":13,\"alternativa\":1}]}";

        String corpo = mockMvc.perform(envio(json))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        List<String> mensagens = JsonPath.read(corpo, "$.errors[*].message");
        assertThat(mensagens).containsExactly("A pergunta 13 não existe", "Falta responder a pergunta 12");
    }

    @Test
    void deveRecusarUmCorpoSemAListaDeRespostas() throws Exception {
        mockMvc.perform(envio("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("respostas"));
    }

    @Test
    void deveRecusarUmaListaComMenosDeDozeRespostas() throws Exception {
        mockMvc.perform(envio("{\"respostas\":[{\"pergunta\":1,\"alternativa\":1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("respostas"));
    }

    @Test
    void deveRecusarItensNulosComUmErroPorPosicaoESemVirar500() throws Exception {
        String nulos = "{\"respostas\":[" + "null,".repeat(11) + "null]}";

        mockMvc.perform(envio(nulos))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(12));
    }

    @Test
    void deveRecusarUmJsonQuebradoSemDetalhesDoParser() throws Exception {
        mockMvc.perform(envio("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Requisição inválida"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }
}