package com.example.skilllsetujava;

/**
 * ✅ FIXED: All clean text, no emoji gibberish
 * Use these constants throughout the app
 */
public class UIConstants {

    // ============ Status Indicators ============
    public static final String CHECK_MARK = "[OK]";
    public static final String CROSS_MARK = "[X]";
    public static final String ARROW_RIGHT = "→";
    public static final String BULLET = "•";
    public static final String LOADING_DOTS = "...";

    // ============ Answer Quality Messages ============
    public static final String MSG_TOO_SHORT = "Too short (min 10 words)";
    public static final String MSG_ADD_DETAILS = "Add more details";
    public static final String MSG_GOOD_ANSWER = "Good answer!";
    public static final String MSG_TOO_LONG = "Too long (max 150 words)";
    public static final String MSG_ADD_TECH_TERMS = "Add technical terms";

    // ============ User Actions ============
    public static final String MSG_SELECT_OPTION = "Please select an option";
    public static final String MSG_ANSWER_TOO_SHORT = "Answer too short. Please improve it.";
    public static final String MSG_PROVIDE_MORE_DETAILS = "Please provide more details";

    // ============ Loading Messages ============
    public static final String MSG_AI_ANALYZING = "AI is analyzing your answer...";
    public static final String MSG_AI_PREPARING = "AI is preparing your interview...\n(10-15 seconds)";
    public static final String MSG_AI_EVALUATING = "AI is evaluating your interview...";
    public static final String MSG_AI_EXPLAINING = "AI is explaining the concept...";

    // ============ Success Messages ============
    public static final String MSG_QUESTIONS_READY = "Questions ready!";
    public static final String MSG_QUICK_ANSWER_SAVED = "Quick answer saved! Now answer the follow-up";
    public static final String MSG_ANSWERS_SAVED = "Answers saved! Add details or move on";
    public static final String MSG_VOICE_CAPTURED = "Voice captured!";
    public static final String MSG_EXPLANATION_SHOWN = "Explanation shown";

    // ============ Interview States ============
    public static final String STATE_ANSWER_QUESTION = "Answer the question below";
    public static final String STATE_SPEAK_NOW = "Speak now...";
    public static final String STATE_LISTENING = "Listening...";
    public static final String STATE_CONTINUE_TYPING = "Continue typing or speaking...";

    // ============ MCQ Feedback ============
    public static final String MCQ_CORRECT = "Correct!";
    public static final String MCQ_INCORRECT = "Incorrect";
    public static final String MCQ_GREAT_CHOICE = "Great choice! You selected:";
    public static final String MCQ_EXPLAIN_CHOICE = "Now explain: Why did you choose this option? What makes it a good approach for this scenario?";

    // ============ Button Labels ============
    public static final String BTN_SUBMIT_ANSWER = "Submit Answer";
    public static final String BTN_SUBMIT_EXPLANATION = "Submit Explanation";
    public static final String BTN_SUBMIT_FOLLOW_UP = "Submit Follow-up Answer";
    public static final String BTN_NEXT_QUESTION = "Next Question →";
    public static final String BTN_CONTINUE_NEXT = "Continue to Next Question →";
    public static final String BTN_VOICE = "Voice";
    public static final String BTN_STOP_VOICE = "Stop";

    // ============ Error Messages ============
    public static final String ERR_FAILED_TO_LOAD = "Failed to load questions: ";
    public static final String ERR_EVALUATION_FAILED = "Evaluation failed. Please try again.";
    public static final String ERR_CONNECTION_ERROR = "Connection error: ";
    public static final String ERR_VOICE_ERROR = "Voice error: ";

    // ============ Dialog Messages ============
    public static final String DIALOG_EXIT_TITLE = "Exit Interview?";
    public static final String DIALOG_EXIT_MESSAGE = "Your progress will be lost. Are you sure?";
    public static final String DIALOG_EXIT_CONFIRM = "Exit";
    public static final String DIALOG_EXIT_CANCEL = "Continue";
    public static final String DIALOG_PERMISSION_TITLE = "Permission Required";
    public static final String DIALOG_PERMISSION_MESSAGE = "Grant microphone permission?";
    public static final String DIALOG_PERMISSION_GRANT = "Grant";
    public static final String DIALOG_PERMISSION_CANCEL = "Cancel";
    public static final String DIALOG_ERROR_TITLE = "Error";
    public static final String DIALOG_RETRY = "Retry";

    // ============ Interview Type Labels ============
    public static final String LABEL_RETAKE = " (Retake)";
    public static final String LABEL_INTERVIEW = " Interview";

    // ============ Hint Text ============
    public static final String HINT_TYPE_ANSWER = "Type your answer here...";
    public static final String HINT_EXPLAIN_REASONING = "Explain your reasoning...";
    public static final String HINT_EXPLAIN_CHOICE = "Explain why you chose this option...";
    public static final String HINT_EXPLAIN_UNDERSTANDING = "Explain your understanding...";

    // ============ Log Tags ============
    public static final String LOG_TAG_INTERVIEW = "Interview";
    public static final String LOG_TAG_AI = "AI";
    public static final String LOG_TAG_GROQ = "GroqAPI";
}