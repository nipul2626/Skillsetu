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

import com.example.skilllsetujava.api.models.*;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

/**
 * 🎯 ENHANCED Evaluation Screen
 *
 * Features:
 * - Beautiful question cards (like roadmap style)
 * - Expandable answers
 * - Coach feedback card
 * - Proper emoji support
 */
public class EvaluationActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private TextView tvInterviewType, tvJobRole, tvTotalTime;
    private NestedScrollView scrollView;

    // Score Section
    private View circularProgressContainer;
    private ProgressBar circularProgressBar;
    private TextView tvOverallScore, tvPerformanceLevel;

    // Content Containers
    private LinearLayout questionAnalysisContainer;
    private CardView coachFeedbackCard;
    private TextView tvCoachFeedback;
    private LinearLayout actionItemsContainer;

    // Buttons
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

        // Get data from intent
        interviewType = getIntent().getStringExtra("interview_type");
        jobRole = getIntent().getStringExtra("job_role");
        totalTime = getIntent().getStringExtra("total_time");

        InterviewResponse response = (InterviewResponse) getIntent()
                .getSerializableExtra("interview_response");

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

        questionAnalysisContainer = findViewById(R.id.questionAnalysisContainer);
        coachFeedbackCard = findViewById(R.id.coachFeedbackCard);
        tvCoachFeedback = findViewById(R.id.tvCoachFeedback);
        actionItemsContainer = findViewById(R.id.actionItemsContainer);

        btnViewRoadmap = findViewById(R.id.btnViewRoadmap);
        btnRetakeInterview = findViewById(R.id.btnRetakeInterview);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Set header info
        tvInterviewType.setText(interviewType + " Interview");
        tvJobRole.setText(jobRole);
        tvTotalTime.setText("⏱️ " + totalTime);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnViewRoadmap.setOnClickListener(v -> {
            if (roadmap == null) {
                Toast.makeText(this, "❌ Roadmap not available", Toast.LENGTH_SHORT).show();
                return;
            }

            GroqAPIService.TrainingPlan plan = convertRoadmapToTrainingPlan(roadmap);
            Intent intent = new Intent(this, RoadmapActivity.class);
            intent.putExtra("training_plan", plan);
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
        new Handler(Looper.getMainLooper()).postDelayed(this::displayQuestionAnalysis, 1000);
        new Handler(Looper.getMainLooper()).postDelayed(this::displayCoachFeedback, 1800);
        new Handler(Looper.getMainLooper()).postDelayed(this::displayActionPlan, 2400);
    }

    /**
     * 🎯 Animate overall score with bounce effect
     */
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

        // Set performance level
        String level = getPerformanceLevel(evaluation.getOverallScore());
        tvPerformanceLevel.setText(level);
    }

    /**
     * 📊 Display question analysis with ENHANCED CARDS
     */
    private void displayQuestionAnalysis() {
        if (evaluation.getQuestionAnalysis() == null ||
                evaluation.getQuestionAnalysis().isEmpty()) {
            Log.e("Evaluation", "❌ No question analysis found");
            return;
        }

        questionAnalysisContainer.removeAllViews();

        // Section title
        TextView title = createSectionTitle("📊 Question-by-Question Analysis");
        questionAnalysisContainer.addView(title);

        // Create enhanced cards for each question
        for (int i = 0; i < evaluation.getQuestionAnalysis().size(); i++) {
            QuestionAnalysis qa = evaluation.getQuestionAnalysis().get(i);
            CardView card = createEnhancedQuestionCard(qa, i);
            questionAnalysisContainer.addView(card);
        }
    }

    /**
     * 🎨 Create BEAUTIFUL question card (like roadmap style)
     */
    private CardView createEnhancedQuestionCard(QuestionAnalysis qa, int index) {
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
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        // ===== Header: Question Number + Score =====
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvQuestionNum = new TextView(this);
        tvQuestionNum.setText("Question " + qa.getQuestionNumber());
        tvQuestionNum.setTextColor(0xFFFFFFFF);
        tvQuestionNum.setTextSize(18);
        tvQuestionNum.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams qParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        tvQuestionNum.setLayoutParams(qParams);
        header.addView(tvQuestionNum);

        // Score badge
        TextView tvScore = new TextView(this);
        tvScore.setText(String.format(Locale.US, "%.1f/10", qa.getScore()));
        tvScore.setTextSize(16);
        tvScore.setTextColor(getScoreColor(qa.getScore()));
        tvScore.setTypeface(null, android.graphics.Typeface.BOLD);
        tvScore.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6));
        tvScore.setBackgroundResource(R.drawable.badge_background);
        header.addView(tvScore);

        layout.addView(header);

        // ===== Answer Section (EXPANDABLE) =====
        TextView tvAnswerLabel = new TextView(this);
        tvAnswerLabel.setText("📝 Your Answer:");
        tvAnswerLabel.setTextColor(0xFF00E5CC);
        tvAnswerLabel.setTextSize(15);
        tvAnswerLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams ansLabelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ansLabelParams.setMargins(0, dpToPx(12), 0, dpToPx(8));
        tvAnswerLabel.setLayoutParams(ansLabelParams);
        layout.addView(tvAnswerLabel);

        // Answer text (collapsible)
        TextView tvAnswer = new TextView(this);
        String answerText = qa.getWhatYouAnswered() != null ?
                qa.getWhatYouAnswered() : "No answer provided";
        tvAnswer.setText(answerText);
        tvAnswer.setTextColor(0xFFFFFFFF);
        tvAnswer.setTextSize(15);
        tvAnswer.setLineSpacing(3, 1f);
        tvAnswer.setMaxLines(2); // Initially show only 2 lines
        tvAnswer.setEllipsize(android.text.TextUtils.TruncateAt.END);
        layout.addView(tvAnswer);

        // Expand/Collapse button
        MaterialButton btnExpand = new MaterialButton(this);
        LinearLayout.LayoutParams expandParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        expandParams.setMargins(0, dpToPx(8), 0, dpToPx(12));
        btnExpand.setLayoutParams(expandParams);
        btnExpand.setText("📖 Expand Answer");
        btnExpand.setTextSize(13);
        btnExpand.setTextColor(0xFF00E5CC);
        btnExpand.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0x20FFFFFF)
        );
        btnExpand.setStrokeWidth(0);
        btnExpand.setCornerRadius(dpToPx(20));
        btnExpand.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));

        final boolean[] isExpanded = {false};
        btnExpand.setOnClickListener(v -> {
            if (isExpanded[0]) {
                // Collapse
                tvAnswer.setMaxLines(2);
                btnExpand.setText("📖 Expand Answer");
                isExpanded[0] = false;
            } else {
                // Expand
                tvAnswer.setMaxLines(Integer.MAX_VALUE);
                btnExpand.setText("📕 Collapse Answer");
                isExpanded[0] = true;
            }
        });
        layout.addView(btnExpand);

        // ===== What Was Good =====
        if (qa.getWhatWasGood() != null && !qa.getWhatWasGood().isEmpty()) {
            TextView tvGoodLabel = new TextView(this);
            tvGoodLabel.setText("✅ What Was Good:");
            tvGoodLabel.setTextColor(0xFF6EE7B7);
            tvGoodLabel.setTextSize(15);
            tvGoodLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams goodLabelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            goodLabelParams.setMargins(0, dpToPx(0), 0, dpToPx(6));
            tvGoodLabel.setLayoutParams(goodLabelParams);
            layout.addView(tvGoodLabel);

            TextView tvGood = new TextView(this);
            tvGood.setText(qa.getWhatWasGood());
            tvGood.setTextColor(0xEEFFFFFF);
            tvGood.setTextSize(14);
            tvGood.setLineSpacing(3, 1f);
            LinearLayout.LayoutParams goodParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            goodParams.setMargins(0, 0, 0, dpToPx(12));
            tvGood.setLayoutParams(goodParams);
            layout.addView(tvGood);
        }

        // ===== What Was Missing =====
        if (qa.getWhatWasMissing() != null && !qa.getWhatWasMissing().isEmpty()) {
            TextView tvMissingLabel = new TextView(this);
            tvMissingLabel.setText("⚠️ What Was Missing:");
            tvMissingLabel.setTextColor(0xFFFCA5A5);
            tvMissingLabel.setTextSize(15);
            tvMissingLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams missLabelParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            missLabelParams.setMargins(0, dpToPx(0), 0, dpToPx(6));
            tvMissingLabel.setLayoutParams(missLabelParams);
            layout.addView(tvMissingLabel);

            TextView tvMissing = new TextView(this);
            tvMissing.setText(qa.getWhatWasMissing());
            tvMissing.setTextColor(0xEEFFFFFF);
            tvMissing.setTextSize(14);
            tvMissing.setLineSpacing(3, 1f);
            layout.addView(tvMissing);
        }

        card.addView(layout);

        // Entrance animation
        card.setAlpha(0f);
        card.setTranslationY(20f);
        card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(index * 120L)
                .start();

        return card;
    }

    /**
     * 💬 Display coach feedback card
     */
    private void displayCoachFeedback() {
        if (evaluation.getCoachFeedback() == null ||
                evaluation.getCoachFeedback().isEmpty()) {
            coachFeedbackCard.setVisibility(View.GONE);
            return;
        }

        tvCoachFeedback.setText(evaluation.getCoachFeedback());

        coachFeedbackCard.setAlpha(0f);
        coachFeedbackCard.setVisibility(View.VISIBLE);
        coachFeedbackCard.animate()
                .alpha(1f)
                .setDuration(600)
                .start();
    }

    /**
     * 🎯 Display action items
     */
    private void displayActionPlan() {
        if (evaluation.getImmediateActions() == null) return;

        actionItemsContainer.removeAllViews();

        for (int i = 0; i < evaluation.getImmediateActions().size(); i++) {
            ImmediateAction action = evaluation.getImmediateActions().get(i);

            LinearLayout actionItem = new LinearLayout(this);
            actionItem.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            itemParams.setMargins(0, 0, 0, dpToPx(12));
            actionItem.setLayoutParams(itemParams);

            // Priority badge
            TextView tvPriority = new TextView(this);
            tvPriority.setText(action.getPriority());
            tvPriority.setTextSize(12);
            tvPriority.setTextColor(0xFFFFFFFF);
            tvPriority.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
            tvPriority.setBackgroundResource(R.drawable.badge_background);
            tvPriority.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams prioParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            prioParams.setMarginEnd(dpToPx(12));
            tvPriority.setLayoutParams(prioParams);
            actionItem.addView(tvPriority);

            // Action text
            TextView tvAction = new TextView(this);
            tvAction.setText("• " + action.getAction());
            tvAction.setTextColor(0xFFFFFFFF);
            tvAction.setTextSize(15);
            tvAction.setLineSpacing(2, 1f);
            LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            tvAction.setLayoutParams(actionParams);
            actionItem.addView(tvAction);

            actionItemsContainer.addView(actionItem);

            // Animation
            actionItem.setAlpha(0f);
            actionItem.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(i * 100L)
                    .start();
        }
    }

    // ==================== HELPER METHODS ====================

    private TextView createSectionTitle(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(20);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(24), 0, dpToPx(16));
        tv.setLayoutParams(params);
        return tv;
    }

    private String getPerformanceLevel(double score) {
        if (score >= 9.0) return "🏆 Outstanding";
        if (score >= 8.0) return "⭐ Excellent";
        if (score >= 7.0) return "✅ Good";
        if (score >= 6.0) return "📈 Fair";
        if (score >= 5.0) return "⚡ Needs Improvement";
        return "📚 Keep Learning";
    }

    private int getScoreColor(double score) {
        if (score >= 8.0) return 0xFF6EE7B7; // Green
        if (score >= 6.0) return 0xFFFFA500; // Orange
        return 0xFFFCA5A5; // Red
    }

    private GroqAPIService.TrainingPlan convertRoadmapToTrainingPlan(Roadmap roadmap) {
        GroqAPIService.TrainingPlan plan = new GroqAPIService.TrainingPlan();
        plan.readinessScore = roadmap.getReadinessScore();
        plan.targetScore = roadmap.getTargetScore();
        plan.timeToTarget = roadmap.getTimeToTarget();
        plan.focusAreas = new java.util.ArrayList<>();
        plan.weeklyPlan = new java.util.ArrayList<>();
        plan.milestones = new java.util.ArrayList<>();

        if (roadmap.getFocusAreas() != null) {
            for (FocusArea fa : roadmap.getFocusAreas()) {
                GroqAPIService.FocusArea gfa = new GroqAPIService.FocusArea();
                gfa.area = fa.getArea();
                gfa.priority = fa.getPriority();
                gfa.currentLevel = fa.getCurrentLevel();
                gfa.targetLevel = fa.getTargetLevel();
                gfa.estimatedHours = fa.getEstimatedHours();
                gfa.keyTopics = fa.getKeyTopics();
                gfa.resources = new java.util.ArrayList<>();
                plan.focusAreas.add(gfa);
            }
        }

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
                gwp.practiceProblems = new java.util.ArrayList<>();
                plan.weeklyPlan.add(gwp);
            }
        }

        return plan;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}