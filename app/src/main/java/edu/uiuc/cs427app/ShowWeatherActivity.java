package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ShowWeatherActivity extends AppCompatActivity implements View.OnClickListener {
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
        setContentView(R.layout.activity_show_weather);

        // Reference the details ConstraintLayout to apply user customizations
        ConstraintLayout wxLayout = findViewById(R.id.wxLayout);

        // Apply the saved background color to the layout
        MainActivity.applyBackgroundColor(backgroundColor, wxLayout);

        Button wxAiButton = findViewById(R.id.wxAiButton);
        wxAiButton.setOnClickListener(this);
        MainActivity.applyButtonColors(this, buttonColor, wxAiButton);

        String cityName = getIntent().getStringExtra("city");
        TextView heading = findViewById(R.id.wxHeading);
        heading.setText(String.format("%s %s", heading.getText(), cityName));
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.wxAiButton) {
        }
    }
}