package io.github.obrenoxs.trilhatech.carga.arquivo;

public record TrilhaCatalogoArquivo(
        Integer ordem,
        String slug,
        String nome,
        String nivelEntrada,
        Integer duracaoTotalSemanas
) {}
