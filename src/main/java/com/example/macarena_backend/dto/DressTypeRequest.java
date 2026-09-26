package com.example.macarena_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DressTypeRequest {

    @NotBlank(message = "Dress type name is required")
    @Size(max = 30, message = "Dress type name must be at most 30 characters")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}