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
    Call<Void> register(@Body RegisterRequest request);

    // ==================== INTERVIEWS ====================

    @POST("api/interviews/evaluate")
    Call<InterviewResponse> evaluateInterview(
            @Header("Authorization") String token,
            @Body InterviewRequest request
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
}