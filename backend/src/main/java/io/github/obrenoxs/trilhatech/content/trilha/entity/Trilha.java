package io.github.obrenoxs.trilhatech.content.trilha.entity;

import io.github.obrenoxs.trilhatech.content.area.entity.Area;
import io.github.obrenoxs.trilhatech.content.trilha.enums.NivelEntrada;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "trilha")
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    private String slug;
    private int ordemNaArea;
    private String nome;
    private String resumo;

    @Enumerated(EnumType.STRING)
    private NivelEntrada nivelEntrada;

    private String textoTrilha;

    @Column(name = "para_quem_e")
    private String paraQuemE;

    private int horasPorSemana;
    private String avisosSalario;
    private LocalDate conteudoConferidoEm;

    public Trilha() {
    }

    public Trilha(
                 Area area,
                 String slug,
                 int ordemNaArea,
                 String nome,
                 String resumo,
                 NivelEntrada nivelEntrada,
                 String textoTrilha,
                 String paraQuemE,
                 int horasPorSemana,
                 String avisosSalario,
                 LocalDate conteudoConferidoEm
    ) {
        this.area = area;
        this.slug = slug;
        this.ordemNaArea = ordemNaArea;
        this.nome = nome;
        this.resumo = resumo;
        this.nivelEntrada = nivelEntrada;
        this.textoTrilha = textoTrilha;
        this.paraQuemE = paraQuemE;
        this.horasPorSemana = horasPorSemana;
        this.avisosSalario = avisosSalario;
        this.conteudoConferidoEm = conteudoConferidoEm;
    }

    public UUID getId() {
        return id;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getOrdemNaArea() {
        return ordemNaArea;
    }

    public void setOrdemNaArea(int ordemNaArea) {
        this.ordemNaArea = ordemNaArea;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public NivelEntrada getNivelEntrada() {
        return nivelEntrada;
    }

    public void setNivelEntrada(NivelEntrada nivelEntrada) {
        this.nivelEntrada = nivelEntrada;
    }

    public String getTextoTrilha() {
        return textoTrilha;
    }

    public void setTextoTrilha(String textoTrilha) {
        this.textoTrilha = textoTrilha;
    }

    public String getParaQuemE() {
        return paraQuemE;
    }

    public void setParaQuemE(String paraQuemE) {
        this.paraQuemE = paraQuemE;
    }

    public int getHorasPorSemana() {
        return horasPorSemana;
    }

    public void setHorasPorSemana(int horasPorSemana) {
        this.horasPorSemana = horasPorSemana;
    }

    public String getAvisosSalario() {
        return avisosSalario;
    }

    public void setAvisosSalario(String avisosSalario) {
        this.avisosSalario = avisosSalario;
    }

    public LocalDate getConteudoConferidoEm() {
        return conteudoConferidoEm;
    }

    public void setConteudoConferidoEm(LocalDate conteudoConferidoEm) {
        this.conteudoConferidoEm = conteudoConferidoEm;
    }
}
