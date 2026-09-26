package com.example.macarena_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "dress_types", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class DressType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Dress type name is required")
    @Size(max = 30, message = "Dress type name must be at most 30 characters")
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    public DressType() {}

    public DressType(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}