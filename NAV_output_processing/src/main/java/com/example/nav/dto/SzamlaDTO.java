package com.example.nav.dto;

import jakarta.persistence.Column;
import java.util.List;

public class SzamlaDTO {

    private Integer esstId;

    private Integer xBruttoHuf;

    private List<String> category;

    private GazdmutDTO customer;

    public SzamlaDTO(Integer esstId, Integer xBruttoHuf, List<String> category, GazdmutDTO customer) {
        this.esstId = esstId;
        this.xBruttoHuf = xBruttoHuf;
        this.category = category;
        this.customer = customer;
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

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public GazdmutDTO getCustomer() {
        return customer;
    }

    public void setCustomer(GazdmutDTO customer) {
        this.customer = customer;
    }
}
