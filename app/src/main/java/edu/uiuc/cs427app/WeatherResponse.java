package edu.uiuc.cs427app;

// API response format is here: https://openweathermap.org/current
public class WeatherResponse {
    public String name;
    public MainData main;
    public WeatherData[] weather;
    public WindData wind;
    public int timezone;  // timezone offset in seconds from UTC

    public static class MainData {
        public double temp;
        public int humidity;
    }

    public static class WeatherData {
        public String main;
        public String description;
    }

    public static class WindData {
        public double speed;
    }
}