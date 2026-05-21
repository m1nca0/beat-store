package com.example.beat_store.model;

public class Beat {
    private Long id;
    private String title;

    private String usernameproducer;

    private String genre;
    private int bpm;

    private String key;

    private String audiofile;

    private String licensetype;

    private double price;
    private String owner;

    public Beat() {}

    public Beat(Long id, String title, String usernameproducer, String genre,
                int bpm, String key, String audiofile, String licensetype, double price) {
        this.id = id;
        this.title = title;
        this.usernameproducer = usernameproducer;
        this.genre = genre;
        this.bpm = bpm;
        this.key = key;
        this.audiofile = audiofile;
        this.licensetype = licensetype;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUserNameProducer() { return usernameproducer; }
    public void setUserNameProducer(String usernameproducer) {
        this.usernameproducer = usernameproducer;
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getBpm() { return bpm; }
    public void setBpm(int bpm) { this.bpm = bpm; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getAudioFile() { return audiofile; }
    public void setAudioFile(String audiofile) { this.audiofile = audiofile; }

    public String getLicenseType() { return licensetype; }
    public void setLicenseType(String licensetype) { this.licensetype = licensetype; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}