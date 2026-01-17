package com.example.skilllsetujava.api.models;


import java.util.List;
import java.util.Map;

/**
 * Skill gap analysis returned from:
 * GET /api/analytics/skill-gaps/{collegeId}
 */
public class SkillGapDTO {

    public List<SkillDeficiency> topDeficiencies;
    public Map<String, IndustryDemand> industryDemand;
    public List<TrainingRecommendation> recommendations;

    public static class SkillDeficiency {
        public String skillName;
        public int studentsLacking;
        public double percentageLacking;
        public String severity;   // Critical, High, Medium, Low
    }

    public static class IndustryDemand {
        public String skillName;
        public int demandCount;
        public int supplyCount;
        public double gapPercentage;
    }

    public static class TrainingRecommendation {
        public String skillName;
        public int priority;
        public String reason;
        public int estimatedStudents;
        public String suggestedDuration; // "4 weeks"
    }
}