package edu.uiuc.cs427app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.VisibleForTesting;

/**
 * LoginActivity is responsible for handling user login and registration.
 * It provides an interface for users to enter their credentials,
 * authenticates users, and manages the transition
 * to the main application page upon successful authentication.
 *
 * LoginActivity utilizes the AuthenticationService for managing user
 * authentication processes and SharedPreferences to load user-specific
 * UI settings.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private AuthenticationService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Link to activity_login.xml

        // Initialize AuthenticationService
        authService = new AuthenticationService(this);

        // Find views by ID
        userNameEditText = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        Button backButton = findViewById(R.id.back);
        Button registerButton = findViewById(R.id.register);

        // Set up button listeners
        loginButton.setOnClickListener(v -> handleLogin());

        registerButton.setOnClickListener(v -> handleRegister());

        backButton.setOnClickListener(v -> {
            // Go back to the previous screen or finish the activity
            finish();
        });
    }

    /**
     * Handles the login process for the user. This method retrieves the
     * username and password from the input fields, validates them,
     * and attempts to authenticate the user through the AuthenticationService.
     * If successful, it loads user-specific theme settings and navigates
     * to the MainActivity. Otherwise, it displays an error message.
     * Any exceptions encountered during the process are logged.
     */
    private void handleLogin() {
        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            boolean isAuthenticated = authService.login(username, password);
            if (isAuthenticated) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Load user-specific theme settings
                SharedPreferences preferences = getSharedPreferences(CustomizeUIActivity.PREFS_NAME, MODE_PRIVATE);
                String buttonColor = preferences.getString(username + "_" + CustomizeUIActivity.BUTTON_COLOR_KEY, "Default");
                String backgroundColor = preferences.getString(username + "_" + CustomizeUIActivity.BACKGROUND_COLOR_KEY, "Default");

                // Pass these as extras to MainActivity so it can apply the theme
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("buttonColor", buttonColor);
                intent.putExtra("backgroundColor", backgroundColor);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred!" + e, Toast.LENGTH_SHORT).show();
            Log.e("LoginActivity", "An error occurred", e);
        }
    }

    /**
     * Handles the registration process for a new user. This method retrieves
     * the username and password from the input fields, validates them (making
     * sure user doesnt already exist) and
     * attempts to register the user using the AuthenticationService. If
     * registration is successful, it navigates to the MainActivity. Otherwise,
     * it displays an error message. Any exceptions are logged.
     */
    private void handleRegister() {
        // Get username and password from EditText fields
        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Attempt to register the user with AuthenticationService
            boolean isRegistered = authService.register(username, password);
            if (isRegistered) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                // Optionally automatically log them in and go to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed. User may already exist.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred!" + e, Toast.LENGTH_SHORT).show();
            Log.e("LoginActivity", "An error occurred", e);
        }
    }

    @VisibleForTesting
    public void setAuthenticationService(AuthenticationService service) {
        this.authService = service;
    }
}
