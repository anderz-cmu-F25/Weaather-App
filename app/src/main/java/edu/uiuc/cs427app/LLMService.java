package edu.uiuc.cs427app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * LLMService: Handles communication with the Gemini API for generating content dynamically.
 */
public class LLMService {
    private final GenerativeModelFutures model;
    private final Executor executor;

    public LLMService(String apiKey) {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash-8b", apiKey);
        this.model = GenerativeModelFutures.from(gm);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Generates weather-related questions dynamically.
     *
     * @param weatherData Contextual weather data as a string.
     * @param callback    Callback to handle the questions generated or errors.
     */
    public void generateQuestions(String weatherData, LLMCallback callback) {
        String prompt = "Today's weather is " + weatherData + ". Generate two context-specific questions "
                + "that users might ask to help them make decisions about their day.";

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        handleResponse(response, callback, "questions");
    }

    /**
     * Generates a response to a user-selected question dynamically.
     *
     * @param question    The question selected by the user.
     * @param weatherData Contextual weather data as a string.
     * @param callback    Callback to handle the generated response or errors.
     */
    public void generateAnswer(String question, String weatherData, LLMCallback callback) {
        String prompt = "Given today's weather (" + weatherData + "), answer the following question: " + question;

        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        handleResponse(response, callback, "answer");
    }

    /**
     * Handles the response from the Gemini API and sends the results to the provided callback.
     *
     * @param response Future response from the API.
     * @param callback Callback to handle the results or errors.
     * @param type     Type of response ("questions" or "answer").
     */
    private void handleResponse(ListenableFuture<GenerateContentResponse> response, LLMCallback callback, String type) {
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if ("questions".equals(type)) {
                        callback.onQuestionsGenerated(result.getText().split("\n")); // Split questions into list
                    } else {
                        callback.onAnswerGenerated(result.getText());
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(t.getMessage()));
            }
        }, executor);
    }

    /**
     * Callback interface for handling LLM results.
     */
    public interface LLMCallback {
        void onQuestionsGenerated(String[] questions);
        void onAnswerGenerated(String answer);
        void onError(String errorMessage);
    }
}