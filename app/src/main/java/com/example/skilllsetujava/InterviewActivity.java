package com.example.skilllsetujava;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import com.example.skilllsetujava.api.RetrofitClient;
import com.example.skilllsetujava.api.models.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * ???? ENHANCED Interview Activity
 * Fixed keyboard issue + Better MCQ feedback
 */
public class InterviewActivity extends AppCompatActivity {

    public static final String EXTRA_INTERVIEW_TYPE = "extra_interview_type";
    public static final String EXTRA_JOB_ROLE = "extra_job_role";
    public static final String EXTRA_IS_RETAKE = "extra_is_retake";

    // UI Components
    private ImageView btnBack;
    private TextView tvInterviewType, tvJobRole, tvProgress, tvTimer;
    private TextView tvQuestion, tvAIStatus, tvFollowUpQuestion;
    private TextView tvWordCount, tvAnswerQuality;
    private ProgressBar progressBar;
    private CardView questionCard, answerCard;
    private NestedScrollView mainScrollView;

    // Answer sections
    private LinearLayout quickAnswerSection, followUpSection, detailedAnswerSection;
    private EditText etQuickAnswer, etFollowUpAnswer, etDetailedAnswer;

    private MaterialButton btnSubmitAnswer, btnVoiceInput, btnAddDetails;
    private LinearLayout loadingLayout;
    private TextView tvLoadingMessage;
    private View aiTypingIndicator;

    // Data
    private String interviewType;
    private String jobRole;
    private boolean isRetake;
    private int currentQuestionNumber = 1;
    private static final int TOTAL_QUESTIONS = 10;

    private List<GroqAPIService.Question> allQuestions = new ArrayList<>();
    private List<HybridQAPair> qaHistory = new ArrayList<>();

    private String currentFollowUp = "";
    private AnswerStage currentStage = AnswerStage.QUICK_ANSWER;

    private String questionSource = "";

    // MCQ-related
    private int selectedMcqOption = -1;

    // AI Service Manager
    private AIServiceManager aiManager;

    // Voice Recognition
    private VoiceRecognitionHelper voiceHelper;
    private boolean isRecording = false;

    // Timer
    private Handler timerHandler;
    private long startTime = 0L;
    private Runnable timerRunnable;

    // Answer validation
    private static final int MIN_WORDS = 10;
    private static final int TARGET_QUICK_WORDS = 50;
    private static final int MAX_QUICK_WORDS = 150;

    private enum AnswerStage {
        QUICK_ANSWER,
        FOLLOW_UP,
        DETAILED_OPTIONAL,
        COMPLETE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        interviewType = getIntent().getStringExtra(EXTRA_INTERVIEW_TYPE);
        jobRole = getIntent().getStringExtra(EXTRA_JOB_ROLE);
        isRetake = getIntent().getBooleanExtra(EXTRA_IS_RETAKE, false);

        initViews();
        setupTimer();
        setupListeners();
        setupKeyboardHandling();

        aiManager = new AIServiceManager(this);
        aiManager.initializeInterview(jobRole, interviewType);

        voiceHelper = new VoiceRecognitionHelper(this);

        loadAllQuestions();

    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvInterviewType = findViewById(R.id.tvInterviewType);
        tvJobRole = findViewById(R.id.tvJobRole);
        tvProgress = findViewById(R.id.tvProgress);
        tvTimer = findViewById(R.id.tvTimer);
        progressBar = findViewById(R.id.progressBar);

        mainScrollView = findViewById(R.id.mainScrollView);
        questionCard = findViewById(R.id.questionCard);
        answerCard = findViewById(R.id.answerCard);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvAIStatus = findViewById(R.id.tvAIStatus);
        tvFollowUpQuestion = findViewById(R.id.tvFollowUpQuestion);
        aiTypingIndicator = findViewById(R.id.aiTypingIndicator);

        quickAnswerSection = findViewById(R.id.quickAnswerSection);
        followUpSection = findViewById(R.id.followUpSection);
        detailedAnswerSection = findViewById(R.id.detailedAnswerSection);

        etQuickAnswer = findViewById(R.id.etQuickAnswer);
        etFollowUpAnswer = findViewById(R.id.etFollowUpAnswer);
        etDetailedAnswer = findViewById(R.id.etDetailedAnswer);

        tvWordCount = findViewById(R.id.tvWordCount);
        tvAnswerQuality = findViewById(R.id.tvAnswerQuality);

        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
        btnVoiceInput = findViewById(R.id.btnVoiceInput);
        btnAddDetails = findViewById(R.id.btnAddDetails);

        loadingLayout = findViewById(R.id.loadingLayout);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);

        tvInterviewType.setText(interviewType + " Interview");
        tvJobRole.setText(jobRole);

