package edu.uiuc.cs427app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * To make HTTP requests to open weather map to fetch weather information
 */
public interface WeatherService {

    @GET("weather")
    Call<WeatherResponse> getWeatherData(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}