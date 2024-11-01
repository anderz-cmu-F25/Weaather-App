package edu.uiuc.cs427app;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<City> cityList;
    private String buttonColor;     // Add this field
    private String backgroundColor; // Add this field

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.cityList = new ArrayList<>();
        this.buttonColor = "Blue";    // Default button color
        this.backgroundColor = "White"; // Default background color
    }

    // Add getters and setters for theme preferences
    public String getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    // get the Username
    public String getUsername() {
        return username;
    }

    // get the UserPassword
    public String getPassword() {
        return password;
    }

    // get the City List
    public List<City> getCityList() {
        return cityList;
    }

    // add one city
    public void addCity(City city) {
        // need to add code
        cityList.add(city);
    }

    // remove one city
    public boolean removeCity(String cityName) {
        // need to add code
        return cityList.removeIf(city -> city.getName().equals(cityName));
    }
}
