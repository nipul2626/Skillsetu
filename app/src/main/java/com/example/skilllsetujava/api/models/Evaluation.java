package com.example.skilllsetujava.api.models;

import java.io.Serializable;
import java.util.List;

public class Evaluation implements Serializable {

    private double overallScore;
    private List<QuestionAnalysis> questionAnalysis;
    private List<ImmediateAction> immediateActions;
    private String coachFeedback;

    public double getOverallScore() {
        return overallScore;
    }

    public List<QuestionAnalysis> getQuestionAnalysis() {
        return questionAnalysis;
    }

    public List<ImmediateAction> getImmediateActions() {
        return immediateActions;
    }

    public String getCoachFeedback() {
        return coachFeedback;
    }
}
