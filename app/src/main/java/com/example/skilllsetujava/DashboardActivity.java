package com.example.skilllsetujava;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.example.skilllsetujava.api.RetrofitClient;
import com.example.skilllsetujava.api.models.DashboardResponse;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    // Header
    private ImageView btnBack, btnNotifications;
    private TextView tvStudentName, tvWelcomeMessage;
    private NestedScrollView scrollView;
    private View loadingOverlay;

    // Readiness
    private View circularProgressContainer;
    private ProgressBar circularProgressBar;
    private TextView tvReadinessPercent, tvReadinessTier;

    // Stats
    private CardView statsInterviews, statsAvgScore, statsRoadmaps, statsWeekly;
    private TextView tvInterviewsCount, tvAvgScoreValue, tvRoadmapsCount, tvWeeklyCount;

    // Actions
    private MaterialButton btnStartMock, btnViewRoadmap;

    // Containers
    private LinearLayout skillAnalysisContainer, recentActivityContainer;
    private TextView tvSkillsEmptyState, tvActivityEmptyState;

    // Data
    private DashboardResponse dashboardData;
    private Long studentId;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        loadAuthData();
        setupListeners();
        loadDashboardData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        scrollView = findViewById(R.id.scrollView);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        circularProgressContainer = findViewById(R.id.circularProgressContainer);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        tvReadinessPercent = findViewById(R.id.tvReadinessPercent);
        tvReadinessTier = findViewById(R.id.tvReadinessTier);

        statsInterviews = findViewById(R.id.statsInterviews);
        statsAvgScore = findViewById(R.id.statsAvgScore);
        statsRoadmaps = findViewById(R.id.statsRoadmaps);
        statsWeekly = findViewById(R.id.statsWeekly);

        tvInterviewsCount = findViewById(R.id.tvInterviewsCount);
        tvAvgScoreValue = findViewById(R.id.tvAvgScoreValue);
        tvRoadmapsCount = findViewById(R.id.tvRoadmapsCount);
        tvWeeklyCount = findViewById(R.id.tvWeeklyCount);

        btnStartMock = findViewById(R.id.btnStartMock);
        btnViewRoadmap = findViewById(R.id.btnViewRoadmap);

        skillAnalysisContainer = findViewById(R.id.skillAnalysisContainer);
        tvSkillsEmptyState = findViewById(R.id.tvSkillsEmptyState);

        recentActivityContainer = findViewById(R.id.recentActivityContainer);
        tvActivityEmptyState = findViewById(R.id.tvActivityEmptyState);

        showLoading(true);
    }

    private void loadAuthData() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        studentId = prefs.getLong("student_id", -1);
        String token = prefs.getString("jwt_token", "");

        if (studentId == 0 || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        authToken = "Bearer " + token;
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "No new notifications", Toast.LENGTH_SHORT).show());

        btnStartMock.setOnClickListener(v ->
                startActivity(new Intent(this, activity_homepage.class)));

        btnViewRoadmap.setOnClickListener(this::openRoadmapsList);
    }

    private void loadDashboardData() {
        showLoading(true);

        RetrofitClient.getApiService()
                .getStudentDashboard(authToken, studentId)
                .enqueue(new Callback<DashboardResponse>() {
                    @Override
                    public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                        showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            showError("Failed to load dashboard");
                            return;
                        }
                        dashboardData = response.body();
                        displayDashboardData();
                    }

                    @Override
                    public void onFailure(Call<DashboardResponse> call, Throwable t) {
                        showLoading(false);
                        showError(t.getMessage());
                    }
                });
    }

    private void displayDashboardData() {
        updateHeader();
        animateEntrance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            displayReadinessScore();
            displayStats();
            displaySkills();
            displayRecentActivity();
        }, 300);
    }

    private void updateHeader() {
        tvStudentName.setText(dashboardData.studentName);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "Good Morning" :
                hour < 17 ? "Good Afternoon" : "Good Evening";
        tvWelcomeMessage.setText(greeting + ",");
    }

    private void animateEntrance() {
        circularProgressContainer.setScaleX(0.4f);
        circularProgressContainer.setScaleY(0.4f);
        circularProgressContainer.setAlpha(0f);
        circularProgressContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(600)
                .start();
    }

    private void displayReadinessScore() {
        int score = dashboardData.readinessScore;

        ValueAnimator anim = ValueAnimator.ofInt(0, score);
        anim.setDuration(1200);
        anim.addUpdateListener(a -> {
            int v = (int) a.getAnimatedValue();
            circularProgressBar.setProgress(v);
            tvReadinessPercent.setText(v + "%");
        });
        anim.start();

        tvReadinessTier.setText(getReadinessTier(score));
    }

    private String getReadinessTier(int score) {
        if (score >= 90) return "🏆 ELITE";
        if (score >= 75) return "⭐ ADVANCED";
        if (score >= 60) return "📈 INTERMEDIATE";
        return "🚀 BEGINNER";
    }

    private void displayStats() {
        animateCounter(tvInterviewsCount, dashboardData.totalInterviews);
        tvAvgScoreValue.setText(String.format(Locale.US, "%.1f%%", dashboardData.averageScore * 10));
        animateCounter(tvRoadmapsCount, dashboardData.roadmapCount);
        animateCounter(tvWeeklyCount, dashboardData.weeklyTrend);
    }

    private void animateCounter(TextView tv, int end) {
        ValueAnimator anim = ValueAnimator.ofInt(0, end);
        anim.setDuration(800);
        anim.addUpdateListener(a -> tv.setText(String.valueOf(a.getAnimatedValue())));
        anim.start();
    }

    private void displaySkills() {
        skillAnalysisContainer.removeAllViews();
        if (dashboardData.skills == null || dashboardData.skills.isEmpty()) {
            tvSkillsEmptyState.setVisibility(View.VISIBLE);
            return;
        }
        tvSkillsEmptyState.setVisibility(View.GONE);
        for (DashboardResponse.Skill s : dashboardData.skills) {
            TextView tv = new TextView(this);
            tv.setText(s.name + " - " + s.percentage + "%");
            tv.setTextColor(0xFFFFFFFF);
            skillAnalysisContainer.addView(tv);
        }
    }

    private void displayRecentActivity() {
        recentActivityContainer.removeAllViews();
        if (dashboardData.recentActivities == null || dashboardData.recentActivities.isEmpty()) {
            tvActivityEmptyState.setVisibility(View.VISIBLE);
            return;
        }
        tvActivityEmptyState.setVisibility(View.GONE);
    }

    private void openRoadmapsList(View v) {
        if (dashboardData.roadmapCount == 0) {
            Toast.makeText(this, "No roadmaps yet", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, RoadmapListActivity.class));
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null)
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.e(TAG, msg);
    }
}
