package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AlternativaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.PerguntaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.QuizArquivo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ValidadorQuiz {

    @Component
    public class ValidadorQuiz {

        private static final int PERGUNTAS_ESPERADAS = 12;
        private static final int ALTERNATIVAS_ESPERADAS = 4;

        public List<String> validar(QuizArquivo quiz, Set<String> slugsDasAreas) {
            List<String> problemas = new ArrayList<>();

            if (quiz == null || quiz.perguntas() == null) {
                problemas.add("O quiz não tem a lista de perguntas");
                return problemas;
            }

            if (textoVazio(quiz.versao())) {
                problemas.add("Quiz: falta a versão");
            }

            if (quiz.perguntas().size() != PERGUNTAS_ESPERADAS) {
                problemas.add("Esperadas " + PERGUNTAS_ESPERADAS + " perguntas, mas vieram " + quiz.perguntas().size());
            }

            for (int i = 0; i < quiz.perguntas().size(); i++) {
                PerguntaArquivo pergunta = quiz.perguntas().get(i);
                String local = "Pergunta nº " + (i + 1);

                if (pergunta == null) {
                    problemas.add(local + ": está vazia");
                    continue;
                }

                if (pergunta.ordem() == null) {
                    problemas.add(local + ": falta a ordem");
                } else if (pergunta.ordem() != i + 1) {
                    problemas.add(local + ": a ordem deveria ser " + (i + 1) + ", mas é " + pergunta.ordem());
                }

                if (textoVazio(pergunta.texto())) {
                    problemas.add(local + ": falta o texto");
                }

                validarAlternativas(pergunta, local, slugsDasAreas, problemas);
            }

            return problemas;
        }

        private void validarAlternativas(PerguntaArquivo pergunta, String localDaPergunta,
                                         Set<String> slugsDasAreas, List<String> problemas) {
            if (pergunta.alternativas() == null) {
                problemas.add(localDaPergunta + ": não tem alternativas");
                return;
            }

            if (pergunta.alternativas().size() != ALTERNATIVAS_ESPERADAS) {
                problemas.add(localDaPergunta + ": esperadas " + ALTERNATIVAS_ESPERADAS
                        + " alternativas, mas vieram " + pergunta.alternativas().size());
            }

            for (int j = 0; j < pergunta.alternativas().size(); j++) {
                AlternativaArquivo alternativa = pergunta.alternativas().get(j);
                String local = localDaPergunta + ", alternativa nº " + (j + 1);

                if (alternativa == null) {
                    problemas.add(local + ": está vazia");
                    continue;
                }

                if (alternativa.ordem() == null) {
                    problemas.add(local + ": falta a ordem");
                } else if (alternativa.ordem() != j + 1) {
                    problemas.add(local + ": a ordem deveria ser " + (j + 1) + ", mas é " + alternativa.ordem());
                }

                if (textoVazio(alternativa.texto())) {
                    problemas.add(local + ": falta o texto");
                }

                validarArea("área principal", alternativa.areaPrincipal(), local, slugsDasAreas, problemas);
                validarArea("área secundária", alternativa.areaSecundaria(), local, slugsDasAreas, problemas);

                if (!textoVazio(alternativa.areaPrincipal())
                        && alternativa.areaPrincipal().equals(alternativa.areaSecundaria())) {
                    problemas.add(local + ": a área principal e a secundária são a mesma ('"
                            + alternativa.areaPrincipal() + "')");
                }
            }
        }

        private void validarArea(String campo, String slug, String local,
                                 Set<String> slugsDasAreas, List<String> problemas) {
            if (textoVazio(slug)) {
                problemas.add(local + ": falta a " + campo);
            } else if (!slugsDasAreas.contains(slug)) {
                problemas.add(local + ": a " + campo + " '" + slug + "' não existe no catálogo");
            }
        }

        private boolean textoVazio(String texto) {
            return texto == null || texto.isBlank();
        }
    }
}
