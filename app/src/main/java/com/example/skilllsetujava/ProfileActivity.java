package com.example.skilllsetujava;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import com.example.skilllsetujava.api.RetrofitClient;
import com.example.skilllsetujava.api.models.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI Components
    private ImageView ivBack, ivProfilePicture, ivEdit;
    private TextView tvStudentName, tvStudentEmail, tvStudentPhone;
    private TextView tvStudentCollege, tvStudentBranch, tvStudentYear, tvStudentCGPA;

    // Stats
    private CardView statInterviewsCard, statSkillsCard, statScoreCard;
    private TextView tvInterviewsCount, tvSkillsCount, tvScoreValue;
    private ProgressBar pbInterviews, pbSkills, pbScore;

    // Academic Cards
    private CardView academicCard, skillsCard, achievementsCard, settingsCard;

    // Action Buttons
    private MaterialButton btnEditProfile, btnViewResume, btnLogout;

    // Sections
    private LinearLayout statsSection, academicSection, actionsSection;

    // Loading indicator
    private View loadingOverlay;
    private ProgressBar loadingSpinner;

    // Data
    private Long studentId;
    private String token;
    private ProfileResponse profileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadAuthData();
        setupListeners();
        animateEntrance();

        if (studentId != null && token != null) {
            loadProfileData();
        } else {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
            redirectToLogin();
        }
    }

    private void initViews() {
        // Header
        ivBack = findViewById(R.id.ivBack);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivEdit = findViewById(R.id.ivEdit);

        // Personal Info
        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvStudentPhone = findViewById(R.id.tvStudentPhone);

        // Academic Info
        tvStudentCollege = findViewById(R.id.tvStudentCollege);
        tvStudentBranch = findViewById(R.id.tvStudentBranch);
        tvStudentYear = findViewById(R.id.tvStudentYear);
        tvStudentCGPA = findViewById(R.id.tvStudentCGPA);

        // Stats
        statInterviewsCard = findViewById(R.id.statInterviewsCard);
        statSkillsCard = findViewById(R.id.statSkillsCard);
        statScoreCard = findViewById(R.id.statScoreCard);

        tvInterviewsCount = findViewById(R.id.tvInterviewsCount);
        tvSkillsCount = findViewById(R.id.tvSkillsCount);
        tvScoreValue = findViewById(R.id.tvScoreValue);

        pbInterviews = findViewById(R.id.pbInterviews);
        pbSkills = findViewById(R.id.pbSkills);
        pbScore = findViewById(R.id.pbScore);

        // Cards
        academicCard = findViewById(R.id.academicCard);
        skillsCard = findViewById(R.id.skillsCard);
        achievementsCard = findViewById(R.id.achievementsCard);
        settingsCard = findViewById(R.id.settingsCard);

        // Buttons
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnViewResume = findViewById(R.id.btnViewResume);
        btnLogout = findViewById(R.id.btnLogout);

        // Sections
        statsSection = findViewById(R.id.statsSection);
        academicSection = findViewById(R.id.academicSection);
        actionsSection = findViewById(R.id.actionsSection);
    }

    private void loadAuthData() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);

        token = prefs.getString("jwt_token", null);
        studentId = prefs.getLong("student_id", -1);

        if (studentId == -1) {
            studentId = null;
        }

        Log.d(TAG, "Loaded auth data - StudentId: " + studentId + ", Token: " + (token != null));
    }

    private void loadProfileData() {
        Log.d(TAG, "Loading profile for student ID: " + studentId);

        String authHeader = "Bearer " + token;

        RetrofitClient.getApiService()
                .getStudentProfile(authHeader, studentId)
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            profileData = response.body();
                            Log.d(TAG, "Profile loaded successfully");
                            bindProfileData();
                        } else {
                            Log.e(TAG, "Failed to load profile: " + response.code());
                            if (response.code() == 401) {
                                Toast.makeText(ProfileActivity.this,
                                        "Session expired. Please login again.",
                                        Toast.LENGTH_LONG).show();
                                redirectToLogin();
                            } else {
                                Toast.makeText(ProfileActivity.this,
                                        "Failed to load profile",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        Toast.makeText(ProfileActivity.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void bindProfileData() {
        if (profileData == null) return;

        // Personal Info
        tvStudentName.setText(profileData.getFullName() != null ? profileData.getFullName() : "N/A");
        tvStudentEmail.setText(profileData.getEmail() != null ? profileData.getEmail() : "N/A");
        tvStudentPhone.setText(profileData.getPhoneNumber() != null ? profileData.getPhoneNumber() : "N/A");

        // Academic Info
        tvStudentCollege.setText(profileData.getCollege() != null ? profileData.getCollege() : "N/A");
        tvStudentBranch.setText(profileData.getBranch() != null ? profileData.getBranch() : "N/A");
        tvStudentYear.setText(profileData.getYear() != null ? profileData.getYear() : "N/A");

        double cgpa = profileData.getCgpa() != null ? profileData.getCgpa() : 0.0;
        tvStudentCGPA.setText("CGPA: " + String.format("%.1f", cgpa));

        // Animate stats with real data
        animateStatsWithData();
    }

    private void animateStatsWithData() {
        int totalInterviews = profileData.getTotalInterviews() != null ? profileData.getTotalInterviews() : 0;
        int skillsLearned = profileData.getSkillsLearned() != null ? profileData.getSkillsLearned() : 0;
        int averageScore = profileData.getAverageScore() != null ? profileData.getAverageScore() : 0;

        // Animate interview count
        new Handler().postDelayed(() -> {
            ValueAnimator interviewAnimator = ValueAnimator.ofInt(0, totalInterviews);
            interviewAnimator.setDuration(1500);
            interviewAnimator.setInterpolator(new DecelerateInterpolator());
            interviewAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                tvInterviewsCount.setText(String.valueOf(value));
                pbInterviews.setProgress((int) (((float) value / 50) * 100));
            });
            interviewAnimator.start();
        }, 500);

        // Animate skills count
        new Handler().postDelayed(() -> {
            ValueAnimator skillsAnimator = ValueAnimator.ofInt(0, skillsLearned);
            skillsAnimator.setDuration(1500);
            skillsAnimator.setInterpolator(new DecelerateInterpolator());
            skillsAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                tvSkillsCount.setText(String.valueOf(value));
                pbSkills.setProgress((int) (((float) value / 20) * 100));
            });
            skillsAnimator.start();
        }, 700);

        // Animate score
        new Handler().postDelayed(() -> {
            ValueAnimator scoreAnimator = ValueAnimator.ofInt(0, averageScore);
            scoreAnimator.setDuration(1500);
            scoreAnimator.setInterpolator(new DecelerateInterpolator());
            scoreAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                tvScoreValue.setText(value + "%");
                pbScore.setProgress(value);
            });
            scoreAnimator.start();
        }, 900);
    }

    private void setupListeners() {
        // Back button
        ivBack.setOnClickListener(v -> {
            animateClick(ivBack);
            onBackPressed();
        });

        // Edit profile picture
        ivEdit.setOnClickListener(v -> {
            animateClick(ivEdit);
            Toast.makeText(this, "Edit profile picture", Toast.LENGTH_SHORT).show();
        });

        // Stats cards
        statInterviewsCard.setOnClickListener(v -> {
            animateCardClick(statInterviewsCard);
            Toast.makeText(this, "View Interview History", Toast.LENGTH_SHORT).show();
        });

        statSkillsCard.setOnClickListener(v -> {
            animateCardClick(statSkillsCard);
            Toast.makeText(this, "View Skills Dashboard", Toast.LENGTH_SHORT).show();
        });

        statScoreCard.setOnClickListener(v -> {
            animateCardClick(statScoreCard);
            Toast.makeText(this, "View Performance Analytics", Toast.LENGTH_SHORT).show();
        });

        // Academic card
        academicCard.setOnClickListener(v -> {
            animateCardClick(academicCard);
            Toast.makeText(this, "View Academic Details", Toast.LENGTH_SHORT).show();
        });

        // Skills card
        skillsCard.setOnClickListener(v -> {
            animateCardClick(skillsCard);
            Toast.makeText(this, "Manage Skills", Toast.LENGTH_SHORT).show();
        });

        // Achievements card
        achievementsCard.setOnClickListener(v -> {
            animateCardClick(achievementsCard);
            Toast.makeText(this, "View Achievements", Toast.LENGTH_SHORT).show();
        });

        // Settings card
        settingsCard.setOnClickListener(v -> {
            animateCardClick(settingsCard);
            Toast.makeText(this, "Open Settings", Toast.LENGTH_SHORT).show();
        });

        // Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            animateButtonPress(btnEditProfile);
            Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
        });

        // View Resume button
        btnViewResume.setOnClickListener(v -> {
            animateButtonPress(btnViewResume);
            Toast.makeText(this, "View Resume", Toast.LENGTH_SHORT).show();
        });

        // Logout button
        btnLogout.setOnClickListener(v -> {
            animateButtonPress(btnLogout);
            handleLogout();
        });
    }

    private void animateEntrance() {
        // Hide initially
        ivProfilePicture.setAlpha(0f);
        ivProfilePicture.setScaleX(0.3f);
        ivProfilePicture.setScaleY(0.3f);

        statsSection.setAlpha(0f);
        statsSection.setTranslationY(30f);

        academicSection.setAlpha(0f);
        academicSection.setTranslationY(30f);

        actionsSection.setAlpha(0f);
        actionsSection.setTranslationY(30f);

        // Animate profile picture
        new Handler().postDelayed(() -> {
            ivProfilePicture.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setInterpolator(new OvershootInterpolator(2f))
                    .start();
        }, 100);

        // Animate stats
        new Handler().postDelayed(() -> {
            statsSection.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, 300);

        // Animate academic section
        new Handler().postDelayed(() -> {
            academicSection.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, 450);

        // Animate actions
        new Handler().postDelayed(() -> {
            actionsSection.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, 600);
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        redirectToLogin();
    }

    private void redirectToLogin() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ProfileActivity.this, activity_login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 500);
    }

    private void animateClick(View view) {
        view.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(100)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .setInterpolator(new OvershootInterpolator())
                                .start()
                )
                .start();
    }

    private void animateCardClick(CardView card) {
        card.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() ->
                        card.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .setInterpolator(new OvershootInterpolator())
                                .start()
                )
                .start();
    }

    private void animateButtonPress(View button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() ->
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .setInterpolator(new OvershootInterpolator())
                                .start()
                )
                .start();
    }
}