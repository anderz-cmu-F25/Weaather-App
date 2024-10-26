package edu.uiuc.cs427app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n, NonConstantResourceId")
@SuppressWarnings("ConstantConditions")
public class CustomizeUIActivity extends AppCompatActivity {

    // SharedPreferences constants for saving and retrieving user customizations
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private static final String UI_PREFIX = "SavedUI_";  // Prefix for saved UI settings

    // Boolean to track whether the saved customization list is expanded or collapsed
    private boolean isExpanded = false;
    // Counter to keep track of the number of saved UIs
    private int savedUICount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the selected UI settings (button and background color) from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(BACKGROUND_COLOR_KEY, "Default");
        savedUICount = preferences.getInt("savedUICount", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_ui);

        // Reference the details ScrollView to apply user customizations
        ScrollView uiView = findViewById(R.id.uiView);
        // Apply the saved background color to the layout
        MainActivity.applyBackgroundColor(backgroundColor, uiView);

        // Initialize the UI components
        RadioGroup buttonColorGroup = findViewById(R.id.radioGroupButtonColor);
        RadioGroup backgroundColorGroup = findViewById(R.id.radioGroupBackgroundColor);
        Button saveButton = findViewById(R.id.saveCustomizationsButton);
        Button viewButton = findViewById(R.id.viewCustomizationsButton);
        Button defaultUIButton = findViewById(R.id.defaultUIButton);
        LinearLayout savedCustomizationsLayout = findViewById(R.id.savedCustomizationsLayout);

        // Apply the saved button color to all buttons and ActionBar
        MainActivity.applyButtonColors(this, buttonColor, saveButton, viewButton, defaultUIButton);

        // Preselect "Blue" for button color and "White" for background color by default
        buttonColorGroup.check(R.id.radioButtonBlue);  // Preselect "Blue Button"
        backgroundColorGroup.check(R.id.radioButtonWhite);  // Preselect "White Background"

        // Save customizations as "Saved UI"
        saveButton.setOnClickListener(v -> {
            // Get selected colors
            int selectedButtonColorId = buttonColorGroup.getCheckedRadioButtonId();
            String buttonColorSelected = getColorFromId(selectedButtonColorId);

            int selectedBackgroundColorId = backgroundColorGroup.getCheckedRadioButtonId();
            String backgroundColorSelected = getColorFromId(selectedBackgroundColorId);

            // Increment the count for saved UIs (this will preserve the previous savedUICount)
            savedUICount++;

            // Create a user-friendly description for the saved customization
            String uiDescription = buttonColorSelected + " Button & "
                    + backgroundColorSelected + " Background";

            // Save the customization with a unique ID (e.g., SavedUI_1, SavedUI_2)
            SharedPreferences.Editor editor = preferences.edit();
            String uiKey = UI_PREFIX + savedUICount;  // E.g. SavedUI_1
            editor.putString(uiKey + "_buttonColor", buttonColorSelected);
            editor.putString(uiKey + "_backgroundColor", backgroundColorSelected);
            editor.putString(uiKey + "_description", uiDescription);  // Save the description

            // Save the updated UI count
            editor.putInt("savedUICount", savedUICount);
            editor.apply();

            // Show a message to the user
            Toast.makeText(CustomizeUIActivity.this, "Customization Saved: "
                    + uiDescription, Toast.LENGTH_SHORT).show();

            // Check if the saved customizations view is currently collapsed
            if (!isExpanded) {
                // Expand the view and update the text of the viewButton
                savedCustomizationsLayout.setVisibility(View.VISIBLE);
                viewButton.setText("Hide Saved Customizations");
                isExpanded = true;  // Mark the view as expanded
            }

            // Refresh the saved customizations
            loadSavedCustomizations(savedCustomizationsLayout);  // Refresh the list
        });

        // Toggle visibility of saved customizations
        viewButton.setOnClickListener(v -> {
            if (isExpanded) {
                // If the list is expanded, collapse it
                savedCustomizationsLayout.setVisibility(View.GONE);
                viewButton.setText("View Saved Customizations");
                isExpanded = false;
            } else {
                // If the list is collapsed, expand it and load the saved customizations
                savedCustomizationsLayout.setVisibility(View.VISIBLE);
                viewButton.setText("Hide Saved Customizations");
                loadSavedCustomizations(savedCustomizationsLayout);
                isExpanded = true;
            }
        });

