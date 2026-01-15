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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.example.skilllsetujava.api.models.Evaluation;
import com.example.skilllsetujava.api.models.ImmediateAction;
import com.example.skilllsetujava.api.models.InterviewResponse;
import com.example.skilllsetujava.api.models.QuestionAnalysis;
import com.example.skilllsetujava.api.models.Roadmap;
import com.google.android.material.button.MaterialButton;

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
    private boolean isRetake;

    private Evaluation evaluation;
    private Roadmap roadmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        interviewType = getIntent().getStringExtra("interview_type");
        jobRole = getIntent().getStringExtra("job_role");
        totalTime = getIntent().getStringExtra("total_time");
        isRetake = getIntent().getBooleanExtra("is_retake", false);

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

        btnViewRoadmap.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoadmapActivity.class);
            intent.putExtra("roadmap", roadmap);
            intent.putExtra("job_role", jobRole);
            intent.putExtra("interview_type", interviewType);
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
        new Handler(Looper.getMainLooper()).postDelayed(this::showButtons, 4500);
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

        tvPerformanceLevel.setText(getPerformanceLevel(evaluation.getOverallScore()));
    }

    private void displayQuestionAnalysis() {
        if (evaluation.getQuestionAnalysis() == null || evaluation.getQuestionAnalysis().isEmpty()) {
            tvQuestionAnalysisTitle.setVisibility(View.GONE);
            return;
        }

        questionAnalysisContainer.removeAllViews();

        for (int i = 0; i < evaluation.getQuestionAnalysis().size(); i++) {
            QuestionAnalysis qa = evaluation.getQuestionAnalysis().get(i);
            CardView card = createQuestionCard(qa, i);
            questionAnalysisContainer.addView(card);
        }
    }

    private CardView createQuestionCard(QuestionAnalysis qa, int index) {
        CardView card = new CardView(this);
        card.setRadius(dpToPx(18));
        card.setCardElevation(dpToPx(4));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        TextView tvQ = new TextView(this);
        tvQ.setText("Question " + qa.getQuestionNumber());
        tvQ.setTextColor(0xFFFFFFFF);
        tvQ.setTextSize(17);
        layout.addView(tvQ);

        TextView tvAns = new TextView(this);
        tvAns.setText(qa.getWhatYouAnswered());
        tvAns.setTextColor(0xCCFFFFFF);
        layout.addView(tvAns);

        card.addView(layout);

        card.setAlpha(0f);
        card.animate().alpha(1f).setStartDelay(index * 150).start();
        return card;
    }

    private void displayCoachFeedback() {
        if (evaluation.getCoachFeedback() == null) {
            coachFeedbackCard.setVisibility(View.GONE);
            return;
        }
        tvCoachFeedback.setText(evaluation.getCoachFeedback());
        coachFeedbackCard.setVisibility(View.VISIBLE);
    }

    private void displayActionPlan() {
        if (evaluation.getImmediateActions() == null) {
            actionPlanCard.setVisibility(View.GONE);
            return;
        }

        actionItemsContainer.removeAllViews();
        for (ImmediateAction action : evaluation.getImmediateActions()) {
            TextView tv = new TextView(this);
            tv.setText("• " + action.getAction());
            tv.setTextColor(0xFFFFFFFF);
            actionItemsContainer.addView(tv);
        }
    }

    private void showButtons() {
        btnViewRoadmap.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator()).start();
        btnRetakeInterview.setVisibility(View.VISIBLE);
        btnBackToHome.setVisibility(View.VISIBLE);
    }

    private String getPerformanceLevel(double score) {
        if (score >= 9) return "🌟 Outstanding";
        if (score >= 8) return "🎯 Excellent";
        if (score >= 7) return "✅ Good";
        if (score >= 6) return "📈 Average";
        return "⚠ Needs Work";
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
