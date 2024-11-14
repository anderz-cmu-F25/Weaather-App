package edu.uiuc.cs427app;

import java.util.List;

public class LLMResponse {
    private List<String> generatedTexts;

    public List<String> getGeneratedTexts() {
        return generatedTexts;
    }

    public void setGeneratedTexts(List<String> generatedTexts) {
        this.generatedTexts = generatedTexts;
    }
}
