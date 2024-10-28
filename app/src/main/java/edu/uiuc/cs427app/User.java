package edu.uiuc.cs427app;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<City> cityList;

    // initialize for the User
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.cityList = new ArrayList<>();
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
    }

    // remove one city
    public void removeCity(String cityName) {
        // need to add code
    }
}
