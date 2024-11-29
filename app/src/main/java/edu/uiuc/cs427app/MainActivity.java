package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

/*
suppress non relevant warnings
*/
@SuppressLint("SetTextI18n, NonConstantResourceId")
@SuppressWarnings("ConstantConditions")

/**
 * MainActivity is the primary entry point for the user interface after logging in.
 *
 * This activity handles user interactions related to managing a list of cities, including
 * adding, viewing, and deleting cities. It retrieves user-specific settings from SharedPreferences,
 * such as the logged-in username and UI theme settings (button and background colors).
 *
 * Main functionalities of this class include:
 * - Loading and saving the user's city list using JSON serialization with Gson.
 * - Applying user-defined colors to buttons and the main layout based on preferences.
 * - Handling click events for various buttons, such as adding a new city, customizing the UI, and logging out.
 * - Displaying city details and managing the layout of city buttons dynamically.
 *
 * This activity extends AppCompatActivity and implements View.OnClickListener, allowing for integration
 * with the Android Action Bar and responsive UI interactions.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ENTER_CITY_NAME_HINT = "Type City Name Here";
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";

    private static final String LOGIN_PREFS = "LoginPrefs";
    private static final String LAST_LOGGED_IN_USER = "lastLoggedInUser";

    private String currentUsername;
    private LinearLayout cityButtonsLayout;
    private List<City> cityList;

    /**
     * Initializes the activity, sets user-specific preferences, and configures UI elements.
     * <p>
     * This method is called when the activity is created. It retrieves the logged-in username
     * from the intent and updates the action bar title to include the username.
     * The method also saves the current username in SharedPreferences to track the last logged-in user.
     * <p>
     * This method also:
     * - Loads the user's saved city list.
     * - Retrieves and applies user-specific theme settings (button and background colors).
     * - Sets up click listeners for main action buttons (Add Location, Customize UI, and Logout).
     * - Calls methods to apply color settings to UI elements and refresh city buttons in the layout.
     *
     * @param savedInstanceState a Bundle containing the activity's previously saved state, if available
     */

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


    //Helper method 1: apply the button colors to all buttons and ActionBar

    /**
     * Applies the specified color to a set of buttons and, if applicable, the activity's action bar.
     * <p>
     * This method changes the background color of each button in the provided array to the specified color.
     * The color defaults to blue if an unrecognized color name is provided.
     *
     * @param activity    the activity containing the buttons and optional action bar
     * @param buttonColor the name of the color to apply (e.g., "Blue", "Red", "Green")
     * @param buttons     the buttons to which the color will be applied
     */
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

    //Helper method 2: apply the background color to the layout

    /**
     * Applies the specified background color to the given layout based on user choice.
     * <p>
     * This method sets the background color of a layout based on the provided color
     * name. It defaults to white if an unrecognized color is specified.
     *
     * @param backgroundColor the name of the color to apply (e.g., "White", "LightGray", "Gray")
     * @param layout          the layout to which the background color will be applied
     */
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
    /* Using sharedPreferences which will be stored in an xml file in the app data folder */

    /**
     * Loads the saved city list from shared preferences for the current user.
     * <p>
     * This method retrieves the JSON string of the city list from shared preferences,
     * deserializes it using Gson, and populates the `cityList` variable. If no saved
     * data is found, an empty city list is initialized.
     */
    private void loadCityList() {
        cityList = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(currentUsername + "_cities", "");

        if (!json.isEmpty()) {
            Type type = new TypeToken<ArrayList<City>>() {
            }.getType();
            cityList = gson.fromJson(json, type);
        }
    }

    /**
     * Saves the current city list to shared preferences.
     * <p>
     * This method converts the city list to a JSON string using Gson and stores it
     * in shared preferences under a key specific to the current user. The data is
     * saved asynchronously to persist the user's city list across sessions.
     */
    private void saveCityList() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cityList);
        editor.putString(currentUsername + "_cities", json);
        editor.apply();
    }

    /**
     * Refreshes the display of city buttons within the layout based on the given color preference.
     * <p>
     * This method dynamically rebuilds a list of buttons for each city in the city list, applying
     * the specified button color. For each city, it creates:
     * - A clickable button to display city details.
     * - A delete button "X" with a confirmation dialog for removing the city.
     * <p>
     * The layout is cleared and updated to reflect any changes to the city list, such as additions
     * or deletions, by calling this method.
     *
     * @param buttonColor the color preference to apply to each city button and delete button
     */
    private void refreshCityButtons(String buttonColor) {
        cityButtonsLayout.removeAllViews();

        for (City city : cityList) {
            //Create horizontal layout for each city row
            LinearLayout cityRow = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 0, 0, 16); // bottom margin between rows
            cityRow.setLayoutParams(rowParams);
            cityRow.setOrientation(LinearLayout.HORIZONTAL);

            //Create the city button
            Button cityButton = new Button(this);
            cityButton.setText(city.getName());
            // Make city button take up most of the space
            LinearLayout.LayoutParams cityButtonParams = new LinearLayout.LayoutParams(
                    0,  // width of 0 with weight will make it fill available space
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cityButtonParams.weight = 1; // take up all space except for delete button
            cityButtonParams.setMargins(0, 0, 16, 0); //right margin for spacing between buttons
            cityButton.setLayoutParams(cityButtonParams);

            cityButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("city", city.getName());
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            });

            //Create the delete button
            Button deleteButton = new Button(this);
            deleteButton.setText("X");

            //Set fixed width for delete button
            LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            deleteButton.setLayoutParams(deleteButtonParams);

            deleteButton.setOnClickListener(v -> {
                //Show confirmation dialog
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

            //Apply colors to both buttons
            applyButtonColors(this, buttonColor, cityButton, deleteButton);

            //Add both buttons to the row
            cityRow.addView(cityButton);
            cityRow.addView(deleteButton);

            //Add the row to the main layout
            cityButtonsLayout.addView(cityRow);
        }
    }

    /**
     * Opens a dialog for the user to add a new city to the city list.
     * <p>
     * The dialog allows the user to enter a city name. If the input is valid (non-empty),
     * a new City object is created and added to the city list, then saved. After adding,
     * the method refreshes city buttons based on the user's selected color preferences.
     * The dialog includes "Add" and "Cancel" options.
     */
    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New City");

        final EditText input = new EditText(this);
        input.setHint(ENTER_CITY_NAME_HINT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String cityName = input.getText().toString().trim();
            if (!cityName.isEmpty()) {
                MapService mapService = new MapService();
                City newCity = new City("Invalid", Double.MIN_VALUE, Double.MIN_VALUE);
                try {
                     newCity = mapService.execute(cityName, getApiKey()).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (newCity.getName().equals("Invalid") && newCity.getLatitude() == Double.MIN_VALUE && newCity.getLongitude() == Double.MIN_VALUE) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Invalid City")
                            .setMessage("The city you entered is invalid. Please try again.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }
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

    /**
     * Logs out the current user by clearing login information from shared preferences
     * and redirects to the login activity.
     * <p>
     * This method removes the last logged-in user's information from shared preferences
     * and starts the LoginActivity with flags to clear the back stack, ensuring the user
     * cannot return to the previous screen. It also finishes the current activity.
     */
    private void handleLogout() {
        SharedPreferences loginPrefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);
        loginPrefs.edit().remove(LAST_LOGGED_IN_USER).apply();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Handles click events for various buttons in the main part of program.
     * <p>
     * Based on the clicked view's ID, this method:
     * - Opens a dialog to add a new city if the "Add Location" button is clicked.
     * - Launches the CustomizeUIActivity with the current username if the "Customize UI" button is clicked.
     * - Calls the logout handler to log out the current user if the "Logout" button is clicked.
     *
     * @param view the view that was clicked, used to determine the action to perform
     */
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

    private String getApiKey() {
        try {
            // Get the ApplicationInfo object
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            // Retrieve the metadata
            Bundle metaData = appInfo.metaData;
            if (metaData != null) {
                return metaData.getString("com.google.android.geo.API_KEY");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}