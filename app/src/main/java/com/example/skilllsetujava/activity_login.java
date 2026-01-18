package com.example.skilllsetujava;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import com.example.skilllsetujava.api.RetrofitClient;
import com.example.skilllsetujava.api.models.LoginRequest;
import com.example.skilllsetujava.api.models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;

import java.util.Locale;
import java.util.Map;

public class activity_login extends AppCompatActivity {

    // UI Components
    private TextInputLayout emailInputLayout, passwordInputLayout, usernameInputLayout;
    private TextInputEditText etEmail, etPassword, etUsername;
    private MaterialButton btnLogin, btnGoogle, btnFacebook;
    private TextView btnStudent, btnTPO, tvForgotPassword, tvSignUp, tvWelcomeMessage, tvLoginTitle;
    private CheckBox cbRememberMe;
    private CardView logoCard, loginCard, biometricCard;
    private ScrollView rootScroll;
    private View decorCircle1, decorCircle2, decorCircle3, selectorBackground;
    private FrameLayout inputContainer;

    // Role selection
    private boolean isStudentSelected = true;
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Setup initial state
        setupInitialState();

        // Setup animations
        setupEntryAnimations();

        // Setup listeners
        setupListeners();

        // Setup email validation
        setupEmailValidation();

        // Setup Sign Up text with color
        setupSignUpText();

        // Test connection when activity starts
        testBackendConnection();


        // Set login button click listener
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void initViews() {

        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        usernameInputLayout = findViewById(R.id.usernameInputLayout);
        tvLoginTitle = findViewById(R.id.tvLoginTitle);


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);

        // âœ… FIX: assign to CLASS variable
        rootScroll = findViewById(R.id.rootScoll);

        inputContainer = findViewById(R.id.inputContainer);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        // âœ… FIX: no local variables
        btnStudent = findViewById(R.id.btnStudent);
        btnTPO = findViewById(R.id.btnTPO);

