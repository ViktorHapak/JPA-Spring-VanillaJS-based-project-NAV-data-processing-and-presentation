package com.example.nav.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.util.List;

public class GazdmutDTO {


    private Integer aaAzon;

    private String vallalatMeret;

    private String teaorKategoria;


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
}