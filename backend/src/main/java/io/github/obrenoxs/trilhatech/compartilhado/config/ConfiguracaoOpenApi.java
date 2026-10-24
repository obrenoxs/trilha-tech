package io.github.obrenoxs.trilhatech.compartilhado.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfiguracaoOpenApi {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url("/").description("Servidor atual")))
                .info(new Info()
                        .title("Trilha Tech API")
                        .description("API pública, somente leitura, do Trilha Tech: áreas, trilhas, quiz e assistente de IA.")
                        .version("v1"));
    }
}