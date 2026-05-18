package com.example.beat_store.model;

public class AuthResponse {
    private String message;
    private String role;
    private String username;

    public AuthResponse() {}
    public String getMessage() { return message; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
}