        selectorBackground = findViewById(R.id.selectorBackground);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);

        cbRememberMe = findViewById(R.id.cbRememberMe);

        logoCard = findViewById(R.id.logoCard);
        loginCard = findViewById(R.id.loginCard);
        biometricCard = findViewById(R.id.biometricCard);

        decorCircle1 = findViewById(R.id.decorCircle1);
        decorCircle2 = findViewById(R.id.decorCircle2);
        decorCircle3 = findViewById(R.id.decorCircle3);
    }


    private void setupInitialState() {
        // Set initial width for selector background (Student side)
        selectorBackground.post(() -> {
            int width = btnStudent.getWidth();
            ViewGroup.LayoutParams params = selectorBackground.getLayoutParams();
            params.width = width;
            selectorBackground.setLayoutParams(params);
        });
    }

    private void setupEntryAnimations() {
        // Initially hide everything with alpha
        logoCard.setAlpha(0f);
        logoCard.setScaleX(0.3f);
        logoCard.setScaleY(0.3f);

        loginCard.setAlpha(0f);
        loginCard.setTranslationY(100f);

        biometricCard.setAlpha(0f);
        biometricCard.setScaleX(0.5f);
        biometricCard.setScaleY(0.5f);

        // Animate logo with bounce
        new Handler().postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(logoCard, "alpha", 0f, 1f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoCard, "scaleX", 0.3f, 1.1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoCard, "scaleY", 0.3f, 1.1f, 1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(logoCard, "rotation", 0f, 360f);

            AnimatorSet logoSet = new AnimatorSet();
            logoSet.playTogether(alpha, scaleX, scaleY, rotation);
            logoSet.setDuration(800);
            logoSet.setInterpolator(new OvershootInterpolator(1.5f));
            logoSet.start();
        }, 100);

        // Animate login card
        new Handler().postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(loginCard, "alpha", 0f, 1f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(loginCard, "translationY", 100f, 0f);

            AnimatorSet cardSet = new AnimatorSet();
            cardSet.playTogether(alpha, translationY);
            cardSet.setDuration(600);
            cardSet.setInterpolator(new DecelerateInterpolator());
            cardSet.start();
        }, 300);

        // Animate biometric button
        new Handler().postDelayed(() -> {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(biometricCard, "alpha", 0f, 1f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(biometricCard, "scaleX", 0.5f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(biometricCard, "scaleY", 0.5f, 1f);

            AnimatorSet biometricSet = new AnimatorSet();
            biometricSet.playTogether(alpha, scaleX, scaleY);
            biometricSet.setDuration(400);
            biometricSet.setInterpolator(new OvershootInterpolator());
            biometricSet.start();
        }, 500);

        // Floating animation for decorative circles
        animateFloatingCircle(decorCircle1, -20f, 20f, 2000);
        animateFloatingCircle(decorCircle2, -30f, 30f, 1000);
        animateFloatingCircle(decorCircle3, -40f, 40f, 1500);
    }

    private void animateFloatingCircle(View view, float from, float to, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", from, to);
        animator.setDuration(duration);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    private void setupListeners() {

        btnStudent.setOnClickListener(v -> {
            if (!isStudentSelected && !isAnimating) {
                selectRole(true);
            }
        });

        btnTPO.setOnClickListener(v -> {
            if (isStudentSelected && !isAnimating) {
                selectRole(false);
            }
        });

        btnLogin.setOnClickListener(v -> handleLogin());
        btnGoogle.setOnClickListener(v -> handleGoogleLogin());
        btnFacebook.setOnClickListener(v -> handleFacebookLogin());

        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
        tvSignUp.setOnClickListener(v -> handleSignUp());

        biometricCard.setOnClickListener(v -> handleBiometricLogin());
    }


    private void selectRole(boolean isStudent) {

        if (isAnimating) return;
        isAnimating = true;
        isStudentSelected = isStudent;

        // âœ… SIMPLE background switch (NO animation)
        if (isStudent) {
            rootScroll.setBackgroundResource(R.drawable.bg_student_gradient);
        } else {
            rootScroll.setBackgroundResource(R.drawable.bg_tpo_gradient);
        }

        int targetX = isStudent ? 0 : btnTPO.getLeft() - btnStudent.getLeft();

        ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(
                selectorBackground,
                "translationX",
                selectorBackground.getTranslationX(),
                targetX
        );
        slideAnimator.setDuration(300);
        slideAnimator.setInterpolator(new OvershootInterpolator(1.5f));

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                isStudent ? btnStudent : btnTPO,
                "scaleX",
                1f, 1.1f, 1f
        );
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                isStudent ? btnStudent : btnTPO,
                "scaleY",
                1f, 1.1f, 1f
        );

        AnimatorSet set = new AnimatorSet();
        set.playTogether(slideAnimator, scaleX, scaleY);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                switchInputFields();
                updateThemeColors();
            }
        });

        set.start();
    }


    private void switchInputFields() {
        // Fade out current input, fade in new input with slide animation
        View currentInput = isStudentSelected ? usernameInputLayout : emailInputLayout;
        View newInput = isStudentSelected ? emailInputLayout : usernameInputLayout;

        // Fade and slide out
        currentInput.animate()
                .alpha(0f)
                .translationX(-50f)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    currentInput.setVisibility(View.GONE);
                    currentInput.setTranslationX(0f);

                    // Prepare new input
                    newInput.setAlpha(0f);
                    newInput.setTranslationX(50f);
                    newInput.setVisibility(View.VISIBLE);

                    // Fade and slide in
                    newInput.animate()
                            .alpha(1f)
                            .translationX(0f)
                            .setDuration(200)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                })
                .start();

        passwordInputLayout.animate()
                .alpha(0f)
                .translationY(10f)
                .setDuration(150)
                .withEndAction(() -> {

                    // Change password hint based on role
                    passwordInputLayout.setHint(
                            isStudentSelected ? "Password" : "TPO Password"
                    );

                    // Bring it back smoothly
                    passwordInputLayout.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(200)
                            .start();
                })
                .start();

        // Update welcome message with animation
        // Animate Welcome Message
        tvWelcomeMessage.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {

                    // Change welcome text
                    if (isStudentSelected) {
                        tvWelcomeMessage.setText("Welcome back! Sign in to continue");
                    } else {
                        tvWelcomeMessage.setText("TPO Portal - Manage placements");
                    }

                    // Fade welcome message back in
                    tvWelcomeMessage.animate()
                            .alpha(1f)
                            .setDuration(150)
                            .start();
                })
                .start();


