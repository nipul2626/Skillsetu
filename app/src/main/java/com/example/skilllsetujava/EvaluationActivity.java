package com.example.skilllsetujava;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ✨ ENHANCED Evaluation Screen
 *
 * Improvements:
 * - Larger, more readable text
 * - Better color scheme (cyan instead of green)
 * - Smooth, professional animations
 * - Better visual hierarchy
 */
public class EvaluationActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private TextView tvInterviewType, tvJobRole, tvTotalTime;
    private NestedScrollView scrollView;

    // Section 1: Score
    private View circularProgressContainer;
    private ProgressBar circularProgressBar;
    private TextView tvOverallScore, tvPerformanceLevel;

    // Section 2: Question Analysis
    private LinearLayout questionAnalysisContainer;
    private TextView tvQuestionAnalysisTitle;

    // Section 3: Coach Feedback
    private CardView coachFeedbackCard;
    private TextView tvCoachFeedback;

    // Section 4: What to Do Next
    private CardView actionPlanCard;
    private LinearLayout actionItemsContainer;

    // Buttons
    private MaterialButton btnViewRoadmap, btnRetakeInterview, btnBackToHome;

    // Data
    private String interviewType;
    private String jobRole;
    private String totalTime;
    private boolean isRetake;
    private String aiSource;

    private GroqAPIService.ComprehensiveEvaluation evaluation;
    private GroqAPIService.TrainingPlan trainingPlan;
    private List<GroqAPIService.QAPair> qaHistory;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        interviewType = getIntent().getStringExtra("interview_type");
        jobRole = getIntent().getStringExtra("job_role");
        totalTime = getIntent().getStringExtra("total_time");
        isRetake = getIntent().getBooleanExtra("is_retake", false);
        aiSource = getIntent().getStringExtra("ai_source");

        evaluation = (GroqAPIService.ComprehensiveEvaluation) getIntent().getSerializableExtra("evaluation");
        trainingPlan = (GroqAPIService.TrainingPlan) getIntent().getSerializableExtra("training_plan");
        String qaJson = getIntent().getStringExtra("qa_history_json");
        qaHistory = parseQaHistory(qaJson);

        initViews();
        setupListeners();

        prefs = getSharedPreferences("interview_history", MODE_PRIVATE);

        autoSaveToHistory();
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

        if (aiSource != null) {
            Log.d("Evaluation", "✅ Powered by: " + aiSource);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnViewRoadmap.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoadmapActivity.class);
            intent.putExtra("training_plan", trainingPlan);
            intent.putExtra("job_role", jobRole);
            intent.putExtra("interview_type", interviewType);
            intent.putExtra("overall_score", evaluation.overallScore);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    /**
     * ✨ ENHANCED: Display evaluation with smooth animations
     */
    private void displayEvaluationWithAnimations() {
        circularProgressContainer.setAlpha(0f);

        new Handler(Looper.getMainLooper()).postDelayed(() -> animateOverallScore(), 400);
        new Handler(Looper.getMainLooper()).postDelayed(() -> displayQuestionAnalysis(), 1800);
        new Handler(Looper.getMainLooper()).postDelayed(() -> displayCoachFeedback(), 2800);
        new Handler(Looper.getMainLooper()).postDelayed(() -> displayActionPlan(), 3800);
        new Handler(Looper.getMainLooper()).postDelayed(() -> showButtons(), 4800);
    }

    /**
     * ✨ SECTION 1: Overall Score with bounce animation
     */
    private void animateOverallScore() {
        // Bounce in animation
        circularProgressContainer.setScaleX(0f);
        circularProgressContainer.setScaleY(0f);
        circularProgressContainer.setAlpha(1f);

        circularProgressContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new BounceInterpolator())
                .start();

        // Animate score
        int targetScore = (int) (evaluation.overallScore * 10);
        ValueAnimator scoreAnimator = ValueAnimator.ofInt(0, targetScore);
        scoreAnimator.setDuration(2000);
        scoreAnimator.setInterpolator(new DecelerateInterpolator());
        scoreAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            circularProgressBar.setProgress(value);
            tvOverallScore.setText(String.format(Locale.US, "%.1f", value / 10.0));
        });
        scoreAnimator.setStartDelay(400);
        scoreAnimator.start();

        String performanceLevel = getPerformanceLevel(evaluation.overallScore);
        tvPerformanceLevel.setText(performanceLevel);

        // Gentle rotation
        ObjectAnimator rotation = ObjectAnimator.ofFloat(circularProgressContainer, "rotation", 0f, 360f);
        rotation.setDuration(2000);
        rotation.setInterpolator(new DecelerateInterpolator());
        rotation.setStartDelay(400);
        rotation.start();
    }

    /**
     * ✨ SECTION 2: Question Analysis with staggered fade-in
     */
    private void displayQuestionAnalysis() {
        if (evaluation == null || evaluation.questionAnalysis == null || evaluation.questionAnalysis.isEmpty()) {
            tvQuestionAnalysisTitle.setVisibility(View.GONE);
            return;
        }

        Log.d("Evaluation", "✅ Displaying " + evaluation.questionAnalysis.size() + " question analyses");

        tvQuestionAnalysisTitle.setVisibility(View.VISIBLE);
        tvQuestionAnalysisTitle.setAlpha(0f);
        tvQuestionAnalysisTitle.setTranslationY(-20f);
        tvQuestionAnalysisTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        questionAnalysisContainer.removeAllViews();

        for (int i = 0; i < evaluation.questionAnalysis.size(); i++) {
            GroqAPIService.QuestionAnalysis analysis = evaluation.questionAnalysis.get(i);

            String questionText = "";
            if (i < qaHistory.size()) {
                questionText = qaHistory.get(i).question;
            } else {
                questionText = "Question " + (i + 1);
            }

            CardView questionCard = createEnhancedQuestionCard(analysis, questionText, i);
            questionAnalysisContainer.addView(questionCard);
        }
    }

    /**
     * ✨ ENHANCED: Better question card with improved styling
     */
    private CardView createEnhancedQuestionCard(GroqAPIService.QuestionAnalysis analysis,
                                                String questionText, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(20));
        card.setCardElevation(dpToPx(4));

        // ✨ Better color coding with cyan theme
        int bgColor;
        if (analysis.score >= 8.0) bgColor = 0xFF1A1F35; // Dark blue
        else if (analysis.score >= 6.0) bgColor = 0xFF2D1B4E; // Dark purple
        else bgColor = 0xFF331C1C; // Dark red

        card.setCardBackgroundColor(bgColor);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));

        // Header with larger text
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvQNum = new TextView(this);
        tvQNum.setText("Question " + analysis.questionNumber);
        tvQNum.setTextColor(0xFFFFFFFF);
        tvQNum.setTextSize(19); // ✨ Larger text
        tvQNum.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams qNumParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        tvQNum.setLayoutParams(qNumParams);
        header.addView(tvQNum);

        // Score badge
        TextView tvScore = new TextView(this);
        tvScore.setText(String.format(Locale.US, "%.1f/10", analysis.score));
        tvScore.setTextSize(18); // ✨ Larger text
        tvScore.setTypeface(null, android.graphics.Typeface.BOLD);
        tvScore.setPadding(dpToPx(18), dpToPx(8), dpToPx(18), dpToPx(8));
        tvScore.setBackgroundResource(R.drawable.badge_background);

        // ✨ Cyan theme colors
        int scoreColor;
        if (analysis.score >= 8.0) scoreColor = 0xFF00E5CC; // Bright cyan
        else if (analysis.score >= 6.0) scoreColor = 0xFFFFA500; // Orange
        else scoreColor = 0xFFFF6B6B; // Red

        tvScore.setTextColor(scoreColor);
        header.addView(tvScore);

        layout.addView(header);

        // Divider
        View divider = new View(this);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(2)
        );
        dividerParams.setMargins(0, dpToPx(16), 0, dpToPx(16));
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(0x40FFFFFF);
        layout.addView(divider);

        // Question asked
        TextView tvQuestionLabel = new TextView(this);
        tvQuestionLabel.setText("📝 Question Asked:");
        tvQuestionLabel.setTextColor(0xCCFFFFFF);
        tvQuestionLabel.setTextSize(15); // ✨ Larger text
        tvQuestionLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvQuestionLabel);

        TextView tvQuestion = new TextView(this);
        tvQuestion.setText(questionText);
        tvQuestion.setTextColor(0xFFFFFFFF);
        tvQuestion.setTextSize(16); // ✨ Larger text
        tvQuestion.setLineSpacing(6, 1f);
        LinearLayout.LayoutParams qParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        qParams.setMargins(0, dpToPx(6), 0, dpToPx(20));
        tvQuestion.setLayoutParams(qParams);
        layout.addView(tvQuestion);

        // Your answer
        TextView tvAnswerLabel = new TextView(this);
        tvAnswerLabel.setText("💭 Your Answer:");
        tvAnswerLabel.setTextColor(0xCCFFFFFF);
        tvAnswerLabel.setTextSize(15); // ✨ Larger text
        tvAnswerLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvAnswerLabel);

        TextView tvAnswer = new TextView(this);
        tvAnswer.setText(analysis.whatYouAnswered);
        tvAnswer.setTextColor(0xFFFFFFFF);
        tvAnswer.setTextSize(16); // ✨ Larger text
        tvAnswer.setLineSpacing(6, 1f);
        LinearLayout.LayoutParams aParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        aParams.setMargins(0, dpToPx(6), 0, dpToPx(20));
        tvAnswer.setLayoutParams(aParams);
        layout.addView(tvAnswer);

        // What was good
        if (analysis.whatWasGood != null && !analysis.whatWasGood.isEmpty()) {
            TextView tvGoodLabel = new TextView(this);
            tvGoodLabel.setText("✅ What Was Good:");
            tvGoodLabel.setTextColor(0xFF00E5CC); // ✨ Cyan
            tvGoodLabel.setTextSize(15); // ✨ Larger text
            tvGoodLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(tvGoodLabel);

            TextView tvGood = new TextView(this);
            tvGood.setText(analysis.whatWasGood);
            tvGood.setTextColor(0xFFFFFFFF);
            tvGood.setTextSize(16); // ✨ Larger text
            tvGood.setLineSpacing(6, 1f);
            LinearLayout.LayoutParams gParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            gParams.setMargins(0, dpToPx(6), 0, dpToPx(20));
            tvGood.setLayoutParams(gParams);
            layout.addView(tvGood);
        }

        // What was missing
        if (analysis.whatWasMissing != null && !analysis.whatWasMissing.isEmpty()) {
            TextView tvMissingLabel = new TextView(this);
            tvMissingLabel.setText("⚠️ What Was Missing:");
            tvMissingLabel.setTextColor(0xFFFFA500);
            tvMissingLabel.setTextSize(15); // ✨ Larger text
            tvMissingLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(tvMissingLabel);

            TextView tvMissing = new TextView(this);
            tvMissing.setText(analysis.whatWasMissing);
            tvMissing.setTextColor(0xFFFFFFFF);
            tvMissing.setTextSize(16); // ✨ Larger text
            tvMissing.setLineSpacing(6, 1f);
            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            mParams.setMargins(0, dpToPx(6), 0, dpToPx(20));
            tvMissing.setLayoutParams(mParams);
            layout.addView(tvMissing);
        }

        // Ideal answer
        if (analysis.idealAnswer != null && !analysis.idealAnswer.isEmpty()) {
            TextView tvIdealLabel = new TextView(this);
            tvIdealLabel.setText("🎯 Ideal Answer Should Include:");
            tvIdealLabel.setTextColor(0xFF00E5CC); // ✨ Cyan
            tvIdealLabel.setTextSize(15); // ✨ Larger text
            tvIdealLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(tvIdealLabel);

            TextView tvIdeal = new TextView(this);
            tvIdeal.setText(analysis.idealAnswer);
            tvIdeal.setTextColor(0xFFFFFFFF);
            tvIdeal.setTextSize(16); // ✨ Larger text
            tvIdeal.setLineSpacing(6, 1f);
            LinearLayout.LayoutParams iParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            iParams.setMargins(0, dpToPx(6), 0, 0);
            tvIdeal.setLayoutParams(iParams);
            layout.addView(tvIdeal);
        }

        card.addView(layout);

        // ✨ Smooth slide-up animation
        card.setAlpha(0f);
        card.setTranslationY(40f);
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
     * ✨ SECTION 3: Coach Feedback with fade-in
     */
    private void displayCoachFeedback() {
        if (evaluation.coachFeedback == null || evaluation.coachFeedback.isEmpty()) {
            coachFeedbackCard.setVisibility(View.GONE);
            return;
        }

        tvCoachFeedback.setText(evaluation.coachFeedback);
        tvCoachFeedback.setTextSize(17); // ✨ Larger text

        coachFeedbackCard.setAlpha(0f);
        coachFeedbackCard.setTranslationY(30f);
        coachFeedbackCard.setVisibility(View.VISIBLE);
        coachFeedbackCard.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * ✨ SECTION 4: Action Plan with slide-in animation
     */
    private void displayActionPlan() {
        if (evaluation.immediateActions == null || evaluation.immediateActions.isEmpty()) {
            actionPlanCard.setVisibility(View.GONE);
            return;
        }

        actionItemsContainer.removeAllViews();

        for (int i = 0; i < evaluation.immediateActions.size(); i++) {
            GroqAPIService.ImmediateAction action = evaluation.immediateActions.get(i);
            CardView actionCard = createEnhancedActionCard(action, i);
            actionItemsContainer.addView(actionCard);
        }

        actionPlanCard.setAlpha(0f);
        actionPlanCard.setTranslationX(-50f);
        actionPlanCard.setVisibility(View.VISIBLE);
        actionPlanCard.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    /**
     * ✨ ENHANCED: Better action card with cyan theme
     */
    private CardView createEnhancedActionCard(GroqAPIService.ImmediateAction action, int index) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(3));
        card.setCardBackgroundColor(0x30FFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        // Priority badge
        TextView tvPriority = new TextView(this);
        tvPriority.setText(action.priority);
        tvPriority.setTextSize(13); // ✨ Larger text
        tvPriority.setTypeface(null, android.graphics.Typeface.BOLD);
        tvPriority.setPadding(dpToPx(14), dpToPx(6), dpToPx(14), dpToPx(6));
        tvPriority.setBackgroundResource(R.drawable.badge_background);

        // ✨ Cyan theme colors
        int priorityColor = action.priority.equalsIgnoreCase("HIGH") ? 0xFFFF6B6B :
                action.priority.equalsIgnoreCase("MEDIUM") ? 0xFFFFA500 : 0xFF00E5CC;
        tvPriority.setTextColor(priorityColor);

        layout.addView(tvPriority);

        // Action text
        TextView tvAction = new TextView(this);
        tvAction.setText(action.action);
        tvAction.setTextColor(0xFFFFFFFF);
        tvAction.setTextSize(17); // ✨ Larger text
        tvAction.setTypeface(null, android.graphics.Typeface.BOLD);
        tvAction.setLineSpacing(4, 1f);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        actionParams.setMargins(0, dpToPx(12), 0, dpToPx(10));
        tvAction.setLayoutParams(actionParams);
        layout.addView(tvAction);

        // Why
        TextView tvWhy = new TextView(this);
        tvWhy.setText("💡 " + action.why);
        tvWhy.setTextColor(0xEEFFFFFF);
        tvWhy.setTextSize(15); // ✨ Larger text
        tvWhy.setLineSpacing(5, 1f);
        LinearLayout.LayoutParams whyParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        whyParams.setMargins(0, 0, 0, dpToPx(16));
        tvWhy.setLayoutParams(whyParams);
        layout.addView(tvWhy);

        // Resources
        if (action.resources != null && !action.resources.isEmpty()) {
            TextView tvResourcesLabel = new TextView(this);
            tvResourcesLabel.setText("📚 Resources:");
            tvResourcesLabel.setTextColor(0xFF00E5CC); // ✨ Cyan
            tvResourcesLabel.setTextSize(15); // ✨ Larger text
            tvResourcesLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            layout.addView(tvResourcesLabel);

            for (String resource : action.resources) {
                TextView tvResource = new TextView(this);
                tvResource.setText("• " + resource);
                tvResource.setTextColor(0xFFFFFFFF);
                tvResource.setTextSize(15); // ✨ Larger text
                tvResource.setLineSpacing(3, 1f);
                LinearLayout.LayoutParams rParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                rParams.setMargins(dpToPx(16), dpToPx(6), 0, dpToPx(6));
                tvResource.setLayoutParams(rParams);
                layout.addView(tvResource);
            }
        }

        card.addView(layout);

        // ✨ Staggered fade-in
        card.setAlpha(0f);
        card.setScaleX(0.95f);
        card.setScaleY(0.95f);
        card.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(index * 120L)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        return card;
    }

    /**
     * ✨ Show action buttons with bounce animation
     */
    private void showButtons() {
        // Primary button (View Roadmap) - Big bounce
        btnViewRoadmap.setAlpha(0f);
        btnViewRoadmap.setScaleX(0.5f);
        btnViewRoadmap.setScaleY(0.5f);

        btnViewRoadmap.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator(3f))
                .start();

        // Secondary buttons - Fade in
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btnRetakeInterview.setAlpha(0f);
            btnRetakeInterview.animate()
                    .alpha(1f)
                    .setDuration(600)
                    .start();

            btnBackToHome.setAlpha(0f);
            btnBackToHome.animate()
                    .alpha(1f)
                    .setDuration(600)
                    .setStartDelay(150)
                    .start();
        }, 400);
    }

    /**
     * 💾 Auto-save to history
     */
    private void autoSaveToHistory() {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault()).format(new Date());
            String interviewId = "interview_" + System.currentTimeMillis();

            JSONObject interviewData = new JSONObject();
            interviewData.put("id", interviewId);
            interviewData.put("timestamp", timestamp);
            interviewData.put("jobRole", jobRole);
            interviewData.put("interviewType", interviewType);
            interviewData.put("totalTime", totalTime);
            interviewData.put("overallScore", evaluation.overallScore);
            interviewData.put("isRetake", isRetake);
            interviewData.put("aiSource", aiSource);

            if (evaluation.topStrengths != null) {
                interviewData.put("topStrengths", new JSONArray(evaluation.topStrengths));
            }
            if (evaluation.criticalGaps != null) {
                interviewData.put("criticalGaps", new JSONArray(evaluation.criticalGaps));
            }

            interviewData.put("coachFeedback", evaluation.coachFeedback);

            JSONArray qaArray = new JSONArray();
            for (GroqAPIService.QAPair qa : qaHistory) {
                JSONObject qaObj = new JSONObject();
                qaObj.put("question", qa.question);
                qaObj.put("answer", qa.answer);
                qaArray.put(qaObj);
            }
            interviewData.put("qaHistory", qaArray);

            String existingData = prefs.getString("all_interviews", "[]");
            JSONArray allInterviews = new JSONArray(existingData);
            allInterviews.put(interviewData);

            prefs.edit()
                    .putString("all_interviews", allInterviews.toString())
                    .putFloat(jobRole + "_" + interviewType + "_last_score", (float) evaluation.overallScore)
                    .putLong(jobRole + "_" + interviewType + "_last_timestamp", System.currentTimeMillis())
                    .apply();

            Log.d("Evaluation", "✅ Interview saved: " + interviewId);

        } catch (Exception e) {
            Log.e("Evaluation", "❌ Save failed", e);
        }
    }

    private List<GroqAPIService.QAPair> parseQaHistory(String json) {
        List<GroqAPIService.QAPair> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new GroqAPIService.QAPair(
                        obj.getString("question"),
                        obj.getString("answer")
                ));
            }
        } catch (Exception e) {
            Log.e("Evaluation", "Parse error", e);
        }
        return list;
    }

    private String getPerformanceLevel(double score) {
        if (score >= 9.0) return "🌟 Outstanding";
        if (score >= 8.0) return "🎯 Excellent";
        if (score >= 7.0) return "✅ Good";
        if (score >= 6.0) return "📈 Average";
        if (score >= 5.0) return "⚠️ Needs Work";
        return "❌ Poor";
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}