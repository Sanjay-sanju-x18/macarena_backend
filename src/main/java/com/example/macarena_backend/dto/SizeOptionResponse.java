package com.example.macarena_backend.dto;

public class SizeOptionResponse {

    private Long id;
    private String sizeType;
    private String label;
    private Integer displayOrder;

    public SizeOptionResponse() {}

    public SizeOptionResponse(Long id, String sizeType, String label, Integer displayOrder) {
        this.id = id;
        this.sizeType = sizeType;
        this.label = label;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSizeType() { return sizeType; }
    public void setSizeType(String sizeType) { this.sizeType = sizeType; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}