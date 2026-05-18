package com.example.beat_store.model;

import java.time.LocalDate;
import java.util.List;

public class Producer extends User {
    public Producer(
            int id,
            String username,
            String email,
            String password,
            LocalDate registrationDate,
            double balance) {
        super(
                id,
                username,
                email,
                password,
                registrationDate,
                balance);
    }
}
