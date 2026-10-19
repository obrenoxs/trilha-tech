package io.github.obrenoxs.trilhatech.conteudo.trilha.entity;

import io.github.obrenoxs.trilhatech.conteudo.trilha.enums.NivelSalarial;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "faixa_salarial")
public class FaixaSalarial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trilha_id")
    private Trilha trilha;

    @Enumerated(EnumType.STRING)
    private NivelSalarial nivel;

    private int minimo;
    private int maximo;
    private int valorReferencia;
    private String tipoReferencia;
    private String fonte;
    private LocalDate dataReferencia;
    private boolean dadosLimitados;
    private String observacao;

    public FaixaSalarial() {
    }

    public FaixaSalarial(
            Trilha trilha,
            NivelSalarial nivel,
            int minimo, int maximo,
            int valorReferencia,
            String tipoReferencia,
            String fonte,
            LocalDate dataReferencia,
            boolean dadosLimitados,
            String observacao) {
        this.trilha = trilha;
        this.nivel = nivel;
        this.minimo = minimo;
        this.maximo = maximo;
        this.valorReferencia = valorReferencia;
        this.tipoReferencia = tipoReferencia;
        this.fonte = fonte;
        this.dataReferencia = dataReferencia;
        this.dadosLimitados = dadosLimitados;
        this.observacao = observacao;
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

    public NivelSalarial getNivel() {
        return nivel;
    }

    public void setNivel(NivelSalarial nivel) {
        this.nivel = nivel;
    }

    public int getMinimo() {
        return minimo;
    }

    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    public int getMaximo() {
        return maximo;
    }

    public void setMaximo(int maximo) {
        this.maximo = maximo;
    }

    public int getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(int valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public String getTipoReferencia() {
        return tipoReferencia;
    }

    public void setTipoReferencia(String tipoReferencia) {
        this.tipoReferencia = tipoReferencia;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public LocalDate getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(LocalDate dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public boolean isDadosLimitados() {
        return dadosLimitados;
    }

    public void setDadosLimitados(boolean dadosLimitados) {
        this.dadosLimitados = dadosLimitados;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
