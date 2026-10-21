package io.github.obrenoxs.trilhatech.carga.leitura;

import io.github.obrenoxs.trilhatech.carga.arquivo.CatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.QuizArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.exception.FalhaLeituraException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

@Component
public class LeitorConteudo {

    private final JsonMapper mapper = JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    private<T> T ler(String caminho, Class<T> tipo) {
        try (InputStream entrada = LeitorConteudo.class.getResourceAsStream(caminho)) {
            if (entrada == null) {
                throw new FalhaLeituraException("Arquivo não encontrado: " + caminho);
            }
            return mapper.readValue(entrada, tipo);
        } catch (IOException | JacksonException e) {
            throw new FalhaLeituraException("Não foi possível ler " + caminho, e);
        }
    }

    public CatalogoArquivo lerCatalogo() {
        return ler( "/conteudo/catalogo.json", CatalogoArquivo.class);
    }

    public TrilhaArquivo lerTrilha(String slug) {
        return ler("/conteudo/trilhas/" + slug + ".json", TrilhaArquivo.class);
    }

    public QuizArquivo lerQuiz() {
        return ler( "/conteudo/quiz.json", QuizArquivo.class);
    }
}
