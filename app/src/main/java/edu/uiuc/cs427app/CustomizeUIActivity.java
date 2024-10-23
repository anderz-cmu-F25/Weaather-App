package edu.uiuc.cs427app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_ui);

        // Initialize the UI components
        RadioGroup buttonColorGroup = findViewById(R.id.radioGroupButtonColor);
        RadioGroup backgroundColorGroup = findViewById(R.id.radioGroupBackgroundColor);
        Button saveButton = findViewById(R.id.saveCustomizationsButton);
        Button viewButton = findViewById(R.id.viewCustomizationsButton);
        Button defaultUIButton = findViewById(R.id.defaultUIButton);
        LinearLayout savedCustomizationsLayout = findViewById(R.id.savedCustomizationsLayout);

        // Retrieve savedUICount from SharedPreferences (to avoid resetting when activity is recreated)
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        savedUICount = preferences.getInt("savedUICount", 0);

        // Apply any existing customizations to the Customize UI page itself
        String buttonColor = preferences.getString(BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(BACKGROUND_COLOR_KEY, "Default");
        applyCustomizations(buttonColor, backgroundColor);

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

            // Apply the changes to Customize UI page itself immediately
            applyCustomizations("Blue", "White");

            // Restart MainActivity to apply the changes globally
            Intent intent = new Intent(CustomizeUIActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the current activity stack
            startActivity(intent);

            Toast.makeText(CustomizeUIActivity.this, "UI Reverted to Default!", Toast.LENGTH_SHORT).show();
        });
    }

    // Helper method 1: convert radio button IDs to color strings
    private String getColorFromId(int colorId) {
        switch (colorId) {
            case R.id.radioButtonBlue:
                return "Blue";
            case R.id.radioButtonRed:
                return "Red";
            case R.id.radioButtonGreen:
                return "Green";
            case R.id.radioButtonWhite:
                return "White";
            case R.id.radioButtonLightGray:
                return "Light Gray";
            case R.id.radioButtonGray:
                return "Gray";
            default:
                return "Default";
        }
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

    // Helper method 3: apply saved customizations to both MainActivity and CustomizeUIActivity
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

        // Apply the changes to Customize UI page itself immediately
        applyCustomizations(buttonColor, backgroundColor);

        // Restart MainActivity to apply the changes globally
        Intent intent = new Intent(CustomizeUIActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);  // Clear the current activity stack
        startActivity(intent);

        // Display the description of the applied customization
        Toast.makeText(this, "Applied " + description, Toast.LENGTH_SHORT).show();
    }

    // Helper method 4: apply customizations dynamically to the Customize UI page
    private void applyCustomizations(String buttonColor, String backgroundColor) {
        // Apply background color to the Customize UI layout
        LinearLayout layout = findViewById(R.id.customizeUILayout);
        switch (backgroundColor) {
            case "White":
                layout.setBackgroundColor(android.graphics.Color.WHITE);
                break;
            case "Light Gray":
                layout.setBackgroundColor(android.graphics.Color.LTGRAY);
                break;
            case "Gray":
                layout.setBackgroundColor(android.graphics.Color.GRAY);
                break;
            default:
                layout.setBackgroundColor(android.graphics.Color.WHITE);  // Default color
                break;
        }

        // Apply button color to buttons in the Customize UI page
        int buttonColorValue = android.graphics.Color.BLUE;  // Default button color
        switch (buttonColor) {
            case "Blue":
                buttonColorValue = android.graphics.Color.BLUE;
                break;
            case "Red":
                buttonColorValue = android.graphics.Color.RED;
                break;
            case "Green":
                buttonColorValue = android.graphics.Color.GREEN;
                break;
        }

        // Find all the buttons on the page and apply the button color
        Button saveButton = findViewById(R.id.saveCustomizationsButton);
        Button viewButton = findViewById(R.id.viewCustomizationsButton);
        Button defaultUIButton = findViewById(R.id.defaultUIButton);

        saveButton.setBackgroundColor(buttonColorValue);
        viewButton.setBackgroundColor(buttonColorValue);
        defaultUIButton.setBackgroundColor(buttonColorValue);

        // Apply the color to the ActionBar if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(buttonColorValue));
        }
    }


    // Helper method 5: delete saved customizations
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
