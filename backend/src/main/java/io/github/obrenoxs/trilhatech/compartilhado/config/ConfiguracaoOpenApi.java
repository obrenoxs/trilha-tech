package io.github.obrenoxs.trilhatech.compartilhado.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoOpenApi {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Trilha Tech API")
                .description("API pública, somente leitura, do Trilha Tech: áreas, trilhas, quiz e assistente de IA.")
                .version("v1"));
    }
}