// Animate Login Title (Student Login / TPO Login)
        tvLoginTitle.animate()
                .alpha(0f)
                .translationY(-20f)
                .setStartDelay(50)
                .setDuration(150)
                .withEndAction(() -> {

                    // Change title text
                    tvLoginTitle.setText(isStudentSelected ? "Student Login" : "TPO Login");

                    // Bring it back smoothly
                    tvLoginTitle.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(200)
                            .start();
                })
                .start();

    }

    private void updateThemeColors() {
        int colorFrom = btnLogin.getBackgroundTintList().getDefaultColor();
        int colorTo = ContextCompat.getColor(this,
                isStudentSelected ? R.color.primary_purple : R.color.tpo_primary);

        // ✅ FIXED: API 17 Compatible color animation
        ValueAnimator colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration(300);

        // We use ArgbEvaluator to manually calculate the color between 'from' and 'to'
        final ArgbEvaluator evaluator = new ArgbEvaluator();

        colorAnimator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            int color = (int) evaluator.evaluate(fraction, colorFrom, colorTo);

            btnLogin.setBackgroundTintList(ColorStateList.valueOf(color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cbRememberMe.setButtonTintList(ColorStateList.valueOf(color));
            }
            tvForgotPassword.setTextColor(color);
            passwordInputLayout.setBoxStrokeColor(color);

            if (isStudentSelected) {
                emailInputLayout.setBoxStrokeColor(color);
            } else {
                usernameInputLayout.setBoxStrokeColor(color);
            }
        });
        colorAnimator.start();

        // Pulse animation on button
        btnLogin.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(150)
                .withEndAction(() ->
                        btnLogin.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start()
                )
                .start();
    }

    private void setupEmailValidation() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if (isValidEmail(email)) {
                    emailInputLayout.setEndIconVisible(true);
                    emailInputLayout.setError(null);
                } else if (email.length() > 0) {
                    emailInputLayout.setEndIconVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setupSignUpText() {
        String fullText = "Don't have an account? Sign Up";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf("Sign Up");
        int endIndex = startIndex + "Sign Up".length();

        spannableString.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary_purple)),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannableString.setSpan(
                new StyleSpan(android.graphics.Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvSignUp.setText(spannableString);
    }


    /**
     * Test if backend is reachable
     */
    private void testBackendConnection() {
        Log.d("Login", "Testing backend connection to: " + RetrofitClient.getBaseUrl());

        RetrofitClient.getApiService()
                .testConnection()
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String message = response.body().get("message");
                            Log.d("Login", "✅ Backend connection successful: " + message);
                            Toast.makeText(activity_login.this,
                                    "✅ Backend connected: " + message,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Login", "❌ Backend responded with error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Log.e("Login", "❌ Cannot connect to backend: " + t.getMessage(), t);
                        Toast.makeText(activity_login.this,
                                "❌ Backend not reachable. Check:\n" +
                                        "1. Backend is running on port 8081\n" +
                                        "2. Using correct URL: " + RetrofitClient.getBaseUrl(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }



    /**
     * Call this method when login button is clicked
     */
    private void handleLogin() {

        String password = etPassword.getText().toString().trim();
        String identifier;

        // ================= INPUT =================
        if (isStudentSelected) {
            identifier = etEmail.getText().toString().trim();
            if (identifier.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
        } else {
            identifier = etUsername.getText().toString().trim();
            if (identifier.isEmpty()) {
                etUsername.setError("Username is required");
                etUsername.requestFocus();
                return;
            }
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // ================= UI LOCK =================
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        Log.d("LOGIN", "Login attempt → identifier=" + identifier);

        // 🔴 IMPORTANT:
        // Backend AUTH DOES NOT NEED ROLE FROM CLIENT
        LoginRequest request = new LoginRequest(identifier, password);

        RetrofitClient.getApiService()
                .login(request)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {

                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");

                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e("LOGIN", "Login failed → code=" + response.code());
                            Toast.makeText(activity_login.this,
                                    "Invalid credentials",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        LoginResponse res = response.body();

                        // ================= LOG EVERYTHING =================
                        Log.d("LOGIN", "Login OK");
                        Log.d("LOGIN", "Token=" + (res.getToken() != null));
                        Log.d("LOGIN", "Role=" + res.getRole());
                        Log.d("LOGIN", "CollegeId=" + res.getCollegeId());

                        // ================= SAVE AUTH (FIXED KEYS) =================
                        SharedPreferences prefs =
                                getSharedPreferences("auth", MODE_PRIVATE);

                        prefs.edit()
                                .putString("jwt_token", res.getToken())   // ✅ FIXED
                                .putString("role", res.getRole())
                                .putLong("student_id",
                                        res.getStudentId() != null ? res.getStudentId() : -1)
                                .putLong("college_id",
                                        res.getCollegeId() != null ? res.getCollegeId() : -1)
                                .putString("full_name", res.getFullName())
                                .apply();

                        Toast.makeText(activity_login.this,
                                "Welcome, " + res.getFullName(),
                                Toast.LENGTH_SHORT).show();

                        // ================= ROLE BASED NAVIGATION =================
                        String role = res.getRole();

                        if (role == null) {
                            Toast.makeText(activity_login.this,
                                    "Role missing from server",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        role = role.trim().toUpperCase(Locale.US);
                        Log.d("LOGIN", "Navigating as " + role);

                        Intent intent;

                        switch (role) {
                            case "ROLE_TPO":
                                intent = new Intent(
                                        activity_login.this,
                                        TPODashboardActivity.class
                                );
                                break;

                            case "ROLE_STUDENT":
                                intent = new Intent(
                                        activity_login.this,
                                        activity_homepage.class
                                );
                                break;

                            default:
                                Toast.makeText(activity_login.this,
                                        "Unsupported role: " + role,
                                        Toast.LENGTH_LONG).show();
                                return;
                        }

                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {

                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");

                        Log.e("LOGIN", "Network error", t);

                        Toast.makeText(activity_login.this,
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }

    private void handleGoogleLogin() {
        animateSocialButton(btnGoogle);
        Intent intent = new Intent(activity_login.this, activity_homepage.class);
        startActivity(intent);
        // TODO: Implement Google Sign-In
    }

    private void handleFacebookLogin() {
        animateSocialButton(btnFacebook);
        Intent signup = new Intent((activity_login.this), activity_signup.class);
        startActivity(signup);
        // TODO: Implement Facebook Login
    }

    private void animateSocialButton(MaterialButton button) {
        button.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() ->
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                )
                .start();
    }

    private void handleForgotPassword() {
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to forgot password screen
    }

    private void handleSignUp() {
        Intent intent = new Intent(activity_login.this, activity_signup.class);
        startActivity(intent);
        // TODO: Navigate to sign up screen
    }

    private void handleBiometricLogin() {
        // Pulse animation for biometric button
        biometricCard.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction(() ->
                        biometricCard.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start()
                )
                .start();

        Toast.makeText(this, "Biometric authentication", Toast.LENGTH_SHORT).show();
        // TODO: Implement biometric authentication
    }
}