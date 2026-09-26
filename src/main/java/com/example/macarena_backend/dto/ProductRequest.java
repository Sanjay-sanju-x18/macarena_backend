package com.example.macarena_backend.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String dressName;

    // ✅ Now sends ID instead of name
    @NotNull(message = "Product type is required")
    private Long dressTypeId;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    private Double offerPercentage;

    @NotNull(message = "Offer price is required")
    @PositiveOrZero(message = "Offer price cannot be negative")
    private Double offerPrice;

    @NotBlank(message = "Size type is required")
    private String sizeType;

    @NotNull(message = "Total quantity is required")
    @Positive(message = "Total quantity must be greater than 0")
    private Integer totalQty;

    @NotEmpty(message = "At least one size must be selected")
    private List<SizeQtyDto> sizes;

    public String getDressName() { return dressName; }
    public void setDressName(String dressName) { this.dressName = dressName; }

    public Long getDressTypeId() { return dressTypeId; }
    public void setDressTypeId(Long dressTypeId) { this.dressTypeId = dressTypeId; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getOfferPercentage() { return offerPercentage; }
    public void setOfferPercentage(Double offerPercentage) { this.offerPercentage = offerPercentage; }

    public Double getOfferPrice() { return offerPrice; }
    public void setOfferPrice(Double offerPrice) { this.offerPrice = offerPrice; }

    public String getSizeType() { return sizeType; }
    public void setSizeType(String sizeType) { this.sizeType = sizeType; }

    public Integer getTotalQty() { return totalQty; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }

    public List<SizeQtyDto> getSizes() { return sizes; }
    public void setSizes(List<SizeQtyDto> sizes) { this.sizes = sizes; }

    public static class SizeQtyDto {
        @NotBlank
        private String size;

        @NotNull
        @Positive
        private Integer qty;

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
    }
}