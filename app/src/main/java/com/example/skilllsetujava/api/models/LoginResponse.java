package com.example.skilllsetujava.api.models;

public class LoginResponse {
    private String token;
    private String email;
    private String role;
    private Long studentId;
    private String message;

    // Getters and setters

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ... add other getters/setters
    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }


}