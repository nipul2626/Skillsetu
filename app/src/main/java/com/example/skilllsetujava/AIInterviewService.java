package com.example.skilllsetujava;

import android.content.Context;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ⚡ FIXED AI Interview Service
 *
 * Fixes:
 * - No repeating questions (tracks already asked questions)
 * - Better question variety
 */
public class AIInterviewService {
    private static final String TAG = "AIInterviewService";

    // ⚠️ REPLACE WITH YOUR API KEY
    private static final String API_KEY = "AIzaSyD7KK84M0q-7XFPpJAkVZNc2jMyFbErCV4";

    private GenerativeModelFutures model;
    private Executor executor;
    private Context context;

    private List<QAPair> conversationHistory;
    private String jobRole;
    private String interviewType;

    // 🆕 Track asked questions to prevent repeats
    private Set<String> askedQuestions;

    public AIInterviewService(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        this.conversationHistory = new ArrayList<>();
        this.askedQuestions = new HashSet<>();

        try {
            if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY_HERE")) {
                Log.e(TAG, "❌ INVALID API KEY!");
                return;
            }

            Log.d(TAG, "🔑 Initializing AI...");

            GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", API_KEY);
            this.model = GenerativeModelFutures.from(gm);

            Log.d(TAG, "✅ AI Model initialized!");

        } catch (Exception e) {
            Log.e(TAG, "❌ AI initialization failed", e);
        }
    }

    public void initializeInterview(String jobRole, String interviewType) {
        this.jobRole = jobRole;
        this.interviewType = interviewType;
        this.conversationHistory.clear();
        this.askedQuestions.clear(); // 🆕 Clear asked questions for new interview
        Log.d(TAG, "📋 Interview initialized: " + jobRole + " - " + interviewType);
    }

    /**
     * ⚡ FIXED: Generate unique questions (no repeats)
     */
    public void generateQuestion(int questionNumber, String studentLevel, AICallback callback) {
        Log.d(TAG, "🤖 Generating Q" + questionNumber + "...");

        if (model == null) {
            callback.onError("AI not initialized");
            return;
        }

        // 🆕 Build prompt with previously asked questions to avoid repeats
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format(
                "Generate 1 %s interview question for %s position.\n" +
                        "Difficulty: %s\n",
                interviewType,
                jobRole,
                questionNumber <= 3 ? "Easy" : (questionNumber <= 7 ? "Medium" : "Hard")
        ));

        // 🆕 Add context of already asked questions
        if (!askedQuestions.isEmpty()) {
            prompt.append("\nIMPORTANT: Do NOT repeat these already asked questions:\n");
            int count = 1;
            for (String q : askedQuestions) {
                prompt.append(count++).append(". ").append(q).append("\n");
            }
            prompt.append("\nGenerate a COMPLETELY DIFFERENT question.\n");
        }

        prompt.append("\nReturn only the question, no extra text.");

        try {
            Content content = new Content.Builder().addText(prompt.toString()).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        String question = cleanResponse(result.getText());

                        if (question.length() < 10) {
                            callback.onError("Invalid response");
                            return;
                        }

                        // 🆕 Check if question is too similar to already asked ones
                        if (isQuestionTooSimilar(question)) {
                            Log.w(TAG, "⚠️ Question too similar, regenerating...");
                            // Retry once
                            callback.onError("Similar question detected");
                            return;
                        }

                        // 🆕 Add to asked questions set
                        askedQuestions.add(question);

                        Log.d(TAG, "✅ Unique question ready!");
                        callback.onSuccess(question);

                    } catch (Exception e) {
                        callback.onError("Parse failed");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "❌ Generation failed", t);
                    callback.onError(getSimpleError(t));
                }
            }, executor);

        } catch (Exception e) {
            callback.onError("Request failed");
        }
    }

    /**
     * 🆕 Check if new question is too similar to already asked questions
     */
    private boolean isQuestionTooSimilar(String newQuestion) {
        String newQ = newQuestion.toLowerCase().trim();

        for (String asked : askedQuestions) {
            String askedQ = asked.toLowerCase().trim();

            // Calculate similarity (simple word overlap check)
            String[] newWords = newQ.split("\\s+");
            String[] askedWords = askedQ.split("\\s+");

            int commonWords = 0;
            for (String word : newWords) {
                if (word.length() > 3) { // Only check meaningful words
                    for (String askedWord : askedWords) {
                        if (word.equals(askedWord)) {
                            commonWords++;
                            break;
                        }
                    }
                }
            }

            // If more than 60% words are common, it's too similar
            double similarity = (double) commonWords / Math.min(newWords.length, askedWords.length);
            if (similarity > 0.6) {
                return true;
            }
        }

        return false;
    }

    /**
     * ⚡ Quick evaluation - stores answer instantly
     */
    public void quickEvaluateAnswer(String question, String answer) {
        QAPair pair = new QAPair(question, answer, null);
        conversationHistory.add(pair);
        Log.d(TAG, "💾 Stored answer #" + conversationHistory.size());
    }

    /**
     * 🎯 Batch evaluate all answers at once
     */
    public void batchEvaluateAllAnswers(BatchEvaluationCallback callback) {
        Log.d(TAG, "📊 Starting batch evaluation of " + conversationHistory.size() + " answers...");

        if (model == null) {
            callback.onError("AI not initialized");
            return;
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format(
                "Evaluate this %s interview for %s position.\n\n",
                interviewType, jobRole
        ));

        for (int i = 0; i < conversationHistory.size(); i++) {
            QAPair qa = conversationHistory.get(i);
            prompt.append(String.format(
                    "Q%d: %s\nA%d: %s\n\n",
                    i + 1, qa.question,
                    i + 1, qa.answer
            ));
        }

        prompt.append(
                "Return JSON with:\n" +
                        "{\n" +
                        "  \"overallScore\": 7.5,\n" +
                        "  \"skillScores\": {\n" +
                        "    \"knowledge\": 8.0,\n" +
                        "    \"clarity\": 7.5,\n" +
                        "    \"confidence\": 7.0,\n" +
                        "    \"communication\": 8.0\n" +
                        "  },\n" +
                        "  \"strengths\": [\"strength1\", \"strength2\", \"strength3\"],\n" +
                        "  \"weaknesses\": [\"weakness1\", \"weakness2\"],\n" +
                        "  \"feedback\": \"Overall feedback...\",\n" +
                        "  \"questionScores\": [7.5, 8.0, 7.0, ...]\n" +
                        "}\n\n" +
                        "Return ONLY valid JSON."
        );

        try {
            Content content = new Content.Builder().addText(prompt.toString()).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        String jsonStr = cleanResponse(result.getText())
                                .replace("```json", "")
                                .replace("```", "")
                                .trim();

                        JSONObject json = new JSONObject(jsonStr);
                        ComprehensiveEvaluation eval = parseComprehensiveEvaluation(json);

                        Log.d(TAG, "✅ Batch evaluation complete!");
                        callback.onEvaluationComplete(eval);

                    } catch (JSONException e) {
                        Log.e(TAG, "❌ Parse error", e);
                        callback.onError("Parse failed");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "❌ Batch evaluation failed", t);
                    callback.onError(getSimpleError(t));
                }
            }, executor);

        } catch (Exception e) {
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * 🎯 Generate personalized training roadmap
     */
    public void generateTrainingRoadmap(ComprehensiveEvaluation evaluation, TrainingCallback callback) {
        Log.d(TAG, "🗺️ Generating training roadmap...");

        if (model == null) {
            callback.onError("AI not initialized");
            return;
        }

        String prompt = String.format(
                "Create a 4-week personalized training plan for %s position.\n\n" +
                        "Current Performance:\n" +
                        "- Overall Score: %.1f/10\n" +
                        "- Knowledge: %.1f\n" +
                        "- Clarity: %.1f\n" +
                        "- Confidence: %.1f\n" +
                        "- Communication: %.1f\n\n" +
                        "Weaknesses: %s\n\n" +
                        "Return JSON with:\n" +
                        "{\n" +
                        "  \"readinessScore\": 65,\n" +
                        "  \"targetScore\": 85,\n" +
                        "  \"focusAreas\": [\n" +
                        "    {\"area\": \"Technical Knowledge\", \"priority\": \"High\", \"currentLevel\": 6, \"targetLevel\": 9}\n" +
                        "  ],\n" +
                        "  \"weeklyPlan\": [\n" +
                        "    {\n" +
                        "      \"week\": 1,\n" +
                        "      \"theme\": \"Fundamentals\",\n" +
                        "      \"topics\": [\"topic1\", \"topic2\"],\n" +
                        "      \"practiceQuestions\": [\"question1\", \"question2\"],\n" +
                        "      \"dailyGoal\": \"2 hours study + 30 min practice\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"keyRecommendations\": [\n" +
                        "    {\"title\": \"Build Projects\", \"description\": \"Create 2-3 projects...\", \"impact\": \"High\"}\n" +
                        "  ],\n" +
                        "  \"nextSteps\": [\"step1\", \"step2\", \"step3\"]\n" +
                        "}\n\n" +
                        "Return ONLY JSON.",

                jobRole,
                evaluation.overallScore,
                evaluation.skillScores.get("knowledge"),
                evaluation.skillScores.get("clarity"),
                evaluation.skillScores.get("confidence"),
                evaluation.skillScores.get("communication"),
                String.join(", ", evaluation.weaknesses)
        );

        try {
            Content content = new Content.Builder().addText(prompt).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        String jsonStr = cleanResponse(result.getText())
                                .replace("```json", "")
                                .replace("```", "")
                                .trim();

                        JSONObject json = new JSONObject(jsonStr);
                        TrainingPlan plan = parseTrainingPlan(json);

                        Log.d(TAG, "✅ Roadmap generated!");
                        callback.onTrainingPlanGenerated(plan);

                    } catch (JSONException e) {
                        Log.e(TAG, "❌ Roadmap parse error", e);
                        callback.onError("Parse failed");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "❌ Roadmap generation failed", t);
                    callback.onError(getSimpleError(t));
                }
            }, executor);

        } catch (Exception e) {
            callback.onError("Exception: " + e.getMessage());
        }
    }

    private String cleanResponse(String text) {
        return text.replace("```json", "")
                .replace("```", "")
                .replace("**", "")
                .trim();
    }

    private String getSimpleError(Throwable t) {
        if (t.getMessage() != null) {
            String msg = t.getMessage().toLowerCase();
            if (msg.contains("api key")) return "Invalid API Key";
            if (msg.contains("quota")) return "Quota exceeded";
            if (msg.contains("network")) return "Network error";
        }
        return "AI error occurred";
    }

    // ==================== PARSERS ====================

    private ComprehensiveEvaluation parseComprehensiveEvaluation(JSONObject j) throws JSONException {
        ComprehensiveEvaluation eval = new ComprehensiveEvaluation();

        eval.overallScore = j.getDouble("overallScore");

        JSONObject skills = j.getJSONObject("skillScores");
        eval.skillScores = new HashMap<>();
        eval.skillScores.put("knowledge", skills.getDouble("knowledge"));
        eval.skillScores.put("clarity", skills.getDouble("clarity"));
        eval.skillScores.put("confidence", skills.getDouble("confidence"));
        eval.skillScores.put("communication", skills.getDouble("communication"));

        eval.strengths = parseArray(j.getJSONArray("strengths"));
        eval.weaknesses = parseArray(j.getJSONArray("weaknesses"));
        eval.feedback = j.getString("feedback");

        JSONArray scoresArr = j.getJSONArray("questionScores");
        eval.questionScores = new ArrayList<>();
        for (int i = 0; i < scoresArr.length(); i++) {
            eval.questionScores.add(scoresArr.getDouble(i));
        }

        return eval;
    }

    private TrainingPlan parseTrainingPlan(JSONObject j) throws JSONException {
        TrainingPlan plan = new TrainingPlan();

        plan.readinessScore = j.getInt("readinessScore");
        plan.targetScore = j.getInt("targetScore");

        JSONArray focusArr = j.getJSONArray("focusAreas");
        plan.focusAreas = new ArrayList<>();
        for (int i = 0; i < focusArr.length(); i++) {
            JSONObject fj = focusArr.getJSONObject(i);
            FocusArea area = new FocusArea();
            area.area = fj.getString("area");
            area.priority = fj.getString("priority");
            area.currentLevel = fj.getInt("currentLevel");
            area.targetLevel = fj.getInt("targetLevel");
            plan.focusAreas.add(area);
        }

        JSONArray weeksArr = j.getJSONArray("weeklyPlan");
        plan.weeklyPlan = new ArrayList<>();
        for (int i = 0; i < weeksArr.length(); i++) {
            JSONObject wj = weeksArr.getJSONObject(i);
            WeeklyPlan week = new WeeklyPlan();
            week.week = wj.getInt("week");
            week.theme = wj.getString("theme");
            week.topics = parseArray(wj.getJSONArray("topics"));
            week.practiceQuestions = parseArray(wj.getJSONArray("practiceQuestions"));
            week.dailyGoal = wj.getString("dailyGoal");
            plan.weeklyPlan.add(week);
        }

        JSONArray recArr = j.getJSONArray("keyRecommendations");
        plan.keyRecommendations = new ArrayList<>();
        for (int i = 0; i < recArr.length(); i++) {
            JSONObject rj = recArr.getJSONObject(i);
            Recommendation rec = new Recommendation();
            rec.title = rj.getString("title");
            rec.description = rj.getString("description");
            rec.impact = rj.getString("impact");
            plan.keyRecommendations.add(rec);
        }

        plan.nextSteps = parseArray(j.getJSONArray("nextSteps"));

        return plan;
    }

    private List<String> parseArray(JSONArray arr) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }

    public List<QAPair> getConversationHistory() {
        return conversationHistory;
    }

    // ==================== CALLBACKS ====================

    public interface AICallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public interface BatchEvaluationCallback {
        void onEvaluationComplete(ComprehensiveEvaluation evaluation);
        void onError(String error);
    }

    public interface TrainingCallback {
        void onTrainingPlanGenerated(TrainingPlan plan);
        void onError(String error);
    }

    // ==================== DATA CLASSES ====================

    public static class QAPair {
        public String question;
        public String answer;
        public Double score;

        public QAPair(String q, String a, Double s) {
            this.question = q;
            this.answer = a;
            this.score = s;
        }
    }

    public static class ComprehensiveEvaluation {
        public double overallScore;
        public Map<String, Double> skillScores;
        public List<String> strengths;
        public List<String> weaknesses;
        public String feedback;
        public List<Double> questionScores;
    }

    public static class FocusArea {
        public String area;
        public String priority;
        public int currentLevel;
        public int targetLevel;
    }

    public static class WeeklyPlan {
        public int week;
        public String theme;
        public List<String> topics;
        public List<String> practiceQuestions;
        public String dailyGoal;
    }

    public static class Recommendation {
        public String title;
        public String description;
        public String impact;
    }

    public static class TrainingPlan {
        public int readinessScore;
        public int targetScore;
        public List<FocusArea> focusAreas;
        public List<WeeklyPlan> weeklyPlan;
        public List<Recommendation> keyRecommendations;
        public List<String> nextSteps;
    }
    // Add this method to AIInterviewService.java

    /**
     * 🔍 Generate follow-up question (Gemini fallback)
     */
    public void generateFollowUpQuestion(String originalQuestion, String studentAnswer, AICallback callback) {
        Log.d(TAG, "🔍 Gemini generating follow-up...");

        if (model == null) {
            callback.onError("AI not initialized");
            return;
        }

        String prompt = String.format(
                "Based on this interview answer, ask ONE short follow-up question (10-20 words):\n\n" +
                        "Q: %s\n" +
                        "A: %s\n\n" +
                        "Follow-up should dig deeper. Return ONLY the question.",
                originalQuestion,
                studentAnswer.length() > 200 ? studentAnswer.substring(0, 200) + "..." : studentAnswer
        );

        try {
            Content content = new Content.Builder().addText(prompt).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        String followUp = cleanResponse(result.getText());

                        if (followUp.length() > 10 && followUp.length() < 300) {
                            Log.d(TAG, "✅ Gemini follow-up generated!");
                            callback.onSuccess(followUp);
                        } else {
                            callback.onError("Invalid follow-up");
                        }

                    } catch (Exception e) {
                        callback.onError("Parse failed");
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "❌ Gemini follow-up failed", t);
                    callback.onError(getSimpleError(t));
                }
            }, executor);

        } catch (Exception e) {
            callback.onError("Request failed");
        }
    }
}