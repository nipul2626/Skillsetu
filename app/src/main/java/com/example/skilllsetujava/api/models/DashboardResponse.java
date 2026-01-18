package com.example.skilllsetujava.api.models;

import java.util.List;

/**
 * Response model for student dashboard data
 * Maps to backend DashboardResponseDTO
 */
public class DashboardResponse {

    public Long studentId;
    public String studentName;

    // Core metrics
    public int readinessScore;        // 0-100
    public int totalInterviews;
    public double averageScore;       // 0-10 scale
    public int weeklyTrend;           // Interviews this week
    public int roadmapCount;          // Number of roadmaps generated

    // Skills breakdown
    public List<Skill> skills;

    // Recent activities (interviews)
    public List<Activity> recentActivities;

    public static class Skill {
        public String name;
        public int percentage;  // 0-100
    }

    public static class Activity {
        public String title;         // "Technical Interview - Software Engineer"
        public int score;            // 0-100
        public String timestamp;     // Formatted date string
        public Long interviewId;     // For navigation
    }
}