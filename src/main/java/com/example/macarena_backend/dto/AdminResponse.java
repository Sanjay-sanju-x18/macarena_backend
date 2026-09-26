package com.example.macarena_backend.dto;

import java.time.LocalDateTime;

public class AdminResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private String message;

    public AdminResponse() {}

    public AdminResponse(Long id, String name, LocalDateTime createdAt, String message) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}