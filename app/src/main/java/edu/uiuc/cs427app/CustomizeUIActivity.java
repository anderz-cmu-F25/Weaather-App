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

/**
 * CustomizeUIActivity allows users to customize their application's user interface
 * by selecting button and background colors. Users can save their customizations,
 * view saved settings, apply them, and revert to default settings.
 */
public class CustomizeUIActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "UserSettings";
    public static final String BUTTON_COLOR_KEY = "button_color";
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    public static final String UI_PREFIX = "SavedUI_";

    private boolean isExpanded = false;
    private int savedUICount = 0;
    private String currentUsername; // Add this field to track current user

    /**
     * Initializes the activity, retrieves user settings from SharedPreferences,
     * and sets up the user interface components for customizing the UI.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get current username from the intent
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            currentUsername = "default"; // Fallback if no username provided
        }
        getSupportActionBar().setTitle(getString(R.string.app_name_with_user, currentUsername));

        // Load user-specific settings using the username as part of the key
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");
        savedUICount = preferences.getInt(currentUsername + "_savedUICount", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_ui);

        ScrollView uiView = findViewById(R.id.uiView);
        MainActivity.applyBackgroundColor(backgroundColor, uiView);

        RadioGroup buttonColorGroup = findViewById(R.id.radioGroupButtonColor);
        RadioGroup backgroundColorGroup = findViewById(R.id.radioGroupBackgroundColor);
        Button saveButton = findViewById(R.id.saveCustomizationsButton);
        Button viewButton = findViewById(R.id.viewCustomizationsButton);
        Button defaultUIButton = findViewById(R.id.defaultUIButton);
        LinearLayout savedCustomizationsLayout = findViewById(R.id.savedCustomizationsLayout);

        MainActivity.applyButtonColors(this, buttonColor, saveButton, viewButton, defaultUIButton);

        buttonColorGroup.check(R.id.radioButtonBlue);
        backgroundColorGroup.check(R.id.radioButtonWhite);

        // Save the selected UI customization
        saveButton.setOnClickListener(v -> {
            int selectedButtonColorId = buttonColorGroup.getCheckedRadioButtonId();
            String buttonColorSelected = getColorFromId(selectedButtonColorId);

            int selectedBackgroundColorId = backgroundColorGroup.getCheckedRadioButtonId();
            String backgroundColorSelected = getColorFromId(selectedBackgroundColorId);

            savedUICount++;

            String uiDescription = buttonColorSelected + " Button & " + backgroundColorSelected + " Background";

            SharedPreferences.Editor editor = preferences.edit();
            // Save with user-specific keys
            String uiKey = currentUsername + "_" + UI_PREFIX + savedUICount;
            editor.putString(uiKey + "_buttonColor", buttonColorSelected);
            editor.putString(uiKey + "_backgroundColor", backgroundColorSelected);
            editor.putString(uiKey + "_description", uiDescription);
            editor.putInt(currentUsername + "_savedUICount", savedUICount);
            editor.apply();

            Toast.makeText(CustomizeUIActivity.this, "Customization Saved: " + uiDescription, Toast.LENGTH_SHORT).show();

            // Automatically expand the saved customizations view if not already expanded
            if (!isExpanded) {
                savedCustomizationsLayout.setVisibility(View.VISIBLE);
                viewButton.setText("Hide Saved Customizations");
                isExpanded = true;
            }

            // Load the saved customizations to display the new theme in the list
            loadSavedCustomizations(savedCustomizationsLayout);
        });


        viewButton.setOnClickListener(v -> {
            if (isExpanded) {
                savedCustomizationsLayout.setVisibility(View.GONE);
                viewButton.setText("View Saved Customizations");
                isExpanded = false;
            } else {
                savedCustomizationsLayout.setVisibility(View.VISIBLE);
                viewButton.setText("Hide Saved Customizations");
                loadSavedCustomizations(savedCustomizationsLayout);
                isExpanded = true;
            }
        });

        defaultUIButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            //Save default settings with user-specific keys
            editor.putString(currentUsername + "_" + BUTTON_COLOR_KEY, "Blue");
            editor.putString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "White");
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Toast.makeText(CustomizeUIActivity.this, "UI Reverted to Default!", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * For Customizing the UI
     * Converts the selected radio button ID to the corresponding color string.
     *
     * @param colorId The ID of the selected radio button.
     * @return The name of the color corresponding to the radio button, or "Default" if not found.
     */
    private String getColorFromId(int colorId) {
        String resourceName = getResources().getResourceEntryName(colorId);
        String[] parts = resourceName.split("radioButton");
        return parts.length > 1 ? parts[1] : "Default";
    }

    /**
     * Loads saved UI customizations from SharedPreferences and displays them in the layout.
     *
     * @param savedCustomizationsLayout The layout where saved customizations will be displayed.
     */
    private void loadSavedCustomizations(LinearLayout savedCustomizationsLayout) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedUICount = preferences.getInt(currentUsername + "_savedUICount", 0);

        savedCustomizationsLayout.removeAllViews();

        for (int i = 1; i <= savedUICount; i++) {
            String uiKey = currentUsername + "_" + UI_PREFIX + i;
            String buttonColor = preferences.getString(uiKey + "_buttonColor", null);
            String backgroundColor = preferences.getString(uiKey + "_backgroundColor", null);
            String description = preferences.getString(uiKey + "_description", null);

            if (buttonColor != null && backgroundColor != null && description != null) {
                TextView savedUIText = new TextView(this);
                savedUIText.setText(description);

                Button applyButton = new Button(this);
                applyButton.setText("Apply " + description);
                applyButton.setOnClickListener(v1 -> applySavedCustomization(uiKey));

                Button deleteButton = new Button(this);
                deleteButton.setText("Delete " + description);
                deleteButton.setOnClickListener(v1 -> {
                    deleteSavedCustomization(uiKey);
                    savedCustomizationsLayout.removeView(savedUIText);
                    savedCustomizationsLayout.removeView(applyButton);
                    savedCustomizationsLayout.removeView(deleteButton);
                });

                savedCustomizationsLayout.addView(savedUIText);
                savedCustomizationsLayout.addView(applyButton);
                savedCustomizationsLayout.addView(deleteButton);
            }
        }
    }

    /**
     * Applies a previously saved UI customization as the current user's settings.
     *
     * @param uiKey The key for the saved UI customization to apply.
     */
    private void applySavedCustomization(String uiKey) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String buttonColor = preferences.getString(uiKey + "_buttonColor", "Default");
        String backgroundColor = preferences.getString(uiKey + "_backgroundColor", "Default");
        String description = preferences.getString(uiKey + "_description", "Default UI");

        SharedPreferences.Editor editor = preferences.edit();
        //Save the applied customization as the new default for the user
        editor.putString(currentUsername + "_" + BUTTON_COLOR_KEY, buttonColor);
        editor.putString(currentUsername + "_" + BACKGROUND_COLOR_KEY, backgroundColor);
        editor.putInt(currentUsername + "_savedUICount", savedUICount);
        editor.apply();

        //Apply changes to MainActivity as well
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", currentUsername);
        startActivity(intent);

        Toast.makeText(this, "Applied " + description, Toast.LENGTH_SHORT).show();
    }

    /**
     * Deletes a saved UI customization from SharedPreferences.
     *
     * @param uiKey The key for the saved UI customization to delete.
     */
    private void deleteSavedCustomization(String uiKey) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String description = preferences.getString(uiKey + "_description", "Unknown Customization");

        editor.remove(uiKey + "_buttonColor");
        editor.remove(uiKey + "_backgroundColor");
        editor.remove(uiKey + "_description");

        int savedUICount = preferences.getInt(currentUsername + "_savedUICount", 0);
        if (savedUICount > 0) {
            savedUICount--;
            editor.putInt(currentUsername + "_savedUICount", savedUICount);
        }
        editor.apply();

        Toast.makeText(this, "Deleted " + description, Toast.LENGTH_SHORT).show();
    }
}