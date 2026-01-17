package com.example.skilllsetujava.api.models;

import java.util.List;

public class DashboardResponse {

    public Long studentId;
    public String studentName;

    public int readinessScore;
    public int totalInterviews;
    public double averageScore;
    public int weeklyTrend;

    public List<Skill> skills;
    public List<Activity> recentActivities;

    public static class Skill {
        public String name;
        public int percentage;
    }

    public static class Activity {
        public String title;
        public int score;
        public String timestamp;
    }
}
