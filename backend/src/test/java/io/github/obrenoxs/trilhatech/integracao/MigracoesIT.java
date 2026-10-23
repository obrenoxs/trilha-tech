package io.github.obrenoxs.trilhatech.integracao;

import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Trilha;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelEntrada;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.TrilhaRepository;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Alternativa;
import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Pergunta;
import io.github.obrenoxs.trilhatech.quiz.pergunta.repository.AlternativaRepository;
import io.github.obrenoxs.trilhatech.quiz.pergunta.repository.PerguntaRepository;
import io.github.obrenoxs.trilhatech.tests.IntegracaoBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MigracoesIT extends IntegracaoBase {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private TrilhaRepository trilhaRepository;

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private AlternativaRepository alternativaRepository;

    @BeforeEach
    void preparar() {
        limparBanco();
    }

    private Area area(String slug, int ordem) {
        return new Area(slug, String.format("%02d", ordem), "Area " + slug, "descricao", "como e", ordem, ordem);
    }

    private Trilha trilha(Area area, String slug, int ordem) {
        return new Trilha(area, slug, ordem, "Nome", "Resumo", NivelEntrada.INICIANTE,
                "Texto", "Para quem", 8, "Aviso", LocalDate.of(2026, 10, 16));
    }

    @Test
    void deveTerAplicadoAsQuatroMigracoesComSucesso() {
        List<String> versoes = jdbcTemplate.queryForList(
                "select version from flyway_schema_history where success and version is not null order by installed_rank",
                String.class);

        assertThat(versoes).containsExactly("1", "2", "3", "4");
    }

    @Test
    void deveTerCriadoAsTabelasDoModelo() {
        List<String> tabelas = jdbcTemplate.queryForList(
                "select table_name from information_schema.tables where table_schema = 'public'", String.class);

        assertThat(tabelas).contains("area", "trilha", "etapa", "recurso", "faixa_salarial",
                "pergunta", "alternativa", "uso_assistente");
    }

    @Test
    void deveRecusarDuasTrilhasComAMesmaOrdemNaArea() {
        Area area = areaRepository.save(area("a", 1));
        trilhaRepository.saveAndFlush(trilha(area, "trilha-1", 1));

        assertThatThrownBy(() -> trilhaRepository.saveAndFlush(trilha(area, "trilha-2", 1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveRecusarAlternativaComAreaPrincipalIgualASecundaria() {
        Area area = areaRepository.save(area("a", 1));
        Pergunta pergunta = perguntaRepository.save(new Pergunta(1, "P"));

        assertThatThrownBy(() -> alternativaRepository.saveAndFlush(new Alternativa(pergunta, 1, "t", area, area)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveRecusarUmContadorDeUsoNegativo() {
        assertThatThrownBy(() -> jdbcTemplate.update(
                "insert into uso_assistente (dia, mensagens) values (current_date, -1)"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveApagarEtapasRecursosEFaixasJuntoComATrilha() {
        carregarConteudoReal();
        int etapasAntes = contar("etapa");
        int recursosAntes = contar("recurso");
        int faixasAntes = contar("faixa_salarial");

        jdbcTemplate.update("delete from trilha where slug = (select slug from trilha order by slug limit 1)");

        assertThat(contar("trilha")).isEqualTo(35);
        assertThat(contar("etapa")).isLessThan(etapasAntes);
        assertThat(contar("recurso")).isLessThan(recursosAntes);
        assertThat(contar("faixa_salarial")).isEqualTo(faixasAntes - 3);
    }

    @Test
    void naoDeveApagarUmaAreaQueAindaTemTrilhas() {
        carregarConteudoReal();

        assertThatThrownBy(() -> jdbcTemplate.update("delete from area where id in (select area_id from trilha)"))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(contar("area")).isEqualTo(8);
    }
}