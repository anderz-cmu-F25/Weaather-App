package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
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
        switch (backgroundColor) {
            case "White":
                mainLayout.setBackgroundColor(Color.WHITE);
                break;
            case "Light Gray":
                mainLayout.setBackgroundColor(Color.LTGRAY);
                break;
            case "Gray":
                mainLayout.setBackgroundColor(Color.GRAY);
                break;
        }

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

        // Set listener for the "Customize UI" button
        buttonCustomizeUI.setOnClickListener(v -> {
            // Open the CustomizeUIActivity to customize the UI
            Intent intent = new Intent(MainActivity.this, CustomizeUIActivity.class);
            startActivity(intent);
        });

        // Apply the saved button color to all buttons and ActionBar
        applyButtonColors(buttonColor, buttonChampaign, buttonChicago, buttonLA, buttonAddLocation, buttonCustomizeUI);
    }

    // Helper method to apply the button colors to all buttons and ActionBar
    private void applyButtonColors(String buttonColor, Button... buttons) {
        int color = Color.BLUE;  // Default button color

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

        // Apply the same color to the ActionBar (top bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        }
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
}
