package io.github.obrenoxs.trilhatech.tests;

import io.github.obrenoxs.trilhatech.carga.arquivo.ConteudoCarregado;
import io.github.obrenoxs.trilhatech.carga.sincronizacao.SincronizadorConteudo;
import io.github.obrenoxs.trilhatech.carga.validacao.ValidadorConteudo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("integracao")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ExigeBancoDeTesteInitializer.class)
@TestPropertySource(properties = "trilhatech.cors.origens=http://localhost:5173")
public abstract class IntegracaoBase {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ValidadorConteudo validadorConteudo;

    @Autowired
    protected SincronizadorConteudo sincronizador;

    protected void limparBanco() {
        exigirBancoDeTeste();
        jdbcTemplate.execute("TRUNCATE TABLE alternativa, pergunta, recurso, etapa, faixa_salarial, trilha, area, uso_assistente CASCADE");
    }

    protected void limparUsoDoAssistente() {
        exigirBancoDeTeste();
        jdbcTemplate.execute("TRUNCATE TABLE uso_assistente");
    }

    protected ConteudoCarregado carregarConteudoReal() {
        exigirBancoDeTeste();
        ConteudoCarregado conteudo = validadorConteudo.carregarEValidar();
        sincronizador.sincronizar(conteudo);
        return conteudo;
    }

    protected int contar(String tabela) {
        Integer total = jdbcTemplate.queryForObject("select count(*) from " + tabela, Integer.class);
        return total == null ? 0 : total;
    }

    private void exigirBancoDeTeste() {
        GuardaDoBancoDeTeste.exigirNome(jdbcTemplate.queryForObject("select current_database()", String.class));
    }
}