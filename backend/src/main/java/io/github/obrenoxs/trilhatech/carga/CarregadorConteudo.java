package io.github.obrenoxs.trilhatech.carga;

import io.github.obrenoxs.trilhatech.carga.arquivo.ConteudoCarregado;
import io.github.obrenoxs.trilhatech.carga.sincronizacao.SincronizadorConteudo;
import io.github.obrenoxs.trilhatech.carga.validacao.ValidadorConteudo;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

@Component
public class CarregadorConteudo implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CarregadorConteudo.class);

    private final ValidadorConteudo validadorConteudo;
    private final SincronizadorConteudo sincronizadorConteudo;

    public CarregadorConteudo(ValidadorConteudo validadorConteudo, SincronizadorConteudo sincronizadorConteudo) {
        this.validadorConteudo = validadorConteudo;
        this.sincronizadorConteudo = sincronizadorConteudo;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Carregando o conteúdo dos arquivos...");

        ConteudoCarregado conteudo = validadorConteudo.carregarEValidar();
        sincronizadorConteudo.sincronizar(conteudo);

        log.info("Conteúdo carregado: {} áreas e {} trilhas",
                conteudo.catalogo().areas().size(),
                conteudo.trilhas().size());
    }
}
