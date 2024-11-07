package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

@SuppressLint("SetTextI18n, NonConstantResourceId")
@SuppressWarnings("ConstantConditions")

/**
 * DetailsActivity class provides detailed information about a specific city.
 * It retrieves user preferences for UI customization and handles navigation
 * to a weather display or map view based on user interactions.
 */
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";

    private String cityName;
    private String currentUsername; // Add this field

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
        setContentView(R.layout.activity_details);

        ConstraintLayout detailsLayout = findViewById(R.id.detailsLayout);
        MainActivity.applyBackgroundColor(backgroundColor, detailsLayout);

        this.cityName = getIntent().getStringExtra("city");
        String welcome = "Welcome to the " + this.cityName;
        String cityWeatherInfo = "Detailed information about the weather of " + this.cityName;

        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);

        Button wxButton = findViewById(R.id.wxButton);
        Button buttonShowMap = findViewById(R.id.mapButton);
        wxButton.setOnClickListener(this);
        buttonShowMap.setOnClickListener(this);

        MainActivity.applyButtonColors(this, buttonColor, wxButton, buttonShowMap);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.wxButton:
                intent = new Intent(this, ShowWeatherActivity.class);
                intent.putExtra("city", this.cityName);
                intent.putExtra("username", currentUsername); // Pass username to weather activity
                startActivity(intent);
                break;
            case R.id.mapButton:
                intent = new Intent(this, ShowMapActivity.class);
                intent.putExtra("city", this.cityName);
                intent.putExtra("username", currentUsername); // Pass username to map activity
                startActivity(intent);
                break;
        }
    }
}