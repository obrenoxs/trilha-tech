package io.github.obrenoxs.trilhatech.quiz.resultado.calculo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculadoraPontuacao {

    private static final int TAMANHO_DO_RESULTADO = 3;

    public List<Recomendacao> calcular(List<Escolha> escolhas, Map<String, Integer> ordemDesempateArea) {
        Map<String, Integer> pontos = new HashMap<>();
        Map<String, Integer> vezesPrincipal = new HashMap<>();

        for (String slug : ordemDesempateArea.keySet()) {
            pontos.put(slug, 0);
            vezesPrincipal.put(slug, 0);
        }

        for (Escolha escolha : escolhas) {
            pontos.merge(escolha.areaPrincipal(), 2, Integer::sum);
            vezesPrincipal.merge(escolha.areaPrincipal(), 1, Integer::sum);
            pontos.merge(escolha.areaSecundaria(), 1, Integer::sum);
        }

        Comparator<String> criterios = Comparator
                .comparingInt((String slug) -> pontos.get(slug)).reversed()
                .thenComparing(Comparator.comparingInt((String slug) -> vezesPrincipal.get(slug)).reversed())
                .thenComparingInt(slug -> ordemDesempateArea.get(slug));

        List<String> ranking = new ArrayList<>(ordemDesempateArea.keySet());
        ranking.sort(criterios);

        int pontosDoPrimeiro = pontos.get(ranking.get(0));
        int quantidade = Math.min(TAMANHO_DO_RESULTADO, ranking.size());

        List<Recomendacao> resultado = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            String slug = ranking.get(i);
            int barra = pontosDoPrimeiro == 0
                    ? 0
                    : (int) Math.round(100.0 * pontos.get(slug) / pontosDoPrimeiro);
            resultado.add(new Recomendacao(i + 1, slug, barra));
        }

        return resultado;
    }
}