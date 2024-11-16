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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowWeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private static final String API_KEY = "964a23165ccbb4d5a9cfe97bdc46aee4";
    private static final String TAG = "ShowWeatherActivity";

    /**
     * A regular expression pattern to parse location strings in the format:
     * "City", "City, State", or "City, State, Country".
     *
     * The pattern matches:
     * 1. A city name, which is any sequence of characters before the first comma.
     * 2. An optional two-letter state abbreviation, preceded by a comma and optional whitespace.
     * 3. An optional two-letter country abbreviation, also preceded by a comma and optional whitespace.
     *
     * Examples of valid matches:
     * - "New York"
     * - "New York, NY"
     * - "New York, NY, US"
     *
     * Pattern breakdown:
     * - ^([^,]+)              : Captures the city name before the first comma.
     * - (?:,\\s*([A-Z]{2}))?  : Optionally captures a two-letter state code following a comma and whitespace.
     * - (?:,\\s*([A-Z]{2}))?$ : Optionally captures a two-letter country code after a comma and whitespace.
     *
     * The pattern is case-insensitive, so state and country codes may be in any combination of upper/lower case.
     */
    private static final Pattern LOCATION_PATTERN = Pattern.compile(
            "^([^,]+)(?:,\\s*([A-Z]{2}))?(?:,\\s*([A-Z]{2}))?$",
            Pattern.CASE_INSENSITIVE
    );

    private String currentUsername;
    private TextView cityName, dateTime, temperature, weatherCondition, humidity, wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            currentUsername = prefs.getString("lastLoggedInUser", "default");
        }

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather);

        initializeViews();

        ConstraintLayout wxLayout = findViewById(R.id.wxLayout);
        MainActivity.applyBackgroundColor(backgroundColor, wxLayout);

        String locationInput = getIntent().getStringExtra("city");
        if (locationInput != null && !locationInput.trim().isEmpty()) {
            fetchWeatherData(locationInput);
        } else {
            Log.e(TAG, "No location provided");
            // Handle the error appropriately in your UI
        }

        Button wxAiButton = findViewById(R.id.wxAiButton);
        wxAiButton.setOnClickListener(this);
        MainActivity.applyButtonColors(this, buttonColor, wxAiButton);
    }

    private void initializeViews() {
        cityName = findViewById(R.id.cityName);
        dateTime = findViewById(R.id.dateTime);
        temperature = findViewById(R.id.temperature);
        weatherCondition = findViewById(R.id.weatherCondition);
        humidity = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);
    }

    /**
     * Formats the location input according to OpenWeather API specifications.
     * Handles various input formats:
     * - City name only
     * - City name, state code (USA only)
     * - City name, country code
     * - City name, state code, country code (USA only)
     *
     * @param input The raw location input string
     * @return Formatted location string for API call
     */
    private String formatLocationInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Location input cannot be empty");
        }

        input = input.trim();
        Matcher matcher = LOCATION_PATTERN.matcher(input);

        if (!matcher.matches()) {
            // If the input doesn't match the expected format, return the cleaned city name
            return input.split(",")[0].trim();
        }

        String city = matcher.group(1).trim();
        String stateCode = matcher.group(2);
        String countryCode = matcher.group(3);

        StringBuilder formattedLocation = new StringBuilder(city);

        //Handle US state codes
        if (stateCode != null && countryCode != null && countryCode.equalsIgnoreCase("US")) {
            formattedLocation.append(",").append(stateCode.toUpperCase());
        }

        //Add country code if present
        if (countryCode != null) {
            formattedLocation.append(",").append(countryCode.toUpperCase());
        }
        //If only state code is present (assumed to be US)
        else if (stateCode != null) {
            formattedLocation.append(",").append(stateCode.toUpperCase()).append(",US");
        }

        return formattedLocation.toString();
    }

    private void fetchWeatherData(String locationInput) {
        try {
            String formattedLocation = formatLocationInput(locationInput);
            Log.d(TAG, "Fetching weather data for location: " + formattedLocation);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            WeatherService weatherService = retrofit.create(WeatherService.class);
            Call<WeatherResponse> call = weatherService.getWeatherData(formattedLocation, API_KEY, "imperial");

            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        updateUI(response.body());
                    } else {
                        Log.e(TAG, "API Error: " + response.code() + " " + response.message());
                        handleApiError(response.code());
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    Log.e(TAG, "API Call Failed: " + t.getMessage());
                    t.printStackTrace();
                    handleApiError(-1);
                }
            });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid location input: " + e.getMessage());
            // Handle the error appropriately in your UI
        }
    }

    private void handleApiError(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case 404:
                errorMessage = "City not found";
                break;
            case 401:
                errorMessage = "Invalid API key";
                break;
            case 429:
                errorMessage = "Too many requests";
                break;
            default:
                errorMessage = "Error fetching weather data";
        }
        // Update UI to show error message
        // You might want to add a TextView for errors in your layout
        Log.e(TAG, errorMessage);
    }

    /**
     * Updates the UI components with weather data from a WeatherResponse object.
     * This method displays the city name, local date and time (adjusted for the
     * timezone from the weather data), temperature, weather condition, humidity,
     * and wind speed.
     *
     * @param weatherData the WeatherResponse object containing the weather data to display
     *
     * Method Details:
     * - Sets the city name in the `cityName` TextView.
     * - Creates a `SimpleTimeZone` based on the timezone offset (in seconds) provided in `weatherData`.
     * - Initializes a `Calendar` instance set to the current time in the specified timezone.
     * - Creates a timezone-aware `SimpleDateFormat` instance for formatting the local date and time.
     * - Formats the current date and time using this formatter and sets it in the `dateTime` TextView.
     * - Updates the UI with the temperature, weather condition, humidity, and wind speed from the `weatherData`.
     */
    private void updateUI(WeatherResponse weatherData) {
        cityName.setText(weatherData.name);

        //Create a TimeZone with the offset from the API
        SimpleTimeZone timeZone = new SimpleTimeZone(weatherData.timezone * 1000, "API_TIMEZONE");

        //Create Calendar instance and set it to current time
        Calendar calendar = Calendar.getInstance(timeZone);
        Date currentDate = new Date();
        calendar.setTime(currentDate);

        //Create DateFormat with the specific timezone
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(timeZone);

        //Format the date using the timezone-aware formatter
        String localDateTime = dateFormat.format(calendar.getTime());

        dateTime.setText("Local Date & Time: " + localDateTime);
        temperature.setText("Temperature: " + weatherData.main.temp + "°F");
        weatherCondition.setText("Weather: " + weatherData.weather[0].main);
        humidity.setText("Humidity: " + weatherData.main.humidity + "%");
        wind.setText("Wind: " + weatherData.wind.speed + " m/s");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.wxAiButton) {
            // Handle button click
            Intent intent = new Intent(this, WeatherInsightsActivity.class);
            intent.putExtra("weatherData", getFormattedWeatherData());
            startActivity(intent);
        }
    }

    // Helper method to format weather data as a string
    private String getFormattedWeatherData() {
        return "Temperature: " + temperature.getText().toString() + ", " +
                "Condition: " + weatherCondition.getText().toString() + ", " +
                "Humidity: " + humidity.getText().toString() + ", " +
                "Wind: " + wind.getText().toString();
    }

}