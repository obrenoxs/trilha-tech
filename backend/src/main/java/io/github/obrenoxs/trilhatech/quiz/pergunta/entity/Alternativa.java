package io.github.obrenoxs.trilhatech.quiz.pergunta.entity;

import io.github.obrenoxs.trilhatech.conteudo.area.entity.Area;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "alternativa")
public class Alternativa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id")
    private Pergunta pergunta;

    private int ordem;
    private String texto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_principal_id")
    private Area areaPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_secundaria_id")
    private Area areaSecundaria;

    public Alternativa() {
    }

    public Alternativa(Pergunta pergunta, int ordem, String texto, Area areaPrincipal, Area areaSecundaria) {
        this.pergunta = pergunta;
        this.ordem = ordem;
        this.texto = texto;
        this.areaPrincipal = areaPrincipal;
        this.areaSecundaria = areaSecundaria;
    }

    public UUID getId() {
        return id;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public void setPergunta(Pergunta pergunta) {
        this.pergunta = pergunta;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Area getAreaPrincipal() {
        return areaPrincipal;
    }

    public void setAreaPrincipal(Area areaPrincipal) {
        this.areaPrincipal = areaPrincipal;
    }

    public Area getAreaSecundaria() {
        return areaSecundaria;
    }

    public void setAreaSecundaria(Area areaSecundaria) {
        this.areaSecundaria = areaSecundaria;
    }
}
