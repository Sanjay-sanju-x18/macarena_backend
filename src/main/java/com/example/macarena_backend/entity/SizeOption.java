package com.example.macarena_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "size_options",
       uniqueConstraints = @UniqueConstraint(columnNames = {"size_type", "label"}))
public class SizeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "alphabet" or "number"
    @NotBlank(message = "Size type is required")
    @Pattern(regexp = "alphabet|number",
             message = "Size type must be 'alphabet' or 'number'")
    @Column(name = "size_type", nullable = false, length = 20)
    private String sizeType;

    // "S", "M", "L", "28", "30"
    @NotBlank(message = "Size label is required")
    @Column(name = "label", nullable = false, length = 10)
    private String label;

    // Display order (S=1, M=2, L=3 ...)
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    public SizeOption() {}

    public SizeOption(String sizeType, String label, Integer displayOrder) {
        this.sizeType = sizeType;
        this.label = label;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
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