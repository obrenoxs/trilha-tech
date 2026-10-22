package io.github.obrenoxs.trilhatech.assistente.uso.limitador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IpDoClienteTest {

    private static final String CONEXAO = "100.64.0.9";

    @Test
    void deveUsarOCabecalhoQuandoForUmIpv4() {
        assertThat(IpDoCliente.escolher("203.0.113.7", CONEXAO)).isEqualTo("203.0.113.7");
    }

    @Test
    void deveUsarOCabecalhoQuandoForUmIpv6() {
        assertThat(IpDoCliente.escolher("2001:db8::1", CONEXAO)).isEqualTo("2001:db8::1");
    }

    @Test
    void deveTirarOsEspacosDasPontasDoCabecalho() {
        assertThat(IpDoCliente.escolher("  203.0.113.7  ", CONEXAO)).isEqualTo("203.0.113.7");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "evil.example.com", "999999", "1.2.3.4, 5.6.7.8", "<script>", "1.2.3.4\n5.6.7.8"})
    void deveCairNoEnderecoDaConexaoQuandoOCabecalhoNaoParecerUmIp(String cabecalho) {
        assertThat(IpDoCliente.escolher(cabecalho, CONEXAO)).isEqualTo(CONEXAO);
    }

    @Test
    void deveRecusarUmCabecalhoGigante() {
        String gigante = "1.".repeat(3000);

        assertThat(IpDoCliente.escolher(gigante, CONEXAO)).isEqualTo(CONEXAO);
    }

    @Test
    void deveDevolverDesconhecidoQuandoNaoHaNenhumEndereco() {
        assertThat(IpDoCliente.escolher(null, null)).isEqualTo("desconhecido");
        assertThat(IpDoCliente.escolher("", "   ")).isEqualTo("desconhecido");
    }
}
