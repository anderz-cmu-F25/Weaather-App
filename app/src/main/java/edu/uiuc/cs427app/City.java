package edu.uiuc.cs427app;

public class City {

    private String name;
    // Latitude
    private double latitude;
    // Longitude
    private double longitude;

    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // Retrieves the name of the city
    public String getName() {
        return name;
    }
    // Gets the latitude coordinate of the city
    public double getLatitude() {
        return latitude;
    }
    // Gets the longitude coordinate of the city
    public double getLongitude() {
        return longitude;
    }
}
