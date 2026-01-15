package com.example.skilllsetujava.api.models;

import java.io.Serializable;

/**
 * ✅ FIXED: Backward compatible with both old and new backend formats
 */
public class QuestionAnalysis implements Serializable {

    private int questionNumber;
    private double score;              // Old format
    private double finalScore;         // New format
    private double relevanceScore;
    private double correctnessScore;
    private double depthScore;
    private String whatYouAnswered;
    private String whatWasGood;
    private String whatWasMissing;
    private String idealAnswer;
    private String reasoning;

    // ✅ SMART GETTER: Works with both old and new backend
    public double getScore() {
        // Prefer finalScore if available, fallback to score
        if (finalScore != 0.0) {
            return finalScore;
        }
        return score;
    }

    // Standard getters
    public int getQuestionNumber() { return questionNumber; }
    public double getFinalScore() { return finalScore; }
    public double getRelevanceScore() { return relevanceScore; }
    public double getCorrectnessScore() { return correctnessScore; }
    public double getDepthScore() { return depthScore; }
    public String getWhatYouAnswered() { return whatYouAnswered; }
    public String getWhatWasGood() { return whatWasGood; }
    public String getWhatWasMissing() { return whatWasMissing; }
    public String getIdealAnswer() { return idealAnswer; }
    public String getReasoning() { return reasoning; }
}