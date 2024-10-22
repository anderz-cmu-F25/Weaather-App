package edu.uiuc.cs427app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;

import android.widget.Button;

// Import the SharedPreferences class to save the selected theme
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    // Define SharedPreferences constants for saving user settings
    // File name for SharedPreferences
    private static final String PREFS_NAME = "UserSettings";
    // Key used to store the theme preference
    private static final String THEME_KEY = "Theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected theme from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userTheme = preferences.getString(THEME_KEY, "Default");
        // Apply the appropriate theme before setting the content view
        setAppTheme(userTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout programmatically
        Button buttonChampaign = findViewById(R.id.buttonChampaign);
        Button buttonChicago = findViewById(R.id.buttonChicago);
        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonNew = findViewById(R.id.buttonAddLocation);

        // New button for switching theme
        Button buttonSwitchTheme = findViewById(R.id.buttonSwitchTheme);

        buttonChampaign.setOnClickListener(this);
        buttonChicago.setOnClickListener(this);
        buttonLA.setOnClickListener(this);
        buttonNew.setOnClickListener(this);

        // Set listener for the switch theme button
        buttonSwitchTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTheme();  // Call method to switch themes
            }
        });
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
        }
    }

    // Method to apply the theme to the activity based on the theme name
    private void setAppTheme(String themeName) {
        // Switch between different themes defined in themes.xml
        switch (themeName) {
            case "Dark":
                setTheme(R.style.Theme_MyFirstApp_Dark);
                break;
            case "Light":
                setTheme(R.style.Theme_MyFirstApp_Light);
                break;
            default:
                setTheme(R.style.Theme_MyFirstApp);
                break;
        }
    }

    // Method to switch between themes when the "Switch Theme" button is clicked
    private void switchTheme() {
        // Access SharedPreferences to retrieve and save the theme
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Get the current theme from SharedPreferences
        String currentTheme = preferences.getString(THEME_KEY, "Default");
        String newTheme;

        // Cycle through the available themes (Default -> Dark -> Light -> Default)
        switch (currentTheme) {
            case "Default":
                newTheme = "Dark";
                break;
            case "Dark":
                newTheme = "Light";
                break;
            default:
                newTheme = "Default";
                break;
        }

        // Save the new theme in SharedPreferences
        editor.putString(THEME_KEY, newTheme);
        editor.apply();  // Commit changes

        // Restart the activity to apply the new theme
        recreate();
    }
}

