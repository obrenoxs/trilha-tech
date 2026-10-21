package io.github.obrenoxs.trilhatech.quiz.pergunta.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "pergunta")
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int ordem;
    private String texto;

    public Pergunta() {
    }

    public Pergunta(int ordem, String texto) {
        this.ordem = ordem;
        this.texto = texto;
    }

    public UUID getId() {
        return id;
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
}
