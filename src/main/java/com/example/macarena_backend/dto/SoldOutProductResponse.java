package com.example.macarena_backend.dto;

import java.time.LocalDateTime;

public class SoldOutProductResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String sizeLabel;
    private LocalDateTime soldOutAt;
    private LocalDateTime expiresAt;

    public SoldOutProductResponse() {}

    public SoldOutProductResponse(Long id, Long productId, String productName,
                                  String sizeLabel,
                                  LocalDateTime soldOutAt, LocalDateTime expiresAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.sizeLabel = sizeLabel;
        this.soldOutAt = soldOutAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }

    public LocalDateTime getSoldOutAt() { return soldOutAt; }
    public void setSoldOutAt(LocalDateTime soldOutAt) { this.soldOutAt = soldOutAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}