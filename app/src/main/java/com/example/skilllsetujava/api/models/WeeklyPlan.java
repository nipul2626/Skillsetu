package com.example.skilllsetujava.api.models;

import java.io.Serializable;
import java.util.List;

public class WeeklyPlan implements Serializable {
    private int week;
    private String theme;
    private String studyTime;
    private String practiceTime;           // ✅ ADD
    private List<String> topics;
    private List<PracticeProblem> practiceProblems;  // ✅ ADD
    private List<String> projects;         // ✅ ADD
    private String weekendTask;            // ✅ ADD

    // Getters
    public int getWeek() { return week; }
    public String getTheme() { return theme; }
    public String getStudyTime() { return studyTime; }
    public String getPracticeTime() { return practiceTime; }  // ✅ ADD
    public List<String> getTopics() { return topics; }
    public List<PracticeProblem> getPracticeProblems() { return practiceProblems; }  // ✅ ADD
    public List<String> getProjects() { return projects; }  // ✅ ADD
    public String getWeekendTask() { return weekendTask; }  // ✅ ADD

    // ✅ NEW: PracticeProblem inner class
    public static class PracticeProblem implements Serializable {
        private String problem;
        private String difficulty;
        private String focusArea;

        public String getProblem() { return problem; }
        public String getDifficulty() { return difficulty; }
        public String getFocusArea() { return focusArea; }
    }
}