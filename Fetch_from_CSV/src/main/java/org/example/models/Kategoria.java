package org.example.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
}
