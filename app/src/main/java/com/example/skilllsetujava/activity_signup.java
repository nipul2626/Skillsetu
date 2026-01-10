package com.example.skilllsetujava;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class activity_signup extends AppCompatActivity {

    // UI Components
    private ViewFlipper viewFlipper;
    private CardView backButton, signupCard;

    // Stepper
    private CardView step1Circle, step2Circle, step3Circle;
    private TextView step1Number, step2Number, step3Number;
    private TextView step1Label, step2Label, step3Label;
    private View progressLine1, progressLine2;

    // Role Selection
    private TextView btnStudentSignup, btnTPOSignup;
    private View selectorBackgroundSignup;
    private boolean isStudentSelected = true;

    // Step 1 - Personal Info
    private TextInputLayout fullNameInputLayout, emailInputLayoutSignup, phoneInputLayout, dobInputLayout;
    private TextInputEditText etFullName, etEmailSignup, etPhone, etDOB;
    private MaterialButton btnNextStep1;

    // Step 2 - Academic Info
    private TextInputLayout collegeInputLayout, branchInputLayout, yearInputLayout, cgpaInputLayout;
    private TextInputEditText etCollege, etBranch, etCGPA;
    private AutoCompleteTextView etYear;
    private MaterialButton btnBackStep2, btnNextStep2;

    // Step 3 - Password & Verification
    private TextInputLayout createPasswordInputLayout, confirmPasswordInputLayout;
    private TextInputEditText etCreatePassword, etConfirmPassword;
    private ProgressBar passwordStrengthBar;
    private TextView tvPasswordStrength;
    private CheckBox cbTerms;
    private MaterialButton btnBackStep3, btnRegister;

    // Social Login
    private MaterialButton btnGoogleSignup, btnTikTokSignup, btnFacebookSignup;
    private TextView tvLogin;

    // Data Storage
    private String fullName, email, phone, dob, college, branch, year, cgpa, password;
    private Calendar calendar = Calendar.getInstance();

    private int currentStep = 0;
    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupInitialState();
        setupListeners();
        setupYearDropdown();
        animateEntrance();
    }

    private void initViews() {
        // Main containers
        viewFlipper = findViewById(R.id.viewFlipper);
        backButton = findViewById(R.id.backButton);
        signupCard = findViewById(R.id.signupCard);

        // Stepper
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step1Number = findViewById(R.id.step1Number);
        step2Number = findViewById(R.id.step2Number);
        step3Number = findViewById(R.id.step3Number);
        step1Label = findViewById(R.id.step1Label);
        step2Label = findViewById(R.id.step2Label);
        step3Label = findViewById(R.id.step3Label);
        progressLine1 = findViewById(R.id.progressLine1);
        progressLine2 = findViewById(R.id.progressLine2);

        // Role selection
        btnStudentSignup = findViewById(R.id.btnStudentSignup);
        btnTPOSignup = findViewById(R.id.btnTPOSignup);
        selectorBackgroundSignup = findViewById(R.id.selectorBackgroundSignup);

        // Step 1
        fullNameInputLayout = findViewById(R.id.fullNameInputLayout);
        emailInputLayoutSignup = findViewById(R.id.emailInputLayoutSignup);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        dobInputLayout = findViewById(R.id.dobInputLayout);
        etFullName = findViewById(R.id.etFullName);
        etEmailSignup = findViewById(R.id.etEmailSignup);
        etPhone = findViewById(R.id.etPhone);
        etDOB = findViewById(R.id.etDOB);
        btnNextStep1 = findViewById(R.id.btnNextStep1);

        // Step 2
        collegeInputLayout = findViewById(R.id.collegeInputLayout);
        branchInputLayout = findViewById(R.id.branchInputLayout);
        yearInputLayout = findViewById(R.id.yearInputLayout);
        cgpaInputLayout = findViewById(R.id.cgpaInputLayout);
        etCollege = findViewById(R.id.etCollege);
        etBranch = findViewById(R.id.etBranch);
        etYear = findViewById(R.id.etYear);
        etCGPA = findViewById(R.id.etCGPA);
        btnBackStep2 = findViewById(R.id.btnBackStep2);
        btnNextStep2 = findViewById(R.id.btnNextStep2);

        // Step 3
        createPasswordInputLayout = findViewById(R.id.createPasswordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        etCreatePassword = findViewById(R.id.etCreatePassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        passwordStrengthBar = findViewById(R.id.passwordStrengthBar);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        cbTerms = findViewById(R.id.cbTerms);
        btnBackStep3 = findViewById(R.id.btnBackStep3);
        btnRegister = findViewById(R.id.btnRegister);

        // Bottom
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupInitialState() {
        // Set selector position for Student
        selectorBackgroundSignup.post(() -> {
            int width = btnStudentSignup.getWidth();
            ViewGroup.LayoutParams params = selectorBackgroundSignup.getLayoutParams();
            params.width = width;
            selectorBackgroundSignup.setLayoutParams(params);
        });

        updateStepperUI();
    }

    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> {
            animateClick(backButton);
            onBackPressed();
        });

        // Role selection
        btnStudentSignup.setOnClickListener(v -> {
            if (!isStudentSelected && !isAnimating) {
                selectRole(true);
            }
        });

        btnTPOSignup.setOnClickListener(v -> {
            if (isStudentSelected && !isAnimating) {
                selectRole(false);
            }
        });

        // Step 1
        btnNextStep1.setOnClickListener(v -> validateAndProceedStep1());

        // Date picker
        etDOB.setOnClickListener(v -> showDatePicker());
        dobInputLayout.setEndIconOnClickListener(v -> showDatePicker());

        // Email validation
        etEmailSignup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if (isValidEmail(email)) {
                    emailInputLayoutSignup.setEndIconVisible(true);
                    emailInputLayoutSignup.setError(null);
                } else if (email.length() > 0) {
                    emailInputLayoutSignup.setEndIconVisible(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Step 2
        btnBackStep2.setOnClickListener(v -> goToPreviousStep());
        btnNextStep2.setOnClickListener(v -> validateAndProceedStep2());

        // Step 3
        btnBackStep3.setOnClickListener(v -> goToPreviousStep());
        btnRegister.setOnClickListener(v -> validateAndRegister());

        // Password strength
        etCreatePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Login redirect
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(activity_signup.this, activity_login.class);
            startActivity(intent);
            finish();
        });
    }

    private void selectRole(boolean isStudent) {
        if (isAnimating) return;
        isAnimating = true;
        isStudentSelected = isStudent;

        int targetX = isStudent ? 0 : btnTPOSignup.getLeft() - btnStudentSignup.getLeft();

        ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(
                selectorBackgroundSignup,
                "translationX",
                selectorBackgroundSignup.getTranslationX(),
                targetX
        );
        slideAnimator.setDuration(300);
        slideAnimator.setInterpolator(new OvershootInterpolator(1.5f));

        slideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });

        slideAnimator.start();

        // Update text colors
        btnStudentSignup.setTextColor(ContextCompat.getColor(this,
                isStudent ? R.color.primary_purple : R.color.white));
        btnTPOSignup.setTextColor(ContextCompat.getColor(this,
                isStudent ? R.color.white : R.color.tpo_primary));
    }

    private void validateAndProceedStep1() {
        // Get values
        fullName = etFullName.getText().toString().trim();
        email = etEmailSignup.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        dob = etDOB.getText().toString().trim();

        // Validate Full Name
        if (fullName.isEmpty()) {
            fullNameInputLayout.setError("Full name is required");
            shakeView(fullNameInputLayout);
            return;
        }
        fullNameInputLayout.setError(null);

        // Validate Email
        if (email.isEmpty()) {
            emailInputLayoutSignup.setError("Email is required");
            shakeView(emailInputLayoutSignup);
            return;
        }
        if (!isValidEmail(email)) {
            emailInputLayoutSignup.setError("Invalid email format");
            shakeView(emailInputLayoutSignup);
            return;
        }
        emailInputLayoutSignup.setError(null);

        // Validate Phone
        if (phone.isEmpty()) {
            phoneInputLayout.setError("Phone number is required");
            shakeView(phoneInputLayout);
            return;
        }
        if (phone.length() != 10) {
            phoneInputLayout.setError("Phone number must be 10 digits");
            shakeView(phoneInputLayout);
            return;
        }
        phoneInputLayout.setError(null);

        // Validate DOB
        if (dob.isEmpty()) {
            dobInputLayout.setError("Date of birth is required");
            shakeView(dobInputLayout);
            return;
        }
        dobInputLayout.setError(null);

        // Proceed to next step
        goToNextStep();
    }

    private void validateAndProceedStep2() {
        // Get values
        college = etCollege.getText().toString().trim();
        branch = etBranch.getText().toString().trim();
        year = etYear.getText().toString().trim();
        cgpa = etCGPA.getText().toString().trim();

        // Validate College
        if (college.isEmpty()) {
            collegeInputLayout.setError("College name is required");
            shakeView(collegeInputLayout);
            return;
        }
        collegeInputLayout.setError(null);

        // Validate Branch
        if (branch.isEmpty()) {
            branchInputLayout.setError("Branch is required");
            shakeView(branchInputLayout);
            return;
        }
        branchInputLayout.setError(null);

        // Validate Year
        if (year.isEmpty()) {
            yearInputLayout.setError("Year is required");
            shakeView(yearInputLayout);
            return;
        }
        yearInputLayout.setError(null);

        // Validate CGPA
        if (cgpa.isEmpty()) {
            cgpaInputLayout.setError("CGPA/Percentage is required");
            shakeView(cgpaInputLayout);
            return;
        }
        cgpaInputLayout.setError(null);

        // Proceed to next step
        goToNextStep();
    }

    private void validateAndRegister() {
        // Get password values
        password = etCreatePassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate Password
        if (password.isEmpty()) {
            createPasswordInputLayout.setError("Password is required");
            shakeView(createPasswordInputLayout);
            return;
        }
        if (password.length() < 8) {
            createPasswordInputLayout.setError("Password must be at least 8 characters");
            shakeView(createPasswordInputLayout);
            return;
        }
        createPasswordInputLayout.setError(null);

        // Validate Confirm Password
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.setError("Please confirm your password");
            shakeView(confirmPasswordInputLayout);
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            shakeView(confirmPasswordInputLayout);
            return;
        }
        confirmPasswordInputLayout.setError(null);

        // Validate Terms
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to Terms & Privacy Policy", Toast.LENGTH_SHORT).show();
            shakeView(cbTerms);
            return;
        }

        // Animate button and register
        animateButtonPress(btnRegister);

        new Handler().postDelayed(() -> {
            // TODO: Save data locally or to Firebase
            // For now, just show success and navigate
            Toast.makeText(this, "âœ… Registration Successful!", Toast.LENGTH_SHORT).show();

            // Navigate to login
            Intent intent = new Intent(activity_signup.this, activity_login.class);
            startActivity(intent);
            finish();
        }, 300);
    }

    private void goToNextStep() {
        if (currentStep < 2) {
            currentStep++;
            animateStepTransition(true);
        }
    }

    private void goToPreviousStep() {
        if (currentStep > 0) {
            currentStep--;
            animateStepTransition(false);
        }
    }

    private void animateStepTransition(boolean forward) {
        // Animate ViewFlipper
        if (forward) {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
            viewFlipper.showNext();
        } else {
            viewFlipper.setInAnimation(this, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_right);
            viewFlipper.showPrevious();
        }

        // Update stepper UI
        updateStepperUI();
    }

    private void updateStepperUI() {
        // Reset all steps
        resetStepCircle(step1Circle, step1Number, step1Label);
        resetStepCircle(step2Circle, step2Number, step2Label);
        resetStepCircle(step3Circle, step3Number, step3Label);

        progressLine1.setBackgroundResource(R.drawable.progress_line_inactive);
        progressLine2.setBackgroundResource(R.drawable.progress_line_inactive);

        // Highlight current and completed steps
        if (currentStep >= 0) {
            activateStepCircle(step1Circle, step1Number, step1Label);
        }
        if (currentStep >= 1) {
            progressLine1.setBackgroundResource(R.drawable.progress_line_active);
            activateStepCircle(step2Circle, step2Number, step2Label);
        }
        if (currentStep >= 2) {
            progressLine2.setBackgroundResource(R.drawable.progress_line_active);
            activateStepCircle(step3Circle, step3Number, step3Label);
        }
    }

    private void resetStepCircle(CardView circle, TextView number, TextView label) {
        circle.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent_40));
        circle.setCardElevation(2 * getResources().getDisplayMetrics().density);
        number.setTextColor(ContextCompat.getColor(this, R.color.white));
        number.setAlpha(0.7f);
        label.setAlpha(0.7f);
    }

    private void activateStepCircle(CardView circle, TextView number, TextView label) {
        circle.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        circle.setCardElevation(4 * getResources().getDisplayMetrics().density);
        number.setTextColor(ContextCompat.getColor(this, R.color.primary_purple));
        number.setAlpha(1f);
        label.setAlpha(1f);

        // Bounce animation
        circle.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .withEndAction(() ->
                        circle.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start()
                )
                .start();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    etDOB.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date to 18 years ago
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void setupYearDropdown() {
        String[] years = {"1st Year", "2nd Year", "3rd Year", "4th Year", "Final Year"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, years);
        etYear.setAdapter(adapter);
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);
        passwordStrengthBar.setProgress(strength);

        if (strength == 0) {
            tvPasswordStrength.setText("");
        } else if (strength < 30) {
            tvPasswordStrength.setText("Weak");
            tvPasswordStrength.setTextColor(Color.RED);
            passwordStrengthBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else if (strength < 60) {
            tvPasswordStrength.setText("Medium");
            tvPasswordStrength.setTextColor(Color.parseColor("#FFA500"));
            passwordStrengthBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));
        } else {
            tvPasswordStrength.setText("Strong");
            tvPasswordStrength.setTextColor(ContextCompat.getColor(this, R.color.success_green));
            passwordStrengthBar.setProgressTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.success_green)));
        }
    }

    private int calculatePasswordStrength(String password) {
        if (password.isEmpty()) return 0;

        int strength = 0;

        // Length
        if (password.length() >= 8) strength += 25;
        if (password.length() >= 12) strength += 15;

        // Contains lowercase
        if (Pattern.compile("[a-z]").matcher(password).find()) strength += 15;

        // Contains uppercase
        if (Pattern.compile("[A-Z]").matcher(password).find()) strength += 15;

        // Contains digit
        if (Pattern.compile("[0-9]").matcher(password).find()) strength += 15;

        // Contains special character
        if (Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) strength += 15;

        return Math.min(strength, 100);
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void animateEntrance() {
        // Initially hide
        signupCard.setAlpha(0f);
        signupCard.setTranslationY(50f);

        // Animate in
        new Handler().postDelayed(() -> {
            signupCard.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }, 200);
    }

    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX",
                0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }

    private void animateClick(View view) {
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() ->
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
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

    @Override
    public void onBackPressed() {
        if (currentStep > 0) {
            goToPreviousStep();
        } else {
            super.onBackPressed();
        }
    }
}