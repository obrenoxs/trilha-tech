package io.github.obrenoxs.trilhatech.compartilhado.config;

import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Configuration
public class ConfiguracaoOpenApi {

    private static final Set<String> CAMPOS_OPCIONAIS = Set.of(
            "RecursoResponse.observacao",
            "EtapaResponse.projetoPratico",
            "AreaDetalheResponse.comoETrabalhar",
            "ErroResponse.errors",
            "Detalhe.field"
    );

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/").description("Servidor atual")))
                .info(new Info()
                        .title("Trilha Tech API")
                        .description("API pública, somente leitura, do Trilha Tech: áreas, trilhas, quiz e assistente de IA.")
                        .version("v1"));
    }

    @Bean
    public OpenApiCustomizer camposObrigatorios() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }

            for (Map.Entry<String, Schema> entrada : openApi.getComponents().getSchemas().entrySet()) {
                String nomeDoSchema = entrada.getKey();
                boolean ehResposta = nomeDoSchema.endsWith("Response") || nomeDoSchema.equals("Detalhe");
                Schema<?> schema = entrada.getValue();

                if (!ehResposta || schema.getProperties() == null) {
                    continue;
                }

                List<String> obrigatorios = new ArrayList<>();
                for (String campo : schema.getProperties().keySet()) {
                    if (!CAMPOS_OPCIONAIS.contains(nomeDoSchema + "." + campo)) {
                        obrigatorios.add(campo);
                    }
                }
                schema.setRequired(obrigatorios);
            }
        };
    }
}