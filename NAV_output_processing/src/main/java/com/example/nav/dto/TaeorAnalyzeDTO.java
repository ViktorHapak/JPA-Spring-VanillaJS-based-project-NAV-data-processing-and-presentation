package com.example.nav.dto;

public class TaeorAnalyzeDTO {

    private Integer companyCount;
    private String typicalScale;
    private Long totalInvoiceCost;

    public TaeorAnalyzeDTO(Integer companyCount, String typicalScale, Long totalInvoiceCost) {
        this.companyCount = companyCount;
        this.typicalScale = typicalScale;
        this.totalInvoiceCost = totalInvoiceCost;
    }

    public Integer getCompanyCount() {
        return companyCount;
    }

    public void setCompanyCount(Integer companyCount) {
        this.companyCount = companyCount;
    }

    public String getTypicalScale() {
        return typicalScale;
    }

    public void setTypicalScale(String typicalScale) {
        this.typicalScale = typicalScale;
    }

    public Long getTotalInvoiceCost() {
        return totalInvoiceCost;
    }

    public void setTotalInvoiceCost(Long totalInvoiceCost) {
        this.totalInvoiceCost = totalInvoiceCost;
    }
}
