package com.example.skilllsetujava.api.models;

import java.util.List;

public class InterviewResponse {

    // ✅ REQUIRED by Activity
    private Long interviewId;
    private Integer overallScore;

    // ✅ Detailed evaluation
    private String feedback;
    private List<String> strengths;
    private List<String> weaknesses;
    private String suggestedRoadmap;

    // ---------------- GETTERS & SETTERS ----------------

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getSuggestedRoadmap() {
        return suggestedRoadmap;
    }

    public void setSuggestedRoadmap(String suggestedRoadmap) {
        this.suggestedRoadmap = suggestedRoadmap;
    }
}
