package com.example.beat_store.model;

public class Beat {
    private Long id;
    private String title;
    private String userNameProducer;
    private String genre;
    private int bpm;
    private String key;
    private String audioFile;
    private String licenseType;
    private double price;

    public Beat(
            Long id,
            String title,
            String userNameProducer,
            String genre,
            int bpm,
            String key,
            String audioFile,
            String licenseType,
            double price) {
        this.id = id;
        this.title = title;
        this.userNameProducer = userNameProducer;
        this.genre = genre;
        this.bpm = bpm;
        this.key = key;
        this.audioFile = audioFile;
        this.licenseType = licenseType;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUserNameProducer() {
        return userNameProducer;
    }

    public String getGenre() {
        return genre;
    }

    public int getBpm() {
        return bpm;
    }

    public String getKey() {
        return key;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public double getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserNameProducer(String userNameProducer) {
        this.userNameProducer = userNameProducer;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
