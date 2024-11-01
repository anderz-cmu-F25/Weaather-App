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

    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private String currentUsername; // Add this field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get username from intent or saved session
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            // Try to get from SharedPreferences if not in intent
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            currentUsername = prefs.getString("lastLoggedInUser", "default");
        }

        // Load user-specific UI settings
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        applyBackgroundColor(backgroundColor, mainLayout);

        Button buttonChampaign = findViewById(R.id.buttonChampaign);
        Button buttonChicago = findViewById(R.id.buttonChicago);
        Button buttonLA = findViewById(R.id.buttonLA);
        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);
        Button buttonCustomizeUI = findViewById(R.id.buttonCustomizeUI);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        buttonChampaign.setOnClickListener(this);
        buttonChicago.setOnClickListener(this);
        buttonLA.setOnClickListener(this);
        buttonAddLocation.setOnClickListener(this);
        buttonCustomizeUI.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        applyButtonColors(this, buttonColor, buttonChampaign, buttonChicago, buttonLA,
                buttonAddLocation, buttonCustomizeUI, buttonLogout);
    }

    // Modified to handle user-specific colors
    public static void applyButtonColors(Activity activity, String buttonColor, Button... buttons) {
        int color = Color.BLUE; // Default color

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
        }

        for (Button button : buttons) {
            button.setBackgroundColor(color);
        }

        if (activity instanceof AppCompatActivity) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }
        }
    }

    public static void applyBackgroundColor(String backgroundColor, ViewGroup layout) {
        int color = Color.WHITE;

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
        }

        layout.setBackgroundColor(color);
    }

    private void handleLogout() {
        // Don't clear theme preferences on logout anymore
        // Instead, only clear session-related data
        SharedPreferences loginPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPrefs.edit();
        loginEditor.remove("lastLoggedInUser");
        loginEditor.apply();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.buttonChampaign:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Champaign");
                intent.putExtra("username", currentUsername); // Pass username to next activity
                startActivity(intent);
                break;
            case R.id.buttonChicago:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Chicago");
                intent.putExtra("username", currentUsername); // Pass username to next activity
                startActivity(intent);
                break;
            case R.id.buttonLA:
                intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("city", "Los Angeles");
                intent.putExtra("username", currentUsername); // Pass username to next activity
                startActivity(intent);
                break;
            case R.id.buttonAddLocation:
                // Implement add location functionality
                break;
            case R.id.buttonCustomizeUI:
                intent = new Intent(this, CustomizeUIActivity.class);
                intent.putExtra("username", currentUsername); // Pass username to CustomizeUIActivity
                startActivity(intent);
                break;
            case R.id.buttonLogout:
                handleLogout();
                break;
        }
    }
}