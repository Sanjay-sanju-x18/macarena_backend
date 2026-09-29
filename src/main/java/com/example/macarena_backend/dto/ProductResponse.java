package com.example.macarena_backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductResponse {

    private Long id;
    private String dressName;

    // ✅ Both ID and name for frontend convenience
    private Long dressTypeId;
    private String dressTypeName;

    private Double price;
    private Double offerPercentage;
    private Double offerPrice;
    private String sizeType;
    private Integer totalQty;
    private List<SizeQtyDto> sizes;
    private List<String> photoUrls;

    /** Size labels that are currently flagged sold-out */
    private List<String> soldOutSizes = new ArrayList<>();

    /** 👇 NEW — non-null when the product has been archived (no longer for sale) */
    private LocalDateTime archivedAt;

    public ProductResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDressName() { return dressName; }
    public void setDressName(String dressName) { this.dressName = dressName; }

    public Long getDressTypeId() { return dressTypeId; }
    public void setDressTypeId(Long dressTypeId) { this.dressTypeId = dressTypeId; }

    public String getDressTypeName() { return dressTypeName; }
    public void setDressTypeName(String dressTypeName) { this.dressTypeName = dressTypeName; }

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

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }

    public List<String> getSoldOutSizes() { return soldOutSizes; }
    public void setSoldOutSizes(List<String> soldOutSizes) {
        this.soldOutSizes = soldOutSizes == null ? new ArrayList<>() : soldOutSizes;
    }

    // 👇 NEW getter/setter for archivedAt
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }

    public static class SizeQtyDto {
        private String size;
        private Integer qty;

        public SizeQtyDto() {}
        public SizeQtyDto(String size, Integer qty) {
            this.size = size;
            this.qty = qty;
        }

        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }

        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
    }
}