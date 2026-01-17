package com.example.skilllsetujava.api.models;

public class LoginResponse {

    private String token;
    private String email;
    private String role;
    private Long studentId;
    private Long collegeId;   // ✅ ADD THIS
    private String fullName;
    private String message;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getCollegeId() { return collegeId; }
    public void setCollegeId(Long collegeId) { this.collegeId = collegeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
