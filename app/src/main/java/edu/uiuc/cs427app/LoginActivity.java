package edu.uiuc.cs427app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button backButton;
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
        loginButton = findViewById(R.id.login);
        backButton = findViewById(R.id.back);

        // Set up button listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the previous screen or finish the activity
                finish();
            }
        });
    }

    private void handleLogin() {
        // Get username and password from EditText fields
        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Attempt login with AuthenticationService
            boolean isAuthenticated = authService.login(username, password);
            if (isAuthenticated) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred!" + e, Toast.LENGTH_SHORT).show();
        }

    }
}
