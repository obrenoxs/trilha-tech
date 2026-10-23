package io.github.obrenoxs.trilhatech.assistente.chat.prompt;

import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaDetalheResponse;
import io.github.obrenoxs.trilhatech.conteudo.area.dto.response.AreaResponse;
import io.github.obrenoxs.trilhatech.conteudo.trilha.dto.response.*;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CriadorPromptTest {

    private final CriadorPrompt criador = new CriadorPrompt();

    private final List<AreaResponse> areas = List.of(
            new AreaResponse("dados-e-ia", "02", "Dados e Inteligência Artificial", "Transforma dados em respostas.", 5));

    @Test
    void deveComecarComAsRegrasETerminarComOLembrete() {
        String prompt = criador.montar(new ContextoPrompt(areas, null, null, null));

        assertThat(prompt).startsWith(CriadorPrompt.REGRAS);
        assertThat(prompt).endsWith(CriadorPrompt.LEMBRETE_FINAL);
    }

    @Test
    void deveListarAsAreasNaPaginaInicial() {
        String prompt = criador.montar(new ContextoPrompt(areas, null, null, null));

        assertThat(prompt).contains("Dados e Inteligência Artificial")
                .contains("página inicial");
    }

    @Test
    void deveIncluirOsDadosDaTrilhaAberta() {
        String prompt = criador.montar(new ContextoPrompt(areas, null, trilhaDeExemplo(), null));

        assertThat(prompt).contains("Java e Spring")
                .contains("Lógica de programação")
                .contains("https://exemplo.com/logica")
                .contains("de R$ 2000 a R$ 4000")
                .contains("Glassdoor")
                .contains("Salários são estimativas.");
    }

    @Test
    void deveIncluirOResultadoDoQuiz() {
        String prompt = criador.montar(new ContextoPrompt(areas, null, null,
                List.of("Dados e Inteligência Artificial", "UX/UI Design")));

        assertThat(prompt).contains("fez o quiz")
                .contains("Dados e Inteligência Artificial, UX/UI Design");
    }

    @Test
    void naoDeveMostrarEtapasQuandoSoAAreaEstaAberta() {
        AreaDetalheResponse area = new AreaDetalheResponse("dados-e-ia", "02", "Dados e Inteligência Artificial",
                "d", "No dia a dia, você analisa dados.",
                List.of(new TrilhaResponse("ciencia-de-dados", "Ciência de Dados", "Modelos e análises.", NivelEntrada.INTERMEDIARIO, 40)));

        String prompt = criador.montar(new ContextoPrompt(areas, area, null, null));

        assertThat(prompt).contains("No dia a dia, você analisa dados.")
                .contains("Ciência de Dados")
                .doesNotContain("Etapa 1");
    }

    @Test
    void deveLimitarOTamanhoDoConteudoMasManterRegrasELembrete() {
        TrilhaDetalheResponse base = trilhaDeExemplo();
        TrilhaDetalheResponse gigante = new TrilhaDetalheResponse(base.slug(), base.nome(), base.resumo(), base.area(),
                base.nivelEntrada(), "x".repeat(100_000), base.paraQuemE(), base.horasPorSemana(),
                base.duracaoTotalSemanas(), base.avisosSalario(), base.conteudoConferidoEm(),
                base.etapas(), base.faixasSalariais());

        String prompt = criador.montar(new ContextoPrompt(areas, null, gigante, null));

        assertThat(prompt.length()).isLessThan(CriadorPrompt.TAMANHO_MAXIMO_DO_CONTEUDO + 5_000);
        assertThat(prompt).startsWith(CriadorPrompt.REGRAS).endsWith(CriadorPrompt.LEMBRETE_FINAL);
    }

    private TrilhaDetalheResponse trilhaDeExemplo() {
        RecursoResponse recurso = new RecursoResponse(1, "Curso de Lógica", "https://exemplo.com/logica",
                TipoRecurso.CURSO, Idioma.PT, Acesso.GRATUITO, null, LocalDate.of(2026, 10, 16));
        EtapaResponse etapa = new EtapaResponse(1, "Lógica de programação", NivelEtapa.BASICO, 5,
                "Aprenda a pensar em passos.", "Criar um jogo de adivinhação.", List.of(recurso));
        FaixaSalarialResponse faixa = new FaixaSalarialResponse(NivelSalarial.JUNIOR, 2000, 4000, 3000,
                "média do salário base", "Glassdoor", YearMonth.of(2026, 9), false, "Estimativa de mercado.");
        return new TrilhaDetalheResponse("java-e-spring", "Java e Spring", "Back-end com Java",
                new AreaTrilhaResponse("desenvolvimento-de-software", "01", "Desenvolvimento de Software"),
                NivelEntrada.INICIANTE, "Texto da trilha.", "Para quem nunca programou.", 8, 36,
                "Salários são estimativas.", LocalDate.of(2026, 10, 16), List.of(etapa), List.of(faixa));
    }
}
