package io.github.obrenoxs.trilhatech.integracao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.ConteudoCarregado;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.tests.IntegracaoBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SincronizadorConteudoIT extends IntegracaoBase {

    // a mesma chave do SincronizadorConteudo.CHAVE_DO_BLOQUEIO
    private static final long CHAVE_DO_BLOQUEIO = 7430912L;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private DataSource dataSource;

    private ConteudoCarregado conteudo;

    @BeforeEach
    void preparar() {
        limparBanco();
        conteudo = validadorConteudo.carregarEValidar();
    }

    @Test
    void deveGravarTodoOConteudoDosArquivos() {
        sincronizador.sincronizar(conteudo);

        int etapas = conteudo.trilhas().stream().mapToInt(t -> t.etapas().size()).sum();
        int recursos = conteudo.trilhas().stream()
                .flatMap(t -> t.etapas().stream())
                .mapToInt(e -> e.recursos().size())
                .sum();

        assertThat(contar("area")).isEqualTo(8);
        assertThat(contar("trilha")).isEqualTo(36);
        assertThat(contar("etapa")).isEqualTo(etapas);
        assertThat(contar("recurso")).isEqualTo(recursos);
        assertThat(contar("faixa_salarial")).isEqualTo(36 * 3);
        assertThat(contar("pergunta")).isEqualTo(12);
        assertThat(contar("alternativa")).isEqualTo(48);
    }

    @Test
    void devePoderCarregarDuasVezesSemDuplicarNada() {
        sincronizador.sincronizar(conteudo);
        List<String> areasAntes = jdbcTemplate.queryForList("select id::text from area order by slug", String.class);
        List<String> trilhasAntes = jdbcTemplate.queryForList("select id::text from trilha order by slug", String.class);

        sincronizador.sincronizar(conteudo);

        assertThat(contar("area")).isEqualTo(8);
        assertThat(contar("trilha")).isEqualTo(36);
        assertThat(contar("pergunta")).isEqualTo(12);
        assertThat(contar("alternativa")).isEqualTo(48);

        // decisão da carga: o id da área é estável, e o das trilhas é recriado a cada carga
        List<String> areasDepois = jdbcTemplate.queryForList("select id::text from area order by slug", String.class);
        List<String> trilhasDepois = jdbcTemplate.queryForList("select id::text from trilha order by slug", String.class);
        assertThat(areasDepois).isEqualTo(areasAntes);
        assertThat(trilhasDepois).doesNotContainAnyElementsOf(trilhasAntes);
    }

    @Test
    void deveAtualizarUmaAreaExistenteSemMudarOId() {
        sincronizador.sincronizar(conteudo);
        AreaCatalogoArquivo primeira = conteudo.catalogo().areas().get(0);
        String idAntes = jdbcTemplate.queryForObject(
                "select id::text from area where slug = ?", String.class, primeira.slug());

        jdbcTemplate.update("update area set nome = 'Nome alterado' where slug = ?", primeira.slug());
        sincronizador.sincronizar(conteudo);

        String nome = jdbcTemplate.queryForObject("select nome from area where slug = ?", String.class, primeira.slug());
        String idDepois = jdbcTemplate.queryForObject(
                "select id::text from area where slug = ?", String.class, primeira.slug());
        assertThat(nome).isEqualTo(primeira.nome());
        assertThat(idDepois).isEqualTo(idAntes);
    }

    @Test
    void deveRemoverUmaAreaQueSaiuDoCatalogo() {
        areaRepository.save(new Area("area-obsoleta", "99", "Obsoleta", "d", "c", 99, 99));

        sincronizador.sincronizar(conteudo);

        assertThat(contar("area")).isEqualTo(8);
        Integer obsoletas = jdbcTemplate.queryForObject(
                "select count(*) from area where slug = 'area-obsoleta'", Integer.class);
        assertThat(obsoletas).isZero();
    }

    @Test
    void deveEsperarQuandoOutraInstanciaEstaSincronizando() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try (Connection outraInstancia = dataSource.getConnection()) {
            outraInstancia.setAutoCommit(false);
            try (Statement trava = outraInstancia.createStatement()) {
                trava.execute("SELECT pg_advisory_xact_lock(" + CHAVE_DO_BLOQUEIO + ")");
            }

            Future<?> sincronizacao = executor.submit(() -> sincronizador.sincronizar(conteudo));

            assertThatThrownBy(() -> sincronizacao.get(2, TimeUnit.SECONDS))
                    .isInstanceOf(TimeoutException.class);
            assertThat(contar("area")).isZero();

            outraInstancia.commit();
            sincronizacao.get(60, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }

        assertThat(contar("area")).isEqualTo(8);
    }
}