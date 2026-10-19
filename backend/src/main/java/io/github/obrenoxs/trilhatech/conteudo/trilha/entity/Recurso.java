package io.github.obrenoxs.trilhatech.conteudo.trilha.entity;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.Acesso;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.Idioma;
import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.TipoRecurso;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recurso")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_id")
    private Etapa etapa;

    private int ordem;
    private String titulo;
    private String url;

    @Enumerated(EnumType.STRING)
    private TipoRecurso tipo;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;

    @Enumerated(EnumType.STRING)
    private Acesso acesso;

    private String observacao;
    private LocalDate verificadoEm;

    public Recurso() {
    }

    public Recurso(
                   Etapa etapa,
                   int ordem,
                   String titulo,
                   String url,
                   TipoRecurso tipo,
                   Idioma idioma,
                   Acesso acesso,
                   String observacao,
                   LocalDate verificadoEm
    ) {
        this.etapa = etapa;
        this.ordem = ordem;
        this.titulo = titulo;
        this.url = url;
        this.tipo = tipo;
        this.idioma = idioma;
        this.acesso = acesso;
        this.observacao = observacao;
        this.verificadoEm = verificadoEm;
    }

    public UUID getId() {
        return id;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public void setEtapa(Etapa etapa) {
        this.etapa = etapa;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TipoRecurso getTipo() {
        return tipo;
    }

    public void setTipo(TipoRecurso tipo) {
        this.tipo = tipo;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public Acesso getAcesso() {
        return acesso;
    }

    public void setAcesso(Acesso acesso) {
        this.acesso = acesso;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDate getVerificadoEm() {
        return verificadoEm;
    }

    public void setVerificadoEm(LocalDate verificadoEm) {
        this.verificadoEm = verificadoEm;
    }
}
