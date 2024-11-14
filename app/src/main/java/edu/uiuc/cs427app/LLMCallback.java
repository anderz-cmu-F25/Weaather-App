package edu.uiuc.cs427app;

import java.util.List;

public interface LLMCallback {
    void onQuestionsGenerated(List<String> questions);
    void onAnswerGenerated(String answer);
    void onError(String errorMessage);
}
