package com.example.beat_store.model;

public class AuthRequest {
    private String username;
    private String password;
    private String email;
    private String role;
    private String artistName;

    public AuthRequest() {}

    public AuthRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public AuthRequest(String username, String password, String email, String role, String artistName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.artistName = artistName;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
}