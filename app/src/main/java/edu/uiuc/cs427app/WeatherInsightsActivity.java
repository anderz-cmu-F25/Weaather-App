package edu.uiuc.cs427app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// weather insights activity, which uses llm service to display weather insights
public class WeatherInsightsActivity extends AppCompatActivity  {
    private LinearLayout questionContainer;
    private TextView responseTextView;
    private String weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather_insights);

        questionContainer = findViewById(R.id.questionContainer);
        responseTextView = findViewById(R.id.responseTextView);

        // Retrieve weather data from Intent
        weatherData = getIntent().getStringExtra("weatherData");

        // Generate questions using LLMService
        fetchQuestions();
    }

    private void fetchQuestions() {
        LLMService llmService = new LLMService();
        llmService.generateQuestions(weatherData, new LLMCallback() {
            @Override
            public void onQuestionsGenerated(List<String> questions) {
                displayQuestions(questions);
            }

            @Override
            public void onAnswerGenerated(String answer) {
                // Not used here
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("WeatherInsightsActivity", "Error: " + errorMessage);
                responseTextView.setText("Failed to load questions. Please try again.");
            }
        });
    }

    private void displayQuestions(List<String> questions) {
        questionContainer.removeAllViews();

        for (String question : questions) {
            Button questionButton = new Button(this);
            questionButton.setText(question);
            questionButton.setOnClickListener(v -> fetchAnswer(question));
            questionContainer.addView(questionButton);
        }
    }

    private void fetchAnswer(String question) {
        LLMService llmService = new LLMService();
        llmService.generateAnswer(question, weatherData, new LLMCallback() {
            @Override
            public void onQuestionsGenerated(List<String> questions) {
                // Not used here
            }

            @Override
            public void onAnswerGenerated(String answer) {
                responseTextView.setText(answer);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("WeatherInsightsActivity", "Error: " + errorMessage);
                responseTextView.setText("Failed to fetch the answer. Please try again.");
            }
        });
    }
}
