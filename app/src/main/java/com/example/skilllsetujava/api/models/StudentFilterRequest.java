package com.example.skilllsetujava.api.models;

import java.util.List;

/**
 * Request body for:
 * POST /api/analytics/students/list/{collegeId}
 */
public class StudentFilterRequest {

    // Search & filters
    public String searchQuery;
    public List<String> branches;
    public List<String> jobRolePreferences;

    // Readiness range
    public Double minReadinessScore;
    public Double maxReadinessScore;

    // Pagination
    public int page;
    public int size;
}
