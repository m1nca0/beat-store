package com.example.beat_store.model;

public class AuthRequest {
    private String username;
    private String password;
    private String role;        // "customer" или "producer"
    private String artistName;  // только для покупателя

    public AuthRequest() {}

    public AuthRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public AuthRequest(String username, String password, String role, String artistName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.artistName = artistName;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
}