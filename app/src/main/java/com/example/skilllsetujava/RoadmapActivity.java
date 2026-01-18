package com.example.skilllsetujava;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * âœ¨ ENHANCED Training Roadmap
 *
 * Improvements:
 * - Larger, readable text
 * - Cyan color scheme
 * - Smooth animations
 * - Better visual hierarchy
 */
public class RoadmapActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvRoadmapTitle, tvJobRole;
    private NestedScrollView scrollView;

    // Progress Header
    private ProgressBar pbOverallProgress;
    private TextView tvProgressPercent, tvTimeEstimate, tvReadinessLevel;
    private CardView readinessCard;

    // Main content containers
    private LinearLayout focusAreasContainer;
    private LinearLayout weeklyPlanContainer;
    private LinearLayout milestonesContainer;

    // Data
    private GroqAPIService.TrainingPlan trainingPlan;
    private String jobRole;
    private String interviewType;
    private double overallScore;

    private SharedPreferences prefs;
    private String roadmapId;
    private int completedTasksCount = 0;
    private int totalTasksCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap);

        trainingPlan = (GroqAPIService.TrainingPlan) getIntent().getSerializableExtra("training_plan");
        jobRole = getIntent().getStringExtra("job_role");
        interviewType = getIntent().getStringExtra("interview_type");
        overallScore = getIntent().getDoubleExtra("overall_score", 0);

        prefs = getSharedPreferences("roadmap_progress", MODE_PRIVATE);
        roadmapId = jobRole + "_" + interviewType + "_" + System.currentTimeMillis();

        initViews();
        loadProgress();
        displayRoadmapWithAnimations();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvRoadmapTitle = findViewById(R.id.tvRoadmapTitle);
        tvJobRole = findViewById(R.id.tvJobRole);
        scrollView = findViewById(R.id.scrollView);

        readinessCard = findViewById(R.id.readinessCard);
        pbOverallProgress = findViewById(R.id.pbOverallProgress);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvTimeEstimate = findViewById(R.id.tvTimeEstimate);
        tvReadinessLevel = findViewById(R.id.tvReadinessLevel);

        focusAreasContainer = findViewById(R.id.focusAreasContainer);
        weeklyPlanContainer = findViewById(R.id.weeklyPlanContainer);
        milestonesContainer = findViewById(R.id.milestonesContainer);

        tvRoadmapTitle.setText("Your 30-Day Training Plan");
        tvJobRole.setText(jobRole + " \uD83D\uDD39 " + interviewType);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProgress() {
        String savedProgress = prefs.getString(roadmapId, "");
        if (!savedProgress.isEmpty()) {
            try {
                org.json.JSONObject json = new org.json.JSONObject(savedProgress);
                completedTasksCount = json.getInt("completedTasks");
                totalTasksCount = json.getInt("totalTasks");
            } catch (Exception e) {
                Log.e("Roadmap", "Failed to load progress", e);
            }
        }
    }

    private void saveProgress() {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("completedTasks", completedTasksCount);
            json.put("totalTasks", totalTasksCount);
            json.put("lastUpdated", System.currentTimeMillis());

            prefs.edit().putString(roadmapId, json.toString()).apply();
        } catch (Exception e) {
            Log.e("Roadmap", "Save failed", e);
        }
    }

    /**
     * âœ¨ Display roadmap with animations
     */
    private void displayRoadmapWithAnimations() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            readinessCard.setAlpha(0f);
            readinessCard.animate().alpha(1f).setDuration(600).start();
            displayProgressHeader();
        }, 200);

        new Handler(Looper.getMainLooper()).postDelayed(() -> displayFocusAreas(), 800);
        new Handler(Looper.getMainLooper()).postDelayed(() -> displayWeeklyPlan(), 1400);
        new Handler(Looper.getMainLooper()).postDelayed(() -> displayMilestones(), 2000);
    }

    private void displayProgressHeader() {
        tvReadinessLevel.setText(getReadinessLabel(trainingPlan.readinessScore));
        tvTimeEstimate.setText(trainingPlan.timeToTarget);

        int currentProgress = totalTasksCount > 0 ?
                (completedTasksCount * 100) / totalTasksCount : 0;

        ValueAnimator animator = ValueAnimator.ofInt(0, currentProgress);
        animator.setDuration(1500);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            pbOverallProgress.setProgress(value);
            tvProgressPercent.setText(value + "% Complete");
        });
        animator.start();
    }

    private void displayFocusAreas() {
        if (trainingPlan.focusAreas == null || trainingPlan.focusAreas.isEmpty()) return;

        TextView sectionTitle = createSectionTitle("\uD83C\uDFAF Key Focus Areas");
        focusAreasContainer.addView(sectionTitle);

        for (int i = 0; i < trainingPlan.focusAreas.size(); i++) {
            GroqAPIService.FocusArea area = trainingPlan.focusAreas.get(i);
            CardView card = createEnhancedFocusAreaCard(area, i);
            focusAreasContainer.addView(card);
        }
    }

    /**
     * âœ¨ ENHANCED: Better focus area card with cyan theme
     */
    private CardView createEnhancedFocusAreaCard(GroqAPIService.FocusArea area, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(20));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(0xFF1A1F35); // Dark blue background

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvArea = new TextView(this);
        tvArea.setText(area.area);
        tvArea.setTextColor(0xFFFFFFFF);
        tvArea.setTextSize(19); // âœ¨ Larger text
        tvArea.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams areaParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        tvArea.setLayoutParams(areaParams);
        header.addView(tvArea);

        TextView tvPriority = new TextView(this);
        tvPriority.setText(area.priority);
        tvPriority.setTextSize(14); // âœ¨ Larger text
        tvPriority.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPriority.setPadding(dpToPx(14), dpToPx(6), dpToPx(14), dpToPx(6));
        tvPriority.setBackgroundResource(R.drawable.badge_background);

        // âœ¨ Cyan theme
        int priorityColor = area.priority.equalsIgnoreCase("High") ? 0xFFFF6B6B :
                area.priority.equalsIgnoreCase("Medium") ? 0xFFFFA500 : 0xFF00E5CC;
        tvPriority.setTextColor(priorityColor);
        header.addView(tvPriority);

        layout.addView(header);

        // Progress info
        TextView tvProgress = new TextView(this);
        tvProgress.setText(String.format(Locale.US,
                "Current: %d/10  →  Target: %d/10  \uD83D\uDD39  %d hours estimated",
                area.currentLevel, area.targetLevel, area.estimatedHours));
        tvProgress.setTextColor(0xEEFFFFFF);
        tvProgress.setTextSize(15); // âœ¨ Larger text
        LinearLayout.LayoutParams pParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        pParams.setMargins(0, dpToPx(10), 0, dpToPx(16));
        tvProgress.setLayoutParams(pParams);
        layout.addView(tvProgress);

        // Progress bar
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(10)
        );
        pbParams.setMargins(0, 0, 0, dpToPx(20));
        progressBar.setLayoutParams(pbParams);
        progressBar.setMax(100);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient));

        int progress = (area.currentLevel * 100) / 10;
        ValueAnimator animator = ValueAnimator.ofInt(0, progress);
        animator.setDuration(1000);
        animator.setStartDelay(index * 200L);
        animator.addUpdateListener(animation -> progressBar.setProgress((int) animation.getAnimatedValue()));
        animator.start();

        layout.addView(progressBar);

        // Key topics
        if (area.keyTopics != null && !area.keyTopics.isEmpty()) {
            TextView tvTopicsLabel = new TextView(this);
            tvTopicsLabel.setText("\uD83D\uDCDA Key Topics:");
            tvTopicsLabel.setTextColor(0xFF00E5CC); // âœ¨ Cyan
            tvTopicsLabel.setTextSize(16); // âœ¨ Larger text
            tvTopicsLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(tvTopicsLabel);

            for (String topic : area.keyTopics) {
                TextView tvTopic = new TextView(this);
                tvTopic.setText("\uD83D\uDD39 " + topic);
                tvTopic.setTextColor(0xFFFFFFFF);
                tvTopic.setTextSize(15); // âœ¨ Larger text
                tvTopic.setLineSpacing(3, 1f);
                LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tParams.setMargins(dpToPx(16), dpToPx(6), 0, dpToPx(6));
                tvTopic.setLayoutParams(tParams);
                layout.addView(tvTopic);
            }
        }

        // Resources
        if (area.resources != null && !area.resources.isEmpty()) {
            TextView tvResourcesLabel = new TextView(this);
            tvResourcesLabel.setText("\uD83D\uDD17 Resources:");
            tvResourcesLabel.setTextColor(0xFF00E5CC); // âœ¨ Cyan
            tvResourcesLabel.setTextSize(16); // âœ¨ Larger text
            tvResourcesLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams rlParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rlParams.setMargins(0, dpToPx(20), 0, dpToPx(10));
            tvResourcesLabel.setLayoutParams(rlParams);
            layout.addView(tvResourcesLabel);

            for (GroqAPIService.Resource resource : area.resources) {
                CardView resourceCard = createEnhancedResourceCard(resource);
                layout.addView(resourceCard);
            }
        }

        card.addView(layout);

        // âœ¨ Slide-up animation
        card.setAlpha(0f);
        card.setTranslationY(30f);
        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(index * 150L)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return card;
    }

    /**
     * âœ¨ ENHANCED: Better resource card with cyan theme
     */
    private CardView createEnhancedResourceCard(GroqAPIService.Resource resource) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(10));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(14));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(0x30FFFFFF);

        card.setClickable(true);
        card.setFocusable(true);
        card.setForeground(getResources().getDrawable(android.R.drawable.list_selector_background));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Type badge + Duration
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvType = new TextView(this);
        tvType.setText(getResourceIcon(resource.type) + " " + resource.type);
        tvType.setTextSize(13); // âœ¨ Larger text
        tvType.setTextColor(0xFF00E5CC); // âœ¨ Cyan
        tvType.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams typeParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        tvType.setLayoutParams(typeParams);
        header.addView(tvType);

        TextView tvDuration = new TextView(this);
        tvDuration.setText(resource.duration);
        tvDuration.setTextSize(13); // âœ¨ Larger text
        tvDuration.setTextColor(0xCCFFFFFF);
        header.addView(tvDuration);

        layout.addView(header);

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(resource.title);
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(16); // âœ¨ Larger text
        tvTitle.setLineSpacing(2, 1f);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, dpToPx(8), 0, dpToPx(6));
        tvTitle.setLayoutParams(titleParams);
        layout.addView(tvTitle);

        // Link
        TextView tvLink = new TextView(this);
        tvLink.setText("\uD83D\uDD17 " + resource.link);
        tvLink.setTextColor(0xFF00E5CC); // âœ¨ Cyan
        tvLink.setTextSize(14); // âœ¨ Larger text
        layout.addView(tvLink);

        card.addView(layout);

        card.setOnClickListener(v -> openResourceLink(resource.link));

        return card;
    }

    private void openResourceLink(String link) {
        try {
            android.content.Intent intent;

            if (link.startsWith("http://") || link.startsWith("https://")) {
                intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse(link));
            } else if (link.toLowerCase().startsWith("search:")) {
                String query = link.substring(7).trim();
                intent = new android.content.Intent(android.content.Intent.ACTION_WEB_SEARCH);
                intent.putExtra(android.app.SearchManager.QUERY, query);
            } else {
                intent = new android.content.Intent(android.content.Intent.ACTION_WEB_SEARCH);
                intent.putExtra(android.app.SearchManager.QUERY, link);
            }

            startActivity(intent);
            android.widget.Toast.makeText(this, "Opening: " + link, android.widget.Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Roadmap", "Failed to open link: " + link, e);
            android.widget.Toast.makeText(this, "Could not open link", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private String getResourceIcon(String type) {
        switch (type.toLowerCase()) {
            case "documentation": return "\uD83D\uDCC4";
            case "tutorial": return "\uD83C\uDF93";
            case "course": return "\uD83D\uDCF9";
            case "book": return "\uD83D\uDCD6";
            case "practice": return "\uD83D\uDCAA";
            default: return "\uD83D\uDCCC";
        }
    }

    private void displayWeeklyPlan() {
        if (trainingPlan.weeklyPlan == null || trainingPlan.weeklyPlan.isEmpty()) {
            Log.e("Roadmap", "âŒ weeklyPlan is null or empty!");
            return;
        }

        Log.d("Roadmap", "✨ Found " + trainingPlan.weeklyPlan.size() + " weeks in plan");

        TextView sectionTitle = createSectionTitle("\uD83D\uDCC5 4-Week Structured Plan");
        weeklyPlanContainer.addView(sectionTitle);

        for (int i = 0; i < trainingPlan.weeklyPlan.size(); i++) {
            GroqAPIService.WeeklyPlan week = trainingPlan.weeklyPlan.get(i);
            Log.d("Roadmap", "Adding Week " + week.week + ": " + week.theme);
            CardView weekCard = createEnhancedWeekCard(week, i);
            weeklyPlanContainer.addView(weekCard);
        }

        // âœ… Button container
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(0, dpToPx(28), 0, dpToPx(24));
        buttonContainer.setLayoutParams(containerParams);

        // âœ… Retest button
        MaterialButton btnRetest = new MaterialButton(this);
        LinearLayout.LayoutParams retestParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        retestParams.setMargins(0, 0, 0, dpToPx(12));
        btnRetest.setLayoutParams(retestParams);
        btnRetest.setText("\uD83D\uDD04 Ready to Retest? Take Interview Again");
        btnRetest.setTextSize(16);
        btnRetest.setTextColor(0xFFFFFFFF);

        // âœ… NO STROKE - Use solid background
        btnRetest.setStrokeWidth(0);
        btnRetest.setStrokeColor(null);
        btnRetest.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF00E5CC) // Cyan
        );

        btnRetest.setCornerRadius(dpToPx(28));
        btnRetest.setPadding(0, dpToPx(16), 0, dpToPx(16));
        btnRetest.setElevation(dpToPx(6));

        btnRetest.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, InterviewActivity.class);
            intent.putExtra(InterviewActivity.EXTRA_INTERVIEW_TYPE, interviewType);
            intent.putExtra(InterviewActivity.EXTRA_JOB_ROLE, jobRole);
            intent.putExtra(InterviewActivity.EXTRA_IS_RETAKE, true);
            startActivity(intent);
            finish();
        });

        buttonContainer.addView(btnRetest);

        // âœ… NEW: Home button
        MaterialButton btnHome = new MaterialButton(this);
        LinearLayout.LayoutParams homeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnHome.setLayoutParams(homeParams);
        btnHome.setText("\uD83C\uDFE0  Back to Home");
        btnHome.setTextSize(16);
        btnHome.setTextColor(0xFFFFFFFF);

        // âœ… NO STROKE - Outlined style with semi-transparent background
        btnHome.setStrokeWidth(0);
        btnHome.setStrokeColor(null);
        btnHome.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0x40FFFFFF) // Semi-transparent
        );

        btnHome.setCornerRadius(dpToPx(28));
        btnHome.setPadding(0, dpToPx(16), 0, dpToPx(16));
        btnHome.setElevation(0);

        btnHome.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, activity_homepage.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        buttonContainer.addView(btnHome);
        weeklyPlanContainer.addView(buttonContainer);
    }

    /**
     * âœ¨ ENHANCED: Better week card with cyan theme
     */
    private CardView createEnhancedWeekCard(GroqAPIService.WeeklyPlan week, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(20));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(0xFF1A1F35); // Dark blue background

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));

        // Week header
        TextView tvWeek = new TextView(this);
        tvWeek.setText("Week " + week.week + ": " + week.theme);
        tvWeek.setTextColor(0xFFFFFFFF);
        tvWeek.setTextSize(20); // âœ¨ Larger text
        tvWeek.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvWeek);

        // Daily schedule
        TextView tvSchedule = new TextView(this);
        tvSchedule.setText("⏰ Study: " + week.studyTime + " \uD83D\uDD39 Practice: " + week.practiceTime);
        tvSchedule.setTextColor(0xFF00E5CC); // âœ¨ Cyan
        tvSchedule.setTextSize(16); // âœ¨ Larger text
        LinearLayout.LayoutParams schedParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        schedParams.setMargins(0, dpToPx(10), 0, dpToPx(20));
        tvSchedule.setLayoutParams(schedParams);
        layout.addView(tvSchedule);

        // Topics with checkboxes
        TextView tvTopicsLabel = new TextView(this);
        tvTopicsLabel.setText("\uD83D\uDCDA Topics to Cover:");
        tvTopicsLabel.setTextColor(0xFFFFFFFF);
        tvTopicsLabel.setTextSize(17); // âœ¨ Larger text
        tvTopicsLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvTopicsLabel);

        for (String topic : week.topics) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(topic);
            checkbox.setTextColor(0xFFFFFFFF);
            checkbox.setTextSize(16); // âœ¨ Larger text
            // âœ¨ Cyan checkbox color
            checkbox.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFF00E5CC));

            String taskId = "week_" + week.week + "_topic_" + topic.hashCode();
            checkbox.setChecked(isTaskCompleted(taskId));

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    completedTasksCount++;
                    markTaskCompleted(taskId);
                } else {
                    completedTasksCount--;
                    unmarkTaskCompleted(taskId);
                }
                saveProgress();
                updateProgressHeader();
            });

            totalTasksCount++;

            LinearLayout.LayoutParams cbParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cbParams.setMargins(0, dpToPx(10), 0, 0);
            checkbox.setLayoutParams(cbParams);
            layout.addView(checkbox);
        }

        // Practice problems
        if (week.practiceProblems != null && !week.practiceProblems.isEmpty()) {
            TextView tvProblemsLabel = new TextView(this);
            tvProblemsLabel.setText("\uD83D\uDCAA Practice Problems:");
            tvProblemsLabel.setTextColor(0xFF00E5CC); // âœ¨ Cyan
            tvProblemsLabel.setTextSize(17); // âœ¨ Larger text
            tvProblemsLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams plParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            plParams.setMargins(0, dpToPx(20), 0, dpToPx(12));
            tvProblemsLabel.setLayoutParams(plParams);
            layout.addView(tvProblemsLabel);

            for (GroqAPIService.PracticeProblem problem : week.practiceProblems) {
                CardView problemCard = createEnhancedProblemCard(problem);
                layout.addView(problemCard);
            }
        }

        // Projects
        if (week.projects != null && !week.projects.isEmpty()) {
            TextView tvProjectsLabel = new TextView(this);
            tvProjectsLabel.setText("\uD83D\uDEE0\uFE0F Projects:");
            tvProjectsLabel.setTextColor(0xFF00E5CC); // âœ¨ Cyan
            tvProjectsLabel.setTextSize(17); // âœ¨ Larger text
            tvProjectsLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams projParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            projParams.setMargins(0, dpToPx(20), 0, dpToPx(10));
            tvProjectsLabel.setLayoutParams(projParams);
            layout.addView(tvProjectsLabel);

            for (String project : week.projects) {
                TextView tvProject = new TextView(this);
                tvProject.setText("\uD83D\uDD39" + project);
                tvProject.setTextColor(0xFFFFFFFF);
                tvProject.setTextSize(16); // âœ¨ Larger text
                tvProject.setLineSpacing(3, 1f);
                LinearLayout.LayoutParams pParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                pParams.setMargins(dpToPx(16), dpToPx(6), 0, dpToPx(6));
                tvProject.setLayoutParams(pParams);
                layout.addView(tvProject);
            }
        }

        // Weekend task
        TextView tvWeekendLabel = new TextView(this);
        tvWeekendLabel.setText("\uD83C\uDFAF Weekend Challenge:");
        tvWeekendLabel.setTextColor(0xFF00E5CC); // âœ¨ Cyan
        tvWeekendLabel.setTextSize(17); // âœ¨ Larger text
        tvWeekendLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams wlParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        wlParams.setMargins(0, dpToPx(20), 0, dpToPx(10));
        tvWeekendLabel.setLayoutParams(wlParams);
        layout.addView(tvWeekendLabel);

        TextView tvWeekend = new TextView(this);
        tvWeekend.setText(week.weekendTask);
        tvWeekend.setTextColor(0xFFFFFFFF);
        tvWeekend.setTextSize(16); // âœ¨ Larger text
        tvWeekend.setLineSpacing(3, 1f);
        layout.addView(tvWeekend);

        card.addView(layout);

        // âœ¨ Slide-in animation
        card.setAlpha(0f);
        card.setTranslationX(-30f);
        card.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(500)
                .setStartDelay(index * 150L)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return card;
    }

    /**
     * âœ¨ ENHANCED: Better problem card
     */
    private CardView createEnhancedProblemCard(GroqAPIService.PracticeProblem problem) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(10));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(14));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(0x30FFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Difficulty badge
        TextView tvDifficulty = new TextView(this);
        tvDifficulty.setText(problem.difficulty);
        tvDifficulty.setTextSize(13); // âœ¨ Larger text
        tvDifficulty.setTextColor(getDifficultyColor(problem.difficulty));
        tvDifficulty.setTypeface(null, android.graphics.Typeface.BOLD);
        tvDifficulty.setPadding(dpToPx(10), dpToPx(6), dpToPx(10), dpToPx(6));
        tvDifficulty.setBackgroundResource(R.drawable.badge_background);
        LinearLayout.LayoutParams diffParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        diffParams.setMarginEnd(dpToPx(16));
        tvDifficulty.setLayoutParams(diffParams);
        layout.addView(tvDifficulty);

        // Problem content
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        content.setLayoutParams(contentParams);

        TextView tvProblem = new TextView(this);
        tvProblem.setText(problem.problem);
        tvProblem.setTextColor(0xFFFFFFFF);
        tvProblem.setTextSize(16); // âœ¨ Larger text
        tvProblem.setLineSpacing(2, 1f);
        content.addView(tvProblem);

        TextView tvFocus = new TextView(this);
        tvFocus.setText("Focus: " + problem.focusArea);
        tvFocus.setTextColor(0xCCFFFFFF);
        tvFocus.setTextSize(14); // âœ¨ Larger text
        LinearLayout.LayoutParams focusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        focusParams.setMargins(0, dpToPx(6), 0, 0);
        tvFocus.setLayoutParams(focusParams);
        content.addView(tvFocus);

        layout.addView(content);
        card.addView(layout);
        return card;
    }

    private int getDifficultyColor(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return 0xFF00E5CC; // âœ¨ Cyan
            case "medium": return 0xFFFFA500; // Orange
            case "hard": return 0xFFFF6B6B; // Red
            default: return 0xFFFFFFFF;
        }
    }

    private void displayMilestones() {
        if (trainingPlan.milestones == null || trainingPlan.milestones.isEmpty()) return;

        TextView sectionTitle = createSectionTitle("\uD83C\uDFC6 Milestones");
        milestonesContainer.addView(sectionTitle);

        for (int i = 0; i < trainingPlan.milestones.size(); i++) {
            GroqAPIService.Milestone milestone = trainingPlan.milestones.get(i);
            CardView milestoneCard = createEnhancedMilestoneCard(milestone, i);
            milestonesContainer.addView(milestoneCard);
        }
    }

    /**
     * âœ¨ ENHANCED: Better milestone card
     */
    private CardView createEnhancedMilestoneCard(GroqAPIService.Milestone milestone, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(14));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(3));
        card.setCardBackgroundColor(0x30FFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        // Week badge
        TextView tvWeek = new TextView(this);
        tvWeek.setText("W" + milestone.week);
        tvWeek.setTextColor(0xFF00E5CC); // âœ¨ Cyan
        tvWeek.setTextSize(22); // âœ¨ Larger text
        tvWeek.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams weekParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        weekParams.setMarginEnd(dpToPx(20));
        tvWeek.setLayoutParams(weekParams);
        layout.addView(tvWeek);

        // Content
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        content.setLayoutParams(contentParams);

        TextView tvMilestone = new TextView(this);
        tvMilestone.setText(milestone.milestone);
        tvMilestone.setTextColor(0xFFFFFFFF);
        tvMilestone.setTextSize(17); // âœ¨ Larger text
        tvMilestone.setTypeface(null, android.graphics.Typeface.BOLD);
        tvMilestone.setLineSpacing(2, 1f);
        content.addView(tvMilestone);

        TextView tvVerification = new TextView(this);
        tvVerification.setText("✨ " + milestone.verification);
        tvVerification.setTextColor(0xEEFFFFFF);
        tvVerification.setTextSize(15); // âœ¨ Larger text
        tvVerification.setLineSpacing(2, 1f);
        LinearLayout.LayoutParams verifyParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        verifyParams.setMargins(0, dpToPx(8), 0, 0);
        tvVerification.setLayoutParams(verifyParams);
        content.addView(tvVerification);

        layout.addView(content);
        card.addView(layout);

        // âœ¨ Fade-in animation
        card.setAlpha(0f);
        card.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(index * 100L)
                .start();

        return card;
    }

    private TextView createSectionTitle(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(22); // âœ¨ Larger text
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(36), 0, dpToPx(20));
        tv.setLayoutParams(params);

        // âœ¨ Fade-in animation
        tv.setAlpha(0f);
        tv.setTranslationY(-10f);
        tv.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();

        return tv;
    }

    private boolean isTaskCompleted(String taskId) {
        Set<String> completed = prefs.getStringSet(roadmapId + "_completed", new HashSet<>());
        return completed.contains(taskId);
    }

    private void markTaskCompleted(String taskId) {
        Set<String> completed = new HashSet<>(prefs.getStringSet(roadmapId + "_completed", new HashSet<>()));
        completed.add(taskId);
        prefs.edit().putStringSet(roadmapId + "_completed", completed).apply();
    }

    private void unmarkTaskCompleted(String taskId) {
        Set<String> completed = new HashSet<>(prefs.getStringSet(roadmapId + "_completed", new HashSet<>()));
        completed.remove(taskId);
        prefs.edit().putStringSet(roadmapId + "_completed", completed).apply();
    }

    private void updateProgressHeader() {
        int currentProgress = totalTasksCount > 0 ?
                (completedTasksCount * 100) / totalTasksCount : 0;
        pbOverallProgress.setProgress(currentProgress);
        tvProgressPercent.setText(currentProgress + "% Complete");
    }

    private String getReadinessLabel(int score) {
        if (score >= 90) return "\uD83C\uDF1F Expert Level";
        if (score >= 80) return "\uD83C\uDFAF Advanced";
        if (score >= 70) return "✨ Proficient";
        if (score >= 60) return "\uD83D\uDCDA Intermediate";
        return "\uD83C\uDF31 Beginner";
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}