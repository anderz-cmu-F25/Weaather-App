package edu.uiuc.cs427app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.content.SharedPreferences;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;


public class WeatherInsightsActivity extends AppCompatActivity {
    private LinearLayout questionContainer;
    private TextView responseTextView;
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private String currentUsername;
    private String buttonColor; // Store button color at class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUsername = getIntent().getStringExtra("username");
        if (currentUsername == null) {
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            currentUsername = prefs.getString("lastLoggedInUser", "default");
        }
        getSupportActionBar().setTitle(getString(R.string.app_name_with_user, currentUsername));

        setContentView(R.layout.activity_show_weather_insights);

        questionContainer = findViewById(R.id.questionContainer);
        responseTextView = findViewById(R.id.responseTextView);

        // Set responseTextView to be initially invisible
        responseTextView.setVisibility(View.GONE);

        // Load and apply user-specific theme settings
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        buttonColor = preferences.getString(currentUsername + "_" + BUTTON_COLOR_KEY, "Default");
        String backgroundColor = preferences.getString(currentUsername + "_" + BACKGROUND_COLOR_KEY, "Default");

        // Apply background color to main layout
        ConstraintLayout mainLayout = findViewById(R.id.wxLayout);
        MainActivity.applyBackgroundColor(backgroundColor, mainLayout);

        // Ensure child layouts are transparent
        LinearLayout llm = findViewById(R.id.llm);
        llm.setBackgroundColor(Color.TRANSPARENT);
        questionContainer.setBackgroundColor(Color.TRANSPARENT);
        responseTextView.setBackgroundColor(Color.TRANSPARENT);

        // Get weather data from Intent
        String weatherData = getIntent().getStringExtra("weatherData");

        // Initialize LLMService
        String apiKey = "AIzaSyCx_NNsf1hpQFGkH2iHrOoCQwi2COGAgBI"; // TODO: Securely retrieve API key
        LLMService llmService = new LLMService(apiKey);

        // Fetch questions
        llmService.generateQuestions(weatherData, new LLMService.LLMCallback() {
            @Override
            public void onQuestionsGenerated(String[] questions) {
                displayQuestions(questions, llmService, weatherData);
            }

            @Override
            public void onAnswerGenerated(String answer) {
                // Not used here
            }

            @Override
            public void onError(String errorMessage) {
                responseTextView.setText("Error: " + errorMessage);
            }
        });
    }

    private void displayQuestions(String[] questions, LLMService llmService, String weatherData) {
        // Clear any existing views to prevent duplicate questions
        questionContainer.removeAllViews();

        // Convert dp to pixels for margins
        int marginInDp = 16;
        float scale = getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);

        // Filter and display questions
        for (String question : questions) {
            if (question == null || question.trim().isEmpty()) continue; // Skip null or empty questions

            // Create a new Button for each question
            Button questionButton = new Button(this);
            questionButton.setText(question);
            questionButton.setAllCaps(false); // Ensure text is not all caps for readability

            // Apply the button color theme
            MainActivity.applyButtonColors(this, buttonColor, questionButton);

            // Create layout parameters with controlled margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, marginInPixels); // Left, Top, Right, Bottom margins
            questionButton.setLayoutParams(params);

            // Apply minimal padding and height to the button
            questionButton.setPadding(0, 16, 0, 16); // Add some padding for better touch area
            questionButton.setMinHeight(0);
            questionButton.setMinimumHeight(0);

            // Set click listener to fetch an answer when the button is clicked
            questionButton.setOnClickListener(v -> fetchAnswer(llmService, question, weatherData));

            // Add the button to the question container
            questionContainer.addView(questionButton);
        }
    }

    private void fetchAnswer(LLMService llmService, String question, String weatherData) {
        llmService.generateAnswer(question, weatherData, new LLMService.LLMCallback() {
            @Override
            public void onQuestionsGenerated(String[] questions) {
                // Not used here
            }

            @Override
            public void onAnswerGenerated(String answer) {
                responseTextView.setVisibility(responseTextView.VISIBLE);
                responseTextView.setText(answer);
            }

            @Override
            public void onError(String errorMessage) {
                responseTextView.setVisibility(responseTextView.VISIBLE);
                responseTextView.setText("Error: " + errorMessage);
            }
        });
    }
}