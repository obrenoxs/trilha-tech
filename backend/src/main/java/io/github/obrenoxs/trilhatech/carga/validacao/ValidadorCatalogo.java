package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.CatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.conversao.ConversorTexto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidadorCatalogo {

    private static final int AREAS_ESPERADAS = 8;
    private static final int TRILHAS_ESPERADAS = 36;

    public List<String> validar(CatalogoArquivo catalogoArquivo) {
        List<String> problemas = new ArrayList<>();

        if (catalogoArquivo == null || catalogoArquivo.areas() == null) {
            problemas.add("O catálogo não tem a lista de áreas");
            return problemas;
        }

        if (catalogoArquivo.areas().size() != AREAS_ESPERADAS) {
            problemas.add("Esperadas " + AREAS_ESPERADAS + " áreas, mas vieram " + catalogoArquivo.areas().size());
        }

        int totalTrilhas = 0;
        Set<String> slugsVistos = new HashSet<>();
        Set<String> numerosVistos = new HashSet<>();
        Set<Integer> ordensDesempateVistas = new HashSet<>();
        Set<String> slugsDeTrilhaVistos = new HashSet<>();

        for (int i = 0; i < catalogoArquivo.areas().size(); i++) {
            AreaCatalogoArquivo area = catalogoArquivo.areas().get(i);
            String local = "Área nº " + (i + 1);

            if (area == null) {
                problemas.add(local + ": está vazia");
                continue;
            }

            if (textoVazio(area.numero())) {
                problemas.add(local + ": falta o número");
            }

            if (textoVazio(area.nome())) {
                problemas.add(local + ": falta o nome");
            }

            if (textoVazio(area.slug())) {
                problemas.add(local + ": falta o slug");
            }

            if (textoVazio(area.descricao())) {
                problemas.add(local + ": falta a descrição");
            }

            if (textoVazio(area.comoETrabalhar())) {
                problemas.add(local + ": falta Como é Trabalhar");
            }

            if (area.ordemDesempate() == null) {
                problemas.add(local + ": falta a ordem de desempate");
            }

            if (!textoVazio(area.slug()) && !slugsVistos.add(area.slug())) {
                problemas.add(local + ": o slug '" + area.slug() + "' já foi usado");
            }

            if (!textoVazio(area.numero()) && !numerosVistos.add(area.numero())) {
                problemas.add(local + ": o número '" + area.numero() + "' já foi usado");
            }

            if (area.ordemDesempate() != null && !ordensDesempateVistas.add(area.ordemDesempate())) {
                problemas.add(local + ": a ordem de desempate " + area.ordemDesempate() + " já foi usada");
            }

            if (area.trilhas() == null || area.trilhas().isEmpty()) {
                problemas.add(local + ": não tem trilhas");
            } else {
                totalTrilhas += area.trilhas().size();
                validarTrilhasDaArea(area, local, problemas, slugsDeTrilhaVistos);
            }
        }

        if (totalTrilhas != TRILHAS_ESPERADAS) {
            problemas.add("Esperados " + TRILHAS_ESPERADAS + " trilhas, mas vieram " + totalTrilhas);
        }

        return problemas;
    }

    private void validarTrilhasDaArea(AreaCatalogoArquivo area, String localDaArea, List<String> problemas, Set<String> slugsDeTrilhaVistos) {
        Set<Integer> ordensVistas = new HashSet<>();

        for (int j = 0; j < area.trilhas().size(); j++) {
            TrilhaCatalogoArquivo trilha = area.trilhas().get(j);
            String local = localDaArea + ", trilha nº " + (j + 1);

            if (trilha == null) {
                problemas.add(local + ": está vazia");
                continue;
            }

            if (textoVazio(trilha.slug())) {
                problemas.add(local + ": falta o slug");
            }

            if (textoVazio(trilha.nome())) {
                problemas.add(local + ": falta o nome");
            }

            if (trilha.ordem() == null) {
                problemas.add(local + ": falta a ordem");
            }

            if (trilha.duracaoTotalSemanas() == null || trilha.duracaoTotalSemanas() <= 0) {
                problemas.add(local + ": a duração total em semanas deve ser maior que zero");
            }

            if (!textoVazio(trilha.slug()) && !slugsDeTrilhaVistos.add(trilha.slug())) {
                problemas.add(local + ": o slug '" + trilha.slug() + "' já foi usado");
            }

            if (trilha.ordem() != null && !ordensVistas.add(trilha.ordem())) {
                problemas.add(local + ": a ordem " + trilha.ordem() + " já foi usada nesta área");
            }

            try {
                ConversorTexto.traduzirNivelEntrada(trilha.nivelEntrada());
            } catch (IllegalArgumentException e) {
                problemas.add(local + ": " + e.getMessage());
            }
        }
    }

    private boolean textoVazio(String texto) {
        return texto == null || texto.isBlank();
    }
}