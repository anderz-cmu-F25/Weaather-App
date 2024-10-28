package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.graphics.drawable.ColorDrawable;

@SuppressLint("SetTextI18n, NonConstantResourceId")
@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.activity_main);

        // Reference the main ConstraintLayout to apply user customizations
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);

        // Apply the saved background color to the layout
        applyBackgroundColor(backgroundColor, mainLayout);

        // Initialize other UI components (buttons)
        Button buttonChampaign = findViewById(R.id.buttonChampaign);
        Button buttonChicago = findViewById(R.id.buttonChicago);
        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);
        Button buttonCustomizeUI = findViewById(R.id.buttonCustomizeUI);

        // Set click listeners for existing buttons
        buttonChampaign.setOnClickListener(this);
        buttonChicago.setOnClickListener(this);
        buttonLA.setOnClickListener(this);
        buttonAddLocation.setOnClickListener(this);
        buttonCustomizeUI.setOnClickListener(this);  // Add listener for "Customize UI" button

        // Apply the saved button color to all buttons and ActionBar
        applyButtonColors(this, buttonColor, buttonChampaign, buttonChicago, buttonLA, buttonAddLocation, buttonCustomizeUI);
    }

    // Helper method 1: apply the button colors to all buttons and ActionBar
    public static void applyButtonColors(Activity activity, String buttonColor, Button... buttons) {
        int color = Color.BLUE;

        // Determine the color based on the saved preference
        switch (buttonColor) {
            case "Blue":
                color = Color.BLUE;
                break;
            case "Red":
                color = Color.RED;
                break;
            case "Green":
                color = Color.GREEN;
                break;
            // Add more colors as needed
        }

        // Apply the color to each button
        for (Button button : buttons) {
            button.setBackgroundColor(color);
        }

        // Apply the same color to the ActionBar (top bar) if applicable
        if (activity instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }
        }
    }

    // Helper method 2: apply the background color to the layout
    public static void applyBackgroundColor(String backgroundColor, ViewGroup layout) {
        int color = Color.WHITE;  // Default background color

        // Determine the color based on the saved preference
        switch (backgroundColor) {
            case "White":
                color = Color.WHITE;
                break;
            case "LightGray":
                color = Color.LTGRAY;
                break;
            case "Gray":
                color = Color.GRAY;
                break;
            // Add more colors as needed
        }

        // Apply the color to the layout
        layout.setBackgroundColor(color);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonChampaign:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Champaign");
                startActivity(intent);
                break;
            case R.id.buttonChicago:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Chicago");
                startActivity(intent);
                break;
            case R.id.buttonLA:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Los Angeles");
                startActivity(intent);
                break;
            case R.id.buttonAddLocation:
                // Implement this action to add a new location to the list of locations
                break;
            case R.id.buttonCustomizeUI:
                // Open the CustomizeUIActivity to customize the UI
                intent = new Intent(this, CustomizeUIActivity.class);
                startActivity(intent);
                break;
        }
    }
}
