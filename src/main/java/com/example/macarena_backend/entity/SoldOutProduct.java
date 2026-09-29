package com.example.macarena_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sold_out_products")
public class SoldOutProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "size_label", length = 20)
    private String sizeLabel;

    @Column(name = "product_name", length = 255)
    private String productName;

    @Column(name = "sold_out_at", nullable = false)
    private LocalDateTime soldOutAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(length = 50)
    private String reason = "STOCK_ZERO";

    public SoldOutProduct() {}

    public SoldOutProduct(Long productId, String sizeLabel, String productName) {
        this.productId = productId;
        this.sizeLabel = sizeLabel;
        this.productName = productName;
        this.soldOutAt = LocalDateTime.now();
        this.expiresAt = this.soldOutAt.plusHours(24);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public LocalDateTime getSoldOutAt() { return soldOutAt; }
    public void setSoldOutAt(LocalDateTime soldOutAt) { this.soldOutAt = soldOutAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}