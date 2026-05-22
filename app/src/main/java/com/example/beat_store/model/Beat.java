package com.example.beat_store.model;

public class Beat {
    private Long id;
    private String title;

    private String usernameproducer;

    private String genre;
    private int bpm;

    private String key;

    private String audiofilepath;

    private String licensetype;

    private Double price;
    private String owner;

    public Beat() {}

    public Beat(Long id, String title, String usernameproducer, String genre,
                int bpm, String key, String audiofilepath, String licensetype, Double price) {
        this.id = id;
        this.title = title;
        this.usernameproducer = usernameproducer;
        this.genre = genre;
        this.bpm = bpm;
        this.key = key;
        this.audiofilepath = audiofilepath;
        this.licensetype = licensetype;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUsernameproducer() {
        return usernameproducer;
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

    public String getAudiofilepath() {
        return audiofilepath;
    }

    public String getLicensetype() {
        return licensetype;
    }

    public Double getPrice() {
        return price;
    }

    public String getOwner() {
        return owner;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsernameproducer(String usernameproducer) {
        this.usernameproducer = usernameproducer;
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

    public void setAudiofilepath(String audiofilepath) {
        this.audiofilepath = audiofilepath;
    }

    public void setLicensetype(String licensetype) {
        this.licensetype = licensetype;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}