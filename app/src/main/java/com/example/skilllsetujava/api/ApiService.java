package com.example.skilllsetujava.api;

import com.example.skilllsetujava.api.models.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {

    // Authentication
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<Void> register(@Body RegisterRequest request);

    // Interview Evaluation
    @POST("api/interviews/evaluate")
    Call<InterviewResponse> evaluateInterview(
            @Header("Authorization") String token,
            @Body InterviewRequest request
    );

    // Get student interviews
    @GET("api/interviews/student/{studentId}")
    Call<List<Interview>> getStudentInterviews(
            @Header("Authorization") String token,
            @Path("studentId") Long studentId,
            @Query("page") int page,
            @Query("size") int size
    );
}