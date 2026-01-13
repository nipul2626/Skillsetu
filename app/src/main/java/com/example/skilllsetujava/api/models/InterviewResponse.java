package com.example.skilllsetujava.api.models;

public class InterviewResponse {
    private Long interviewId;
    private Double overallScore;
    private String message;
    // Add evaluation and roadmap objects as needed

    public Long getInterviewId() { return interviewId; }
    public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}