package com.example.skilllsetujava.api.models;

import java.io.Serializable;
import java.util.List;

public class Roadmap implements Serializable {

    private int readinessScore;
    private int targetScore;
    private String timeToTarget;
    private List<FocusArea> focusAreas;
    private List<WeeklyPlan> weeklyPlan;

    public int getReadinessScore() {
        return readinessScore;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public String getTimeToTarget() {
        return timeToTarget;
    }

    public List<FocusArea> getFocusAreas() {
        return focusAreas;
    }

    public List<WeeklyPlan> getWeeklyPlan() {
        return weeklyPlan;
    }
}
