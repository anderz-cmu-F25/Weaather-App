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


/**
 * WeatherInsightsActivity displays weather insights for a user, allowing interaction
 * with generated questions related to weather data. It applies user-specific theme
 * settings, handles the retrieval of weather data, and integrates with an LLM service
 * to generate and display questions and answers related to the weather data.
 */
public class WeatherInsightsActivity extends AppCompatActivity {
    private LinearLayout questionContainer;
    private TextView responseTextView;
    private static final String PREFS_NAME = "UserSettings";
    private static final String BUTTON_COLOR_KEY = "button_color";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private String currentUsername;
    private String buttonColor; // Store button color at class level

    /**
     * Called when the activity is first created. Initializes UI components,
     * retrieves user-specific settings, applies themes, and fetches weather data
     * passed via an Intent. Also sets up a Language Learning Model (LLM) service
     * to generate and display weather-related questions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *        previously being shut down, this Bundle contains the data it most
     *        recently supplied. Otherwise, it is null.
     */
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

    /**
     * Displays a list of questions as buttons in the UI. When a button is clicked,
     * it triggers fetching an answer to the question using the LLM service.
     *
     * @param questions   The list of questions to be displayed.
     * @param llmService  The LLM service used to generate answers.
     * @param weatherData The weather data to provide context for the questions.
     */
    private void displayQuestions(String[] questions, LLMService llmService, String weatherData) {
        questionContainer.removeAllViews();

        // Convert dp to pixels for margins
        int marginInDp = 16; // You can adjust this value to increase/decrease spacing
        float scale = getResources().getDisplayMetrics().density;
        int marginInPixels = (int) (marginInDp * scale + 0.5f);

        for (String question : questions) {
            Button questionButton = new Button(this);
            questionButton.setText(question);

            // Apply the button color theme
            MainActivity.applyButtonColors(this, buttonColor, questionButton);

            // Create layout parameters with margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, marginInPixels); // Left, Top, Right, Bottom margins
            questionButton.setLayoutParams(params);

            questionButton.setOnClickListener(v -> fetchAnswer(llmService, question, weatherData));
            questionContainer.addView(questionButton);
        }
    }

    /**
     * Fetches an answer for the selected question using the LLM service.
     * The result is displayed in the responseTextView.
     *
     * @param llmService  The LLM service used to generate the answer.
     * @param question    The question selected by the user.
     * @param weatherData The weather data to provide context for generating the answer.
     */
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