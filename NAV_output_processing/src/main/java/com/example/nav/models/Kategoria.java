package com.example.nav.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "kategoria")
public class Kategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NEV", nullable = false, length = 35)
    private String nev;

    @ManyToMany(mappedBy = "kategoriak", fetch = FetchType.LAZY)
    private Set<Szamla> szamlak = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public Set<Szamla> getSzamlak() {
        return szamlak;
    }

    public void setSzamlak(Set<Szamla> szamlak) {
        this.szamlak = szamlak;
    }

    @Override
    public String toString() {
        return "Kategoria{" +
                "nev='" + nev + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Kategoria kategoria = (Kategoria) o;
        return Objects.equals(getId(), kategoria.getId()) && Objects.equals(getNev(), kategoria.getNev());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNev());
    }
}
