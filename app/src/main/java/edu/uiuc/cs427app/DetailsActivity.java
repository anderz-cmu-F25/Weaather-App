package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

@SuppressLint("SetTextI18n, NonConstantResourceId")
@SuppressWarnings("ConstantConditions")
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    // Define SharedPreferences constants for saving user settings
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected UI settings (button and background color) from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(BACKGROUND_COLOR_KEY, "Default");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Reference the details ConstraintLayout to apply user customizations
        ConstraintLayout detailsLayout = findViewById(R.id.detailsLayout);

        // Apply the saved background color to the layout
        MainActivity.applyBackgroundColor(backgroundColor, detailsLayout);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city").toString();
        String welcome = "Welcome to the "+cityName;
        String cityWeatherInfo = "Detailed information about the weather of "+cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);
        // Get the weather information from a Service that connects to a weather server and show the results

        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this);

        // Apply the saved button color to all buttons and ActionBar
        MainActivity.applyButtonColors(this, buttonColor, buttonMap);
    }


    @Override
    public void onClick(View view) {
        //Implement this (create an Intent that goes to a new Activity, which shows the map)
    }
}