        if (isRetake) {
            tvInterviewType.setText(interviewType + " Interview (Retake)");
        }

        updateProgress();
    }

    /**
     * ???? FIX: Setup keyboard handling to prevent EditText from being covered
     */
    private void setupKeyboardHandling() {
        // Listen for focus changes on EditTexts
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> {
            if (hasFocus) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    mainScrollView.smoothScrollTo(0, v.getBottom());
                }, 300);
            }
        };

        etQuickAnswer.setOnFocusChangeListener(focusListener);
        etFollowUpAnswer.setOnFocusChangeListener(focusListener);
        etDetailedAnswer.setOnFocusChangeListener(focusListener);

        // Add global layout listener
        mainScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private int previousHeight = 0;

                    @Override
                    public void onGlobalLayout() {
                        int currentHeight = mainScrollView.getRootView().getHeight();

                        if (previousHeight != 0) {
                            int diff = previousHeight - currentHeight;

                            // Keyboard opened
                            if (diff > 200) {
                                View focusedView = getCurrentFocus();
                                if (focusedView instanceof EditText) {
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        mainScrollView.smoothScrollTo(0, focusedView.getBottom() + 100);
                                    }, 100);
                                }
                            }
                        }

                        previousHeight = currentHeight;
                    }
                }
        );
    }

    private void setupTimer() {
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = SystemClock.uptimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> showExitConfirmation());

        etQuickAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAnswerMetrics(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etFollowUpAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasContent = s.toString().trim().split("\\s+").length >= 10;
                btnSubmitAnswer.setEnabled(hasContent);
                btnSubmitAnswer.setAlpha(hasContent ? 1f : 0.6f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmitAnswer.setOnClickListener(v -> handleAnswerSubmission());
        btnVoiceInput.setOnClickListener(v -> toggleVoiceInput());
        btnAddDetails.setOnClickListener(v -> showDetailedAnswerSection());
    }

    private void updateAnswerMetrics(String text) {
        String[] words = text.trim().split("\\s+");
        int wordCount = text.trim().isEmpty() ? 0 : words.length;

        tvWordCount.setText(wordCount + " words");

        if (wordCount < MIN_WORDS) {
            tvAnswerQuality.setText("?? ? Too short (min 10 words)");
            tvAnswerQuality.setTextColor(
                    ContextCompat.getColor(this, R.color.error_red)
            );
            btnSubmitAnswer.setEnabled(false);
            btnSubmitAnswer.setAlpha(0.6f);
        } else if (wordCount < TARGET_QUICK_WORDS) {
            tvAnswerQuality.setText("???? Add more details");
            tvAnswerQuality.setTextColor(ContextCompat.getColor(this,R.color.warning_amber));
            btnSubmitAnswer.setEnabled(true);
            btnSubmitAnswer.setAlpha(1f);
        } else if (wordCount <= MAX_QUICK_WORDS) {
            boolean hasTechnicalTerms = containsTechnicalTerms(text);
            if (hasTechnicalTerms) {
                tvAnswerQuality.setText("??? Good answer!");
                tvAnswerQuality.setTextColor(
                        ContextCompat.getColor(this,R.color.success_green));
            } else {
                tvAnswerQuality.setText("???? Add technical terms");
                tvAnswerQuality.setTextColor(
                        ContextCompat.getColor(this,R.color.warning_amber));
            }
            btnSubmitAnswer.setEnabled(true);
            btnSubmitAnswer.setAlpha(1f);
        } else {
            tvAnswerQuality.setText("?? ? Too long (max 150 words)");
            tvAnswerQuality.setTextColor(
                    ContextCompat.getColor(this,R.color.error_red));
            btnSubmitAnswer.setEnabled(false);
            btnSubmitAnswer.setAlpha(0.6f);
        }
    }

    private boolean containsTechnicalTerms(String text) {
        String lower = text.toLowerCase();
        String[] keywords = {
                "function", "class", "method", "variable", "algorithm", "complexity",
                "api", "database", "query", "interface", "object", "array", "loop",
                "condition", "exception", "thread", "async", "callback", "promise",
                "component", "module", "library", "framework", "architecture",
                "pattern", "design", "implement", "optimize", "performance"
        };

        int techTermCount = 0;
        for (String keyword : keywords) {
            if (lower.contains(keyword)) {
                techTermCount++;
            }
        }

        return techTermCount >= 2;
    }

    private void handleAnswerSubmission() {
        switch (currentStage) {
            case QUICK_ANSWER:
                submitQuickAnswer();
                break;
            case FOLLOW_UP:
                submitFollowUpAnswer();
                break;
            case DETAILED_OPTIONAL:
            case COMPLETE:
                moveToNextQuestion();
                break;
        }
    }

    /**
     * ??? IMPROVED: Submit quick answer with better MCQ handling
     */
    private void submitQuickAnswer() {

        GroqAPIService.Question currentQuestion =
                allQuestions.get(currentQuestionNumber - 1);

        boolean isMcq = currentQuestion.type != null &&
                (currentQuestion.type.equals("mcq_all_correct")
                        || currentQuestion.type.equals("mcq_proper"));

        // =========================
        // MCQ FLOW
        // =========================
        if (isMcq) {

            if (selectedMcqOption == -1) {
                Toast.makeText(this, "?? ? Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedOption = currentQuestion.options.get(selectedMcqOption);
            String quickAnswer =
                    "Selected: " + (char) ('A' + selectedMcqOption) + ") " + selectedOption;

            // Save quick answer immediately
            currentFollowUp = "";
            etQuickAnswer.setText(quickAnswer);

            // ---------- PROPER MCQ ----------
            if ("mcq_proper".equals(currentQuestion.type)) {

                boolean isCorrect =
                        selectedMcqOption == currentQuestion.correctIndex;

                String correctOption =
                        currentQuestion.options.get(currentQuestion.correctIndex);

                showProperMcqFeedbackLocal(
                        currentQuestion,
                        selectedMcqOption
                );

                return;
            }

            // ---------- ALL-CORRECT MCQ ----------
            showAllCorrectMcqFeedback(selectedOption);
            return;
        }

        // =========================
        // OPEN-ENDED FLOW
        // =========================
        String quickAnswer = etQuickAnswer.getText().toString().trim();

        if (!isAnswerValid(quickAnswer)) {
            Toast.makeText(this,
                    "?? ? Answer too short or unclear. Please improve it.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Lock UI
        btnSubmitAnswer.setEnabled(false);
        btnSubmitAnswer.setAlpha(0.6f);
        etQuickAnswer.setEnabled(false);

        showLoading(true, "???? AI is analyzing your answer...");

        aiManager.generateFollowUpQuestion(
                currentQuestion.text,
                quickAnswer,
                new AIServiceManager.FollowUpCallback() {

                    @Override
                    public void onFollowUpGenerated(String followUp) {
                        runOnUiThread(() -> {
                            showLoading(false, "");

                            currentFollowUp = followUp;
                            showFollowUpSection(followUp);

                            Log.d("Interview",
                                    "??? Follow-up generated for Q" + currentQuestionNumber);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            showLoading(false, "");

                            Log.w("Interview",
                                    "?? ? Follow-up generation failed: " + error);

                            // Graceful fallback
                            currentStage = AnswerStage.COMPLETE;
                            storeCurrentAnswer();
                            showAddDetailsOption();
                        });
                    }
                }
        );
    }


    /**
     * ??? Type 2: All-Correct MCQ - NO API call
     * User just needs to explain why they chose it
     */
    private void showAllCorrectMcqFeedback(String selectedOption) {
        // Hide MCQ options
        LinearLayout mcqContainer = findViewById(R.id.mcqContainer);
        if (mcqContainer != null) {
            mcqContainer.setVisibility(View.GONE);
        }

        String feedbackMessage = "Great choice! You selected:\n" +
                selectedOption + "\n\n" +
                "Now explain: Why did you choose this option? " +
                "What makes it a good approach for this scenario?";

        tvFollowUpQuestion.setText(feedbackMessage);
        followUpSection.setVisibility(View.VISIBLE);
        followUpSection.setAlpha(0f);
        followUpSection.animate().alpha(1f).setDuration(400).start();

        currentStage = AnswerStage.FOLLOW_UP;
        btnSubmitAnswer.setText("Submit Explanation");
        btnSubmitAnswer.setEnabled(false);
        btnSubmitAnswer.setAlpha(0.6f);

        etFollowUpAnswer.requestFocus();
        etFollowUpAnswer.setHint("Explain your reasoning...");

        Log.d("Interview", "??? All-correct MCQ - No API call needed");
    }

    /**
     * ??? Type 3: Proper MCQ - ONE API call for explanation
     * AI explains why correct/incorrect in a SINGLE call
     */
    private void showProperMcqFeedbackLocal(
            GroqAPIService.Question question,
            int selectedIndex
    ) {
        LinearLayout mcqContainer = findViewById(R.id.mcqContainer);
        if (mcqContainer != null) {
            mcqContainer.setVisibility(View.GONE);
        }

        boolean isCorrect = selectedIndex == question.correctIndex;

        StringBuilder feedback = new StringBuilder();

        if (isCorrect) {
            feedback.append("? Correct!\n\n");
            feedback.append(question.correctExplanation);
        } else {
            feedback.append("? Incorrect\n\n");
            feedback.append("Correct Answer:\n");
            feedback.append(
                            (char) ('A' + question.correctIndex))
                    .append(") ")
                    .append(question.options.get(question.correctIndex))
                    .append("\n\nWhy this is correct:\n")
                    .append(question.correctExplanation)
                    .append("\n\nWhy your choice was incorrect:\n");

            int wrongExplanationIndex =
                    selectedIndex < question.correctIndex
                            ? selectedIndex
                            : selectedIndex - 1;

            feedback.append(
                    question.wrongExplanations.get(wrongExplanationIndex)
            );
        }

        tvFollowUpQuestion.setText(feedback.toString());
        followUpSection.setVisibility(View.VISIBLE);

        // ? HIDE the EditText for MCQ proper
        etFollowUpAnswer.setVisibility(View.GONE);

        followUpSection.setAlpha(0f);
        followUpSection.animate().alpha(1f).setDuration(400).start();

        // ? No follow-up for mcq_proper
        currentFollowUp = feedback.toString();
        currentStage = AnswerStage.COMPLETE;

        storeCurrentAnswer();

        btnSubmitAnswer.setText("Continue to Next Question ?");
        btnSubmitAnswer.setEnabled(true);
        btnSubmitAnswer.setAlpha(1f);

        Toast.makeText(
                this,
                "? Explanation shown (no AI used)",
                Toast.LENGTH_SHORT
        ).show();
    }



    /**
     * ??? NEW: Enhanced MCQ feedback with AI explanation
     */
    private void showEnhancedMcqFeedback(boolean isCorrect, String correctOption, String wrongOption) {
        // Hide MCQ options
        LinearLayout mcqContainer = findViewById(R.id.mcqContainer);
        if (mcqContainer != null) {
            mcqContainer.setVisibility(View.GONE);
        }

        // Generate AI explanation
        String prompt;
        if (isCorrect) {
            if (allQuestions.get(currentQuestionNumber - 1).type.equals("mcq_proper")) {
                prompt = "??? Correct! " + correctOption + "\n\n";
            } else {
                prompt = "Great choice! " + correctOption + "\n\n";
            }
            prompt += "Briefly explain (2-3 sentences) why this is the correct answer and what makes it important.";
        } else {
            prompt = "?? Not quite right.\n\n" +
                    "You selected: " + wrongOption + "\n" +
                    "Correct answer: " + correctOption + "\n\n" +
                    "Briefly explain (2-3 sentences) why the correct answer is right and what concept this tests.";
        }

        // Show loading for AI explanation
        tvFollowUpQuestion.setText(prompt);
        followUpSection.setVisibility(View.VISIBLE);
        followUpSection.setAlpha(0f);
        followUpSection.animate().alpha(1f).setDuration(400).start();

        // Request AI explanation
        showLoading(true, "???? AI is explaining the concept...");

        GroqAPIService.Question currentQuestion = allQuestions.get(currentQuestionNumber - 1);

        aiManager.generateFollowUpQuestion(
                currentQuestion.text + "\nCorrect answer: " + correctOption,
                "Explain why this is correct in 2-3 sentences",
                new AIServiceManager.FollowUpCallback() {
                    @Override
                    public void onFollowUpGenerated(String explanation) {
                        runOnUiThread(() -> {
                            showLoading(false, "");

                            // Update feedback with AI explanation
                            String finalFeedback = (isCorrect ?
                                    "??? Correct! " + correctOption :
                                    "?? Not quite. Correct: " + correctOption) +
                                    "\n\n???? Explanation:\n" + explanation +
                                    "\n\nNow, explain in your own words why this answer is correct.";

                            tvFollowUpQuestion.setText(finalFeedback);

                            currentStage = AnswerStage.FOLLOW_UP;
                            btnSubmitAnswer.setText("Submit Explanation");
                            btnSubmitAnswer.setEnabled(false);
                            btnSubmitAnswer.setAlpha(0.6f);

                            etFollowUpAnswer.requestFocus();
                            etFollowUpAnswer.setHint("Explain your understanding...");
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            showLoading(false, "");
                            // Fallback without AI explanation
                            String fallbackFeedback = (isCorrect ?
                                    "??? Correct! " + correctOption :
                                    "?? Not quite. Correct: " + correctOption) +
                                    "\n\nExplain in your own words why this answer is correct.";

                            tvFollowUpQuestion.setText(fallbackFeedback);

                            currentStage = AnswerStage.FOLLOW_UP;
                            btnSubmitAnswer.setText("Submit Explanation");
                            btnSubmitAnswer.setEnabled(false);
                            btnSubmitAnswer.setAlpha(0.6f);

                            etFollowUpAnswer.requestFocus();
                        });
                    }
                }
        );
    }

    private void showFollowUpSection(String followUp) {
        currentStage = AnswerStage.FOLLOW_UP;

        tvFollowUpQuestion.setText(followUp);
        followUpSection.setVisibility(View.VISIBLE);
        followUpSection.setAlpha(0f);
        followUpSection.animate()
                .alpha(1f)
                .setDuration(400)
                .start();

        btnSubmitAnswer.setText("Submit Follow-up Answer");
        btnSubmitAnswer.setEnabled(false);
        btnSubmitAnswer.setAlpha(0.6f);

        etFollowUpAnswer.requestFocus();

        Toast.makeText(this, "??? Quick answer saved! Now answer the follow-up", Toast.LENGTH_SHORT).show();
    }

    private void submitFollowUpAnswer() {
        String followUpAnswer = etFollowUpAnswer.getText().toString().trim();

        if (followUpAnswer.split("\\s+").length < 10) {
            Toast.makeText(this, "?? ? Please provide more details", Toast.LENGTH_SHORT).show();
            return;
        }

        currentStage = AnswerStage.COMPLETE;
        etFollowUpAnswer.setEnabled(false);

        storeCurrentAnswer();
        showAddDetailsOption();
    }

    private void showAddDetailsOption() {
        btnAddDetails.setVisibility(View.VISIBLE);
        btnSubmitAnswer.setText("Next Question ???");
        btnSubmitAnswer.setEnabled(true);
        btnSubmitAnswer.setAlpha(1f);

        Toast.makeText(this, "??? Answers saved! Add details or move on", Toast.LENGTH_SHORT).show();
    }

    private void showDetailedAnswerSection() {
        detailedAnswerSection.setVisibility(View.VISIBLE);
        detailedAnswerSection.setAlpha(0f);
        detailedAnswerSection.animate()
                .alpha(1f)
                .setDuration(400)
                .start();

        btnAddDetails.setVisibility(View.GONE);
        etDetailedAnswer.requestFocus();
    }

    private void storeCurrentAnswer() {
        GroqAPIService.Question question = allQuestions.get(currentQuestionNumber - 1);
        String questionText = question.text;

        String quickAnswer;
        if (question.type != null && (question.type.equals("mcq_all_correct") || question.type.equals("mcq_proper"))) {
            if (question.options != null && selectedMcqOption >= 0 && selectedMcqOption < question.options.size()) {
                quickAnswer = "Selected: " + (char)('A' + selectedMcqOption) + ") " + question.options.get(selectedMcqOption);
            } else {
                quickAnswer = "No option selected";
            }
        } else {
            quickAnswer = etQuickAnswer.getText().toString().trim();
        }

        String followUpAnswer = etFollowUpAnswer.getText().toString().trim();
        String detailedAnswer = etDetailedAnswer.getText().toString().trim();

        HybridQAPair qa = new HybridQAPair(
                questionText,
                quickAnswer,
                currentFollowUp,
                followUpAnswer,
                detailedAnswer
        );

        qaHistory.add(qa);

        Log.d("Interview", "??? Stored Q" + currentQuestionNumber + " with " +
                qa.getTotalWordCount() + " words");
    }

    private void moveToNextQuestion() {
        if (qaHistory.size() < currentQuestionNumber) {
            storeCurrentAnswer();
        }

        if (currentQuestionNumber < TOTAL_QUESTIONS) {
            currentQuestionNumber++;
            updateProgress();
            resetAnswerSections();
            displayCurrentQuestion();
        } else {
            finishInterview();
        }
    }

    private void resetAnswerSections() {
        currentStage = AnswerStage.QUICK_ANSWER;
        currentFollowUp = "";
        selectedMcqOption = -1;

        etQuickAnswer.setText("");
        etQuickAnswer.setEnabled(true);
        etFollowUpAnswer.setText("");
        etFollowUpAnswer.setEnabled(true);
        etDetailedAnswer.setText("");

        quickAnswerSection.setVisibility(View.VISIBLE);
        followUpSection.setVisibility(View.GONE);
        detailedAnswerSection.setVisibility(View.GONE);

        LinearLayout mcqContainer = findViewById(R.id.mcqContainer);
        if (mcqContainer != null) {
            mcqContainer.setVisibility(View.GONE);
        }

        btnAddDetails.setVisibility(View.GONE);
        btnSubmitAnswer.setText("Submit Answer");
        btnSubmitAnswer.setEnabled(false);
        btnSubmitAnswer.setAlpha(0.6f);
    }

    private boolean isAnswerValid(String answer) {
        String[] words = answer.trim().split("\\s+");
        int wordCount = words.length;

        if (wordCount < MIN_WORDS) {
            return false;
        }

        int gibberishCount = 0;
        for (String word : words) {
            if (isGibberish(word)) {
                gibberishCount++;
            }
        }

        return gibberishCount < wordCount * 0.3;
    }

    private boolean isGibberish(String word) {
        if (word.matches(".*[{};\\(\\)\\[\\]<>].*")) {
            return false;
        }

        if (word.length() > 20) {
            return true;
        }

        if (word.matches("(.)\\1{4,}") && !word.matches(".*[0-9].*")) {
            return true;
        }

        if (!word.matches(".*[aeiouAEIOU].*") && !word.matches(".*[0-9].*")) {
            return true;
        }

        return false;
    }

    private void loadAllQuestions() {
        showLoading(true, "???? AI is preparing your interview...\n(10-15 seconds)");

        aiManager.generateAllQuestions(new AIServiceManager.QuestionGenerationCallback() {
            @Override
            public void onQuestionsGenerated(List<GroqAPIService.Question> questions, String source) {
                runOnUiThread(() -> {
                    allQuestions = questions;
                    questionSource = source;

                    showLoading(false, "");
                    Toast.makeText(InterviewActivity.this,
                            "??? Questions ready! (" + source + ")",
                            Toast.LENGTH_SHORT).show();

                    startInterview();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false, "");

                    new AlertDialog.Builder(InterviewActivity.this)
                            .setTitle("?? Error")
                            .setMessage("Failed to load questions: " + error)
                            .setPositiveButton("Retry", (dialog, which) -> loadAllQuestions())
                            .setNegativeButton("Exit", (dialog, which) -> finish())
                            .show();
                });
            }
        });
    }

    private void startInterview() {
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        displayCurrentQuestion();
    }

    private void displayCurrentQuestion() {
        if (currentQuestionNumber > allQuestions.size()) {
            return;
        }

        GroqAPIService.Question question = allQuestions.get(currentQuestionNumber - 1);

        showAITyping(false);
        tvAIStatus.setText("???? Answer the question below");

        tvQuestion.setText(question.text);

        if (question.type != null && question.type.equals("open_ended")) {
            setupOpenEndedQuestion();
        } else {
            setupMcqQuestion(question);
        }

        questionCard.setAlpha(0f);
        questionCard.setVisibility(View.VISIBLE);
        questionCard.animate()
                .alpha(1f)
                .setDuration(400)
                .withEndAction(() -> {
                    answerCard.setAlpha(0f);
                    answerCard.setVisibility(View.VISIBLE);
                    answerCard.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .start();

                    btnSubmitAnswer.setVisibility(View.VISIBLE);
                })
                .start();
    }

    private void setupOpenEndedQuestion() {
        quickAnswerSection.setVisibility(View.VISIBLE);
        etQuickAnswer.requestFocus();
        selectedMcqOption = -1;
    }

    /**
     * ??? ENHANCED: Better MCQ button styling - NO GREY OUTLINE
     */
    private void setupMcqQuestion(GroqAPIService.Question question) {
        quickAnswerSection.setVisibility(View.GONE);

        if (question.options == null || question.options.isEmpty()) {
            Log.e("Interview", "MCQ question has no options!");
            setupOpenEndedQuestion();
            return;
        }

        LinearLayout mcqContainer = findViewById(R.id.mcqContainer);
        if (mcqContainer == null) {
            mcqContainer = new LinearLayout(this);
            mcqContainer.setId(R.id.mcqContainer);
            mcqContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout parent = (LinearLayout) quickAnswerSection.getParent();
            int index = parent.indexOfChild(followUpSection);
            parent.addView(mcqContainer, index);
        } else {
            mcqContainer.removeAllViews();
        }
        mcqContainer.setVisibility(View.VISIBLE);
        final LinearLayout containerFinal = mcqContainer;

        // ??? FIXED: Better button styling with NO grey outline
        for (int i = 0; i < question.options.size(); i++) {
            final int optionIndex = i;
            String optionText = question.options.get(i);

            MaterialButton optionBtn = new MaterialButton(this);
            optionBtn.setText((char)('A' + i) + ") " + optionText);
            optionBtn.setTextColor(
                    ContextCompat.getColor(this,R.color.white));
            optionBtn.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            optionBtn.setTextSize(15);

            // ??? SOLUTION: Remove all strokes and outlines
            optionBtn.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0x40FFFFFF) // Semi-transparent white
            );
            optionBtn.setStrokeWidth(0); // ??? NO stroke
            optionBtn.setStrokeColor(null); // ??? NO stroke color
            optionBtn.setCornerRadius(dpToPx(16));
            optionBtn.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                optionBtn.setElevation(0);
            }
            optionBtn.setRippleColor(android.content.res.ColorStateList.valueOf(0x40FFFFFF));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, dpToPx(12));
            optionBtn.setLayoutParams(params);

            optionBtn.setOnClickListener(v -> {
                selectedMcqOption = optionIndex;

                for (int j = 0; j < containerFinal.getChildCount(); j++) {
                    View child = containerFinal.getChildAt(j);

                    if (child instanceof MaterialButton) {
                        MaterialButton btn = (MaterialButton) child;

                        if (j == optionIndex) {
                            // ??? Selected state - vibrant cyan, NO stroke
                            btn.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(0xFF00E5CC)
                            );
                            btn.setTextColor(
                                    ContextCompat.getColor(this,R.color.white));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                btn.setElevation(dpToPx(6));
                            }
                            btn.setStrokeWidth(0); // ??? Still no stroke
                        } else {
                            // ??? Unselected state - semi-transparent, NO stroke
                            btn.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(0x40FFFFFF)
                            );
                            btn.setTextColor(
                                    ContextCompat.getColor(this,R.color.white));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                btn.setElevation(0);
                            }
                            btn.setStrokeWidth(0); // ??? Still no stroke
                        }
                    }
                }

                btnSubmitAnswer.setEnabled(true);
                btnSubmitAnswer.setAlpha(1f);
            });

            mcqContainer.addView(optionBtn);
        }

        selectedMcqOption = -1;
        btnSubmitAnswer.setEnabled(false);
        btnSubmitAnswer.setAlpha(0.6f);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void finishInterview() {
        timerHandler.removeCallbacks(timerRunnable);

        showLoading(true, "? AI is evaluating your interview...");

        // Get JWT token
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("jwt_token", "");
        Long studentId = prefs.getLong("student_id", 0);

        // Create request
        InterviewRequest request = new InterviewRequest();
        request.setStudentId(studentId);
        request.setInterviewType(interviewType);
        request.setJobRole(jobRole);
        request.setTotalTime(tvTimer.getText().toString());
        request.setIsRetake(isRetake);
        request.setQaHistory(convertToQAPairs());

        // Call backend API
        RetrofitClient.getApiService()
                .evaluateInterview(token, request)
                .enqueue(new Callback<InterviewResponse>() {
                    @Override
                    public void onResponse(Call<InterviewResponse> call, Response<InterviewResponse> response) {
                        showLoading(false, "");

                        if (response.isSuccessful() && response.body() != null) {
                            InterviewResponse result = response.body();

                            // Navigate to evaluation screen
                            Intent intent = new Intent(InterviewActivity.this, EvaluationActivity.class);
                            intent.putExtra("interview_id", result.getInterviewId());
                            intent.putExtra("overall_score", result.getOverallScore());
                            intent.putExtra("interview_type", interviewType);
                            intent.putExtra("job_role", jobRole);
                            intent.putExtra("total_time", tvTimer.getText().toString());
                            // Pass evaluation and roadmap data
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(InterviewActivity.this,
                                    "Evaluation failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<InterviewResponse> call, Throwable t) {
                        showLoading(false, "");

                        Log.e("Interview", "Evaluation error", t);
                        Toast.makeText(InterviewActivity.this,
                                "Connection error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Helper method to convert QA history
    private List<InterviewRequest.QAPair> convertToQAPairs() {
        List<InterviewRequest.QAPair> pairs = new ArrayList<>();
        for (HybridQAPair hybrid : qaHistory) {
            InterviewRequest.QAPair pair = new InterviewRequest.QAPair();
            pair.setQuestion(hybrid.question);

            // Combine all answers
            StringBuilder fullAnswer = new StringBuilder(hybrid.quickAnswer);
            if (!hybrid.followUpAnswer.isEmpty()) {
                fullAnswer.append("\n\nFollow-up: ").append(hybrid.followUpAnswer);
            }
            if (!hybrid.detailedAnswer.isEmpty()) {
                fullAnswer.append("\n\nDetails: ").append(hybrid.detailedAnswer);
            }

            pair.setAnswer(fullAnswer.toString());
            pairs.add(pair);
        }
        return pairs;
    }

    private List<GroqAPIService.QAPair> convertToStandardQA() {
        List<GroqAPIService.QAPair> standard = new ArrayList<>();

        for (HybridQAPair hybrid : qaHistory) {
            StringBuilder fullAnswer = new StringBuilder();
            fullAnswer.append(hybrid.quickAnswer);

            if (!hybrid.followUpAnswer.isEmpty()) {
                fullAnswer.append("\n\nFollow-up: ").append(hybrid.followUpAnswer);
            }

            if (!hybrid.detailedAnswer.isEmpty()) {
                fullAnswer.append("\n\nAdditional details: ").append(hybrid.detailedAnswer);
            }

            standard.add(new GroqAPIService.QAPair(
                    hybrid.question,
                    fullAnswer.toString()
            ));
        }

        return standard;
    }

    // Remaining helper methods...
    private String hybridQaToJson(List<HybridQAPair> list) {
        try {
            JSONArray array = new JSONArray();
            for (HybridQAPair qa : list) {
                JSONObject obj = new JSONObject();
                obj.put("question", qa.question);
                obj.put("quickAnswer", qa.quickAnswer);
                obj.put("followUp", qa.followUpQuestion);
                obj.put("followUpAnswer", qa.followUpAnswer);
                obj.put("detailedAnswer", qa.detailedAnswer);
                array.put(obj);
            }
            return array.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    private void updateProgress() {
        tvProgress.setText(String.format(Locale.getDefault(),
                "Question %d of %d", currentQuestionNumber, TOTAL_QUESTIONS));
        progressBar.setProgress(currentQuestionNumber);
    }

    private void showAITyping(boolean show) {
        aiTypingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            ObjectAnimator pulse = ObjectAnimator.ofFloat(aiTypingIndicator, "alpha", 1f, 0.3f, 1f);
            pulse.setDuration(1000);
            pulse.setRepeatCount(ObjectAnimator.INFINITE);
            pulse.start();
        }
    }

    private void showLoading(boolean show, String message) {
        loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            tvLoadingMessage.setText(message);
        }
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("?? Exit Interview?")
                .setMessage("Your progress will be lost. Are you sure?")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setNegativeButton("Continue", null)
                .show();
    }

    private void toggleVoiceInput() {
        if (isRecording) {
            stopVoiceInput();
        } else {
            startVoiceInput();
        }
    }

    private void startVoiceInput() {
        EditText targetField;
        if (currentStage == AnswerStage.QUICK_ANSWER) {
            targetField = etQuickAnswer;
        } else if (currentStage == AnswerStage.FOLLOW_UP) {
            targetField = etFollowUpAnswer;
        } else {
            targetField = etDetailedAnswer;
        }

        voiceHelper.startListening(targetField, new VoiceRecognitionHelper.VoiceRecognitionCallback() {
            @Override
            public void onListeningStarted() {
                runOnUiThread(() -> {
                    isRecording = true;
                    btnVoiceInput.setText("?? Stop");

                    btnVoiceInput.setBackgroundTintList(
                            ContextCompat.getColorStateList(InterviewActivity.this,R.color.error_red));
                    Toast.makeText(InterviewActivity.this, "???? Listening...", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onReadyForSpeech() {
                runOnUiThread(() -> tvAIStatus.setText("???? Speak now..."));
            }

            @Override
            public void onPartialResult(String partialText) {}

            @Override
            public void onFinalResult(String finalText) {
                runOnUiThread(() -> {
                    stopVoiceInput();
                    Toast.makeText(InterviewActivity.this, "??? Voice captured!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onListeningStopped() {
                runOnUiThread(() -> stopVoiceInput());
            }

            @Override
            public void onVolumeChanged(float volume) {}

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    stopVoiceInput();
                    Toast.makeText(InterviewActivity.this, "?? " + error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onPermissionRequired() {
                runOnUiThread(() -> {
                    new AlertDialog.Builder(InterviewActivity.this)
                            .setTitle("???? Permission Required")
                            .setMessage("Grant microphone permission?")
                            .setPositiveButton("Grant", (dialog, which) -> voiceHelper.checkPermission())
                            .setNegativeButton("Cancel", null)
                            .show();
                });
            }
        });
    }

    private void stopVoiceInput() {
        if (isRecording) {
            voiceHelper.stopListening();
            isRecording = false;
            btnVoiceInput.setText("???? Voice");
            btnVoiceInput.setBackgroundTintList(null);
            btnVoiceInput.clearAnimation();
            tvAIStatus.setText("???? Continue typing or speaking...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
        }

        if (voiceHelper != null) {
            voiceHelper.destroy();
        }

        if (aiManager != null) {
            aiManager.shutdown();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showExitConfirmation();
    }

    public static class HybridQAPair implements java.io.Serializable {
        public String question;
        public String quickAnswer;
        public String followUpQuestion;
        public String followUpAnswer;
        public String detailedAnswer;

        public HybridQAPair(String q, String qa, String fq, String fa, String da) {
            this.question = q;
            this.quickAnswer = qa;
            this.followUpQuestion = fq;
            this.followUpAnswer = fa;
            this.detailedAnswer = da;
        }

        public int getTotalWordCount() {
            String combined = quickAnswer + " " + followUpAnswer + " " + detailedAnswer;
            return combined.trim().split("\\s+").length;
        }
    }
}