package com.example.macarena_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SizeOptionRequest {

    @NotBlank(message = "Size type is required")
    @Pattern(regexp = "alphabet|number",
             message = "Size type must be 'alphabet' or 'number'")
    private String sizeType;

    @NotBlank(message = "Size label is required")
    private String label;

    private Integer displayOrder;

    public String getSizeType() { return sizeType; }
    public void setSizeType(String sizeType) { this.sizeType = sizeType; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}