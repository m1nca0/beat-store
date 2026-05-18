package com.example.beat_store.model;

import java.time.LocalDate;
public class Customer extends User {
    private String artistName;
    public Customer(
            Long id,
            String username,
            String email,
            String password,
            LocalDate registrationDate,
            double balance,
            String artistName) {
        super(
                id,
                username,
                email,
                password,
                registrationDate,
                balance);
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
