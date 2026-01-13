package com.example.skilllsetujava.api.models;

import java.util.List;

public class InterviewRequest {

    private Long studentId;
    private String interviewType;
    private String jobRole;
    private String totalTime;
    private Boolean isRetake;

    // ✅ Structured Q&A history
    private List<QAPair> qaHistory;

    // ---------- Getters & Setters ----------

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Boolean getIsRetake() {
        return isRetake;
    }

    public void setIsRetake(Boolean isRetake) {
        this.isRetake = isRetake;
    }

    public List<QAPair> getQaHistory() {
        return qaHistory;
    }

    public void setQaHistory(List<QAPair> qaHistory) {
        this.qaHistory = qaHistory;
    }

    // =====================================================
    // ✅ INNER CLASS: QAPair (THIS FIXES YOUR ERROR)
    // =====================================================
    public static class QAPair {

        private String question;
        private String answer;

        public QAPair() {
        }

        public QAPair(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        // ✅ REQUIRED by your Activity
        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        // ✅ THIS FIXES "Cannot resolve method setAnswer()"
        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
