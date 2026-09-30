package org.example.models;

import javax.persistence.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gazdmut")
public class Gazdmut {

    @Id
    @Column(name = "AA_AZON")
    private Integer aaAzon;

    @Column(name = "VALLALATMERET", nullable = false, length = 20)
    private String vallalatMeret;

    @Column(name = "TEAOR_KATEGORIA", length = 100)
    private String teaorKategoria;

    @OneToMany(
            mappedBy = "gazdmut",
            fetch = FetchType.LAZY
    )
    private List<Szamla> szamlak = new ArrayList<>();

    public Gazdmut() {
    }

    public Gazdmut(Integer aaAzon, String vallalatMeret, String teaorKategoria) {
        this.aaAzon = aaAzon;
        this.vallalatMeret = vallalatMeret;
        this.teaorKategoria = teaorKategoria;
    }

    public Integer getAaAzon() {
        return aaAzon;
    }

    public void setAaAzon(Integer aaAzon) {
        this.aaAzon = aaAzon;
    }

    public String getVallalatMeret() {
        return vallalatMeret;
    }

    public void setVallalatMeret(String vallalatMeret) {
        this.vallalatMeret = vallalatMeret;
    }

    public String getTeaorKategoria() {
        return teaorKategoria;
    }

    public void setTeaorKategoria(String teaorKategoria) {
        this.teaorKategoria = teaorKategoria;
    }

    public List<Szamla> getSzamlak() {
        return szamlak;
    }

    public void setSzamlak(List<Szamla> szamlak) {
        this.szamlak = szamlak;
    }

    public void addSzamla(Szamla szamla) {
        szamlak.add(szamla);
        szamla.setGazdmut(this);
    }

    public void removeSzamla(Szamla szamla) {
        szamlak.remove(szamla);
        szamla.setGazdmut(null);
    }

    @Override
    public String toString() {
        return "Gazdmut{" +
                "aaAzon=" + aaAzon +
                ", vallalatMeret='" + vallalatMeret + '\'' +
                ", teaorKategoria='" + teaorKategoria + '\'' +
                '}';
    }
}
