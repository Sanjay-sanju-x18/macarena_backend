package com.example.macarena_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String dressName;

    // ✅ FK to dress_types table
    @NotNull(message = "Product type is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dress_type_id", nullable = false)
    private DressType dressType;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    @Column(nullable = true)
    private Double offerPercentage;

    @NotNull(message = "Offer price is required")
    @PositiveOrZero(message = "Offer price cannot be negative")
    @Column(nullable = false)
    private Double offerPrice;

    @NotBlank(message = "Size type is required")
    @Column(nullable = false)
    private String sizeType;

    @NotNull(message = "Total quantity is required")
    @Column(nullable = false)
    private Integer totalQty;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_sizes",
            joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductSize> sizes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_photos",
            joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "photo_path")
    private List<String> photos = new ArrayList<>();

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDressName() { return dressName; }
    public void setDressName(String dressName) { this.dressName = dressName; }

    public DressType getDressType() { return dressType; }
    public void setDressType(DressType dressType) { this.dressType = dressType; }

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

    public List<ProductSize> getSizes() { return sizes; }
    public void setSizes(List<ProductSize> sizes) { this.sizes = sizes; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
}