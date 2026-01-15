package com.example.skilllsetujava.api.models;

import java.io.Serializable;

public class InterviewResponse implements Serializable {

    private Long interviewId;
    private Double overallScore;
    private Evaluation evaluation;
    private Roadmap roadmap;
    private String message;

    public Long getInterviewId() { return interviewId; }
    public Double getOverallScore() { return overallScore; }
    public Evaluation getEvaluation() { return evaluation; }
    public Roadmap getRoadmap() { return roadmap; }
    public String getMessage() { return message; }
}
