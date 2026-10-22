package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.*;
import io.github.obrenoxs.trilhatech.carga.exception.ConteudoInvalidoException;
import io.github.obrenoxs.trilhatech.carga.leitura.LeitorConteudo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidadorConteudo {

    private static final String PASTA_DOS_PDFS = "conteudo/pdfs/";

    private final LeitorConteudo leitorConteudo;
    private final ValidadorCatalogo validadorCatalogo;
    private final ValidadorTrilha validadorTrilha;
    private final ValidadorConsistencia validadorConsistencia;
    private final ValidadorQuiz validadorQuiz;

    public ValidadorConteudo(LeitorConteudo leitorConteudo, ValidadorCatalogo validadorCatalogo, ValidadorTrilha validadorTrilha, ValidadorConsistencia validadorConsistencia, ValidadorQuiz validadorQuiz) {
        this.leitorConteudo = leitorConteudo;
        this.validadorCatalogo = validadorCatalogo;
        this.validadorTrilha = validadorTrilha;
        this.validadorConsistencia = validadorConsistencia;
        this.validadorQuiz = validadorQuiz;
    }

    public ConteudoCarregado carregarEValidar() {
        CatalogoArquivo catalogo = leitorConteudo.lerCatalogo();

        List<String> problemasDoCatalogo = validadorCatalogo.validar(catalogo);
        if (!problemasDoCatalogo.isEmpty()) {
            throw new ConteudoInvalidoException(problemasDoCatalogo);
        }

        List<String> problemas = new ArrayList<>();
        List<TrilhaArquivo> trilhasLidas = new ArrayList<>();

        for (AreaCatalogoArquivo area : catalogo.areas()) {
            for (TrilhaCatalogoArquivo trilhaCatalogo : area.trilhas()) {
                TrilhaArquivo arquivo = leitorConteudo.lerTrilha(trilhaCatalogo.slug());
                trilhasLidas.add(arquivo);

                problemas.addAll(validadorTrilha.validar(trilhaCatalogo.slug(), arquivo));
                problemas.addAll(validadorConsistencia.validar(area, trilhaCatalogo, arquivo));
                ClassPathResource pdf = new ClassPathResource(PASTA_DOS_PDFS + "roadmap-" + trilhaCatalogo.slug() + ".pdf");
                if (!pdf.exists()) {
                    problemas.add("Trilha '" + trilhaCatalogo.slug() + "': falta o PDF " + pdf.getPath());
                }
            }
        }

        QuizArquivo quiz = leitorConteudo.lerQuiz();

        Set<String> slugsDasAreas = new HashSet<>();
        for (AreaCatalogoArquivo area : catalogo.areas()) {
            slugsDasAreas.add(area.slug());
        }

        problemas.addAll(validadorQuiz.validar(quiz, slugsDasAreas));

        if (!problemas.isEmpty()) {
            throw new ConteudoInvalidoException(problemas);
        }

        return new ConteudoCarregado(catalogo, trilhasLidas, quiz);
    }
}
