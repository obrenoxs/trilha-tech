package io.github.obrenoxs.trilhatech.carga.validacao;

import io.github.obrenoxs.trilhatech.carga.arquivo.AreaCatalogoArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.DadosTrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaArquivo;
import io.github.obrenoxs.trilhatech.carga.arquivo.TrilhaCatalogoArquivo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ValidadorConsistencia {

    public List<String> validar(AreaCatalogoArquivo area, TrilhaCatalogoArquivo trilhaCatalogo, TrilhaArquivo arquivo) {
        List<String> problemas = new ArrayList<>();

        if (arquivo == null || arquivo.trilha() == null) {
            return problemas;
        }

        DadosTrilhaArquivo dados = arquivo.trilha();
        String local = "Trilha '" + trilhaCatalogo.slug() + "'";

        conferirIgualdade("slug", trilhaCatalogo.slug(), dados.slug(), local, problemas);
        conferirIgualdade("nome", trilhaCatalogo.nome(), dados.nome(), local, problemas);
        conferirIgualdade("nível de entrada", trilhaCatalogo.nivelEntrada(), dados.nivelEntrada(), local, problemas);
        conferirIgualdade("duração total em semanas", trilhaCatalogo.duracaoTotalSemanas(), dados.duracaoTotalSemanas(), local, problemas);

        if (dados.area() != null) {
            conferirIgualdade("slug da área", area.slug(), dados.area().slug(), local, problemas);
            conferirIgualdade("número da área", area.numero(), dados.area().numero(), local, problemas);
            conferirIgualdade("nome da área", area.nome(), dados.area().nome(), local, problemas);
        }

        return problemas;
    }

    private void conferirIgualdade(String campo, Object doCatalogo, Object doArquivo,
                                   String local, List<String> problemas) {
        if (!Objects.equals(doCatalogo, doArquivo)) {
            problemas.add(local + ": " + campo + " diferente (catálogo: '" + doCatalogo
                    + "', arquivo: '" + doArquivo + "')");
        }
    }
}