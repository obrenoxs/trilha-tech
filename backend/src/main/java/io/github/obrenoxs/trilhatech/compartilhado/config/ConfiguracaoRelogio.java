package io.github.obrenoxs.trilhatech.compartilhado.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ConfiguracaoRelogio {

    @Bean
    public Clock relogio() {
        return Clock.systemUTC();
    }

}
