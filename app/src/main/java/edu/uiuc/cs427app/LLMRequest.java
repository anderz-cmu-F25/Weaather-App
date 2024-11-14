package edu.uiuc.cs427app;

public class LLMRequest {
    private String prompt;

    public LLMRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
