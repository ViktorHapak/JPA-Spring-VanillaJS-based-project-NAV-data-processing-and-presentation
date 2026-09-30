package com.example.nav.models;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "szamla")
public class Szamla {

    @Id
    @Column(name = "ESST_ID")
    private Integer esstId;

    @Column(name = "X_BRUTTO_HUF", nullable = false)
    private Integer xBruttoHuf;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "V_ADOSZAM_TORZ", referencedColumnName = "AA_AZON", nullable = false)
    private Gazdmut gazdmut;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="szamla_kategoria", joinColumns = @JoinColumn(name="ESST_ID"),
            inverseJoinColumns = @JoinColumn(name = "KATEGORIA_ID"))
    private Set<Kategoria> kategoriak = new HashSet<>();

    public Szamla() {
    }

    public Szamla(Integer esstId, Integer xBruttoHuf, String kategoria) {
        this.esstId = esstId;
        this.xBruttoHuf = xBruttoHuf;
    }

    public Integer getEsstId() {
        return esstId;
    }

    public void setEsstId(Integer esstId) {
        this.esstId = esstId;
    }

    public Integer getXBruttoHuf() {
        return xBruttoHuf;
    }

    public void setXBruttoHuf(Integer xBruttoHuf) {
        this.xBruttoHuf = xBruttoHuf;
    }

    public Gazdmut getGazdmut() {
        return gazdmut;
    }

    public void setGazdmut(Gazdmut gazdmut) {
        this.gazdmut = gazdmut;
    }

    public Set<Kategoria> getKategoriak() {
        return kategoriak;
    }

    public void setKategoriak(Set<Kategoria> kategoriak) {
        this.kategoriak = kategoriak;
    }

    public void addKategoria(Kategoria kategoria) {
        if (this.kategoriak.add(kategoria)) {
            kategoria.getSzamlak().add(this);
        }
    }

    public void removeKategoria(Kategoria kategoria) {
        this.kategoriak.remove(kategoria);
    }

    @Override
    public String toString() {
        return "Szamla{" +
                "esstId=" + esstId +
                ", xBruttoHuf=" + xBruttoHuf + '\'' +
                ", kategoria: {" + kategoriak.stream().toString() + "}" + '\'' +
                '}';
    }
}
