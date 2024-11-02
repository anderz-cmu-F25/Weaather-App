package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.graphics.drawable.ColorDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n, NonConstantResourceId")
@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";

    private static final String LOGIN_PREFS = "LoginPrefs";
    private static final String LAST_LOGGED_IN_USER = "lastLoggedInUser";

    private String currentUsername;
    private LinearLayout cityButtonsLayout;
    private List<City> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the last logged-in username from SharedPreferences
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            SharedPreferences prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
            currentUsername = prefs.getString(LAST_LOGGED_IN_USER, "default");
        }

        //Set title to include the logged in username
        getSupportActionBar().setTitle(getString(R.string.app_name_with_user, currentUsername));

        // Save current username to SharedPreferences to keep track of the last logged-in user
        SharedPreferences loginPrefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        loginPrefs.edit().putString(LAST_LOGGED_IN_USER, currentUsername).apply();

        setContentView(R.layout.activity_main);
        loadCityList();

        // Load and apply user-specific theme settings
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");

        // Apply background and button colors
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        applyBackgroundColor(backgroundColor, mainLayout);

        cityButtonsLayout = findViewById(R.id.cityButtonsLayout);

        Button buttonAddLocation = findViewById(R.id.buttonAddLocation);
        Button buttonCustomizeUI = findViewById(R.id.buttonCustomizeUI);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        buttonAddLocation.setOnClickListener(this);
        buttonCustomizeUI.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);

        applyButtonColors(this, buttonColor, buttonAddLocation, buttonCustomizeUI, buttonLogout);
        refreshCityButtons(buttonColor);
    }


    // Helper method 1: apply the button colors to all buttons and ActionBar
    public static void applyButtonColors(Activity activity, String buttonColor, Button... buttons) {
        int color = Color.BLUE;  // Default color

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

    // Helper method 2: apply the background color to the layout
    public static void applyBackgroundColor(String backgroundColor, ViewGroup layout) {
        int color = Color.WHITE;  // Default background color

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

    private void loadCityList() {
        cityList = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(currentUsername + "_cities", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<City>>(){}.getType();
            cityList = gson.fromJson(json, type);
        }
    }

    private void saveCityList() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        editor.putString(currentUsername + "_cities", json);
        editor.apply();
    }

    private void refreshCityButtons(String buttonColor) {
        cityButtonsLayout.removeAllViews();

        for (City city : cityList) {
            // Create horizontal layout for each city row
            LinearLayout cityRow = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 0, 0, 16); // bottom margin between rows
            cityRow.setLayoutParams(rowParams);
            cityRow.setOrientation(LinearLayout.HORIZONTAL);

            // Create the city button
            Button cityButton = new Button(this);
            cityButton.setText(city.getName());
            // Make city button take up most of the space
            LinearLayout.LayoutParams cityButtonParams = new LinearLayout.LayoutParams(
                    0,  // width of 0 with weight will make it fill available space
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cityButtonParams.weight = 1; // This makes it take up all space except for delete button
            cityButtonParams.setMargins(0, 0, 16, 0); // right margin for spacing between buttons
            cityButton.setLayoutParams(cityButtonParams);

            cityButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("city", city.getName());
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            });

            // Create the delete button
            Button deleteButton = new Button(this);
            deleteButton.setText("X");

            // Set fixed width for delete button
            LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            deleteButton.setLayoutParams(deleteButtonParams);

            deleteButton.setOnClickListener(v -> {
                // Show confirmation dialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete City")
                        .setMessage("Are you sure you want to delete " + city.getName() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            cityList.remove(city);
                            saveCityList();
                            refreshCityButtons(buttonColor);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            // Apply colors to both buttons
            applyButtonColors(this, buttonColor, cityButton, deleteButton);

            // Add both buttons to the row
            cityRow.addView(cityButton);
            cityRow.addView(deleteButton);

            // Add the row to the main layout
            cityButtonsLayout.addView(cityRow);
        }
    }

    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New City");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String cityName = input.getText().toString().trim();
            if (!cityName.isEmpty()) {
                City newCity = new City(cityName, 0.0, 0.0);
                cityList.add(newCity);
                saveCityList();

                String buttonColor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
                refreshCityButtons(buttonColor);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void handleLogout() {
        SharedPreferences loginPrefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        loginPrefs.edit().remove(LAST_LOGGED_IN_USER).apply();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.buttonAddLocation) {
            showAddCityDialog();
        } else if (view.getId() == R.id.buttonCustomizeUI) {
            intent = new Intent(this, CustomizeUIActivity.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        } else if (view.getId() == R.id.buttonLogout) {
            handleLogout();
        }
    }
}