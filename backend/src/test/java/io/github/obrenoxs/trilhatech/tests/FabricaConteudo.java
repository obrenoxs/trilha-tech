package io.github.obrenoxs.trilhatech.tests;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaTrilhaArquivo;

public final class FabricaConteudo {

    private FabricaConteudo() {
    }

    public static AreaTrilhaArquivo areaDaTrilha(String numero, String slug, String nome) {
        return new AreaTrilhaArquivo(numero, slug, nome);
    }
}
