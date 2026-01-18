package com.example.skilllsetujava.api.models;

public class LoginRequest {

    private String email;     // email OR username
    private String password;
    private String role;      // optional

    // ✅ NEW CONSTRUCTOR (RECOMMENDED)
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Existing constructor (keep it if needed elsewhere)
    public LoginRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
