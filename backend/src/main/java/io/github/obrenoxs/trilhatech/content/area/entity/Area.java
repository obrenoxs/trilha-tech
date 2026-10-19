package io.github.obrenoxs.trilhatech.content.area.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "area")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String slug;
    private String numero;
    private String nome;
    private String descricao;

    @Column(name = "como_e_trabalhar")
    private String comoETrabalhar;

    private int ordemExibicao;
    private int ordemDesempate;

    public Area() {
    }

    public Area(
                String slug,
                String numero,
                String nome,
                String descricao,
                String comoETrabalhar,
                int ordemExibicao,
                int ordemDesempate) {
        this.slug = slug;
        this.numero = numero;
        this.nome = nome;
        this.descricao = descricao;
        this.comoETrabalhar = comoETrabalhar;
        this.ordemExibicao = ordemExibicao;
        this.ordemDesempate = ordemDesempate;
    }

    public UUID getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getComoETrabalhar() {
        return comoETrabalhar;
    }

    public void setComoETrabalhar(String comoETrabalhar) {
        this.comoETrabalhar = comoETrabalhar;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public int getOrdemDesempate() {
        return ordemDesempate;
    }

    public void setOrdemDesempate(int ordemDesempate) {
        this.ordemDesempate = ordemDesempate;
    }
}
