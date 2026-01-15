package com.example.skilllsetujava;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.example.skilllsetujava.api.models.Evaluation;
import com.example.skilllsetujava.api.models.ImmediateAction;
import com.example.skilllsetujava.api.models.InterviewResponse;
import com.example.skilllsetujava.api.models.QuestionAnalysis;
import com.example.skilllsetujava.api.models.Roadmap;
import com.example.skilllsetujava.api.models.FocusArea;
import com.example.skilllsetujava.api.models.WeeklyPlan;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EvaluationActivity extends AppCompatActivity {

    // UI
    private ImageView btnBack;
    private TextView tvInterviewType, tvJobRole, tvTotalTime;
    private NestedScrollView scrollView;

    private View circularProgressContainer;
    private ProgressBar circularProgressBar;
    private TextView tvOverallScore, tvPerformanceLevel;

    private TextView tvQuestionAnalysisTitle;
    private LinearLayout questionAnalysisContainer;

    private CardView coachFeedbackCard;
    private TextView tvCoachFeedback;

    private CardView actionPlanCard;
    private LinearLayout actionItemsContainer;

    private MaterialButton btnViewRoadmap, btnRetakeInterview, btnBackToHome;

    // Data
    private String interviewType;
    private String jobRole;
    private String totalTime;

    private Evaluation evaluation;
    private Roadmap roadmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        interviewType = getIntent().getStringExtra("interview_type");
        jobRole = getIntent().getStringExtra("job_role");
        totalTime = getIntent().getStringExtra("total_time");

        InterviewResponse response =
                (InterviewResponse) getIntent().getSerializableExtra("interview_response");

        if (response == null || response.getEvaluation() == null) {
            Toast.makeText(this, "Evaluation data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        evaluation = response.getEvaluation();
        roadmap = response.getRoadmap();

        initViews();
        setupListeners();
        displayEvaluationWithAnimations();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvInterviewType = findViewById(R.id.tvInterviewType);
        tvJobRole = findViewById(R.id.tvJobRole);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        scrollView = findViewById(R.id.scrollView);

        circularProgressContainer = findViewById(R.id.circularProgressContainer);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        tvOverallScore = findViewById(R.id.tvOverallScore);
        tvPerformanceLevel = findViewById(R.id.tvPerformanceLevel);

        tvQuestionAnalysisTitle = findViewById(R.id.tvQuestionAnalysisTitle);
        questionAnalysisContainer = findViewById(R.id.questionAnalysisContainer);

        coachFeedbackCard = findViewById(R.id.coachFeedbackCard);
        tvCoachFeedback = findViewById(R.id.tvCoachFeedback);

        actionPlanCard = findViewById(R.id.actionPlanCard);
        actionItemsContainer = findViewById(R.id.actionItemsContainer);

        btnViewRoadmap = findViewById(R.id.btnViewRoadmap);
        btnRetakeInterview = findViewById(R.id.btnRetakeInterview);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        tvInterviewType.setText(interviewType + " Interview");
        tvJobRole.setText(jobRole);
        tvTotalTime.setText("⏱️ " + totalTime);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // ✅ FIXED ROADMAP NAVIGATION
        btnViewRoadmap.setOnClickListener(v -> {
            if (roadmap == null) {
                Toast.makeText(this, "❌ Roadmap data not available", Toast.LENGTH_SHORT).show();
                Log.e("EvaluationActivity", "Roadmap is NULL!");
                return;
            }

            // ✅ Convert DTO Roadmap to GroqAPIService.TrainingPlan format
            GroqAPIService.TrainingPlan plan = convertRoadmapToTrainingPlan(roadmap);

            Intent intent = new Intent(this, RoadmapActivity.class);
            intent.putExtra("training_plan", plan);  // ✅ Use correct key
            intent.putExtra("job_role", jobRole);
            intent.putExtra("interview_type", interviewType);
            intent.putExtra("overall_score", evaluation.getOverallScore());
            startActivity(intent);
        });

        btnRetakeInterview.setOnClickListener(v -> {
            Intent intent = new Intent(this, InterviewActivity.class);
            intent.putExtra(InterviewActivity.EXTRA_INTERVIEW_TYPE, interviewType);
            intent.putExtra(InterviewActivity.EXTRA_JOB_ROLE, jobRole);
            intent.putExtra(InterviewActivity.EXTRA_IS_RETAKE, true);
            startActivity(intent);
            finish();
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, activity_homepage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void displayEvaluationWithAnimations() {
        new Handler(Looper.getMainLooper()).postDelayed(this::animateOverallScore, 400);
        new Handler(Looper.getMainLooper()).postDelayed(this::displayQuestionAnalysis, 1500);
        new Handler(Looper.getMainLooper()).postDelayed(this::displayCoachFeedback, 2500);
        new Handler(Looper.getMainLooper()).postDelayed(this::displayActionPlan, 3500);
    }

    private void animateOverallScore() {
        circularProgressContainer.setScaleX(0f);
        circularProgressContainer.setScaleY(0f);
        circularProgressContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new BounceInterpolator())
                .setDuration(800)
                .start();

        int target = (int) (evaluation.getOverallScore() * 10);
        ValueAnimator animator = ValueAnimator.ofInt(0, target);
        animator.setDuration(1500);
        animator.addUpdateListener(a -> {
            int v = (int) a.getAnimatedValue();
            circularProgressBar.setProgress(v);
            tvOverallScore.setText(String.format(Locale.US, "%.1f", v / 10.0));
        });
        animator.start();
    }

    private void displayQuestionAnalysis() {
        if (evaluation.getQuestionAnalysis() == null || evaluation.getQuestionAnalysis().isEmpty()) {
            tvQuestionAnalysisTitle.setVisibility(View.GONE);
            Log.e("EvaluationActivity", "❌ Question analysis is NULL or EMPTY!");
            return;
        }

        Log.d("EvaluationActivity", "✅ Found " + evaluation.getQuestionAnalysis().size() + " question analyses");

        questionAnalysisContainer.removeAllViews();

        for (int i = 0; i < evaluation.getQuestionAnalysis().size(); i++) {
            QuestionAnalysis qa = evaluation.getQuestionAnalysis().get(i);

            Log.d("EvaluationActivity", String.format(
                    "Q%d: score=%.1f, answered='%s', good='%s'",
                    qa.getQuestionNumber(),
                    qa.getScore(),
                    qa.getWhatYouAnswered() != null ? qa.getWhatYouAnswered().substring(0, Math.min(30, qa.getWhatYouAnswered().length())) : "NULL",
                    qa.getWhatWasGood() != null ? qa.getWhatWasGood().substring(0, Math.min(30, qa.getWhatWasGood().length())) : "NULL"
            ));

            CardView card = createQuestionCard(qa, i);
            questionAnalysisContainer.addView(card);
        }
    }
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private CardView createQuestionCard(QuestionAnalysis qa, int index) {

        CardView card = new CardView(this);
        card.setRadius(dpToPx(20));
        card.setCardElevation(dpToPx(6));
        card.setUseCompatPadding(true);
        card.setCardBackgroundColor(0xFF1C2233); // 🔥 Dark roadmap-like card

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        // 🧠 Question header
        TextView tvQuestion = new TextView(this);
        tvQuestion.setText("Question " + qa.getQuestionNumber());
        tvQuestion.setTextSize(16);
        tvQuestion.setTextColor(0xFFFFFFFF);
        tvQuestion.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvQuestion);

        // ⭐ Score
        TextView tvScore = new TextView(this);
        tvScore.setText("Score: " + qa.getScore());
        tvScore.setTextColor(0xFF9AA4BF);
        tvScore.setPadding(0, dpToPx(4), 0, dpToPx(12));
        layout.addView(tvScore);

        // 📝 Your Answer
        if (qa.getWhatYouAnswered() != null && !qa.getWhatYouAnswered().isEmpty()) {
            TextView tvAnswer = new TextView(this);
            tvAnswer.setText("Your Answer:\n" + qa.getWhatYouAnswered());
            tvAnswer.setTextColor(0xFFD6DBF5);
            tvAnswer.setPadding(0, 0, 0, dpToPx(12));
            layout.addView(tvAnswer);
        }

        // ✅ What was good
        if (qa.getWhatWasGood() != null && !qa.getWhatWasGood().isEmpty()) {
            TextView tvGood = new TextView(this);
            tvGood.setText("✔ What was good:\n" + qa.getWhatWasGood());
            tvGood.setTextColor(0xFF6EE7B7);
            tvGood.setPadding(0, 0, 0, dpToPx(8));
            layout.addView(tvGood);
        }

        // ⚠ What was missing
        if (qa.getWhatWasMissing() != null && !qa.getWhatWasMissing().isEmpty()) {
            TextView tvMissing = new TextView(this);
            tvMissing.setText("⚠ What was missing:\n" + qa.getWhatWasMissing());
            tvMissing.setTextColor(0xFFFCA5A5);
            layout.addView(tvMissing);
        }

        card.addView(layout);

        // ✨ Smooth entry animation
        card.setAlpha(0f);
        card.setTranslationY(dpToPx(20));
        card.animate()
                .alpha(1f)
                .translationY(0)
                .setStartDelay(index * 120L)
                .setDuration(300)
                .start();

        return card;
    }


    private void displayCoachFeedback() {
        tvCoachFeedback.setText(evaluation.getCoachFeedback());
    }

    private void displayActionPlan() {
        if (evaluation.getImmediateActions() == null) return;

        for (ImmediateAction action : evaluation.getImmediateActions()) {
            TextView tv = new TextView(this);
            tv.setText("• " + action.getAction());
            tv.setTextColor(0xFFFFFFFF);
            actionItemsContainer.addView(tv);
        }
    }

    // ================= CONVERSION LOGIC =================

    private GroqAPIService.TrainingPlan convertRoadmapToTrainingPlan(Roadmap roadmap) {

        GroqAPIService.TrainingPlan plan = new GroqAPIService.TrainingPlan();

        plan.readinessScore = roadmap.getReadinessScore();
        plan.targetScore = roadmap.getTargetScore();
        plan.timeToTarget = roadmap.getTimeToTarget();

        plan.focusAreas = new ArrayList<>();
        if (roadmap.getFocusAreas() != null) {
            for (FocusArea fa : roadmap.getFocusAreas()) {
                GroqAPIService.FocusArea gfa = new GroqAPIService.FocusArea();
                gfa.area = fa.getArea();
                gfa.priority = fa.getPriority();
                gfa.currentLevel = fa.getCurrentLevel();
                gfa.targetLevel = fa.getTargetLevel();
                gfa.estimatedHours = fa.getEstimatedHours();
                gfa.keyTopics = fa.getKeyTopics();
                gfa.resources = new ArrayList<>();
                plan.focusAreas.add(gfa);
            }
        }

        plan.weeklyPlan = new ArrayList<>();
        if (roadmap.getWeeklyPlan() != null) {
            for (WeeklyPlan wp : roadmap.getWeeklyPlan()) {
                GroqAPIService.WeeklyPlan gwp = new GroqAPIService.WeeklyPlan();
                gwp.week = wp.getWeek();
                gwp.theme = wp.getTheme();
                gwp.studyTime = wp.getStudyTime();
                gwp.practiceTime = wp.getPracticeTime();
                gwp.topics = wp.getTopics();
                gwp.projects = wp.getProjects();
                gwp.weekendTask = wp.getWeekendTask();
                gwp.practiceProblems = new ArrayList<>();
                plan.weeklyPlan.add(gwp);
            }
        }

        plan.milestones = new ArrayList<>();
        return plan;
    }
}
