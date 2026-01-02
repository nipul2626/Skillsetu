package com.example.skilllsetujava;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 🎨 IMPROVED Voice Recognition Helper
 *
 * Improvements:
 * - Beautiful gradient fade-in (purple/blue)
 * - Smart capitalization (sentences, proper nouns)
 * - Better punctuation handling
 * - Smoother animations
 */
public class VoiceRecognitionHelper {
    private static final String TAG = "VoiceRecognition";
    private static final int PERMISSION_REQUEST_CODE = 200;

    private Activity activity;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private EditText targetEditText;

    private boolean isListening = false;
    private StringBuilder currentTranscript;
    private int lastWordCount = 0;

    // 🎨 Modern colors for fade-in
    private static final int COLOR_START = Color.parseColor("#A78BFA"); // Light purple
    private static final int COLOR_END = Color.parseColor("#FFFFFF");   // White

    private Handler animationHandler;
    private static final long WORD_FADE_DURATION = 250; // ms per word

    private VoiceRecognitionCallback callback;

    public VoiceRecognitionHelper(Activity activity) {
        this.activity = activity;
        this.animationHandler = new Handler(Looper.getMainLooper());
        this.currentTranscript = new StringBuilder();

        initializeSpeechRecognizer();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(activity)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);

            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);

            Log.d(TAG, "✅ Speech Recognizer initialized");
        } else {
            Log.e(TAG, "❌ Speech Recognition not available");
            Toast.makeText(activity, "Speech recognition not available", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void startListening(EditText editText, VoiceRecognitionCallback callback) {
        if (!checkPermission()) {
            callback.onPermissionRequired();
            return;
        }

        if (isListening) {
            stopListening();
            return;
        }

        this.targetEditText = editText;
        this.callback = callback;
        this.currentTranscript = new StringBuilder();
        this.lastWordCount = 0;

        // Keep existing text or start fresh
        if (targetEditText.getText().length() > 0) {
            currentTranscript.append(targetEditText.getText().toString()).append(" ");
        }

        setupRecognitionListener();

        try {
            speechRecognizer.startListening(recognizerIntent);
            isListening = true;
            callback.onListeningStarted();
            Log.d(TAG, "🎤 Started listening...");
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to start listening", e);
            callback.onError("Failed to start: " + e.getMessage());
        }
    }

    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.stopListening();
            isListening = false;

            if (callback != null) {
                callback.onListeningStopped();
            }

            Log.d(TAG, "🛑 Stopped listening");
        }
    }

    private void setupRecognitionListener() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "🎤 Ready for speech");
                if (callback != null) {
                    callback.onReadyForSpeech();
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "🗣️ Speech started");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                if (callback != null) {
                    callback.onVolumeChanged(rmsdB);
                }
            }

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "🤐 Speech ended");
            }

            @Override
            public void onError(int error) {
                String errorMessage = getErrorText(error);
                Log.e(TAG, "❌ Recognition error: " + errorMessage);

                isListening = false;

                if (callback != null) {
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null && !matches.isEmpty()) {
                    String bestMatch = matches.get(0);

                    // 🎨 Apply smart formatting
                    String processedText = applySmartFormatting(bestMatch);

                    // Append to existing transcript
                    if (currentTranscript.length() > 0 && !currentTranscript.toString().endsWith(" ")) {
                        currentTranscript.append(" ");
                    }

                    int startIndex = currentTranscript.length();
                    currentTranscript.append(processedText);

                    // 🎨 Animate new words with gradient fade
                    animateNewWordsWithGradient(currentTranscript.toString(), startIndex);

                    Log.d(TAG, "✅ Recognized: " + processedText);
                }

                isListening = false;

                if (callback != null) {
                    callback.onFinalResult(currentTranscript.toString());
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partialMatches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (partialMatches != null && !partialMatches.isEmpty()) {
                    String partialText = partialMatches.get(0);

                    // Show partial results with lighter color
                    String fullText = currentTranscript.toString();
                    if (fullText.length() > 0 && !fullText.endsWith(" ")) {
                        fullText += " ";
                    }
                    fullText += partialText;

                    animatePartialTextWithGradient(fullText);

                    if (callback != null) {
                        callback.onPartialResult(partialText);
                    }
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    /**
     * 🎨 NEW: Animate words with beautiful purple-to-white gradient fade
     */
    private void animateNewWordsWithGradient(String fullText, int startIndex) {
        if (targetEditText == null) return;

        String newText = fullText.substring(startIndex);
        String[] words = newText.split("\\s+");

        final String baseText = fullText.substring(0, startIndex);

        // Animate each word appearing one by one with gradient fade
        for (int i = 0; i < words.length; i++) {
            final int wordIndex = i;
            final String word = words[i];

            animationHandler.postDelayed(() -> {
                StringBuilder animatedText = new StringBuilder(baseText);

                // Add all words up to current
                for (int j = 0; j <= wordIndex; j++) {
                    if (animatedText.length() > 0 && !animatedText.toString().endsWith(" ")) {
                        animatedText.append(" ");
                    }
                    animatedText.append(words[j]);
                }

                // Create spannable with gradient color for current word
                SpannableString spannableString = new SpannableString(animatedText.toString());

                int currentWordStart = animatedText.length() - word.length();
                int currentWordEnd = animatedText.length();

                // Animate color from purple to white
                animateWordColorTransition(spannableString, currentWordStart, currentWordEnd);

                targetEditText.setText(spannableString);
                targetEditText.setSelection(targetEditText.getText().length());

            }, wordIndex * WORD_FADE_DURATION);
        }
    }

    /**
     * 🎨 Animate word color transition (purple → white)
     */
    private void animateWordColorTransition(SpannableString spannableString, int start, int end) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), COLOR_START, COLOR_END);
        colorAnimator.setDuration(WORD_FADE_DURATION);

        colorAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Update EditText
            activity.runOnUiThread(() -> {
                if (targetEditText != null) {
                    int selectionStart = targetEditText.getSelectionStart();
                    targetEditText.setText(spannableString);
                    if (selectionStart >= 0) {
                        targetEditText.setSelection(Math.min(selectionStart, targetEditText.getText().length()));
                    }
                }
            });
        });

        colorAnimator.start();
    }

    /**
     * 🎨 Animate partial text with lighter purple (indicating it's temporary)
     */
    private void animatePartialTextWithGradient(String text) {
        if (targetEditText == null) return;

        activity.runOnUiThread(() -> {
            SpannableString spannableString = new SpannableString(text);

            // Make partial text light purple to show it's temporary
            int partialStart = currentTranscript.length();
            if (partialStart < text.length()) {
                ForegroundColorSpan purpleSpan = new ForegroundColorSpan(Color.parseColor("#C4B5FD"));
                spannableString.setSpan(purpleSpan, partialStart, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            targetEditText.setText(spannableString);
            targetEditText.setSelection(targetEditText.getText().length());
        });
    }

    /**
     * 🎨 IMPROVED: Smart formatting with proper capitalization and punctuation
     */
    private String applySmartFormatting(String text) {
        if (text == null || text.isEmpty()) return text;

        // Replace voice commands with punctuation
        text = text.replaceAll("(?i)\\bcomma\\b", ",");
        text = text.replaceAll("(?i)\\bperiod\\b", ".");
        text = text.replaceAll("(?i)\\bquestion mark\\b", "?");
        text = text.replaceAll("(?i)\\bexclamation mark\\b", "!");
        text = text.replaceAll("(?i)\\bnew line\\b", "\n");
        text = text.replaceAll("(?i)\\bnew paragraph\\b", "\n\n");

        // Split into sentences for proper capitalization
        String[] sentences = text.split("(?<=[.!?])\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String sentence : sentences) {
            if (sentence.trim().isEmpty()) continue;

            // Capitalize first letter of sentence
            sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1);

            // Add period if sentence doesn't end with punctuation
            if (!sentence.matches(".*[.!?,]$")) {
                sentence += ".";
            }

            formatted.append(sentence).append(" ");
        }

        String result = formatted.toString().trim();

        // If no sentence structure detected, just capitalize first letter
        if (result.equals(text.trim())) {
            result = text.substring(0, 1).toUpperCase() + text.substring(1);
            if (!result.matches(".*[.!?]$")) {
                result += ".";
            }
        }

        return result;
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No speech match found";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input";
            default:
                return "Unknown error";
        }
    }

    public boolean isListening() {
        return isListening;
    }

    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }

        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }

        Log.d(TAG, "🧹 Voice Recognition cleaned up");
    }

    public interface VoiceRecognitionCallback {
        void onListeningStarted();
        void onReadyForSpeech();
        void onPartialResult(String partialText);
        void onFinalResult(String finalText);
        void onListeningStopped();
        void onVolumeChanged(float volume);
        void onError(String error);
        void onPermissionRequired();
    }
}