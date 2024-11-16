package edu.uiuc.cs427app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


// weather insights activity, which uses llm service to display weather insights
public class WeatherInsightsActivity extends AppCompatActivity {
    private LinearLayout questionContainer;
    private TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather_insights);

        questionContainer = findViewById(R.id.questionContainer);
        responseTextView = findViewById(R.id.responseTextView);

        // Get weather data from Intent
        String weatherData = getIntent().getStringExtra("weatherData");

        // Initialize LLMService
        String apiKey = "AIzaSyCx_NNsf1hpQFGkH2iHrOoCQwi2COGAgBI"; // TODO: Securely retrieve API key, instead of hardcoding it
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
        questionContainer.removeAllViews();

        for (String question : questions) {
            Button questionButton = new Button(this);
            questionButton.setText(question);
            questionButton.setOnClickListener(v -> fetchAnswer(llmService, question, weatherData));
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
                responseTextView.setText(answer);
            }

            @Override
            public void onError(String errorMessage) {
                responseTextView.setText("Error: " + errorMessage);
            }
        });
    }
}