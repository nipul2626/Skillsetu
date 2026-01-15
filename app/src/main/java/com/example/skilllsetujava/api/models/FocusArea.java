package com.example.skilllsetujava.api.models;

import java.io.Serializable;
import java.util.List;

public class FocusArea implements Serializable {
    private String area;
    private String priority;
    private int currentLevel;
    private int targetLevel;
    private int estimatedHours;
    private List<String> keyTopics;        // ✅ ADD
    private List<Resource> resources;      // ✅ ADD

    // Getters
    public String getArea() { return area; }
    public String getPriority() { return priority; }
    public int getCurrentLevel() { return currentLevel; }
    public int getTargetLevel() { return targetLevel; }
    public int getEstimatedHours() { return estimatedHours; }
    public List<String> getKeyTopics() { return keyTopics; }  // ✅ ADD
    public List<Resource> getResources() { return resources; }  // ✅ ADD

    // ✅ NEW: Resource inner class
    public static class Resource implements Serializable {
        private String type;
        private String title;
        private String link;
        private String duration;

        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getLink() { return link; }
        public String getDuration() { return duration; }
    }
}