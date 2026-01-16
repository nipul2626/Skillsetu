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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 📊 STUDENT PERFORMANCE DASHBOARD
 *
 * Features:
 * - Readiness score with circular progress
 * - Interview statistics (total, average score, trend)
 * - Skills breakdown with progress bars
 * - Recent activity timeline
 * - Quick actions (Start Mock, View Roadmap)
 * - Achievements/badges
 * - Weekly progress chart
 */
public class DashboardActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack, btnNotifications;
    private TextView tvStudentName, tvWelcomeMessage;
    private NestedScrollView scrollView;

    // Readiness Section
    private View circularProgressContainer;
    private ProgressBar circularProgressBar;
    private TextView tvReadinessPercent, tvReadinessTier;

    // Stats Cards
    private CardView statsInterviews, statsAvgScore, statsSkills, statsTime;
    private TextView tvInterviewsCount, tvInterviewsTrend;
    private TextView tvAvgScoreValue, tvAvgScoreTrend;
    private TextView tvSkillsCount, tvSkillsLabel;
    private TextView tvTimeValue, tvTimeLabel;

    // Quick Actions
    private MaterialButton btnStartMock, btnViewRoadmap;

    // Score Trend Section
    private CardView scoreTrendCard;
    private TextView tvScoreTrendPercent;
    private LinearLayout scoreTrendChart;

    // Skill Analysis Section
    private LinearLayout skillAnalysisContainer;

    // Achievements Section
    private LinearLayout achievementsContainer;

    // Recent Activity Section
    private LinearLayout recentActivityContainer;
    private TextView tvViewAllActivity;

    // Data (will be fetched from backend/SharedPreferences)
    private String studentName = "Student";
    private int readinessScore = 68;
    private int totalInterviews = 12;
    private double avgScore = 7.4;
    private int skillsMastered = 8;
    private int totalTimeInvested = 45; // hours
    private int weeklyTrend = 12; // percentage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        loadUserData();
        setupListeners();
        animateEntrance();
        populateDashboard();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        scrollView = findViewById(R.id.scrollView);

        // Readiness
        circularProgressContainer = findViewById(R.id.circularProgressContainer);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        tvReadinessPercent = findViewById(R.id.tvReadinessPercent);
        tvReadinessTier = findViewById(R.id.tvReadinessTier);

        // Stats
        statsInterviews = findViewById(R.id.statsInterviews);
        statsAvgScore = findViewById(R.id.statsAvgScore);
        statsSkills = findViewById(R.id.statsSkills);
        statsTime = findViewById(R.id.statsTime);

        tvInterviewsCount = findViewById(R.id.tvInterviewsCount);
        tvInterviewsTrend = findViewById(R.id.tvInterviewsTrend);
        tvAvgScoreValue = findViewById(R.id.tvAvgScoreValue);
        tvAvgScoreTrend = findViewById(R.id.tvAvgScoreTrend);
        tvSkillsCount = findViewById(R.id.tvSkillsCount);
        tvSkillsLabel = findViewById(R.id.tvSkillsLabel);
        tvTimeValue = findViewById(R.id.tvTimeValue);
        tvTimeLabel = findViewById(R.id.tvTimeLabel);

        // Quick Actions
        btnStartMock = findViewById(R.id.btnStartMock);
        btnViewRoadmap = findViewById(R.id.btnViewRoadmap);

        // Score Trend
        scoreTrendCard = findViewById(R.id.scoreTrendCard);
        tvScoreTrendPercent = findViewById(R.id.tvScoreTrendPercent);
        scoreTrendChart = findViewById(R.id.scoreTrendChart);

        // Containers
        skillAnalysisContainer = findViewById(R.id.skillAnalysisContainer);
        achievementsContainer = findViewById(R.id.achievementsContainer);
        recentActivityContainer = findViewById(R.id.recentActivityContainer);
        tvViewAllActivity = findViewById(R.id.tvViewAllActivity);
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        studentName = prefs.getString("student_name", "Student");

        // TODO: Load actual data from backend
        // For now using sample data

        tvStudentName.setText(studentName);
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour < 12) {
            greeting = "Good morning";
        } else if (hour < 17) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }

        tvWelcomeMessage.setText(greeting + ",");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "🔔 No new notifications", Toast.LENGTH_SHORT).show();
        });

        circularProgressContainer.setOnClickListener(v -> {
            // Already on dashboard, show detailed breakdown
            Toast.makeText(this, "📊 Dashboard view", Toast.LENGTH_SHORT).show();
        });

        btnStartMock.setOnClickListener(v -> {
            Intent intent = new Intent(this, activity_homepage.class);
            startActivity(intent);
        });

        btnViewRoadmap.setOnClickListener(v -> {
            // TODO: Open latest roadmap
            Toast.makeText(this, "🗺️ Opening your latest roadmap...", Toast.LENGTH_SHORT).show();
        });

        tvViewAllActivity.setOnClickListener(v -> {
            Toast.makeText(this, "📋 View all activity", Toast.LENGTH_SHORT).show();
        });
    }

    private void animateEntrance() {
        // Hide sections initially
        circularProgressContainer.setAlpha(0f);
        circularProgressContainer.setScaleX(0.3f);
        circularProgressContainer.setScaleY(0.3f);

        // Animate readiness circle
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            circularProgressContainer.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(2f))
                    .start();
        }, 100);

        // Animate stats cards
        CardView[] statsCards = {statsInterviews, statsAvgScore, statsSkills, statsTime};
        for (int i = 0; i < statsCards.length; i++) {
            CardView card = statsCards[i];
            card.setAlpha(0f);
            card.setTranslationY(30f);

            final int index = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                card.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(400)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }, 300 + (index * 100));
        }
    }

    private void populateDashboard() {
        // Animate readiness score
        animateReadinessScore();

        // Populate stats
        populateStats();

        // Populate score trend
        populateScoreTrend();

        // Populate skill analysis
        populateSkillAnalysis();

        // Populate achievements
        populateAchievements();

        // Populate recent activity
        populateRecentActivity();
    }

    private void animateReadinessScore() {
        ValueAnimator animator = ValueAnimator.ofInt(0, readinessScore);
        animator.setDuration(1500);
        animator.setStartDelay(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            circularProgressBar.setProgress(value);
            tvReadinessPercent.setText(value + "%");
        });
        animator.start();

        // Set tier
        String tier = getReadinessTier(readinessScore);
        tvReadinessTier.setText(tier);
    }

    private String getReadinessTier(int score) {
        if (score >= 90) return "🏆 ELITE TIER";
        if (score >= 75) return "⭐ PRO TIER";
        if (score >= 60) return "📈 ADVANCED";
        if (score >= 40) return "📚 INTERMEDIATE";
        return "🌱 BEGINNER";
    }

    private void populateStats() {
        // Interviews
        animateCounter(tvInterviewsCount, 0, totalInterviews, 1000);
        tvInterviewsTrend.setText("+2 this week");
        tvInterviewsTrend.setTextColor(0xFF00E5CC);

        // Average Score
        tvAvgScoreValue.setText(String.format(Locale.US, "%.1f%%", avgScore * 10));
        tvAvgScoreTrend.setText("Top 15%");
        tvAvgScoreTrend.setTextColor(0xFF00E5CC);

        // Skills
        animateCounter(tvSkillsCount, 0, skillsMastered, 1000);
        tvSkillsLabel.setText("Mastered");

        // Time
        animateCounter(tvTimeValue, 0, totalTimeInvested, 1000);
        tvTimeLabel.setText("Total Invested");
    }

    private void animateCounter(TextView textView, int start, int end, long duration) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration);
        animator.setStartDelay(300);
        animator.addUpdateListener(animation -> {
            textView.setText(String.valueOf(animation.getAnimatedValue()));
        });
        animator.start();
    }

    private void populateScoreTrend() {
        tvScoreTrendPercent.setText("+12%");

        // Sample data for last 7 days
        int[] scores = {35, 42, 38, 55, 62, 68, 74};
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        scoreTrendChart.removeAllViews();

        int maxScore = 100;
        for (int i = 0; i < scores.length; i++) {
            LinearLayout dayColumn = new LinearLayout(this);
            dayColumn.setOrientation(LinearLayout.VERTICAL);
            dayColumn.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    dpToPx(120),
                    1f
            );
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dayColumn.setLayoutParams(params);

            // Bar
            View bar = new View(this);
            int barHeight = (int) ((scores[i] / (float) maxScore) * dpToPx(100));
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                    dpToPx(24),
                    barHeight
            );
            bar.setLayoutParams(barParams);

            // Gradient for current day, solid for others
            if (i == scores.length - 1) {
                bar.setBackgroundResource(R.drawable.progress_bar_gradient);
            } else {
                bar.setBackgroundColor(0x4000E5CC);
            }
            bar.setAlpha(0f);

            // Day label
            TextView dayLabel = new TextView(this);
            dayLabel.setText(days[i]);
            dayLabel.setTextColor(0x80FFFFFF);
            dayLabel.setTextSize(11);
            dayLabel.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            labelParams.setMargins(0, dpToPx(8), 0, 0);
            dayLabel.setLayoutParams(labelParams);

            dayColumn.addView(bar);
            dayColumn.addView(dayLabel);
            scoreTrendChart.addView(dayColumn);

            // Animate bar
            final int index = i;
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                bar.animate()
                        .alpha(1f)
                        .setDuration(400)
                        .start();
            }, 800 + (index * 100));
        }
    }

    private void populateSkillAnalysis() {
        String[] skills = {"Data Structures", "System Design", "Soft Skills"};
        int[] percentages = {85, 60, 72};
        int[] colors = {0xFF00E5CC, 0xFFFFA500, 0xFF6B8AFF};

        skillAnalysisContainer.removeAllViews();

        for (int i = 0; i < skills.length; i++) {
            View skillItem = createSkillItem(skills[i], percentages[i], colors[i], i);
            skillAnalysisContainer.addView(skillItem);
        }
    }

    private View createSkillItem(String skillName, int percentage, int color, int index) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        container.setLayoutParams(params);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        headerParams.setMargins(0, 0, 0, dpToPx(8));
        header.setLayoutParams(headerParams);

        TextView skillLabel = new TextView(this);
        skillLabel.setText(skillName);
        skillLabel.setTextColor(0xFFFFFFFF);
        skillLabel.setTextSize(15);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        skillLabel.setLayoutParams(labelParams);
        header.addView(skillLabel);

        TextView percentLabel = new TextView(this);
        percentLabel.setText(percentage + "%");
        percentLabel.setTextColor(color);
        percentLabel.setTextSize(16);
        percentLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(percentLabel);

        container.addView(header);

        // Progress Bar
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(8)
        );
        progressBar.setLayoutParams(pbParams);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        // Create custom drawable
        android.graphics.drawable.GradientDrawable bgDrawable = new android.graphics.drawable.GradientDrawable();
        bgDrawable.setCornerRadius(dpToPx(4));
        bgDrawable.setColor(0x20FFFFFF);

        android.graphics.drawable.GradientDrawable progressDrawable = new android.graphics.drawable.GradientDrawable();
        progressDrawable.setCornerRadius(dpToPx(4));
        progressDrawable.setColor(color);

        android.graphics.drawable.ClipDrawable clip = new android.graphics.drawable.ClipDrawable(
                progressDrawable,
                android.view.Gravity.START,
                android.graphics.drawable.ClipDrawable.HORIZONTAL
        );

        android.graphics.drawable.LayerDrawable layerDrawable = new android.graphics.drawable.LayerDrawable(
                new android.graphics.drawable.Drawable[]{bgDrawable, clip}
        );
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.progress);

        progressBar.setProgressDrawable(layerDrawable);

        container.addView(progressBar);

        // Animate progress
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ValueAnimator animator = ValueAnimator.ofInt(0, percentage);
            animator.setDuration(1000);
            animator.addUpdateListener(animation -> {
                progressBar.setProgress((int) animation.getAnimatedValue());
            });
            animator.start();
        }, 1000 + (index * 200));

        container.setAlpha(0f);
        container.animate()
                .alpha(1f)
                .setDuration(400)
                .setStartDelay(1000 + (index * 150))
                .start();

        return container;
    }

    private void populateAchievements() {
        String[] achievements = {"First 100", "Streak Master", "Algorithm Wiz"};
        int[] icons = {
                android.R.drawable.star_big_on,
                android.R.drawable.ic_menu_recent_history,
                android.R.drawable.ic_menu_info_details
        };
        int[] bgColors = {0xFF2C3E50, 0xFF1A2332, 0xFF2D1B3D};

        achievementsContainer.removeAllViews();

        for (int i = 0; i < achievements.length; i++) {
            CardView achievementCard = createAchievementCard(achievements[i], icons[i], bgColors[i], i);
            achievementsContainer.addView(achievementCard);
        }
    }

    private CardView createAchievementCard(String title, int iconRes, int bgColor, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(100),
                dpToPx(120)
        );
        params.setMargins(0, 0, dpToPx(12), 0);
        card.setLayoutParams(params);
        card.setRadius(dpToPx(16));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(bgColor);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setPadding(dpToPx(12), dpToPx(16), dpToPx(12), dpToPx(16));

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        iconParams.setMargins(0, 0, 0, dpToPx(12));
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setColorFilter(0xFFFFD700);
        layout.addView(icon);

        TextView text = new TextView(this);
        text.setText(title);
        text.setTextColor(0xFFFFFFFF);
        text.setTextSize(12);
        text.setGravity(android.view.Gravity.CENTER);
        text.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(text);

        card.addView(layout);

        card.setAlpha(0f);
        card.setScaleX(0.8f);
        card.setScaleY(0.8f);
        card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setStartDelay(1200 + (index * 100))
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();

        return card;
    }

    private void populateRecentActivity() {
        String[] activities = {"Mock Interview #12", "Aptitude Test"};
        String[] timestamps = {"Today, 10:50 AM", "Yesterday"};
        int[] scores = {82, 65};
        int[] icons = {R.drawable.ic_terminal, R.drawable.ic_psychology};

        recentActivityContainer.removeAllViews();

        for (int i = 0; i < activities.length; i++) {
            View activityItem = createActivityItem(
                    activities[i],
                    timestamps[i],
                    scores[i],
                    icons[i],
                    i
            );
            recentActivityContainer.addView(activityItem);
        }
    }

    private View createActivityItem(String title, String timestamp, int score, int iconRes, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(16));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(0x20FFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Icon
        View iconBg = new View(this);
        LinearLayout.LayoutParams iconBgParams = new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        iconBgParams.setMarginEnd(dpToPx(16));
        iconBg.setLayoutParams(iconBgParams);
        iconBg.setBackgroundResource(R.drawable.icon_background_circle);
        layout.addView(iconBg);

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        iconParams.setMarginEnd(dpToPx(-40));
        iconParams.setMarginStart(-dpToPx(40));
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        icon.setColorFilter(0xFFFFFFFF);
        layout.addView(icon);

        // Content
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        content.setLayoutParams(contentParams);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextColor(0xFFFFFFFF);
        titleText.setTextSize(15);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        content.addView(titleText);

        TextView timeText = new TextView(this);
        timeText.setText(timestamp);
        timeText.setTextColor(0x80FFFFFF);
        timeText.setTextSize(12);
        content.addView(timeText);

        layout.addView(content);

        // Score
        TextView scoreText = new TextView(this);
        scoreText.setText(score + "%");
        scoreText.setTextColor(score >= 70 ? 0xFF00E5CC : 0xFFFFA500);
        scoreText.setTextSize(18);
        scoreText.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(scoreText);

        card.addView(layout);

        card.setAlpha(0f);
        card.setTranslationX(-30f);
        card.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(400)
                .setStartDelay(1400 + (index * 100))
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return card;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}