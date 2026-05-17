package com.example.beat_store.model;

public class Beat {
    private Long id;
    private String title;

    // Название совпадает с ключом в JSON: "userNameProducer"
    private String userNameProducer;

    private String genre;
    private int bpm;

    // Добавил новое поле "key" (тональность)
    private String key;

    // Ссылка на аудиофайл
    private String audioFile;

    // Тип лицензии
    private String licenseType;

    private double price;

    // Пустой конструктор (обязателен для Gson)
    public Beat() {}

    // Конструктор со всеми полями
    public Beat(Long id, String title, String userNameProducer, String genre,
                int bpm, String key, String audioFile, String licenseType, double price) {
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

    // Геттеры и сеттеры для всех полей
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUserNameProducer() { return userNameProducer; }
    public void setUserNameProducer(String userNameProducer) {
        this.userNameProducer = userNameProducer;
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getBpm() { return bpm; }
    public void setBpm(int bpm) { this.bpm = bpm; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getAudioFile() { return audioFile; }
    public void setAudioFile(String audioFile) { this.audioFile = audioFile; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}