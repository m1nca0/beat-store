package com.example.beat_store.model;

import java.time.LocalDate;
public class Customer extends User {
    private String artist_name;
    public Customer(
            Long id,
            String username,
            String email,
            String password,
            LocalDate registrationDate,
            double balance,
            String artist_name) {
        super(
                id,
                username,
                email,
                password,
                registrationDate,
                balance);
        this.artist_name = artist_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
}
