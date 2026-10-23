package io.github.obrenoxs.trilhatech.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GuardaDoBancoDeTesteTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "jdbc:postgresql://localhost:5432/trilhatech_test",
            "jdbc:postgresql://localhost:5432/trilhatech_test?sslmode=disable",
            "jdbc:postgresql://db.exemplo.com/outro_test"})
    void deveAceitarBancosDeTeste(String url) {
        assertThatCode(() -> GuardaDoBancoDeTeste.exigirUrl(url)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "jdbc:postgresql://localhost:5432/trilhatech_dev",
            "jdbc:postgresql://localhost:5432/trilhatech_test_antigo",
            "jdbc:postgresql://localhost:5432/trilhatech_dev?currentSchema=x_test",
            "jdbc:postgresql://localhost:5432/test",
            "jdbc:postgresql://localhost:5432/"})
    void deveRecusarQualquerOutroBanco(String url) {
        assertThatThrownBy(() -> GuardaDoBancoDeTeste.exigirUrl(url))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void naoDeveMostrarCredenciaisDaUrlNaMensagem() {
        String url = "jdbc:postgresql://localhost:5432/trilhatech_dev?user=breno&password=segredo123";

        assertThatThrownBy(() -> GuardaDoBancoDeTeste.exigirUrl(url))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("trilhatech_dev")
                .hasMessageNotContaining("segredo123");
    }

    @Test
    void deveExtrairONomeDoBancoIgnorandoOsParametros() {
        assertThat(GuardaDoBancoDeTeste.nomeDoBancoNaUrl("jdbc:postgresql://h:5432/meu_banco?a=b/c"))
                .isEqualTo("meu_banco");
    }
}