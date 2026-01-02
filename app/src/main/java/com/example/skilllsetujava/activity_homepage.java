package com.example.skilllsetujava;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class activity_homepage extends AppCompatActivity {

    // UI Components
    private DrawerLayout drawerLayout;
    private NestedScrollView scrollView;
    private ImageView ivMenu, ivProfile;
    private LinearLayout heroSection, interviewTypeSection, jobRoleSection;
    private LinearLayout jobRolesRow1, jobRolesRow2;
    private View circularProgressContainer;

    // Hero Section
    private ProgressBar circularProgressBar;
    private TextView tvPercentage;
    private MaterialButton btnReadyInterview;

    // Interview Type Cards
    private CardView cardHRInterview, cardTechnicalInterview, cardAptitude, cardMixedInterview;
    private List<CardView> interviewTypeCards = new ArrayList<>();

    // Job Roles
    private List<JobRoleCard> jobRoleCards = new ArrayList<>();

    // Main CTA Button
    private MaterialButton btnStartInterview;

    // Navigation Drawer Items
    private LinearLayout navAIQuestions, navChatInterface, navTimedResponses;
    private LinearLayout navConfidenceAI, navProgressTrack, navMultiLanguage;
    private TextView navStudentName, navStudentEmail;
    private ImageView navProfileImage;

    // Selection State
    private CardView selectedInterviewCard = null;
    private JobRoleCard selectedJobRoleCard = null;
    private String selectedInterviewType = null;
    private String selectedJobRole = null;
    private int placementReadiness = 68; // Example: 68%

    // Animation state
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize views
        initViews();

        // Setup job roles
        setupJobRoles();

        // Setup navigation drawer
        setupNavigationDrawer();

        // Setup listeners
        setupListeners();

        // Start entrance animations
        startEntranceAnimations();

        // Animate circular progress
        animateCircularProgress();


    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        scrollView = findViewById(R.id.scrollView);
        ivMenu = findViewById(R.id.ivMenu);
        ivProfile = findViewById(R.id.ivProfile);

        // Sections
        heroSection = findViewById(R.id.heroSection);
        interviewTypeSection = findViewById(R.id.interviewTypeSection);
        jobRoleSection = findViewById(R.id.jobRoleSection);

        // Job Roles Rows
        jobRolesRow1 = findViewById(R.id.jobRolesRow1);
        jobRolesRow2 = findViewById(R.id.jobRolesRow2);

        // Hero
        circularProgressContainer = findViewById(R.id.circularProgressContainer);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        tvPercentage = findViewById(R.id.tvPercentage);
        btnReadyInterview = findViewById(R.id.btnReadyInterview);

        // Interview Types
        cardHRInterview = findViewById(R.id.cardHRInterview);
        cardTechnicalInterview = findViewById(R.id.cardTechnicalInterview);
        cardAptitude = findViewById(R.id.cardAptitude);
        cardMixedInterview = findViewById(R.id.cardMixedInterview);

        // Add to list for easy management
        interviewTypeCards.add(cardHRInterview);
        interviewTypeCards.add(cardTechnicalInterview);
        interviewTypeCards.add(cardAptitude);
        interviewTypeCards.add(cardMixedInterview);

        // Main Button
        btnStartInterview = findViewById(R.id.btnStartInterview);

        // Navigation Drawer
        navStudentName = findViewById(R.id.navStudentName);
        navStudentEmail = findViewById(R.id.navStudentEmail);
        navProfileImage = findViewById(R.id.navProfileImage);
        navAIQuestions = findViewById(R.id.navAIQuestions);
        navChatInterface = findViewById(R.id.navChatInterface);
        navTimedResponses = findViewById(R.id.navTimedResponses);
        navConfidenceAI = findViewById(R.id.navConfidenceAI);
        navProgressTrack = findViewById(R.id.navProgressTrack);
        navMultiLanguage = findViewById(R.id.navMultiLanguage);
    }

    private void setupJobRoles() {
        String[] jobRoles = {
                "Software Developer",
                "Android Developer",
                "Web Developer",
                "Cyber Security",
                "DevOps Engineer",
                "UI/UX Designer",
                "QA/Tester",
                "AI Engineer"
        };

        int[] icons = {
                R.drawable.ic_code,
                R.drawable.ic_phone_android,
                R.drawable.ic_web,
                R.drawable.ic_engineering,
                R.drawable.ic_code,
                R.drawable.ic_web,
                R.drawable.ic_analytics,
                R.drawable.ic_ai
        };

        // Distribute across 2 rows (4 cards per row)
        for (int i = 0; i < jobRoles.length; i++) {
            LinearLayout targetRow = (i < 4) ? jobRolesRow1 : jobRolesRow2;
            addJobRoleCard(jobRoles[i], icons[i], targetRow);
        }
    }

    private void addJobRoleCard(String roleName, int iconRes, LinearLayout targetRow) {
        // Create CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                (int) (160 * getResources().getDisplayMetrics().density), // 160dp width
                (int) (140 * getResources().getDisplayMetrics().density)  // 140dp height
        );
        cardParams.setMargins(
                (int) (6 * getResources().getDisplayMetrics().density),
                0,
                (int) (6 * getResources().getDisplayMetrics().density),
                0
        );
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(20 * getResources().getDisplayMetrics().density);
        cardView.setCardElevation(6 * getResources().getDisplayMetrics().density);
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.card_unselected));
        cardView.setClickable(true);
        cardView.setFocusable(true);
        cardView.setForeground(ContextCompat.getDrawable(this, android.R.drawable.list_selector_background));

        // Create inner LinearLayout
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setGravity(android.view.Gravity.CENTER);
        innerLayout.setPadding(
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density)
        );

        // Icon background
        View iconBg = new View(this);
        LinearLayout.LayoutParams iconBgParams = new LinearLayout.LayoutParams(
                (int) (56 * getResources().getDisplayMetrics().density),
                (int) (56 * getResources().getDisplayMetrics().density)
        );
        iconBg.setLayoutParams(iconBgParams);
        iconBg.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_background_circle));
        innerLayout.addView(iconBg);

        // Icon
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (56 * getResources().getDisplayMetrics().density),
                (int) (56 * getResources().getDisplayMetrics().density)
        );
        iconParams.setMargins(0, (int) (-56 * getResources().getDisplayMetrics().density), 0, 0);
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setPadding(
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density),
                (int) (16 * getResources().getDisplayMetrics().density)
        );
        icon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        innerLayout.addView(icon);

        // Text
        TextView text = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(0, (int) (12 * getResources().getDisplayMetrics().density), 0, 0);
        text.setLayoutParams(textParams);
        text.setText(roleName);
        text.setTextColor(ContextCompat.getColor(this, R.color.white));
        text.setTextSize(13);
        text.setTypeface(null, android.graphics.Typeface.BOLD);
        text.setGravity(android.view.Gravity.CENTER);
        innerLayout.addView(text);

        cardView.addView(innerLayout);

        // Create card object
        JobRoleCard card = new JobRoleCard(cardView, icon, text, roleName);
        jobRoleCards.add(card);

        // Add click listener
        cardView.setOnClickListener(v -> selectJobRole(card));

        // Add to target row
        targetRow.addView(cardView);
    }

    private void selectJobRole(JobRoleCard clickedCard) {
        // If clicking the same card, deselect it
        if (selectedJobRoleCard == clickedCard) {
            deselectJobRole(clickedCard);
            return;
        }

        // Deselect previous card if any
        if (selectedJobRoleCard != null) {
            deselectJobRole(selectedJobRoleCard);
        }

        // Select new card
        selectedJobRoleCard = clickedCard;
        selectedJobRole = clickedCard.roleName;

        // Update visual state - subtle glow
        clickedCard.cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.card_selected_glow));
        clickedCard.cardView.setCardElevation(12 * getResources().getDisplayMetrics().density);

        // Very subtle scale animation (same as interview type)
        clickedCard.cardView.animate()
                .scaleX(1.03f)
                .scaleY(1.03f)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .start();

        Toast.makeText(this, "✓ Selected: " + selectedJobRole, Toast.LENGTH_SHORT).show();
        checkSelectionState();
    }

    private void deselectJobRole(JobRoleCard card) {
        selectedJobRoleCard = null;
        selectedJobRole = null;

        // Reset visual state
        card.cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.card_unselected));
        card.cardView.setCardElevation(6 * getResources().getDisplayMetrics().density);

        // Reset scale
        card.cardView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        checkSelectionState();
    }

    private void setupNavigationDrawer() {
        // Set student info (you can load from SharedPreferences/Database later)
        navStudentName.setText("Raj Kumar");
        navStudentEmail.setText("raj.kumar@college.edu");

        // Setup navigation item listeners
        navAIQuestions.setOnClickListener(v -> {
            animateNavItem(navAIQuestions);
            Toast.makeText(this, "Opening AI Questions...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to AI Questions screen
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navChatInterface.setOnClickListener(v -> {
            animateNavItem(navChatInterface);
            Toast.makeText(this, "Opening Chat Interface...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Chat Interface screen
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navTimedResponses.setOnClickListener(v -> {
            animateNavItem(navTimedResponses);
            Toast.makeText(this, "Opening Timed Responses...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Timed Responses screen
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navConfidenceAI.setOnClickListener(v -> {
            animateNavItem(navConfidenceAI);
            Toast.makeText(this, "Opening Confidence AI...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Confidence AI screen
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navProgressTrack.setOnClickListener(v -> {
            animateNavItem(navProgressTrack);
            Toast.makeText(this, "Opening Progress Track...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Progress Track screen
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        navMultiLanguage.setOnClickListener(v -> {
            animateNavItem(navMultiLanguage);
            Toast.makeText(this, "Opening Multi-Language...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Multi-Language screen
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    private void setupListeners() {
        // Header buttons
        ivMenu.setOnClickListener(v -> {
            animateClick(ivMenu);
            drawerLayout.openDrawer(GravityCompat.START);
        });

        ivProfile.setOnClickListener(v -> {
            animateClick(ivProfile);
            openProfileScreen();
        });

        // Circular progress - opens dashboard
        circularProgressContainer.setOnClickListener(v -> {
            animateClick(circularProgressContainer);
            openDashboard();
        });

        // Ready for interview button
        btnReadyInterview.setOnClickListener(v -> {
            animateButtonPress(btnReadyInterview);
            scrollToSection(jobRoleSection);
        });

        // Interview Type Cards
        cardHRInterview.setOnClickListener(v -> selectInterviewType("HR Interview", cardHRInterview));
        cardTechnicalInterview.setOnClickListener(v -> selectInterviewType("Technical", cardTechnicalInterview));
        cardAptitude.setOnClickListener(v -> selectInterviewType("Aptitude", cardAptitude));
        cardMixedInterview.setOnClickListener(v -> selectInterviewType("Mixed", cardMixedInterview));

        // Main CTA Button
        btnStartInterview.setOnClickListener(v -> handleStartInterview());
    }

    private void selectInterviewType(String type, CardView clickedCard) {
        // If clicking the same card, deselect it
        if (selectedInterviewCard == clickedCard) {
            deselectInterviewType(clickedCard);
            return;
        }

        // Deselect previous card if any
        if (selectedInterviewCard != null) {
            deselectInterviewType(selectedInterviewCard);
        }

        // Select new card
        selectedInterviewCard = clickedCard;
        selectedInterviewType = type;

        // Update visual state - subtle glow
        clickedCard.setBackground(ContextCompat.getDrawable(this, R.drawable.card_selected_glow));
        clickedCard.setCardElevation(12 * getResources().getDisplayMetrics().density);

        // Very subtle scale animation
        clickedCard.animate()
                .scaleX(1.03f)
                .scaleY(1.03f)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .start();

        Toast.makeText(this, "✓ " + type + " Interview selected", Toast.LENGTH_SHORT).show();
        checkSelectionState();
    }

    private void deselectInterviewType(CardView card) {
        selectedInterviewCard = null;
        selectedInterviewType = null;

        // Reset visual state
        card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        card.setBackground(ContextCompat.getDrawable(this, R.drawable.card_unselected));
        card.setCardElevation(6 * getResources().getDisplayMetrics().density);

        // Reset scale
        card.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        checkSelectionState();
    }

    private void checkSelectionState() {
        if (selectedInterviewType != null && selectedJobRole != null) {
            // Both selected - enable main button with animation
            btnStartInterview.setEnabled(true);
            btnStartInterview.setAlpha(1f);

            // Bounce animation
            btnStartInterview.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator())
                    .withEndAction(() ->
                            btnStartInterview.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(300)
                                    .start()
                    )
                    .start();

            // Add pulsing glow effect
            addPulsingGlow(btnStartInterview);
        } else {
            // Disable button
            btnStartInterview.setEnabled(false);
            btnStartInterview.setAlpha(0.6f);
        }
    }

    private void handleStartInterview() {

        // 1️⃣ Validate Interview Type
        if (selectedInterviewType == null) {
            Toast.makeText(
                    this,
                    "⚠️ Please select an interview type first",
                    Toast.LENGTH_SHORT
            ).show();

            scrollToSection(interviewTypeSection);
            shakeView(interviewTypeSection);
            return;
        }

        // 2️⃣ Validate Job Role
        if (selectedJobRole == null) {
            Toast.makeText(
                    this,
                    "⚠️ Please select a job role first",
                    Toast.LENGTH_SHORT
            ).show();

            scrollToSection(jobRoleSection);
            shakeView(jobRoleSection);
            return;
        }

        // 3️⃣ Animate button press
        animateButtonPress(btnStartInterview);

        // 4️⃣ Small delay for animation smoothness
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // 5️⃣ Launch Interview Activity
            Intent intent = new Intent(activity_homepage.this, InterviewActivity.class);

            intent.putExtra(
                    InterviewActivity.EXTRA_INTERVIEW_TYPE,
                    selectedInterviewType
            );
            intent.putExtra(
                    InterviewActivity.EXTRA_JOB_ROLE,
                    selectedJobRole
            );

            startActivity(intent);

        }, 250); // delay looks professional
    }


    private void openProfileScreen() {
        Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Profile Activity
        // Intent intent = new Intent(this, ProfileActivity.class);
        // startActivity(intent);
    }

    private void openDashboard() {
        Toast.makeText(this, "📊 Opening Dashboard...", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Dashboard Activity
        // Intent intent = new Intent(this, DashboardActivity.class);
        // intent.putExtra("readiness", placementReadiness);
        // startActivity(intent);
    }

    // ==================== ANIMATIONS ====================

    private void startEntranceAnimations() {
        // Initially hide all sections
        heroSection.setAlpha(0f);
        heroSection.setTranslationY(-50f);

        jobRoleSection.setAlpha(0f);
        jobRoleSection.setTranslationY(50f);

        interviewTypeSection.setAlpha(0f);
        interviewTypeSection.setTranslationY(50f);

        btnStartInterview.setAlpha(0f);
        btnStartInterview.setScaleX(0.8f);
        btnStartInterview.setScaleY(0.8f);

        // Staggered entrance animations
        animateSection(heroSection, 0, -50f);
        animateSection(jobRoleSection, 200, 50f);
        animateSection(interviewTypeSection, 400, 50f);

        // Animate main button
        new Handler().postDelayed(() -> {
            btnStartInterview.animate()
                    .alpha(0.6f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }, 600);
    }

    private void animateSection(View section, long delay, float translationY) {
        new Handler().postDelayed(() -> {
            section.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, delay);
    }

    private void animateCircularProgress() {
        // Animate percentage from 0 to target
        ValueAnimator percentageAnimator = ValueAnimator.ofInt(0, placementReadiness);
        percentageAnimator.setDuration(2000);
        percentageAnimator.setStartDelay(500);
        percentageAnimator.setInterpolator(new DecelerateInterpolator());
        percentageAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            tvPercentage.setText(value + "%");
            circularProgressBar.setProgress(value);
        });
        percentageAnimator.start();

        // Add rotation animation to container
        ObjectAnimator rotation = ObjectAnimator.ofFloat(circularProgressContainer, "rotation", 0f, 360f);
        rotation.setDuration(2000);
        rotation.setStartDelay(500);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotation.start();
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

    private void animateNavItem(View item) {
        item.animate()
                .scaleX(0.97f)
                .scaleY(0.97f)
                .setDuration(100)
                .withEndAction(() ->
                        item.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                )
                .start();
    }

    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }

    private void scrollToSection(View section) {
        new Handler().postDelayed(() -> {
            scrollView.smoothScrollTo(0, section.getTop() - 100);
        }, 100);
    }

    private void addPulsingGlow(View view) {
        ObjectAnimator pulse = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.9f, 1f);
        pulse.setDuration(1000);
        pulse.setRepeatCount(ValueAnimator.INFINITE);
        pulse.start();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ==================== INNER CLASS ====================

    private static class JobRoleCard {
        CardView cardView;
        ImageView icon;
        TextView text;
        String roleName;

        JobRoleCard(CardView cardView, ImageView icon, TextView text, String roleName) {
            this.cardView = cardView;
            this.icon = icon;
            this.text = text;
            this.roleName = roleName;
        }
    }
}
