package io.github.obrenoxs.trilhatech.carga.sincronizacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.*;
import io.github.obrenoxs.trilhatech.carga.conversao.ConversorTexto;
import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import io.github.obrenoxs.trilhatech.conteudo.area.repository.AreaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Etapa;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.FaixaSalarial;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Recurso;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Trilha;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.EtapaRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.FaixaSalarialRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.RecursoRepository;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.TrilhaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class SincronizadorConteudo {

    private static final long CHAVE_DO_BLOQUEIO = 7430912L;

    private final AreaRepository areaRepository;
    private final TrilhaRepository trilhaRepository;
    private final EtapaRepository etapaRepository;
    private final RecursoRepository recursoRepository;
    private final FaixaSalarialRepository faixaSalarialRepository;
    private final JdbcTemplate jdbcTemplate;

    public SincronizadorConteudo(
            AreaRepository areaRepository,
            TrilhaRepository trilhaRepository,
            EtapaRepository etapaRepository,
            RecursoRepository recursoRepository,
            FaixaSalarialRepository faixaSalarialRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.areaRepository = areaRepository;
        this.trilhaRepository = trilhaRepository;
        this.etapaRepository = etapaRepository;
        this.recursoRepository = recursoRepository;
        this.faixaSalarialRepository = faixaSalarialRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void sincronizar(ConteudoCarregado conteudo) {
        travarSincronizacao();
        trilhaRepository.deleteAllInBatch();

        Map<String, Area> areasPorSlug = sincronizarAreas(conteudo.catalogo());
        sincronizarTrilhas(conteudo, areasPorSlug);
    }

    private Map<String, Area> sincronizarAreas(CatalogoArquivo catalogo) {
        Set<String> slugsDoCatalogo = new HashSet<>();
        for (AreaCatalogoArquivo arquivo : catalogo.areas()) {
            slugsDoCatalogo.add(arquivo.slug());
        }

        for (Area existente : areaRepository.findAll()) {
            if (!slugsDoCatalogo.contains(existente.getSlug())) {
                areaRepository.delete(existente);
            }
        }
        areaRepository.flush();

        Map<String, Area> areasPorSlug = new HashMap<>();

        for (int i = 0; i < catalogo.areas().size(); i++) {
            AreaCatalogoArquivo arquivo = catalogo.areas().get(i);
            int ordemExibicao = i + 1;

            Area area = areaRepository.findBySlug(arquivo.slug()).orElse(null);

            if (area == null) {
                area = new Area(
                        arquivo.slug(),
                        arquivo.numero(),
                        arquivo.nome(),
                        arquivo.descricao(),
                        arquivo.comoETrabalhar(),
                        ordemExibicao,
                        arquivo.ordemDesempate()
                );
            } else {
                area.setNumero(arquivo.numero());
                area.setNome(arquivo.nome());
                area.setDescricao(arquivo.descricao());
                area.setComoETrabalhar(arquivo.comoETrabalhar());
                area.setOrdemExibicao(ordemExibicao);
                area.setOrdemDesempate(arquivo.ordemDesempate());
            }

            areasPorSlug.put(arquivo.slug(), areaRepository.save(area));
        }

        return areasPorSlug;
    }

    private void sincronizarTrilhas(ConteudoCarregado conteudo, Map<String, Area> areasPorSlug) {
        Map<String, TrilhaArquivo> arquivosPorSlug = new HashMap<>();
        for (TrilhaArquivo arquivo : conteudo.trilhas()) {
            arquivosPorSlug.put(arquivo.trilha().slug(), arquivo);
        }

        for (AreaCatalogoArquivo areaCatalogo : conteudo.catalogo().areas()) {
            Area area = areasPorSlug.get(areaCatalogo.slug());

            for (TrilhaCatalogoArquivo trilhaCatalogo : areaCatalogo.trilhas()) {
                TrilhaArquivo arquivo = arquivosPorSlug.get(trilhaCatalogo.slug());
                DadosTrilhaArquivo dados = arquivo.trilha();

                Trilha trilha = new Trilha(
                        area,
                        dados.slug(),
                        trilhaCatalogo.ordem(),
                        dados.nome(),
                        dados.resumo(),
                        ConversorTexto.traduzirNivelEntrada(dados.nivelEntrada()),
                        dados.textoTrilha(),
                        dados.paraQuemE(),
                        dados.horasPorSemana(),
                        arquivo.avisosSalario(),
                        ConversorTexto.traduzirData(arquivo.geradoEm())
                );

                Trilha trilhaGravada = trilhaRepository.save(trilha);
                sincronizarEtapas(trilhaGravada, arquivo.etapas());
                sincronizarFaixas(trilhaGravada, arquivo.faixaSalarial());
            }
        }
    }

    private void sincronizarEtapas(Trilha trilha, List<EtapaArquivo> etapasDoArquivo) {
        for (EtapaArquivo etapaArquivo : etapasDoArquivo) {
            Etapa etapa = new Etapa(
                    trilha,
                    etapaArquivo.ordem(),
                    etapaArquivo.titulo(),
                    ConversorTexto.traduzirNivelEtapa(etapaArquivo.nivel()),
                    etapaArquivo.duracaoSemanas(),
                    etapaArquivo.descricao(),
                    etapaArquivo.projetoPratico()
            );
            Etapa etapaGravada = etapaRepository.save(etapa);

            sincronizarRecursos(etapaGravada, etapaArquivo.recursos());
        }
    }

    private void sincronizarRecursos(Etapa etapa, List<RecursoArquivo> recursosDoArquivo) {
        for (int i = 0; i < recursosDoArquivo.size(); i++) {
            RecursoArquivo recursoArquivo = recursosDoArquivo.get(i);

            Recurso recurso = new Recurso(
                    etapa,
                    i + 1,
                    recursoArquivo.titulo(),
                    recursoArquivo.url(),
                    ConversorTexto.traduzirTipoRecurso(recursoArquivo.tipo()),
                    ConversorTexto.traduzirIdioma(recursoArquivo.idioma()),
                    ConversorTexto.traduzirAcesso(recursoArquivo.acesso()),
                    recursoArquivo.observacao(),
                    ConversorTexto.traduzirData(recursoArquivo.verificadoEm())
            );

            recursoRepository.save(recurso);
        }
    }

    private void sincronizarFaixas(Trilha trilha, List<FaixaSalarialArquivo> faixasDoArquivo) {
        for (FaixaSalarialArquivo faixa : faixasDoArquivo) {
            FaixaSalarial faixaSalarial = new FaixaSalarial(
                    trilha,
                    ConversorTexto.traduzirNivelSalarial(faixa.nivel()),
                    faixa.minimo(),
                    faixa.maximo(),
                    faixa.valorReferencia(),
                    faixa.tipoReferencia(),
                    faixa.fonte(),
                    ConversorTexto.traduzirMes(faixa.dataReferencia()),
                    faixa.dadosLimitados(),
                    faixa.observacao()
            );

            faixaSalarialRepository.save(faixaSalarial);
        }
    }

    private void travarSincronizacao() {
        jdbcTemplate.execute("SELECT pg_advisory_xact_lock(" + CHAVE_DO_BLOQUEIO + ")");
    }
}
