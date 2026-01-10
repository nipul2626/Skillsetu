package com.example.skilllsetujava;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
        int primaryColor = ContextCompat.getColor(this,
                isStudentSelected ? R.color.primary_purple : R.color.tpo_primary);

        // Animate button color change
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                btnLogin.getBackgroundTintList().getDefaultColor(),
                primaryColor
        );
        colorAnimator.setDuration(300);
        colorAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            btnLogin.setBackgroundTintList(ColorStateList.valueOf(color));
            cbRememberMe.setButtonTintList(ColorStateList.valueOf(color));
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

    private void handleLogin() {
        String identifier = isStudentSelected ?
                etEmail.getText().toString().trim() :
                etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (isStudentSelected) {
            if (identifier.isEmpty()) {
                emailInputLayout.setError("Email is required");
                shakeView(emailInputLayout);
                return;
            }
            if (!isValidEmail(identifier)) {
                emailInputLayout.setError("Invalid email format");
                shakeView(emailInputLayout);
                return;
            }
        } else {
            if (identifier.isEmpty()) {
                usernameInputLayout.setError("Username is required");
                shakeView(usernameInputLayout);
                return;
            }
        }

        if (password.isEmpty()) {
            passwordInputLayout.setError("Password is required");
            shakeView(passwordInputLayout);
            return;
        }
        if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            shakeView(passwordInputLayout);
            return;
        }

        // Button press animation
        btnLogin.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    btnLogin.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();

                    // Show success message
                    String roleText = isStudentSelected ? "Student" : "TPO";
                    Toast.makeText(this, "Logging in as " + roleText + "...", Toast.LENGTH_SHORT).show();

                    // TODO: Implement actual login logic here
                })
                .start();
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