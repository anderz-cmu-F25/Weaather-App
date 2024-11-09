package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowWeatherActivity extends AppCompatActivity implements View.OnClickListener {
    // Define SharedPreferences constants for saving user settings
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private static final String API_KEY = "7c5165db4a5665f335243817a27495c7";

    private String city;
    private String currentUsername; // Add this field
    private TextView cityName, dateTime, temperature, weatherCondition, humidity, wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get username from intent
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            // Fallback to shared preferences if not in intent
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            currentUsername = prefs.getString("lastLoggedInUser", "default");
        }

        // Load user-specific UI settings
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather);

        cityName = findViewById(R.id.cityName);
        dateTime = findViewById(R.id.dateTime);
        temperature = findViewById(R.id.temperature);
        weatherCondition = findViewById(R.id.weatherCondition);
        humidity = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);

        // Reference the details ConstraintLayout to apply user customizations
        ConstraintLayout wxLayout = findViewById(R.id.wxLayout);

        // Apply the saved background color to the layout
        MainActivity.applyBackgroundColor(backgroundColor, wxLayout);

        // fetch weather data from third-party API
        city = getIntent().getStringExtra("city");
        fetchWeatherData(city);

        Button wxAiButton = findViewById(R.id.wxAiButton);
        wxAiButton.setOnClickListener(this);
        MainActivity.applyButtonColors(this, buttonColor, wxAiButton);

        //TextView heading = findViewById(R.id.wxHeading);
        //heading.setText(String.format("%s %s", heading.getText(), cityName));
    }

    private void fetchWeatherData(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = weatherService.getWeatherData(city, API_KEY, "imperial");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.d("WeatherActivity", "API Call Failed: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void updateUI(WeatherResponse weatherData) {
        cityName.setText(weatherData.name);

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());
        dateTime.setText("Date & Time: " + currentDateTime);
        temperature.setText("Temperature: " + weatherData.main.temp + "°F");
        weatherCondition.setText("Weather: " + weatherData.weather[0].main);
        humidity.setText("Humidity: " + weatherData.main.humidity + "%");
        wind.setText("Wind: " + weatherData.wind.speed + " m/s");
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.wxAiButton) {
        }
    }
}