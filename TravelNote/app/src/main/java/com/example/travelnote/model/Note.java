package com.example.travelnote.model;

import java.io.Serializable;

public class Note implements Serializable {
    private int id;
    private String title;
    private String description;
    private String date;
    private double latitude;
    private double longitude;


    public Note(int id, String title, String description, String date, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Note(String title, String description, String date, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Геттеры
    public int getId() {return id;}
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDate() { return date; }

    // Сеттеры
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
}
