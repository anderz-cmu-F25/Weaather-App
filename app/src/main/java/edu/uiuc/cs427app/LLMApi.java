package edu.uiuc.cs427app;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LLMApi {
    @POST("generate")
    Call<LLMResponse> generateQuestions(
            @Header("Authorization") String apiKey,
            @Body LLMRequest request
    );
}
