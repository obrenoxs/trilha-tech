package io.github.obrenoxs.trilhatech.carga.leitura;

import io.github.obrenoxs.trilhatech.carga.arquivo.CatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.exception.FalhaLeituraException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

@Component
public class LeitorConteudo {

    private final JsonMapper mapper = JsonMapper.builder().build();

    public CatalogoArquivo lerCatalogo() {
        String caminho = "/conteudo/catalogo.json";
        try (InputStream entrada = LeitorConteudo.class.getResourceAsStream(caminho)) {
            if(entrada == null) {
                throw new FalhaLeituraException("Arquivo não encontrado: " + caminho);
            }
            return mapper.readValue(entrada, CatalogoArquivo.class);
        } catch (IOException | JacksonException e) {
            throw new FalhaLeituraException("Não foi possível ler " + caminho, e);
        }
    }
}
