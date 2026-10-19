package io.github.obrenoxs.trilhatech.content.trilha.entity;

import io.github.obrenoxs.trilhatech.content.trilha.enums.NivelEtapa;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "etapa")
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trilha_id")
    private Trilha trilha;

    private int ordem;
    private String titulo;

    @Enumerated(EnumType.STRING)
    private NivelEtapa nivel;

    private int duracaoSemanas;
    private String descricao;
    private String projetoPratico;

    public Etapa() {
    }

    public Etapa(
            Trilha trilha,
            int ordem,
            String titulo,
            NivelEtapa nivel,
            int duracaoSemanas,
            String descricao,
            String projetoPratico
    ) {
        this.trilha = trilha;
        this.ordem = ordem;
        this.titulo = titulo;
        this.nivel = nivel;
        this.duracaoSemanas = duracaoSemanas;
        this.descricao = descricao;
        this.projetoPratico = projetoPratico;
    }

    public UUID getId() {
        return id;
    }

    public Trilha getTrilha() {
        return trilha;
    }

    public void setTrilha(Trilha trilha) {
        this.trilha = trilha;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public NivelEtapa getNivel() {
        return nivel;
    }

    public void setNivel(NivelEtapa nivel) {
        this.nivel = nivel;
    }

    public int getDuracaoSemanas() {
        return duracaoSemanas;
    }

    public void setDuracaoSemanas(int duracaoSemanas) {
        this.duracaoSemanas = duracaoSemanas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getProjetoPratico() {
        return projetoPratico;
    }

    public void setProjetoPratico(String projetoPratico) {
        this.projetoPratico = projetoPratico;
    }
}
