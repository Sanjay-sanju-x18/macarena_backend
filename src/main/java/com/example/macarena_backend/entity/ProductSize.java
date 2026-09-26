package com.example.macarena_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductSize {

    @Column(name = "size_label")
    private String size;

    @Column(name = "quantity")
    private Integer qty;

    public ProductSize() {}

    public ProductSize(String size, Integer qty) {
        this.size = size;
        this.qty = qty;
    }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
}