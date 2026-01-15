package com.example.skilllsetujava.api.models;

import java.io.Serializable;

public class FocusArea implements Serializable {
    private String area;
    private String priority;
    private int currentLevel;
    private int targetLevel;
    private int estimatedHours;

    public String getArea() { return area; }
    public String getPriority() { return priority; }
    public int getCurrentLevel() { return currentLevel; }
    public int getTargetLevel() { return targetLevel; }
    public int getEstimatedHours() { return estimatedHours; }
}
