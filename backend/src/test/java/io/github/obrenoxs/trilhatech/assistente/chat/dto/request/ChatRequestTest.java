package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import io.github.obrenoxs.trilhatech.assistente.cliente.PapelMensagem;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class ChatRequestTest {

    private static final Validator VALIDADOR = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<String> caminhosComErro(ChatRequest requisicao) {
        Set<String> caminhos = new HashSet<>();
        for (ConstraintViolation<ChatRequest> violacao : VALIDADOR.validate(requisicao)) {
            caminhos.add(violacao.getPropertyPath().toString());
        }
        return caminhos;
    }

    private ContextoRequest contextoValido() {
        return new ContextoRequest("desenvolvimento-de-software", "java-e-spring", List.of("dados-e-ia"));
    }

    @Test
    void deveAceitarUmaRequisicaoValida() {
        ChatRequest requisicao = new ChatRequest("Quanto ganha uma pessoa júnior?", List.of(), contextoValido());

        assertThat(caminhosComErro(requisicao)).isEmpty();
    }

    @Test
    void deveAceitarUmaRequisicaoSemHistoricoNemContexto() {
        assertThat(caminhosComErro(new ChatRequest("Oi", null, null))).isEmpty();
    }

    @Test
    void deveRecusarMensagemEmBranco() {
        assertThat(caminhosComErro(new ChatRequest("   ", List.of(), null))).contains("mensagem");
    }

    @Test
    void deveAceitarMensagemComExatamente500CaracteresERecusarComMais() {
        assertThat(caminhosComErro(new ChatRequest("a".repeat(500), List.of(), null))).isEmpty();
        assertThat(caminhosComErro(new ChatRequest("a".repeat(501), List.of(), null))).contains("mensagem");
    }

    @Test
    void deveRecusarHistoricoComMaisDeSeisMensagens() {
        List<MensagemHistoricoRequest> sete = Collections.nCopies(7,
                new MensagemHistoricoRequest(PapelMensagem.USUARIO, "oi"));

        assertThat(caminhosComErro(new ChatRequest("Oi", sete, null))).contains("historico");
    }

    @Test
    void deveRecusarUmItemNuloNoHistorico() {
        List<MensagemHistoricoRequest> comNulo = Arrays.asList(
                new MensagemHistoricoRequest(PapelMensagem.USUARIO, "oi"), null);

        assertThat(caminhosComErro(new ChatRequest("Oi", comNulo, null)))
                .anyMatch(caminho -> caminho.startsWith("historico"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"../etc/passwd", "Java E Spring", "java_e_spring", "'; DROP TABLE area;--"})
    void deveRecusarSlugsComCaracteresEstranhos(String slug) {
        ContextoRequest contexto = new ContextoRequest(slug, null, null);

        assertThat(caminhosComErro(new ChatRequest("Oi", List.of(), contexto))).contains("contexto.areaSlug");
    }

    @Test
    void deveRecusarSlugComMaisDeOitentaCaracteres() {
        ContextoRequest contexto = new ContextoRequest(null, "a".repeat(81), null);

        assertThat(caminhosComErro(new ChatRequest("Oi", List.of(), contexto))).contains("contexto.trilhaSlug");
    }
}
