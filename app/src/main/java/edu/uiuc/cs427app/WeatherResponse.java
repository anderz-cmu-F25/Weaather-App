package edu.uiuc.cs427app;

// API response format is here: https://openweathermap.org/current
public class WeatherResponse {
    public Main main;
    public Wind wind;
    public Weather[] weather;
    public String name;

    public class Main {
        public double temp;
        public int humidity;
    }

    public class Wind {
        public double speed;
    }

    public class Weather {
        public String main;
        public String description;
    }
}