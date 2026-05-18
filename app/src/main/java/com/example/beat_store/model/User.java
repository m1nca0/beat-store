package com.example.beat_store.model;
import java.time.LocalDate;
public abstract class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private LocalDate registrationDate;
    private double balance;

    public User(
            Long id,
            String username,
            String email,
            String password,
            LocalDate registrationDate,
            double balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

