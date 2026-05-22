package com.example.beat_store.model;
import java.time.LocalDate;
public abstract class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private LocalDate reg_date;
    private double balance;

    public User(
            Long id,
            String username,
            String email,
            String password,
            LocalDate reg_date,
            double balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.reg_date = reg_date;
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

    public LocalDate getReg_date() {
        return reg_date;
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

    public void setReg_date(LocalDate reg_date) {
        this.reg_date = reg_date;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

