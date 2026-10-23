package io.github.obrenoxs.trilhatech.compartilhado.filtro;

import io.github.obrenoxs.trilhatech.compartilhado.exception.CorpoRequisicaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class LimiteCorpoFiltroTest {

    @Mock
    private HandlerExceptionResolver resolvedor;

    private LimiteCorpoFiltro filtro;

    @BeforeEach
    void preparar() {
        filtro = new LimiteCorpoFiltro(resolvedor, 100);
    }

    private MockHttpServletRequest requisicao(String metodo, String caminho, int tamanhoDoCorpo) {
        MockHttpServletRequest requisicao = new MockHttpServletRequest(metodo, caminho);
        requisicao.setContent(new byte[tamanhoDoCorpo]);
        return requisicao;
    }

    @Test
    void deveRecusarUmCorpoMaiorQueOLimiteSemPassarAdiante() throws Exception {
        MockHttpServletRequest requisicao = requisicao("POST", "/api/assistente/mensagens", 101);
        MockHttpServletResponse resposta = new MockHttpServletResponse();
        MockFilterChain cadeia = new MockFilterChain();

        filtro.doFilter(requisicao, resposta, cadeia);

        verify(resolvedor).resolveException(eq(requisicao), eq(resposta), isNull(), any(CorpoRequisicaoException.class));
        assertThat(cadeia.getRequest()).isNull();
    }

    @Test
    void deveAceitarUmCorpoExatamenteNoLimite() throws Exception {
        MockHttpServletRequest requisicao = requisicao("POST", "/api/assistente/mensagens", 100);
        MockFilterChain cadeia = new MockFilterChain();

        filtro.doFilter(requisicao, new MockHttpServletResponse(), cadeia);

        assertThat(cadeia.getRequest()).isNotNull();
        verifyNoInteractions(resolvedor);
    }

    @Test
    void deveDeixarPassarUmaRequisicaoSemCorpo() throws Exception {
        MockFilterChain cadeia = new MockFilterChain();

        filtro.doFilter(new MockHttpServletRequest("GET", "/api/areas"), new MockHttpServletResponse(), cadeia);

        assertThat(cadeia.getRequest()).isNotNull();
        verifyNoInteractions(resolvedor);
    }

    @Test
    void naoDeveFiltrarRotasForaDeApi() throws Exception {
        MockHttpServletRequest requisicao = requisicao("POST", "/outra-rota", 5000);
        MockFilterChain cadeia = new MockFilterChain();

        filtro.doFilter(requisicao, new MockHttpServletResponse(), cadeia);

        assertThat(cadeia.getRequest()).isNotNull();
        verifyNoInteractions(resolvedor);
    }
}
