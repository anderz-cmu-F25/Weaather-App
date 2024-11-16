package edu.uiuc.cs427app;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Service to interact with the Gemini LLM API for generating weather-related questions and answers.
 */
public class LLMService {
    private static final String BASE_URL = "https://api.google.dev/gemini-api/";
    private static final String API_KEY = "AIzaSyCx_NNsf1hpQFGkH2iHrOoCQwi2COGAgBI";

    private final LLMApi llmApi;

    public LLMService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        llmApi = retrofit.create(LLMApi.class);
    }

    /**
     * Generates context-specific questions based on the provided weather data.
     *
     * @param weatherData The prompt to be feeded into the Gemini LLM API.
     * @param callback The callback to handle generated questions.
     */
    public void generateQuestions(String weatherData, LLMCallback callback) {
        String prompt = "Today's weather is " + weatherData +
                ". Generate two questions related to daily decisions based on the weather.";
        Call<LLMResponse> call = llmApi.generateQuestions("Bearer " + API_KEY, new LLMRequest(prompt));

        call.enqueue(new Callback<LLMResponse>() {
            @Override
            public void onResponse(Call<LLMResponse> call, Response<LLMResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onQuestionsGenerated(response.body().getGeneratedTexts());
                } else {
                    Log.e("LLMService", "Failed to generate questions: " + response.code() + " " + response.message());
                    callback.onError("Error generating questions from LLM API.");
                }
            }

            @Override
            public void onFailure(Call<LLMResponse> call, Throwable t) {
                Log.e("LLMService", "LLM API call failed: " + t.getMessage());
                callback.onError("Failed to connect to LLM API.");
            }
        });
    }

    /**
     * Generates an answer for a user-selected question based on the provided weather data.
     *
     * @param question The question selected by the user.
     * @param weatherData The weather data to provide context.
     * @param callback The callback to handle the generated answer.
     */
    public void generateAnswer(String question, String weatherData, final LLMCallback callback) {
        String prompt = "Given today's weather (" + weatherData + "), answer the following question: " + question;

        Call<LLMResponse> call = llmApi.generateQuestions("Bearer " + API_KEY, new LLMRequest(prompt));

        call.enqueue(new Callback<LLMResponse>() {
            @Override
            public void onResponse(Call<LLMResponse> call, Response<LLMResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onAnswerGenerated(response.body().getGeneratedTexts().get(0));
                } else {
                    Log.e("LLMService", "Failed to generate answer: " + response.code() + " " + response.message());
                    callback.onError("Error generating answer from LLM API.");
                }
            }

            @Override
            public void onFailure(Call<LLMResponse> call, Throwable t) {
                Log.e("LLMService", "LLM API call failed: " + t.getMessage());
                callback.onError("Failed to connect to LLM API.");
            }
        });
    }
}
