package com.example.skilllsetujava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.example.skilllsetujava.api.RetrofitClient;
import com.example.skilllsetujava.api.models.RoadmapSummary;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to display list of all student roadmaps
 */
public class RoadmapListActivity extends AppCompatActivity {

    private static final String TAG = "RoadmapListActivity";

    private ImageView btnBack;
    private TextView tvTitle, tvEmptyState;
    private NestedScrollView scrollView;
    private LinearLayout roadmapsContainer;
    private ProgressBar progressBar;

    private Long studentId;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap_list);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        long studentId = prefs.getLong("student_id", -1);

        if (studentId <= 0) {
            Log.e("RoadmapList", "Invalid studentId: " + studentId);
            Toast.makeText(this,
                    "Session error. Please login again.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        String token = prefs.getString("jwt_token", "");
        authToken = "Bearer " + token;

        initViews();
        setupListeners();
        loadRoadmaps();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        scrollView = findViewById(R.id.scrollView);
        roadmapsContainer = findViewById(R.id.roadmapsContainer);
        progressBar = findViewById(R.id.progressBar1);

        tvTitle.setText("Your Training Roadmaps");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadRoadmaps() {
        showLoading(true);

        RetrofitClient.getApiService()
                .getStudentRoadmaps(authToken, studentId)
                .enqueue(new Callback<List<RoadmapSummary>>() {
                    @Override
                    public void onResponse(Call<List<RoadmapSummary>> call, Response<List<RoadmapSummary>> response) {
                        showLoading(false);

                        if (!response.isSuccessful() || response.body() == null) {
                            showError("Failed to load roadmaps");
                            return;
                        }

                        List<RoadmapSummary> roadmaps = response.body();
                        Log.d(TAG, "Loaded " + roadmaps.size() + " roadmaps");

                        if (roadmaps.isEmpty()) {
                            showEmptyState();
                        } else {
                            displayRoadmaps(roadmaps);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RoadmapSummary>> call, Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Failed to load roadmaps", t);
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void displayRoadmaps(List<RoadmapSummary> roadmaps) {
        tvEmptyState.setVisibility(View.GONE);
        roadmapsContainer.removeAllViews();

        for (RoadmapSummary roadmap : roadmaps) {
            CardView card = createRoadmapCard(roadmap);
            roadmapsContainer.addView(card);
        }
    }

    private CardView createRoadmapCard(RoadmapSummary roadmap) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        card.setLayoutParams(params);
        card.setRadius(dpToPx(20));
        card.setCardElevation(dpToPx(4));
        card.setCardBackgroundColor(0xFF1A1F35);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvRole = new TextView(this);
        tvRole.setText(roadmap.jobRole);
        tvRole.setTextColor(0xFFFFFFFF);
        tvRole.setTextSize(18);
        tvRole.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams roleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        );
        tvRole.setLayoutParams(roleParams);
        header.addView(tvRole);

        TextView tvScore = new TextView(this);
        tvScore.setText(roadmap.readinessScore + "%");
        tvScore.setTextColor(0xFF00E5CC);
        tvScore.setTextSize(24);
        tvScore.setTypeface(null, android.graphics.Typeface.BOLD);
        header.addView(tvScore);

        layout.addView(header);

        // Interview Type
        TextView tvType = new TextView(this);
        tvType.setText(formatInterviewType(roadmap.interviewType));
        tvType.setTextColor(0x99FFFFFF);
        tvType.setTextSize(14);
        LinearLayout.LayoutParams typeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        typeParams.setMargins(0, dpToPx(8), 0, dpToPx(16));
        tvType.setLayoutParams(typeParams);
        layout.addView(tvType);

        // Timeline
        TextView tvTimeline = new TextView(this);
        tvTimeline.setText("⏱️ " + roadmap.timeToTarget);
        tvTimeline.setTextColor(0xFFFFFFFF);
        tvTimeline.setTextSize(14);
        layout.addView(tvTimeline);

        // Date
        TextView tvDate = new TextView(this);
        tvDate.setText("📅 Created: " + roadmap.createdAt);
        tvDate.setTextColor(0x80FFFFFF);
        tvDate.setTextSize(13);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateParams.setMargins(0, dpToPx(8), 0, 0);
        tvDate.setLayoutParams(dateParams);
        layout.addView(tvDate);

        card.addView(layout);

        // Click to open roadmap
        card.setOnClickListener(v -> {
            // TODO: Open detailed roadmap view
            Toast.makeText(this, "Opening roadmap #" + roadmap.id, Toast.LENGTH_SHORT).show();
        });

        return card;
    }

    private String formatInterviewType(String type) {
        if (type == null) return "Interview";
        switch (type.toLowerCase()) {
            case "technical": return "🔧 Technical Interview";
            case "hr": return "💬 HR Interview";
            case "aptitude": return "🧠 Aptitude Test";
            case "mixed": return "🎯 Mixed Interview";
            default: return type;
        }
    }

    private void showEmptyState() {
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText("No roadmaps yet.\nComplete an interview to generate your first roadmap!");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}