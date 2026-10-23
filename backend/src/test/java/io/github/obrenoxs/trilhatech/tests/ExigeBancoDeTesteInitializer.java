package io.github.obrenoxs.trilhatech.tests;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ExigeBancoDeTesteInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext contexto) {
        GuardaDoBancoDeTeste.exigirUrl(contexto.getEnvironment().getProperty("spring.datasource.url"));
    }
}