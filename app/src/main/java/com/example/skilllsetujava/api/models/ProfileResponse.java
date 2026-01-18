package com.example.skilllsetujava.api.models;

public class ProfileResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String college;
    private String branch;
    private String year;
    private Double cgpa;
    private Integer totalInterviews;
    private Integer skillsLearned;
    private Integer averageScore;
    private Double placementReadinessScore;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Integer getTotalInterviews() { return totalInterviews; }
    public void setTotalInterviews(Integer totalInterviews) { this.totalInterviews = totalInterviews; }

    public Integer getSkillsLearned() { return skillsLearned; }
    public void setSkillsLearned(Integer skillsLearned) { this.skillsLearned = skillsLearned; }

    public Integer getAverageScore() { return averageScore; }
    public void setAverageScore(Integer averageScore) { this.averageScore = averageScore; }

    public Double getPlacementReadinessScore() { return placementReadinessScore; }
    public void setPlacementReadinessScore(Double placementReadinessScore) {
        this.placementReadinessScore = placementReadinessScore;
    }
}