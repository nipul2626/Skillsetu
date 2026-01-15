package com.example.skilllsetujava.api.models;

import java.io.Serializable;

public class QuestionAnalysis implements Serializable {

    private int questionNumber;
    private double score;
    private String whatYouAnswered;
    private String whatWasGood;
    private String whatWasMissing;
    private String idealAnswer;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public double getScore() {
        return score;
    }

    public String getWhatYouAnswered() {
        return whatYouAnswered;
    }

    public String getWhatWasGood() {
        return whatWasGood;
    }

    public String getWhatWasMissing() {
        return whatWasMissing;
    }

    public String getIdealAnswer() {
        return idealAnswer;
    }
}
