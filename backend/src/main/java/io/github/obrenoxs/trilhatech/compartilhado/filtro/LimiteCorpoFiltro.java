package io.github.obrenoxs.trilhatech.compartilhado.filtro;

import io.github.obrenoxs.trilhatech.compartilhado.exception.CorpoRequisicaoException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class LimiteCorpoFiltro extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolvedorDeExcecoes;
    private final long maximoDeBytes;

    public LimiteCorpoFiltro(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedorDeExcecoes,
            @Value("${trilhatech.limites.corpo-maximo-bytes}") long maximoDeBytes
    ) {
        this.resolvedorDeExcecoes = resolvedorDeExcecoes;
        this.maximoDeBytes = maximoDeBytes;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getContentLengthLong() > maximoDeBytes) {
            resolvedorDeExcecoes.resolveException(request, response, null,
                    new CorpoRequisicaoException("O corpo da requisição passa do limite"));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
