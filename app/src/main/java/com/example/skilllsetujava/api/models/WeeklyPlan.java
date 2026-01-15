package com.example.skilllsetujava.api.models;

import java.io.Serializable;

public class WeeklyPlan implements Serializable {
    private int week;
    private String theme;
    private String studyTime;
    private java.util.List<String> topics;

    public int getWeek() { return week; }
    public String getTheme() { return theme; }
    public String getStudyTime() { return studyTime; }
    public java.util.List<String> getTopics() { return topics; }
}
