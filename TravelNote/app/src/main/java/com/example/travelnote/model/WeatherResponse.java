package com.example.travelnote.model;

public class WeatherResponse {
    public CurrentWeather current_weather;

    public static class CurrentWeather {
        public double temperature;
        public int weathercode;
    }
}

