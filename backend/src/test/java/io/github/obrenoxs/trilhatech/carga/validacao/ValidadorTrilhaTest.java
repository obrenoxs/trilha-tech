package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaTrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.DadosTrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.EtapaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.FaixaSalarialArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.RecursoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.tests.FabricaConteudo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidadorTrilhaTest {

    private static final String SLUG = "java-e-spring";
    private static final String LOCAL = "Trilha '" + SLUG + "'";
    private static final String LOCAL_ETAPA = LOCAL + ", etapa nº 1";
    private static final String LOCAL_RECURSO = LOCAL_ETAPA + ", recurso nº 1";
    private static final String LOCAL_FAIXA = LOCAL + ", faixa nº 1";

    private final ValidadorTrilha validador = new ValidadorTrilha();

    private static class DadosBuilder {
        String slug = SLUG;
        String nome = "Nome";
        String resumo = "Resumo";
        AreaTrilhaArquivo area = FabricaConteudo.areaDaTrilha("01", "area-1", "Area 1");
        String nivelEntrada = "Iniciante";
        String textoTrilha = "Texto";
        String paraQuemE = "Para quem";
        Integer horasPorSemana = 8;
        Integer duracaoTotalSemanas = 10;

        DadosTrilhaArquivo build() {
            return new DadosTrilhaArquivo(slug, nome, resumo, area, nivelEntrada, textoTrilha,
                    paraQuemE, horasPorSemana, duracaoTotalSemanas);
        }
    }

    private static class RecursoBuilder {
        String titulo = "Curso";
        String url = "https://exemplo.com/curso";
        String tipo = "curso";
        String idioma = "PT";
        Boolean gratuito = true;
        String acesso = "gratuito";
        String verificadoEm = "2026-10-16";
        String observacao = "Gratuito";

        RecursoArquivo build() {
            return new RecursoArquivo(titulo, url, tipo, idioma, gratuito, acesso, verificadoEm, observacao);
        }
    }

    private static class EtapaBuilder {
        Integer ordem = 1;
        String titulo = "Titulo";
        String nivel = "Básico";
        Integer duracaoSemanas = 4;
        String descricao = "Descricao";
        String projetoPratico = "Projeto";
        List<RecursoArquivo> recursos = List.of(new RecursoBuilder().build());

        EtapaArquivo build() {
            return new EtapaArquivo(ordem, titulo, nivel, duracaoSemanas, descricao, projetoPratico, recursos);
        }
    }

    private static class FaixaBuilder {
        String nivel = "Júnior";
        Integer minimo = 2000;
        Integer maximo = 4000;
        Integer valorReferencia = 3000;
        String tipoReferencia = "média";
        String fonte = "Glassdoor";
        String dataReferencia = "2026-09";
        Boolean dadosLimitados = false;
        String observacao = "obs";

        FaixaSalarialArquivo build() {
            return new FaixaSalarialArquivo(nivel, minimo, maximo, valorReferencia, tipoReferencia,
                    fonte, dataReferencia, dadosLimitados, observacao);
        }
    }

    private DadosTrilhaArquivo dados() {
        return new DadosBuilder().build();
    }

    private List<EtapaArquivo> etapasValidas() {
        EtapaBuilder segunda = new EtapaBuilder();
        segunda.ordem = 2;
        segunda.duracaoSemanas = 6;
        return new ArrayList<>(List.of(new EtapaBuilder().build(), segunda.build()));
    }

    private List<FaixaSalarialArquivo> faixasValidas() {
        FaixaBuilder pleno = new FaixaBuilder();
        pleno.nivel = "Pleno";
        FaixaBuilder senior = new FaixaBuilder();
        senior.nivel = "Sênior";
        return new ArrayList<>(List.of(new FaixaBuilder().build(), pleno.build(), senior.build()));
    }

    private TrilhaArquivo arquivo(DadosTrilhaArquivo dados, List<EtapaArquivo> etapas, List<FaixaSalarialArquivo> faixas) {
        return new TrilhaArquivo("1", "2026-10-16", dados, etapas, faixas, "Aviso", List.of());
    }

    private TrilhaArquivo valida() {
        return arquivo(dados(), etapasValidas(), faixasValidas());
    }

    private TrilhaArquivo comDados(DadosBuilder dados) {
        return arquivo(dados.build(), etapasValidas(), faixasValidas());
    }

    private TrilhaArquivo comPrimeiraEtapa(EtapaBuilder etapa) {
        List<EtapaArquivo> etapas = etapasValidas();
        etapas.set(0, etapa.build());
        return arquivo(dados(), etapas, faixasValidas());
    }

    private TrilhaArquivo comPrimeiroRecurso(RecursoBuilder recurso) {
        EtapaBuilder etapa = new EtapaBuilder();
        etapa.recursos = List.of(recurso.build());
        return comPrimeiraEtapa(etapa);
    }

    private TrilhaArquivo comPrimeiraFaixa(FaixaBuilder faixa) {
        List<FaixaSalarialArquivo> faixas = faixasValidas();
        faixas.set(0, faixa.build());
        return arquivo(dados(), etapasValidas(), faixas);
    }

    private List<String> validar(TrilhaArquivo arquivo) {
        return validador.validar(SLUG, arquivo);
    }

    // ---------- arquivo e cabeçalho ----------

    @Test
    void deveAceitarUmaTrilhaValida() {
        assertThat(validar(valida())).isEmpty();
    }

    @Test
    void deveRecusarUmArquivoSemOsDadosDaTrilha() {
        assertThat(validar(null))
                .containsExactly(LOCAL + ": o arquivo não tem os dados da trilha");
        assertThat(validar(arquivo(null, etapasValidas(), faixasValidas())))
                .containsExactly(LOCAL + ": o arquivo não tem os dados da trilha");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarVersaoEmBranco(String versao) {
        TrilhaArquivo arquivo = new TrilhaArquivo(versao, "2026-10-16", dados(), etapasValidas(),
                faixasValidas(), "Aviso", List.of());

        assertThat(validar(arquivo)).containsExactly(LOCAL + ": falta a versão");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAvisosDeSalarioEmBranco(String avisos) {
        TrilhaArquivo arquivo = new TrilhaArquivo("1", "2026-10-16", dados(), etapasValidas(),
                faixasValidas(), avisos, List.of());

        assertThat(validar(arquivo)).containsExactly(LOCAL + ": faltam os avisos de salário");
    }

    @Test
    void deveRecusarDataDeGeracaoInvalidaOuAusente() {
        TrilhaArquivo invalida = new TrilhaArquivo("1", "ontem", dados(), etapasValidas(),
                faixasValidas(), "Aviso", List.of());
        TrilhaArquivo ausente = new TrilhaArquivo("1", null, dados(), etapasValidas(),
                faixasValidas(), "Aviso", List.of());

        assertThat(validar(invalida)).containsExactly(LOCAL + ": geradoEm: Data inválida: ontem");
        assertThat(validar(ausente)).containsExactly(LOCAL + ": geradoEm: Data não informada");
    }

    // ---------- dados da trilha ----------

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarSlugEmBranco(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.slug = valor;

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o slug");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarNomeEmBranco(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.nome = valor;

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o nome");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarResumoEmBranco(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.resumo = valor;

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o resumo");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarTextoDaTrilhaEmBranco(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.textoTrilha = valor;

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o texto da trilha");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarParaQuemEEmBranco(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.paraQuemE = valor;

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o 'para quem é'");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void deveRecusarHorasPorSemanaInvalidas(Integer valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.horasPorSemana = valor;

        assertThat(validar(comDados(dados)))
                .containsExactly(LOCAL + ": as horas por semana devem ser maiores que zero");
    }

    @Test
    void deveRecusarDuracaoTotalAusente() {
        DadosBuilder dados = new DadosBuilder();
        dados.duracaoTotalSemanas = null;

        assertThat(validar(comDados(dados)))
                .containsExactly(LOCAL + ": a duração total em semanas deve ser maior que zero");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void deveRecusarDuracaoTotalNaoPositivaEAindaApontarADiferencaComAsEtapas(int valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.duracaoTotalSemanas = valor;

        assertThat(validar(comDados(dados))).containsExactly(
                LOCAL + ": a duração total em semanas deve ser maior que zero",
                LOCAL + ": a soma das semanas das etapas (10) é diferente da duração total (" + valor + ")");
    }

    @Test
    void deveRecusarNivelDeEntradaAusenteOuDesconhecido() {
        DadosBuilder ausente = new DadosBuilder();
        ausente.nivelEntrada = null;
        DadosBuilder desconhecido = new DadosBuilder();
        desconhecido.nivelEntrada = "Expert";

        assertThat(validar(comDados(ausente))).containsExactly(LOCAL + ": Nível de entrada não informado");
        assertThat(validar(comDados(desconhecido))).containsExactly(LOCAL + ": Nível de entrada desconhecido: Expert");
    }

    @Test
    void deveRecusarTrilhaSemDadosDaArea() {
        DadosBuilder dados = new DadosBuilder();
        dados.area = null;

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": faltam os dados da área");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaDaTrilhaSemNumero(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.area = FabricaConteudo.areaDaTrilha(valor, "area-1", "Area 1");

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o número da área");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaDaTrilhaSemSlug(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.area = FabricaConteudo.areaDaTrilha("01", valor, "Area 1");

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o slug da área");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarAreaDaTrilhaSemNome(String valor) {
        DadosBuilder dados = new DadosBuilder();
        dados.area = FabricaConteudo.areaDaTrilha("01", "area-1", valor);

        assertThat(validar(comDados(dados))).containsExactly(LOCAL + ": falta o nome da área");
    }

    // ---------- etapas ----------

    @Test
    void deveRecusarTrilhaSemEtapas() {
        assertThat(validar(arquivo(dados(), null, faixasValidas())))
                .containsExactly(LOCAL + ": não tem etapas");
        assertThat(validar(arquivo(dados(), List.of(), faixasValidas())))
                .containsExactly(LOCAL + ": não tem etapas");
    }

    @Test
    void deveRecusarUmaEtapaNulaEApontarAsSemanasQueFaltam() {
        List<EtapaArquivo> etapas = etapasValidas();
        etapas.set(0, null);

        assertThat(validar(arquivo(dados(), etapas, faixasValidas()))).containsExactly(
                LOCAL_ETAPA + ": está vazia",
                LOCAL + ": a soma das semanas das etapas (6) é diferente da duração total (10)");
    }

    @Test
    void deveRecusarEtapaSemOrdemOuComOrdemErrada() {
        EtapaBuilder semOrdem = new EtapaBuilder();
        semOrdem.ordem = null;
        EtapaBuilder ordemErrada = new EtapaBuilder();
        ordemErrada.ordem = 5;

        assertThat(validar(comPrimeiraEtapa(semOrdem))).containsExactly(LOCAL_ETAPA + ": falta a ordem");
        assertThat(validar(comPrimeiraEtapa(ordemErrada)))
                .containsExactly(LOCAL_ETAPA + ": a ordem deveria ser 1, mas é 5");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarEtapaSemTitulo(String valor) {
        EtapaBuilder etapa = new EtapaBuilder();
        etapa.titulo = valor;

        assertThat(validar(comPrimeiraEtapa(etapa))).containsExactly(LOCAL_ETAPA + ": falta o título");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarEtapaSemDescricao(String valor) {
        EtapaBuilder etapa = new EtapaBuilder();
        etapa.descricao = valor;

        assertThat(validar(comPrimeiraEtapa(etapa))).containsExactly(LOCAL_ETAPA + ": falta a descrição");
    }

    @Test
    void deveRecusarEtapaComNivelAusenteOuDesconhecido() {
        EtapaBuilder ausente = new EtapaBuilder();
        ausente.nivel = null;
        EtapaBuilder desconhecido = new EtapaBuilder();
        desconhecido.nivel = "Expert";

        assertThat(validar(comPrimeiraEtapa(ausente)))
                .containsExactly(LOCAL_ETAPA + ": Nível de etapa não informado");
        assertThat(validar(comPrimeiraEtapa(desconhecido)))
                .containsExactly(LOCAL_ETAPA + ": Nível de etapa desconhecido: Expert");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void deveRecusarEtapaComDuracaoInvalidaEApontarASomaQueNaoFecha(Integer valor) {
        EtapaBuilder etapa = new EtapaBuilder();
        etapa.duracaoSemanas = valor;

        assertThat(validar(comPrimeiraEtapa(etapa))).containsExactly(
                LOCAL_ETAPA + ": a duração em semanas deve ser maior que zero",
                LOCAL + ": a soma das semanas das etapas (6) é diferente da duração total (10)");
    }

    @Test
    void deveRecusarQuandoASomaDasSemanasDiferenteDaDuracaoTotal() {
        DadosBuilder dados = new DadosBuilder();
        dados.duracaoTotalSemanas = 12;

        assertThat(validar(comDados(dados)))
                .containsExactly(LOCAL + ": a soma das semanas das etapas (10) é diferente da duração total (12)");
    }

    @Test
    void deveAceitarEtapaSemProjetoPratico() {
        EtapaBuilder etapa = new EtapaBuilder();
        etapa.projetoPratico = null;

        assertThat(validar(comPrimeiraEtapa(etapa))).isEmpty();
    }

    // ---------- recursos ----------

    @Test
    void deveRecusarEtapaSemRecursos() {
        EtapaBuilder semLista = new EtapaBuilder();
        semLista.recursos = null;
        EtapaBuilder semItens = new EtapaBuilder();
        semItens.recursos = List.of();

        assertThat(validar(comPrimeiraEtapa(semLista))).containsExactly(LOCAL_ETAPA + ": não tem recursos");
        assertThat(validar(comPrimeiraEtapa(semItens))).containsExactly(LOCAL_ETAPA + ": não tem recursos");
    }

    @Test
    void deveRecusarUmRecursoNulo() {
        EtapaBuilder etapa = new EtapaBuilder();
        etapa.recursos = Collections.singletonList(null);

        assertThat(validar(comPrimeiraEtapa(etapa))).containsExactly(LOCAL_RECURSO + ": está vazio");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarRecursoSemTitulo(String valor) {
        RecursoBuilder recurso = new RecursoBuilder();
        recurso.titulo = valor;

        assertThat(validar(comPrimeiroRecurso(recurso))).containsExactly(LOCAL_RECURSO + ": falta o título");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarRecursoSemUrl(String valor) {
        RecursoBuilder recurso = new RecursoBuilder();
        recurso.url = valor;

        assertThat(validar(comPrimeiroRecurso(recurso))).containsExactly(LOCAL_RECURSO + ": falta a url");
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://exemplo.com/curso", "ftp://exemplo.com", "exemplo.com/curso"})
    void deveRecusarUrlQueNaoComecaComHttps(String valor) {
        RecursoBuilder recurso = new RecursoBuilder();
        recurso.url = valor;

        assertThat(validar(comPrimeiroRecurso(recurso)))
                .containsExactly(LOCAL_RECURSO + ": a url deve começar com https://");
    }

    @Test
    void deveRecusarRecursoComTipoIdiomaOuDataInvalidos() {
        RecursoBuilder tipo = new RecursoBuilder();
        tipo.tipo = "podcast";
        RecursoBuilder idioma = new RecursoBuilder();
        idioma.idioma = "pt";
        RecursoBuilder data = new RecursoBuilder();
        data.verificadoEm = "ontem";

        assertThat(validar(comPrimeiroRecurso(tipo)))
                .containsExactly(LOCAL_RECURSO + ": Tipo do recurso desconhecido: podcast");
        assertThat(validar(comPrimeiroRecurso(idioma)))
                .containsExactly(LOCAL_RECURSO + ": Idioma desconhecido: pt");
        assertThat(validar(comPrimeiroRecurso(data)))
                .containsExactly(LOCAL_RECURSO + ": verificadoEm: Data inválida: ontem");
    }

    @Test
    void deveRecusarAcessoDesconhecidoSemApontarContradicoes() {
        RecursoBuilder recurso = new RecursoBuilder();
        recurso.acesso = "Gratuito";

        assertThat(validar(comPrimeiroRecurso(recurso)))
                .containsExactly(LOCAL_RECURSO + ": Acesso desconhecido: Gratuito");
    }

    @Test
    void deveRecusarRecursoSemOCampoGratuito() {
        RecursoBuilder recurso = new RecursoBuilder();
        recurso.gratuito = null;

        assertThat(validar(comPrimeiroRecurso(recurso)))
                .containsExactly(LOCAL_RECURSO + ": falta o campo gratuito");
    }

    @Test
    void deveRecusarGratuitoQueContradizOAcesso() {
        RecursoBuilder pagoMarcadoComoGratuito = new RecursoBuilder();
        pagoMarcadoComoGratuito.acesso = "pago";
        pagoMarcadoComoGratuito.gratuito = true;
        pagoMarcadoComoGratuito.observacao = "Pago: assinatura mensal";

        RecursoBuilder gratuitoMarcadoComoPago = new RecursoBuilder();
        gratuitoMarcadoComoPago.gratuito = false;

        assertThat(validar(comPrimeiroRecurso(pagoMarcadoComoGratuito)))
                .containsExactly(LOCAL_RECURSO + ": 'gratuito' (true) contradiz o acesso 'pago'");
        assertThat(validar(comPrimeiroRecurso(gratuitoMarcadoComoPago)))
                .containsExactly(LOCAL_RECURSO + ": 'gratuito' (false) contradiz o acesso 'gratuito'");
    }

    @Test
    void deveRecusarObservacaoQueContradizOAcesso() {
        RecursoBuilder pagoSemObservacaoDePago = new RecursoBuilder();
        pagoSemObservacaoDePago.acesso = "pago";
        pagoSemObservacaoDePago.gratuito = false;
        pagoSemObservacaoDePago.observacao = "Assinatura mensal";

        RecursoBuilder gratuitoComObservacaoDePago = new RecursoBuilder();
        gratuitoComObservacaoDePago.observacao = "Pago apenas o certificado";

        assertThat(validar(comPrimeiroRecurso(pagoSemObservacaoDePago))).containsExactly(
                LOCAL_RECURSO + ": a observação contradiz o acesso 'pago' (itens pagos começam com 'Pago')");
        assertThat(validar(comPrimeiroRecurso(gratuitoComObservacaoDePago))).containsExactly(
                LOCAL_RECURSO + ": a observação contradiz o acesso 'gratuito' (itens pagos começam com 'Pago')");
    }

    @Test
    void deveAceitarOsTresTiposDeAcessoCoerentes() {
        RecursoBuilder pago = new RecursoBuilder();
        pago.acesso = "pago";
        pago.gratuito = false;
        pago.observacao = "Pago: assinatura mensal";

        RecursoBuilder naoConfirmado = new RecursoBuilder();
        naoConfirmado.acesso = "nao_confirmado";
        naoConfirmado.gratuito = false;
        naoConfirmado.observacao = "Preço não confirmado";

        RecursoBuilder gratuitoSemObservacao = new RecursoBuilder();
        gratuitoSemObservacao.observacao = null;

        assertThat(validar(comPrimeiroRecurso(pago))).isEmpty();
        assertThat(validar(comPrimeiroRecurso(naoConfirmado))).isEmpty();
        assertThat(validar(comPrimeiroRecurso(gratuitoSemObservacao))).isEmpty();
    }

    // ---------- faixas salariais ----------

    @Test
    void deveRecusarTrilhaSemFaixas() {
        assertThat(validar(arquivo(dados(), etapasValidas(), null)))
                .containsExactly(LOCAL + ": não tem faixas salariais");
    }

    @Test
    void deveRecusarTrilhaSemTresFaixas() {
        List<FaixaSalarialArquivo> duas = faixasValidas().subList(0, 2);

        assertThat(validar(arquivo(dados(), etapasValidas(), duas)))
                .containsExactly(LOCAL + ": esperadas 3 faixas salariais, mas vieram 2");
    }

    @Test
    void deveRecusarUmaFaixaNula() {
        List<FaixaSalarialArquivo> faixas = faixasValidas();
        faixas.set(0, null);

        assertThat(validar(arquivo(dados(), etapasValidas(), faixas)))
                .containsExactly(LOCAL_FAIXA + ": está vazia");
    }

    @Test
    void deveRecusarNivelSalarialRepetido() {
        List<FaixaSalarialArquivo> faixas = faixasValidas();
        faixas.set(1, new FaixaBuilder().build());

        assertThat(validar(arquivo(dados(), etapasValidas(), faixas)))
                .containsExactly(LOCAL + ", faixa nº 2: o nível 'Júnior' aparece mais de uma vez");
    }

    @Test
    void deveRecusarNivelSalarialDesconhecido() {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.nivel = "Estagiário";

        assertThat(validar(comPrimeiraFaixa(faixa)))
                .containsExactly(LOCAL_FAIXA + ": Nível salarial desconhecido: Estagiário");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void deveRecusarMinimoInvalido(Integer valor) {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.minimo = valor;

        assertThat(validar(comPrimeiraFaixa(faixa)))
                .containsExactly(LOCAL_FAIXA + ": o mínimo deve ser maior que zero");
    }

    @Test
    void deveRecusarFaixaSemMaximoOuSemValorDeReferencia() {
        FaixaBuilder semMaximo = new FaixaBuilder();
        semMaximo.maximo = null;
        FaixaBuilder semReferencia = new FaixaBuilder();
        semReferencia.valorReferencia = null;

        assertThat(validar(comPrimeiraFaixa(semMaximo))).containsExactly(LOCAL_FAIXA + ": falta o máximo");
        assertThat(validar(comPrimeiraFaixa(semReferencia)))
                .containsExactly(LOCAL_FAIXA + ": falta o valor de referência");
    }

    @Test
    void deveRecusarMinimoMaiorQueOMaximo() {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.minimo = 5000;
        faixa.maximo = 4000;

        assertThat(validar(comPrimeiraFaixa(faixa)))
                .containsExactly(LOCAL_FAIXA + ": o mínimo (5000) é maior que o máximo (4000)");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarFaixaSemTipoDeReferencia(String valor) {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.tipoReferencia = valor;

        assertThat(validar(comPrimeiraFaixa(faixa)))
                .containsExactly(LOCAL_FAIXA + ": falta o tipo de referência");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarFaixaSemFonte(String valor) {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.fonte = valor;

        assertThat(validar(comPrimeiraFaixa(faixa))).containsExactly(LOCAL_FAIXA + ": falta a fonte");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void deveRecusarFaixaSemObservacao(String valor) {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.observacao = valor;

        assertThat(validar(comPrimeiraFaixa(faixa))).containsExactly(LOCAL_FAIXA + ": falta a observação");
    }

    @Test
    void deveRecusarDataDeReferenciaInvalidaOuAusente() {
        FaixaBuilder invalida = new FaixaBuilder();
        invalida.dataReferencia = "setembro";
        FaixaBuilder ausente = new FaixaBuilder();
        ausente.dataReferencia = null;

        assertThat(validar(comPrimeiraFaixa(invalida)))
                .containsExactly(LOCAL_FAIXA + ": dataReferencia: Mês inválido: setembro");
        assertThat(validar(comPrimeiraFaixa(ausente)))
                .containsExactly(LOCAL_FAIXA + ": dataReferencia: Mês não informado");
    }

    @Test
    void deveRecusarFaixaSemOCampoDadosLimitados() {
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.dadosLimitados = null;

        assertThat(validar(comPrimeiraFaixa(faixa)))
                .containsExactly(LOCAL_FAIXA + ": falta o campo dadosLimitados");
    }

    // ---------- tudo junto ----------

    @Test
    void deveRelatarTodosOsProblemasJuntosNaOrdemDoArquivo() {
        DadosBuilder dados = new DadosBuilder();
        dados.nome = null;
        FaixaBuilder faixa = new FaixaBuilder();
        faixa.maximo = null;
        List<FaixaSalarialArquivo> faixas = faixasValidas();
        faixas.set(0, faixa.build());

        assertThat(validar(arquivo(dados.build(), etapasValidas(), faixas))).containsExactly(
                LOCAL + ": falta o nome",
                LOCAL_FAIXA + ": falta o máximo");
    }
}
