package com.example.nav.dto;

import java.util.List;

public class CategoryIntersectionDTO {

    private List<String> categories;
    private int count;
    private long totalValue;

    public CategoryIntersectionDTO(List<String> categories, int count, long totalValue) {
        this.categories = categories;
        this.count = count;
        this.totalValue = totalValue;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getCount() {
        return count;
    }

    public long getTotalValue() {
        return totalValue;
    }
}
