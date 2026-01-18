package com.example.skilllsetujava.api;

import com.example.skilllsetujava.api.models.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface ApiService {

    // ==================== TEST ENDPOINTS ====================

    @GET("api/test/hello")
    Call<Map<String, String>> testConnection();

    @GET("api/test/health")
    Call<Map<String, Object>> healthCheck();

    // ==================== AUTHENTICATION ====================

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // ==================== PROFILE ====================

    @GET("api/profile/{studentId}")
    Call<ProfileResponse> getStudentProfile(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId
    );

    // ==================== INTERVIEWS ====================

    @POST("api/interviews/evaluate")
    Call<InterviewResponse> evaluateInterview(
            @Header("Authorization") String token,
            @Body InterviewRequest request
    );

    @POST("api/analytics/students/list/{collegeId}")
    Call<Map<String, Object>> getStudents(
            @Path("collegeId") long collegeId,
            @Body StudentFilterRequest request
    );

    @POST("api/analytics/refresh/{collegeId}")
    Call<Map<String, String>> refreshAnalytics(
            @Header("Authorization") String token,
            @Path("collegeId") Long collegeId
    );

    @GET("api/interviews/student/{studentId}")
    Call<List<Interview>> getStudentInterviews(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/interviews/{interviewId}")
    Call<Interview> getInterviewDetails(
            @Header("Authorization") String token,
            @Path("interviewId") Long interviewId
    );

    @GET("api/dashboard/student/{studentId}")
    Call<DashboardResponse> getStudentDashboard(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId
    );

    // TPO Dashboard stats
    @GET("api/analytics/dashboard/{collegeId}")
    Call<Map<String, Object>> getTPODashboardStats(
            @Header("Authorization") String token,
            @Path("collegeId") Long collegeId
    );

    // Filtered students (TPO)
    @POST("api/analytics/students/list/{collegeId}")
    Call<Map<String, Object>> getFilteredStudents(
            @Header("Authorization") String token,
            @Path("collegeId") Long collegeId,
            @Body StudentFilterRequest request
    );

    // Trends (TPO)
    @GET("api/analytics/trends/{collegeId}")
    Call<TrendDataDTO> getTrends(
            @Header("Authorization") String token,
            @Path("collegeId") Long collegeId,
            @Query("days") int days
    );

    // Skill gaps (TPO)
    @GET("api/analytics/skill-gaps/{collegeId}")
    Call<SkillGapDTO> getSkillGaps(
            @Header("Authorization") String token,
            @Path("collegeId") Long collegeId
    );
}