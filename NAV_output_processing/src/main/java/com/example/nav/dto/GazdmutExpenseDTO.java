package com.example.nav.dto;

public class GazdmutExpenseDTO {

    private Integer aaAzon;
    private String vallalatMeret;
    private String teaorKategoria;
    private Long totalExpense;

    public GazdmutExpenseDTO(Integer aaAzon, String vallalatMeret,  String teaorKategoria, Long totalExpense) {
        this.aaAzon = aaAzon;
        this.vallalatMeret = vallalatMeret;
        this.teaorKategoria = teaorKategoria;
        this.totalExpense = totalExpense;
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

    public Long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Long totalExpense) {
        this.totalExpense = totalExpense;
    }
}
