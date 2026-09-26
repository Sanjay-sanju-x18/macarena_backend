package com.example.macarena_backend.dto;

public class LoginResponse {

    private Long id;
    private String email;
    private String name;
    private String userType;      // "customer" or "admin"
    private String token;         // JWT token
    private String message;

    public LoginResponse() {}

    public LoginResponse(Long id, String email, String name,
                         String userType, String token, String message) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.token = token;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}