        // Revert to Default UI (Blue Button, White Background)
        defaultUIButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();

            // Set default button color to "Blue" and background color to "White"
            editor.putString(BUTTON_COLOR_KEY, "Blue");
            editor.putString(BACKGROUND_COLOR_KEY, "White");
            editor.apply();

            // Restart MainActivity to apply the changes globally
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the current activity stack
            startActivity(intent);

            Toast.makeText(CustomizeUIActivity.this, "UI Reverted to Default!", Toast.LENGTH_SHORT).show();
        });
    }

    // Helper method 1: convert radio button IDs to color strings
    private String getColorFromId(int colorId) {
        String resourceName = getResources().getResourceEntryName(colorId); // e.g., "radioButtonBlue"
        String[] parts = resourceName.split("radioButton"); // Split at "radioButton"
        return parts.length > 1 ? parts[1] : "Default"; // Get the color part or return "Default"
    }

    // Helper method 2: load saved customizations
    private void loadSavedCustomizations(LinearLayout savedCustomizationsLayout) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve the current UI count from SharedPreferences
        int savedUICount = preferences.getInt("savedUICount", 0);

        // Clear previous views
        savedCustomizationsLayout.removeAllViews();

        // Loop through saved UIs and display them
        for (int i = 1; i <= savedUICount; i++) {
            String uiKey = UI_PREFIX + i;
            String buttonColor = preferences.getString(uiKey + "_buttonColor", null);
            String backgroundColor = preferences.getString(uiKey + "_backgroundColor", null);
            String description = preferences.getString(uiKey + "_description", null);  // Retrieve the description

            if (buttonColor != null && backgroundColor != null && description != null) {
                // Dynamically create a TextView for each saved UI
                TextView savedUIText = new TextView(this);
                savedUIText.setText(description);  // Use the saved description

                // Dynamically create an "Apply" button
                Button applyButton = new Button(this);
                applyButton.setText("Apply " + description);  // Show the description in the button
                applyButton.setOnClickListener(v1 -> {
                    // Apply the selected UI
                    applySavedCustomization(uiKey);
                });

                // Dynamically create a "Delete" button
                Button deleteButton = new Button(this);
                deleteButton.setText("Delete " + description);  // Show the description in the button
                deleteButton.setOnClickListener(v1 -> {
                    // Delete the selected UI
                    deleteSavedCustomization(uiKey);
                    savedCustomizationsLayout.removeView(savedUIText);
                    savedCustomizationsLayout.removeView(applyButton);
                    savedCustomizationsLayout.removeView(deleteButton);
                });

                // Add the TextView, Apply Button, and Delete Button to the layout
                savedCustomizationsLayout.addView(savedUIText);
                savedCustomizationsLayout.addView(applyButton);
                savedCustomizationsLayout.addView(deleteButton);
            }
        }
    }

    // Helper method 3: apply saved customizations to the whole app
    private void applySavedCustomization(String uiKey) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(uiKey + "_buttonColor", "Default");
        String backgroundColor = preferences.getString(uiKey + "_backgroundColor", "Default");
        String description = preferences.getString(uiKey + "_description", "Default UI");  // Get the description

        // Apply button color and background color
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BUTTON_COLOR_KEY, buttonColor);
        editor.putString(BACKGROUND_COLOR_KEY, backgroundColor);
        editor.apply();

        // Restart MainActivity to apply the changes globally
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // Clear the current activity stack
        startActivity(intent);

        // Display the description of the applied customization
        Toast.makeText(this, "Applied " + description, Toast.LENGTH_SHORT).show();
    }

    // Helper method 4: delete saved customizations
    private void deleteSavedCustomization(String uiKey) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Get the description for the saved customization to display in the toast
        String description = preferences.getString(uiKey + "_description", "Unknown Customization");

        // Remove the saved customization from SharedPreferences
        editor.remove(uiKey + "_buttonColor");
        editor.remove(uiKey + "_backgroundColor");
        editor.remove(uiKey + "_description");  // Remove the description as well

        // Retrieve and update the UI count
        int savedUICount = preferences.getInt("savedUICount", 0);
        if (savedUICount > 0) {
            savedUICount--;
            editor.putInt("savedUICount", savedUICount);
        }
        editor.apply();

        // Display a toast with the descriptive name of the deleted customization
        Toast.makeText(this, "Deleted " + description, Toast.LENGTH_SHORT).show();
    }
}
