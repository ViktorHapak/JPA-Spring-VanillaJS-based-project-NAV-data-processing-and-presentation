package com.example.nav.dto;

public class CategoryAnalyzeDTO {

    private Integer categoryCount;
    private Long categoryValue;

    public CategoryAnalyzeDTO(Integer categoryCount, Long categoryValue) {
        this.categoryCount = categoryCount;
        this.categoryValue = categoryValue;
    }

    public Integer getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Integer categoryCount) {
        this.categoryCount = categoryCount;
    }

    public Long getCategoryValue() {
        return categoryValue;
    }

    public void setCategoryValue(Long categoryValue) {
        this.categoryValue = categoryValue;
    }
}
