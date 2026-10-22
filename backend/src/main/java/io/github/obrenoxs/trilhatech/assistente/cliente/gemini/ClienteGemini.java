package io.github.obrenoxs.trilhatech.assistente.cliente.gemini;

import io.github.obrenoxs.trilhatech.assistente.cliente.ClienteIa;
import io.github.obrenoxs.trilhatech.assistente.cliente.MensagemIa;
import io.github.obrenoxs.trilhatech.assistente.cliente.PapelMensagem;
import io.github.obrenoxs.trilhatech.compartilhado.exception.IaIndisponivelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteGemini implements ClienteIa {

    private static final Logger log = LoggerFactory.getLogger(ClienteGemini.class);

    private final RestClient restClient;
    private final String chave;
    private final String modelo;
    private final int maximoDeTokens;

    public ClienteGemini(
            @Value("${trilhatech.assistente.gemini.chave}") String chave,
            @Value("${trilhatech.assistente.gemini.modelo}") String modelo,
            @Value("${trilhatech.assistente.gemini.url-base}") String urlBase,
            @Value("${trilhatech.assistente.gemini.timeout-segundos}") int timeoutSegundos,
            @Value("${trilhatech.assistente.gemini.maximo-de-tokens}") int maximoDeTokens
    ) {
        this.chave = chave;
        this.modelo = modelo;
        this.maximoDeTokens = maximoDeTokens;

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(httpClient);
        fabrica.setReadTimeout(Duration.ofSeconds(timeoutSegundos));

        this.restClient = RestClient.builder()
                .baseUrl(urlBase)
                .requestFactory(fabrica)
                .build();
    }

    @Override
    public String responder(String instrucaoSistema, List<MensagemIa> conversa) {
        if (chave == null || chave.isBlank()) {
            throw new IaIndisponivelException("A chave do Gemini não está configurada");
        }

        GeminiRequisicao requisicao = montarRequisicao(instrucaoSistema, conversa);

        GeminiResposta resposta;
        try {
            resposta = restClient.post()
                    .uri("/models/{modelo}:generateContent", modelo)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", chave)
                    .body(requisicao)
                    .retrieve()
                    .body(GeminiResposta.class);
        } catch (RestClientResponseException e) {
            log.warn("O Gemini respondeu com o status {}", e.getStatusCode().value());
            throw new IaIndisponivelException(
                    "O provedor de IA recusou a chamada (status " + e.getStatusCode().value() + ")", e);
        } catch (RestClientException e) {
            log.warn("Falha de comunicação com o Gemini: {}", e.getClass().getSimpleName());
            throw new IaIndisponivelException("Não foi possível falar com o provedor de IA", e);
        }

        String texto = extrairTexto(resposta);
        if (texto.isBlank()) {
            throw new IaIndisponivelException("O provedor de IA não devolveu texto");
        }
        return texto;
    }

    private GeminiRequisicao montarRequisicao(String instrucaoSistema, List<MensagemIa> conversa) {
        List<GeminiRequisicao.Conteudo> conteudos = new ArrayList<>();
        for (MensagemIa mensagem : conversa) {
            String papel = mensagem.papel() == PapelMensagem.USUARIO ? "user" : "model";
            conteudos.add(new GeminiRequisicao.Conteudo(
                    papel, List.of(new GeminiRequisicao.Parte(mensagem.texto()))));
        }

        GeminiRequisicao.Conteudo sistema = new GeminiRequisicao.Conteudo(
                null, List.of(new GeminiRequisicao.Parte(instrucaoSistema)));

        return new GeminiRequisicao(sistema, conteudos, new GeminiRequisicao.Geracao(maximoDeTokens));
    }

    static String extrairTexto(GeminiResposta resposta) {
        if (resposta == null || resposta.candidates() == null || resposta.candidates().isEmpty()) {
            return "";
        }

        GeminiResposta.Candidato candidato = resposta.candidates().get(0);
        if (candidato == null || candidato.content() == null || candidato.content().parts() == null) {
            return "";
        }

        StringBuilder texto = new StringBuilder();
        for (GeminiResposta.Parte parte : candidato.content().parts()) {
            if (parte != null && parte.text() != null) {
                texto.append(parte.text());
            }
        }

        return texto.toString().trim();
    }
}
