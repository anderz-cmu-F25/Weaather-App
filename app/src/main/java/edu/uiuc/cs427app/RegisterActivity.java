package edu.uiuc.cs427app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


public class RegisterActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button backButton;
    private AuthenticationService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Link to activity_register.xml

        // Initialize AuthenticationService
        authService = new AuthenticationService(this);

        // Find views by ID
        userNameEditText = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.login); // 'login' button in XML is actually for registration
        backButton = findViewById(R.id.back);

        // Set up button listeners
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
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
            } else {
                Toast.makeText(this, "Registration failed. User may already exist.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred!" + e, Toast.LENGTH_SHORT).show();
            Log.e("RegisterActivity", "An error occurred", e);
        }

    }
}
