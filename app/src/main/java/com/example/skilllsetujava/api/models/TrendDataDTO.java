package com.example.skilllsetujava.api.models;


import java.util.List;

/**
 * Trend data returned from:
 * GET /api/analytics/trends/{collegeId}
 */
public class TrendDataDTO {

    public List<MonthlyTrend> monthlyTrends;
    public ComparisonData batchComparison;

    public static class MonthlyTrend {
        public String month;          // "Jan 2025"
        public int totalInterviews;
        public double averageScore;
        public int studentsImproved;
        public double avgReadinessScore;
    }

    public static class ComparisonData {
        public String currentBatch;
        public String previousBatch;
        public double currentAvgScore;
        public double previousAvgScore;
        public double improvement;
    }
}