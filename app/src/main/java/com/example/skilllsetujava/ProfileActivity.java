package com.example.skilllsetujava;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

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

    // Sample Data (Later from Firebase/SharedPreferences)
    private String name = "Raj Kumar";
    private String email = "raj.kumar@college.edu";
    private String phone = "+91 9876543210";
    private String college = "Indian Institute of Technology";
    private String branch = "Computer Science";
    private String year = "3rd Year";
    private String cgpa = "8.5";
    private int totalInterviews = 24;
    private int skillsLearned = 12;
    private int averageScore = 85;
    private int placementReadiness;
    private FirebaseAuthHelper authHelper;
    private FirebaseDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        authHelper = new FirebaseAuthHelper(this);
        dbHelper = new FirebaseDatabaseHelper();

        // Check if user is logged in
        if (!authHelper.isUserLoggedIn()) {
            Intent intent = new Intent(this, activity_login.class);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        loadUserData(); // This will now load from Firebase
        setupListeners();
        animateEntrance();
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

    private void loadUserData() {
        String userId = authHelper.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading (optional)
        // Add a ProgressBar in your layout if needed

        dbHelper.getUserProfile(userId, new FirebaseDatabaseHelper.UserProfileCallback() {
            @Override
            public void onSuccess(FirebaseDatabaseHelper.UserProfile profile) {
                runOnUiThread(() -> {
                    // Personal Info
                    name = profile.fullName;
                    email = profile.email;
                    phone = profile.phone;

                    tvStudentName.setText(profile.fullName);
                    tvStudentEmail.setText(profile.email);
                    tvStudentPhone.setText(profile.phone);

                    // Academic Info
                    college = profile.college;
                    branch = profile.branch;
                    year = profile.year;
                    cgpa = profile.cgpa;

                    tvStudentCollege.setText(profile.college);
                    tvStudentBranch.setText(profile.branch);
                    tvStudentYear.setText(profile.year);
                    tvStudentCGPA.setText("CGPA: " + profile.cgpa);

                    // Stats
                    totalInterviews = profile.totalInterviews;
                    averageScore = (int) profile.averageScore;
                    skillsLearned = profile.skillsLearned;
                    placementReadiness = profile.placementReadiness;

                    // Animate stats with real data
                    animateStats();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this,
                            "Failed to load profile: " + error,
                            Toast.LENGTH_SHORT).show();

                    // Use default values
                    tvStudentName.setText("User");
                    tvStudentEmail.setText(authHelper.getCurrentUserEmail());
                });
            }
        });
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
            // TODO: Open image picker
        });

        // Stats cards
        statInterviewsCard.setOnClickListener(v -> {
            animateCardClick(statInterviewsCard);
            Toast.makeText(this, "View Interview History", Toast.LENGTH_SHORT).show();
            // TODO: Open interview history
        });

        statSkillsCard.setOnClickListener(v -> {
            animateCardClick(statSkillsCard);
            Toast.makeText(this, "View Skills Dashboard", Toast.LENGTH_SHORT).show();
            // TODO: Open skills dashboard
        });

        statScoreCard.setOnClickListener(v -> {
            animateCardClick(statScoreCard);
            Toast.makeText(this, "View Performance Analytics", Toast.LENGTH_SHORT).show();
            // TODO: Open analytics
        });

        // Academic card
        academicCard.setOnClickListener(v -> {
            animateCardClick(academicCard);
            Toast.makeText(this, "View Academic Details", Toast.LENGTH_SHORT).show();
            // TODO: Open academic details
        });

        // Skills card
        skillsCard.setOnClickListener(v -> {
            animateCardClick(skillsCard);
            Toast.makeText(this, "Manage Skills", Toast.LENGTH_SHORT).show();
            // TODO: Open skills manager
        });

        // Achievements card
        achievementsCard.setOnClickListener(v -> {
            animateCardClick(achievementsCard);
            Toast.makeText(this, "View Achievements", Toast.LENGTH_SHORT).show();
            // TODO: Open achievements
        });

        // Settings card
        settingsCard.setOnClickListener(v -> {
            animateCardClick(settingsCard);
            Toast.makeText(this, "Open Settings", Toast.LENGTH_SHORT).show();
            // TODO: Open settings
        });

        // Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            animateButtonPress(btnEditProfile);
            Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
            // TODO: Open edit profile screen
        });

        // View Resume button
        btnViewResume.setOnClickListener(v -> {
            animateButtonPress(btnViewResume);
            Toast.makeText(this, "View Resume", Toast.LENGTH_SHORT).show();
            // TODO: Open resume viewer
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

    private void animateStats() {
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

    private void handleLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    authHelper.signOut(new FirebaseAuthHelper.SignOutCallback() {
                        @Override
                        public void onSignOutComplete() {
                            Toast.makeText(ProfileActivity.this,
                                    "✅ Logged out successfully",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ProfileActivity.this, activity_login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
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