package io.github.obrenoxs.trilhatech.carga.conversao;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public final class ConversorTexto {

    private ConversorTexto() {
    }

    public static NivelEntrada traduzirNivelEntrada(String nivelEntrada) {
        if (nivelEntrada == null) {
            throw new IllegalArgumentException("Nível de entrada não informado");
        }
        return switch (nivelEntrada) {
            case "Iniciante" -> NivelEntrada.INICIANTE;
            case "Intermediário" -> NivelEntrada.INTERMEDIARIO;
            case "Avançado" -> NivelEntrada.AVANCADO;
            default -> throw new IllegalArgumentException("Nível de entrada desconhecido: " + nivelEntrada);
        };
    }

    public static NivelEtapa traduzirNivelEtapa(String nivelEtapa) {
        if (nivelEtapa == null) {
            throw new IllegalArgumentException("Nível de etapa não informado");
        }
        return switch (nivelEtapa) {
            case "Básico" -> NivelEtapa.BASICO;
            case "Intermediário" -> NivelEtapa.INTERMEDIARIO;
            case "Avançado" -> NivelEtapa.AVANCADO;
            default -> throw new IllegalArgumentException("Nível de etapa desconhecido: " + nivelEtapa);
        };
    }

    public static NivelSalarial traduzirNivelSalarial(String nivelSalarial) {
        if (nivelSalarial == null) {
            throw new IllegalArgumentException("Nível salarial não informado");
        }
        return switch (nivelSalarial) {
            case "Júnior" -> NivelSalarial.JUNIOR;
            case "Pleno" -> NivelSalarial.PLENO;
            case "Sênior" -> NivelSalarial.SENIOR;
            default -> throw new IllegalArgumentException("Nível salarial desconhecido: " + nivelSalarial);
        };
    }

    public static Idioma traduzirIdioma(String idioma) {
        if (idioma == null) {
            throw new IllegalArgumentException("Idioma não informado");
        }
        return switch (idioma) {
            case "PT" -> Idioma.PT;
            case "EN" -> Idioma.EN;
            default -> throw new IllegalArgumentException("Idioma desconhecido: " + idioma);
        };
    }

    public static Acesso traduzirAcesso(String acesso) {
        if (acesso == null) {
            throw new IllegalArgumentException("Acesso não informado");
        }
        return switch (acesso) {
            case "gratuito" -> Acesso.GRATUITO;
            case "pago" -> Acesso.PAGO;
            case "nao_confirmado" -> Acesso.NAO_CONFIRMADO;
            default -> throw new IllegalArgumentException("Acesso desconhecido: " + acesso);
        };
    }

    public static TipoRecurso traduzirTipoRecurso(String tipoRecurso) {
        if (tipoRecurso == null) {
            throw new IllegalArgumentException("Tipo do recurso não informado");
        }
        return switch (tipoRecurso) {
            case "documentação" -> TipoRecurso.DOCUMENTACAO;
            case "curso" -> TipoRecurso.CURSO;
            case "livro" -> TipoRecurso.LIVRO;
            case "formação" -> TipoRecurso.FORMACAO;
            case "certificação" -> TipoRecurso.CERTIFICACAO;
            case "tutorial" -> TipoRecurso.TUTORIAL;
            case "guia" -> TipoRecurso.GUIA;
            case "vídeo" -> TipoRecurso.VIDEO;
            case "ferramenta" -> TipoRecurso.FERRAMENTA;
            default -> throw new IllegalArgumentException("Tipo do recurso desconhecido: " + tipoRecurso);
        };
    }

    public static LocalDate traduzirData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Data não informada");
        }
        try {
            return LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: "+ data, e);
        }
    }

    public static LocalDate traduzirMes(String mes) {
        if (mes == null) {
            throw new IllegalArgumentException("Mês não informado");
        }
        try {
            return YearMonth.parse(mes).atDay(1);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Mês inválido: "+ mes, e);
        }
    }
}
