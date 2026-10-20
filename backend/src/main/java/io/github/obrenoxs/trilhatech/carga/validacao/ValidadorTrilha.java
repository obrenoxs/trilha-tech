package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.*;
import io.github.obrenoxs.trilhatech.carga.conversao.ConversorTexto;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.Acesso;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelSalarial;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidadorTrilha {

    public List<String> validar(String slugDoCatalogo, TrilhaArquivo arquivo) {
        List<String> problemas = new ArrayList<>();
        String local = "Trilha '" + slugDoCatalogo + "'";

        if (arquivo == null || arquivo.trilha() == null) {
            problemas.add(local + ": o arquivo não tem os dados da trilha");
            return problemas;
        }

        if (textoVazio(arquivo.versao())) {
            problemas.add(local + ": falta a versão");
        }

        if (textoVazio(arquivo.avisosSalario())) {
            problemas.add(local + ": faltam os avisos de salário");
        }

        try {
            ConversorTexto.traduzirData(arquivo.geradoEm());
        } catch (IllegalArgumentException e) {
            problemas.add(local + ": geradoEm: " + e.getMessage());
        }

        validarDados(arquivo.trilha(), local, problemas);
        validarEtapas(arquivo.etapas(), arquivo.trilha().duracaoTotalSemanas(), local, problemas);
        validarFaixas(arquivo.faixaSalarial(), local, problemas);

        return problemas;
    }

    private void validarDados(DadosTrilhaArquivo dados, String local, List<String> problemas) {
        if (textoVazio(dados.slug())) {
            problemas.add(local + ": falta o slug");
        }

        if (textoVazio(dados.nome())) {
            problemas.add(local + ": falta o nome");
        }

        if (textoVazio(dados.resumo())) {
            problemas.add(local + ": falta o resumo");
        }

        if (textoVazio(dados.textoTrilha())) {
            problemas.add(local + ": falta o texto da trilha");
        }

        if (textoVazio(dados.paraQuemE())) {
            problemas.add(local + ": falta o 'para quem é'");
        }

        if (dados.horasPorSemana() == null || dados.horasPorSemana() <= 0) {
            problemas.add(local + ": as horas por semana devem ser maiores que zero");
        }

        if (dados.duracaoTotalSemanas() == null || dados.duracaoTotalSemanas() <= 0) {
            problemas.add(local + ": a duração total em semanas deve ser maior que zero");
        }

        try {
            ConversorTexto.traduzirNivelEntrada(dados.nivelEntrada());
        } catch (IllegalArgumentException e) {
            problemas.add(local + ": " + e.getMessage());
        }

        if (dados.area() == null) {
            problemas.add(local + ": faltam os dados da área");
        } else {
            if (textoVazio(dados.area().numero())) {
                problemas.add(local + ": falta o número da área");
            }
            if (textoVazio(dados.area().slug())) {
                problemas.add(local + ": falta o slug da área");
            }
            if (textoVazio(dados.area().nome())) {
                problemas.add(local + ": falta o nome da área");
            }
        }
    }

    private boolean textoVazio(String texto) {
        return texto == null || texto.isBlank();
    }

    private void validarEtapas(List<EtapaArquivo> etapas, Integer duracaoTotal,
                               String localDaTrilha, List<String> problemas) {
        if (etapas == null || etapas.isEmpty()) {
            problemas.add(localDaTrilha + ": não tem etapas");
            return;
        }

        int somaDasSemanas = 0;

        for (int i = 0; i < etapas.size(); i++) {
            EtapaArquivo etapa = etapas.get(i);
            String local = localDaTrilha + ", etapa nº " + (i + 1);

            if (etapa == null) {
                problemas.add(local + ": está vazia");
                continue;
            }

            if (etapa.ordem() == null) {
                problemas.add(local + ": falta a ordem");
            } else if (etapa.ordem() != i + 1) {
                problemas.add(local + ": a ordem deveria ser " + (i + 1) + ", mas é " + etapa.ordem());
            }

            if (textoVazio(etapa.titulo())) {
                problemas.add(local + ": falta o título");
            }

            if (textoVazio(etapa.descricao())) {
                problemas.add(local + ": falta a descrição");
            }

            try {
                ConversorTexto.traduzirNivelEtapa(etapa.nivel());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": " + e.getMessage());
            }

            if (etapa.duracaoSemanas() == null || etapa.duracaoSemanas() <= 0) {
                problemas.add(local + ": a duração em semanas deve ser maior que zero");
            } else {
                somaDasSemanas += etapa.duracaoSemanas();
            }

            validarRecursos(etapa, local, problemas);
        }

        if (duracaoTotal != null && somaDasSemanas != duracaoTotal) {
            problemas.add(localDaTrilha + ": a soma das semanas das etapas (" + somaDasSemanas
                    + ") é diferente da duração total (" + duracaoTotal + ")");
        }
    }

    private void validarRecursos(EtapaArquivo etapa, String localDaEtapa, List<String> problemas) {
        if (etapa.recursos() == null || etapa.recursos().isEmpty()) {
            problemas.add(localDaEtapa + ": não tem recursos");
            return;
        }

        for (int j = 0; j < etapa.recursos().size(); j++) {
            RecursoArquivo recurso = etapa.recursos().get(j);
            String local = localDaEtapa + ", recurso nº " + (j + 1);

            if (recurso == null) {
                problemas.add(local + ": está vazio");
                continue;
            }

            if (textoVazio(recurso.titulo())) {
                problemas.add(local + ": falta o título");
            }

            if (textoVazio(recurso.url())) {
                problemas.add(local + ": falta a url");
            } else if (!recurso.url().startsWith("https://")) {
                problemas.add(local + ": a url deve começar com https://");
            }

            try {
                ConversorTexto.traduzirTipoRecurso(recurso.tipo());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": " + e.getMessage());
            }

            try {
                ConversorTexto.traduzirIdioma(recurso.idioma());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": " + e.getMessage());
            }

            try {
                ConversorTexto.traduzirData(recurso.verificadoEm());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": verificadoEm: " + e.getMessage());
            }

            Acesso acesso = null;
            try {
                acesso = ConversorTexto.traduzirAcesso(recurso.acesso());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": " + e.getMessage());
            }

            if (recurso.gratuito() == null) {
                problemas.add(local + ": falta o campo gratuito");
            }

            if (acesso != null && recurso.gratuito() != null) {
                boolean observacaoDePago = recurso.observacao() != null
                        && recurso.observacao().startsWith("Pago");

                if (recurso.gratuito() != (acesso == Acesso.GRATUITO)) {
                    problemas.add(local + ": 'gratuito' (" + recurso.gratuito()
                            + ") contradiz o acesso '" + recurso.acesso() + "'");
                }

                if (observacaoDePago != (acesso == Acesso.PAGO)) {
                    problemas.add(local + ": a observação contradiz o acesso '" + recurso.acesso()
                            + "' (itens pagos começam com 'Pago')");
                }
            }
        }
    }

    private static final int FAIXAS_ESPERADAS = 3;

    private void validarFaixas(List<FaixaSalarialArquivo> faixas, String localDaTrilha, List<String> problemas) {
        if (faixas == null) {
            problemas.add(localDaTrilha + ": não tem faixas salariais");
            return;
        }

        if (faixas.size() != FAIXAS_ESPERADAS) {
            problemas.add(localDaTrilha + ": esperadas " + FAIXAS_ESPERADAS
                    + " faixas salariais, mas vieram " + faixas.size());
        }

        Set<NivelSalarial> niveisVistos = new HashSet<>();

        for (int i = 0; i < faixas.size(); i++) {
            FaixaSalarialArquivo faixa = faixas.get(i);
            String local = localDaTrilha + ", faixa nº " + (i + 1);

            if (faixa == null) {
                problemas.add(local + ": está vazia");
                continue;
            }

            try {
                NivelSalarial nivel = ConversorTexto.traduzirNivelSalarial(faixa.nivel());
                if (!niveisVistos.add(nivel)) {
                    problemas.add(local + ": o nível '" + faixa.nivel() + "' aparece mais de uma vez");
                }
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": " + e.getMessage());
            }

            if (faixa.minimo() == null || faixa.minimo() <= 0) {
                problemas.add(local + ": o mínimo deve ser maior que zero");
            }

            if (faixa.maximo() == null) {
                problemas.add(local + ": falta o máximo");
            }

            if (faixa.valorReferencia() == null) {
                problemas.add(local + ": falta o valor de referência");
            }

            if (faixa.minimo() != null && faixa.maximo() != null && faixa.minimo() > faixa.maximo()) {
                problemas.add(local + ": o mínimo (" + faixa.minimo()
                        + ") é maior que o máximo (" + faixa.maximo() + ")");
            }

            if (textoVazio(faixa.tipoReferencia())) {
                problemas.add(local + ": falta o tipo de referência");
            }

            if (textoVazio(faixa.fonte())) {
                problemas.add(local + ": falta a fonte");
            }

            if (textoVazio(faixa.observacao())) {
                problemas.add(local + ": falta a observação");
            }

            try {
                ConversorTexto.traduzirMes(faixa.dataReferencia());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": dataReferencia: " + e.getMessage());
            }

            if (faixa.dadosLimitados() == null) {
                problemas.add(local + ": falta o campo dadosLimitados");
            }
        }
    }
}