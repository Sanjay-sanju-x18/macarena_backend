package com.example.macarena_backend.dto;

public class LikedProductResponse {

    private Long productId;
    private String name;
    private String image;
    private Double price;

    public LikedProductResponse() {}

    public LikedProductResponse(Long productId, String name, String image, Double price) {
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}