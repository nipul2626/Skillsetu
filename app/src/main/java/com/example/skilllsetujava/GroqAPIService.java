package com.example.skilllsetujava;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 🏆 ULTRA-STRICT Groq API Service
 *
 * Features:
 * - MAXIMUM strictness in answer validation
 * - Detects repeated/similar answers across questions
 * - No mercy for low-quality responses
 * - GUARANTEED question analysis generation
 */
public class GroqAPIService {
    private static final String TAG = "GroqAPI";
    private static final String GROQ_API_KEY = "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";
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.1-8b-instant";
    private static final int MAX_RETRIES = 2;

    private ExecutorService executor;
    private Context context;
    private int apiCallCount = 0;

    public GroqAPIService(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * 🎯 Generate ALL 10 questions
     */
    public void generateAllQuestions(String jobRole, String interviewType, BulkQuestionCallback callback) {
        Log.d(TAG, "🚀 Generating questions...");

        if (!isValidApiKey()) {
            callback.onError("Invalid API Key");
            return;
        }

        executor.execute(() -> {
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                attempt++;
                try {
                    String prompt = buildNationalLevelQuestionPrompt(jobRole, interviewType);
                    String response = makeGroqRequest(prompt, 4096);

                    if (response != null) {
                        List<Question> questions = parseBulkQuestions(response);
                        if (questions.size() == 10) {
                            apiCallCount++;
                            Log.d(TAG, "✅ Questions generated! API calls: " + apiCallCount);
                            callback.onSuccess(questions);
                            return;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "❌ Attempt " + attempt + " failed", e);
                    if (attempt >= MAX_RETRIES) {
                        callback.onError("Failed after " + MAX_RETRIES + " attempts");
                        return;
                    }
                    try { Thread.sleep(1000); } catch (InterruptedException ie) {}
                }
            }
        });
    }

    /**
     * 🆕 Generate follow-up question
     */
    public void generateFollowUp(String jobRole, String interviewType, String originalQuestion,
                                 String studentAnswer, FollowUpQuestionCallback callback) {
        Log.d(TAG, "🔍 Generating follow-up...");

        if (!isValidApiKey()) {
            callback.onError("Invalid API Key");
            return;
        }

        executor.execute(() -> {
            try {
                String prompt = buildFollowUpPrompt(jobRole, interviewType, originalQuestion, studentAnswer);
                String response = makeGroqRequest(prompt, 150);

                if (response != null) {
                    String followUp = cleanFollowUp(response);
                    if (followUp.length() > 10 && followUp.length() < 300) {
                        apiCallCount++;
                        callback.onSuccess(followUp);
                        return;
                    }
                }
                callback.onError("Invalid follow-up");
            } catch (Exception e) {
                Log.e(TAG, "❌ Follow-up failed", e);
                callback.onError("Follow-up failed");
            }
        });
    }

    /**
     * 🏆 ULTRA-STRICT NATIONAL LEVEL EVALUATION
     */
    public void evaluateAndGenerateRoadmap(String jobRole, String interviewType,
                                           List<QAPair> qaHistory, CombinedEvaluationCallback callback) {
        Log.d(TAG, "🎯 Starting ULTRA-STRICT evaluation...");

        if (!isValidApiKey()) {
            callback.onError("Invalid API Key");
            return;
        }

        executor.execute(() -> {
            int attempt = 0;
            while (attempt < MAX_RETRIES) {
                attempt++;
                try {
                    String prompt = buildUltraStrictEvaluationPrompt(jobRole, interviewType, qaHistory);
                    String response = makeGroqRequest(prompt, 8192);

                    if (response != null) {
                        CombinedResult result = parseCombinedResult(response);
                        if (result != null && result.evaluation != null &&
                                result.evaluation.questionAnalysis != null &&
                                !result.evaluation.questionAnalysis.isEmpty()) {
                            apiCallCount++;
                            Log.d(TAG, "✅ ULTRA-STRICT evaluation complete! API calls: " + apiCallCount);
                            callback.onSuccess(result);
                            return;
                        } else {
                            Log.w(TAG, "⚠️ Question analysis missing, retrying...");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "❌ Evaluation attempt " + attempt + " failed", e);
                    if (attempt >= MAX_RETRIES) {
                        callback.onError("Evaluation failed after " + MAX_RETRIES + " attempts");
                        return;
                    }
                    try { Thread.sleep(1500); } catch (InterruptedException ie) {}
                }
            }
        });
    }

    // ==================== ULTRA-STRICT PROMPT BUILDERS ====================

    private String buildNationalLevelQuestionPrompt(String jobRole, String interviewType) {
        return String.format(
                "You are the Chief Technical Officer of a Fortune 500 tech company conducting a %s interview for a %s position.\n\n" +

                        "TASK: Generate EXACTLY 10 interview questions that meet Fortune 500 company standards.\n\n" +

                        "===== STRICT REQUIREMENTS =====\n" +
                        "1. QUESTION DISTRIBUTION:\n" +
                        "   • Questions 1-3: Foundation level (test basic understanding)\n" +
                        "   • Questions 4-7: Intermediate (real-world scenarios, trade-offs)\n" +
                        "   • Questions 8-10: Advanced (system design, optimization, edge cases)\n\n" +

                        "2. QUESTION TYPES (randomly distributed):\n" +
                        "   • MCQ (All Correct): Present 4 valid solutions, candidate picks one and explains why\n" +
                        "   • MCQ (Proper): 1 correct answer, 3 wrong options - MUST include explanations for ALL\n" +
                        "   • Open-ended: Requires detailed technical explanation\n\n" +

                        "3. FOR MCQ PROPER QUESTIONS - CRITICAL:\n" +
                        "   • You MUST provide 'correctExplanation': Why the correct answer is right (2-3 sentences)\n" +
                        "   • You MUST provide 'wrongExplanations': Array explaining why EACH wrong option is incorrect (1-2 sentences each)\n" +
                        "   • This allows instant feedback without additional AI calls\n\n" +

                        "===== OUTPUT FORMAT =====\n" +
                        "Return ONLY a valid JSON object:\n" +
                        "{\n" +
                        "  \"questions\": [\n" +
                        "    {\n" +
                        "      \"type\": \"mcq_proper\",\n" +
                        "      \"question\": \"Clear, specific question text\",\n" +
                        "      \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n" +
                        "      \"correctIndex\": 0,\n" +
                        "      \"correctExplanation\": \"This is correct because... It demonstrates understanding of...\",\n" +
                        "      \"wrongExplanations\": [\n" +
                        "        \"Option B is wrong because...\",\n" +
                        "        \"Option C is incorrect as...\",\n" +
                        "        \"Option D fails because...\"\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"mcq_all_correct\",\n" +
                        "      \"question\": \"Question text\",\n" +
                        "      \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n" +
                        "      \"correctIndex\": -1\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"type\": \"open_ended\",\n" +
                        "      \"question\": \"Question text\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}\n\n" +

                        "IMPORTANT:\n" +
                        "- For mcq_proper: correctExplanation and wrongExplanations are MANDATORY\n" +
                        "- For mcq_all_correct: No explanations needed\n" +
                        "- For open_ended: No options needed\n\n" +

                        "Return ONLY valid JSON, no markdown, no extra text.",

                interviewType, jobRole
        );
    }

    private String buildFollowUpPrompt(String jobRole, String interviewType,
                                       String originalQuestion, String studentAnswer) {
        return String.format(
                "You're interviewing a candidate for %s (%s). They just answered:\n\n" +
                        "Q: %s\n" +
                        "A: %s\n\n" +

                        "Generate ONE follow-up question (10-20 words) that:\n" +
                        "1. Digs deeper into their answer\n" +
                        "2. Tests understanding vs memorization\n" +
                        "3. Explores edge cases or trade-offs\n\n" +

                        "Return ONLY the question, nothing else.",

                jobRole, interviewType, originalQuestion, truncateAnswer(studentAnswer, 200)
        );
    }

    /**
     * 🏆 Build ULTRA-STRICT evaluation prompt
     */
    private String buildUltraStrictEvaluationPrompt(String jobRole, String interviewType,
                                                    List<QAPair> qaHistory) {
        StringBuilder prompt = new StringBuilder();

        // Pre-validate answers and detect patterns
        Map<String, Integer> answerSimilarity = detectAnswerSimilarity(qaHistory);
        int lowQualityCount = countLowQualityAnswers(qaHistory);

        prompt.append(String.format(
                "You are Dr. Priya Sharma, a Senior Technical Interview Coach at Google.\n\n" +

                        "CONTEXT:\n" +
                        "You just conducted a %s interview for a %s position.\n" +
                        "⚠️ PRE-ANALYSIS DETECTED:\n" +
                        "- %d answers appear to be low quality or repeated\n" +
                        "- Answer similarity detected: %s\n\n" +

                        "YOUR TASK:\n" +
                        "Evaluate with MAXIMUM STRICTNESS. This is a real job interview, not a practice test.\n\n" +

                        "===== ULTRA-STRICT SCORING RULES =====\n\n" +

                        "AUTOMATIC SCORE REDUCTIONS:\n" +
                        "• Same/similar answer repeated = 0 for ALL repeated instances\n" +
                        "• Gibberish (asdf, qwerty, random text) = 0\n" +
                        "• Copy-pasted question = 0\n" +
                        "• Vague answer (<30 words) = Maximum 3.0\n" +
                        "• Generic answer (no specifics) = Maximum 5.0\n" +
                        "• Missing key concepts = -2.0 penalty\n" +
                        "• No examples when needed = -1.0 penalty\n\n" +

                        "SCORING SCALE (0-10 per question):\n" +
                        "• 9-10: EXCEPTIONAL - Covers everything + edge cases + real examples + best practices\n" +
                        "• 7-8: STRONG - Solid understanding with minor gaps\n" +
                        "• 5-6: ADEQUATE - Basic knowledge, lacks depth or examples\n" +
                        "• 3-4: WEAK - Significant gaps, surface-level only\n" +
                        "• 0-2: UNACCEPTABLE - Wrong/copied/gibberish/repeated\n\n" +

                        "OVERALL SCORE CALCULATION:\n" +
                        "• If >3 questions scored 0-4: Overall CANNOT exceed 5.0\n" +
                        "• If >5 questions scored 0-4: Overall CANNOT exceed 3.0\n" +
                        "• If answer repetition detected: Reduce overall by 2.0\n" +
                        "• If mostly generic answers: Reduce overall by 1.5\n\n" +

                        "===== INTERVIEW TRANSCRIPT =====\n",

                interviewType, jobRole, lowQualityCount, answerSimilarity.toString()
        ));

        // Add Q&A pairs with strict flags
        for (int i = 0; i < qaHistory.size(); i++) {
            QAPair qa = qaHistory.get(i);

            boolean isValid = isAnswerValid(qa.answer);
            boolean isCopied = isCopiedQuestion(qa.question, qa.answer);
            boolean isGibberish = isGibberishAnswer(qa.answer);
            boolean isRepeated = isAnswerRepeated(qa.answer, qaHistory, i);
            boolean isVague = qa.answer.split("\\s+").length < 30;

            prompt.append(String.format("\n--- QUESTION %d ---\n", i + 1));
            prompt.append("Q: ").append(qa.question).append("\n");

            if (isGibberish) {
                prompt.append(String.format("A: [GIBBERISH] '%s'\n", truncateAnswer(qa.answer, 100)));
                prompt.append("🚫 FLAG: Random text entered - AUTOMATIC 0\n");
            } else if (isCopied) {
                prompt.append(String.format("A: [COPIED QUESTION] '%s'\n", truncateAnswer(qa.answer, 150)));
                prompt.append("🚫 FLAG: Question copied, not answered - AUTOMATIC 0\n");
            } else if (isRepeated) {
                prompt.append(String.format("A: [REPEATED ANSWER] '%s'\n", truncateAnswer(qa.answer, 200)));
                prompt.append("🚫 FLAG: Same answer as previous question - AUTOMATIC 0\n");
            } else if (!isValid) {
                prompt.append(String.format("A: [INVALID] '%s'\n", truncateAnswer(qa.answer, 100)));
                prompt.append("⚠️ FLAG: Low quality - Maximum score 3.0\n");
            } else if (isVague) {
                prompt.append("A: ").append(truncateAnswer(qa.answer, 500)).append("\n");
                prompt.append("⚠️ FLAG: Too short (<30 words) - Maximum score 3.0\n");
            } else {
                prompt.append("A: ").append(truncateAnswer(qa.answer, 500)).append("\n");
            }
        }

        prompt.append("\n\n===== REQUIRED JSON OUTPUT =====\n\n");
        prompt.append(
                "You MUST return EXACTLY this JSON structure (NO EXCEPTIONS):\n\n" +
                        "{\n" +
                        "  \"evaluation\": {\n" +
                        "    \"overallScore\": 4.2,\n" +
                        "    \"scoreBreakdown\": {\n" +
                        "      \"technicalKnowledge\": 4.5,\n" +
                        "      \"problemSolving\": 3.8,\n" +
                        "      \"communication\": 4.9,\n" +
                        "      \"depthOfUnderstanding\": 3.5\n" +
                        "    },\n" +
                        "    \"questionAnalysis\": [\n" +
                        "      {\n" +
                        "        \"questionNumber\": 1,\n" +
                        "        \"score\": 8.5,\n" +
                        "        \"whatYouAnswered\": \"1 sentence summary of their actual answer\",\n" +
                        "        \"whatWasGood\": \"Specific strengths (or 'Nothing - answer was invalid/copied/repeated')\",\n" +
                        "        \"whatWasMissing\": \"Specific gaps (or 'Everything - no valid answer provided')\",\n" +
                        "        \"idealAnswer\": \"2-3 specific points a 10/10 answer must include\"\n" +
                        "      }\n" +
                        "      ... INCLUDE ALL 10 QUESTIONS - THIS IS MANDATORY\n" +
                        "    ],\n" +
                        "    \"coachFeedback\": \"2-3 sentences ONLY. High-level summary: Overall performance + main weakness + one action item.\",\n" +
                        "    \"topStrengths\": [\n" +
                        "      \"Specific strength with question reference: 'Good explanation of X in Q3'\"\n" +
                        "    ],\n" +
                        "    \"criticalGaps\": [\n" +
                        "      \"Specific gap: 'Missed Y concept in Q5 - critical for production'\"\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  \"actionPlan\": {\n" +
                        "    \"immediateActions\": [\n" +
                        "      {\n" +
                        "        \"priority\": \"HIGH\",\n" +
                        "        \"action\": \"Study [specific topic] - you scored 0-3 on Q[X]\",\n" +
                        "        \"why\": \"This is fundamental for [job role]\",\n" +
                        "        \"resources\": [\"Specific URL or 'Search: [exact keyword]'\"]\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"weeklyGoals\": [\n" +
                        "      \"Week 1: Specific measurable goal\"\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  \"trainingPlan\": {\n" +
                        "    \"readinessScore\": 45,\n" +
                        "    \"targetScore\": 75,\n" +
                        "    \"timeToTarget\": \"6 weeks with 3hrs/day\",\n" +
                        "    \"focusAreas\": [\n" +
                        "      {\n" +
                        "        \"area\": \"Specific technical area\",\n" +
                        "        \"priority\": \"High\",\n" +
                        "        \"currentLevel\": 4,\n" +
                        "        \"targetLevel\": 8,\n" +
                        "        \"estimatedHours\": 25,\n" +
                        "        \"keyTopics\": [\"Topic 1\", \"Topic 2\", \"Topic 3\"],\n" +
                        "        \"resources\": [\n" +
                        "          {\n" +
                        "            \"type\": \"Documentation\",\n" +
                        "            \"title\": \"Actual resource name\",\n" +
                        "            \"link\": \"Real URL or 'Search: keyword'\",\n" +
                        "            \"duration\": \"X hours\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    ],\n" +
                        "    \"weeklyPlan\": [\n" +
                        "      {\n" +
                        "        \"week\": 1,\n" +
                        "        \"theme\": \"Week 1 focus\",\n" +
                        "        \"studyTime\": \"90 min/day\",\n" +
                        "        \"practiceTime\": \"30 min/day\",\n" +
                        "        \"topics\": [\"Specific topic 1\", \"Specific topic 2\"],\n" +
                        "        \"practiceProblems\": [\n" +
                        "          {\n" +
                        "            \"problem\": \"Specific coding/design problem\",\n" +
                        "            \"difficulty\": \"Easy\",\n" +
                        "            \"focusArea\": \"What this practices\"\n" +
                        "          }\n" +
                        "        ],\n" +
                        "        \"projects\": [\"Specific project with deliverable\"],\n" +
                        "        \"weekendTask\": \"Specific weekend task\"\n" +
                        "      }\n" +
                        "      ... INCLUDE ALL 4 WEEKS - THIS IS MANDATORY\n" +
                        "    ],\n" +
                        "    \"milestones\": [\n" +
                        "      {\n" +
                        "        \"week\": 1,\n" +
                        "        \"milestone\": \"Specific achievement\",\n" +
                        "        \"verification\": \"How to verify completion\"\n" +
                        "      }\n" +
                        "      ... INCLUDE 4 MILESTONES (one per week)\n" +
                        "    ]\n" +
                        "  }\n" +
                        "}\n\n" +

                        "===== CRITICAL INSTRUCTIONS =====\n\n" +
                        "1. BE BRUTALLY HONEST:\n" +
                        "   • If 5+ answers were poor, overall score MUST be 2.0-4.0\n" +
                        "   • If answers are repeated/similar, reduce score by 2.0\n" +
                        "   • No grade inflation - this hurts the student\n\n" +

                        "2. QUESTION ANALYSIS (MANDATORY FOR ALL 10):\n" +
                        "   • You MUST provide analysis for ALL 10 questions\n" +
                        "   • For invalid answers: whatWasGood = 'Nothing - answer invalid'\n" +
                        "   • For repeated answers: score = 0, flag explicitly\n" +
                        "   • Always provide idealAnswer with 2-3 specific points\n\n" +

                        "3. COACH FEEDBACK (2-3 SENTENCES MAX):\n" +
                        "   • Sentence 1: Overall impression + score justification\n" +
                        "   • Sentence 2: Main weakness/pattern observed\n" +
                        "   • Sentence 3: One immediate action to improve\n\n" +

                        "4. TRAINING PLAN (4 WEEKS MANDATORY):\n" +
                        "   • Include all 4 weeks in weeklyPlan array\n" +
                        "   • Each week needs topics, practiceProblems, projects, weekendTask\n" +
                        "   • Resources must have real URLs or 'Search: keyword'\n" +
                        "   • Milestones for all 4 weeks\n\n" +

                        "Return ONLY valid JSON. No markdown. No explanations."
        );

        return prompt.toString();
    }

    // ==================== ULTRA-STRICT VALIDATION ====================

    private Map<String, Integer> detectAnswerSimilarity(List<QAPair> qaHistory) {
        Map<String, Integer> similarity = new HashMap<>();

        for (int i = 0; i < qaHistory.size(); i++) {
            for (int j = i + 1; j < qaHistory.size(); j++) {
                double sim = calculateSimilarity(qaHistory.get(i).answer, qaHistory.get(j).answer);
                if (sim > 0.7) {
                    String key = "Q" + (i+1) + "-Q" + (j+1);
                    similarity.put(key, (int)(sim * 100));
                }
            }
        }

        return similarity;
    }

    private double calculateSimilarity(String answer1, String answer2) {
        Set<String> words1 = new HashSet<>();
        Set<String> words2 = new HashSet<>();

        for (String word : answer1.toLowerCase().split("\\s+")) {
            if (word.length() > 3) words1.add(word);
        }
        for (String word : answer2.toLowerCase().split("\\s+")) {
            if (word.length() > 3) words2.add(word);
        }

        if (words1.isEmpty() || words2.isEmpty()) return 0;

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return (double) intersection.size() / union.size();
    }

    private boolean isAnswerRepeated(String answer, List<QAPair> qaHistory, int currentIndex) {
        for (int i = 0; i < currentIndex; i++) {
            if (calculateSimilarity(answer, qaHistory.get(i).answer) > 0.7) {
                return true;
            }
        }
        return false;
    }

    private int countLowQualityAnswers(List<QAPair> qaHistory) {
        int count = 0;
        for (QAPair qa : qaHistory) {
            if (!isAnswerValid(qa.answer) ||
                    isGibberishAnswer(qa.answer) ||
                    isCopiedQuestion(qa.question, qa.answer) ||
                    qa.answer.split("\\s+").length < 20) {
                count++;
            }
        }
        return count;
    }

    private boolean isAnswerValid(String answer) {
        if (answer == null || answer.trim().isEmpty()) return false;

        String[] words = answer.trim().split("\\s+");
        if (words.length < 10) return false;

        int gibberishCount = 0;
        int codePatternCount = 0;

        for (String word : words) {
            if (word.matches(".*[{};\\(\\)\\[\\]<>=].*") ||
                    word.matches(".*\\d+.*") ||
                    word.matches("[a-zA-Z]+\\.[a-zA-Z]+")) {
                codePatternCount++;
                continue;
            }

            if (word.length() > 20 ||
                    word.matches("(.)\\1{4,}") ||
                    (!word.matches(".*[aeiouAEIOU].*") && word.length() > 5)) {
                gibberishCount++;
            }
        }

        if (codePatternCount > words.length * 0.3) return true;
        return gibberishCount < words.length * 0.3;
    }

    private boolean isCopiedQuestion(String question, String answer) {
        if (question == null || answer == null) return false;

        String qLower = question.toLowerCase().trim();
        String aLower = answer.toLowerCase().trim();

        String[] questionWords = {"what", "is", "are", "how", "why", "when", "where",
                "explain", "describe", "tell", "can", "you", "the", "a", "an"};
        for (String word : questionWords) {
            qLower = qLower.replaceAll("\\b" + word + "\\b", "").trim();
        }

        String[] qWords = qLower.split("\\s+");
        String[] aWords = aLower.split("\\s+");

        if (qWords.length < 3 || aWords.length < 3) return false;

        int matches = 0;
        for (String qWord : qWords) {
            if (qWord.length() > 3) {
                for (String aWord : aWords) {
                    if (aWord.equals(qWord) || aWord.contains(qWord) || qWord.contains(aWord)) {
                        matches++;
                        break;
                    }
                }
            }
        }

        return (matches * 100.0 / qWords.length) > 60;
    }

    private boolean isGibberishAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) return true;

        String[] words = answer.trim().split("\\s+");
        if (words.length < 10) return true;

        String lowerAnswer = answer.toLowerCase();
        String[] gibberishPatterns = {
                "asdf", "qwerty", "zxcv", "hjkl", "aaaa", "1234",
                "abcd", "test test", "hello hello", "same same"
        };

        for (String pattern : gibberishPatterns) {
            if (lowerAnswer.contains(pattern)) return true;
        }

        int repeatCount = 0;
        for (int i = 1; i < words.length; i++) {
            if (words[i].equals(words[i - 1])) {
                repeatCount++;
            }
        }

        return repeatCount > words.length * 0.3;
    }

    private String truncateAnswer(String answer, int maxChars) {
        if (answer == null || answer.length() <= maxChars) return answer;
        return answer.substring(0, maxChars) + "...";
    }

    private String cleanFollowUp(String response) {
        return response.replace("Follow-up:", "").replace("Question:", "").trim();
    }

    // ==================== HTTP REQUEST ====================

    private String makeGroqRequest(String prompt, int maxTokens) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(GROQ_API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + GROQ_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", maxTokens);

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);

            requestBody.put("messages", messages);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject messageObj = firstChoice.getJSONObject("message");
                return messageObj.getString("content");

            } else {
                Log.e(TAG, "❌ HTTP Error " + responseCode);
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Request failed", e);
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    // ==================== PARSERS ====================

    private List<Question> parseBulkQuestions(String response) {
        List<Question> questions = new ArrayList<>();
        try {
            String cleaned = response.replace("```json", "").replace("```", "").trim();
            JSONObject json = new JSONObject(cleaned);
            JSONArray questionsArray = json.getJSONArray("questions");

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject qObj = questionsArray.getJSONObject(i);
                Question question = new Question();

                question.type = qObj.getString("type");
                question.text = qObj.getString("question");

                if (qObj.has("options") && !qObj.isNull("options")) {
                    JSONArray options = qObj.getJSONArray("options");
                    question.options = new ArrayList<>();
                    for (int j = 0; j < options.length(); j++) {
                        question.options.add(options.getString(j));
                    }
                }

                if (qObj.has("correctIndex") && !qObj.isNull("correctIndex")) {
                    question.correctIndex = qObj.getInt("correctIndex");
                } else {
                    question.correctIndex = -1;
                }

                // ✅ NEW: Parse explanations for MCQ proper
                if (question.type.equals("mcq_proper")) {
                    if (qObj.has("correctExplanation")) {
                        question.correctExplanation = qObj.getString("correctExplanation");
                    }

                    if (qObj.has("wrongExplanations")) {
                        JSONArray wrongArray = qObj.getJSONArray("wrongExplanations");
                        question.wrongExplanations = new ArrayList<>();
                        for (int j = 0; j < wrongArray.length(); j++) {
                            question.wrongExplanations.add(wrongArray.getString(j));
                        }
                    }
                }

                questions.add(question);
            }

            Log.d(TAG, "✅ Parsed " + questions.size() + " questions with explanations");

        } catch (Exception e) {
            Log.e(TAG, "❌ Parse error", e);
        }
        return questions;
    }

    private CombinedResult parseCombinedResult(String response) {
        try {
            String cleaned = response.replace("```json", "").replace("```", "").trim();

            // Find JSON object bounds
            int start = cleaned.indexOf('{');
            int end = cleaned.lastIndexOf('}');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start, end + 1);
            }

            JSONObject json = new JSONObject(cleaned);

            // Parse evaluation
            JSONObject evalObj = json.getJSONObject("evaluation");
            ComprehensiveEvaluation evaluation = new ComprehensiveEvaluation();

            evaluation.overallScore = evalObj.getDouble("overallScore");

            // Parse score breakdown
            if (evalObj.has("scoreBreakdown")) {
                JSONObject breakdown = evalObj.getJSONObject("scoreBreakdown");
                evaluation.scoreBreakdown = new HashMap<>();
                evaluation.scoreBreakdown.put("technicalKnowledge", breakdown.getDouble("technicalKnowledge"));
                evaluation.scoreBreakdown.put("problemSolving", breakdown.getDouble("problemSolving"));
                evaluation.scoreBreakdown.put("communication", breakdown.getDouble("communication"));
                evaluation.scoreBreakdown.put("depthOfUnderstanding", breakdown.getDouble("depthOfUnderstanding"));
            }

            // Parse question analysis - CRITICAL
            if (evalObj.has("questionAnalysis")) {
                JSONArray analysisArray = evalObj.getJSONArray("questionAnalysis");
                evaluation.questionAnalysis = new ArrayList<>();

                Log.d(TAG, "✅ Found questionAnalysis with " + analysisArray.length() + " items");

                for (int i = 0; i < analysisArray.length(); i++) {
                    JSONObject qAnalysis = analysisArray.getJSONObject(i);
                    QuestionAnalysis analysis = new QuestionAnalysis();
                    analysis.questionNumber = qAnalysis.getInt("questionNumber");
                    analysis.score = qAnalysis.getDouble("score");
                    analysis.whatYouAnswered = qAnalysis.getString("whatYouAnswered");
                    analysis.whatWasGood = qAnalysis.getString("whatWasGood");
                    analysis.whatWasMissing = qAnalysis.getString("whatWasMissing");
                    analysis.idealAnswer = qAnalysis.getString("idealAnswer");
                    evaluation.questionAnalysis.add(analysis);
                }
            } else {
                Log.e(TAG, "❌ questionAnalysis NOT FOUND in response!");
                return null; // Force retry
            }

            evaluation.coachFeedback = evalObj.getString("coachFeedback");
            evaluation.topStrengths = parseStringArray(evalObj.getJSONArray("topStrengths"));
            evaluation.criticalGaps = parseStringArray(evalObj.getJSONArray("criticalGaps"));

            // Parse action plan
            JSONObject actionObj = json.getJSONObject("actionPlan");
            evaluation.immediateActions = parseImmediateActions(actionObj.getJSONArray("immediateActions"));
            evaluation.weeklyGoals = parseStringArray(actionObj.getJSONArray("weeklyGoals"));

            // Parse training plan
            JSONObject planObj = json.getJSONObject("trainingPlan");
            TrainingPlan plan = parseTrainingPlan(planObj);

            CombinedResult result = new CombinedResult();
            result.evaluation = evaluation;
            result.trainingPlan = plan;

            Log.d(TAG, "✅ Parsed complete result with " + evaluation.questionAnalysis.size() + " question analyses");
            return result;

        } catch (Exception e) {
            Log.e(TAG, "❌ Parse error: " + e.getMessage(), e);
            return null;
        }
    }

    private List<ImmediateAction> parseImmediateActions(JSONArray arr) throws Exception {
        List<ImmediateAction> actions = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            ImmediateAction action = new ImmediateAction();
            action.priority = obj.getString("priority");
            action.action = obj.getString("action");
            action.why = obj.getString("why");
            action.resources = parseStringArray(obj.getJSONArray("resources"));
            actions.add(action);
        }
        return actions;
    }

    private TrainingPlan parseTrainingPlan(JSONObject json) throws Exception {
        TrainingPlan plan = new TrainingPlan();

        plan.readinessScore = json.getInt("readinessScore");
        plan.targetScore = json.getInt("targetScore");
        plan.timeToTarget = json.getString("timeToTarget");

        // Parse focus areas
        JSONArray focusArr = json.getJSONArray("focusAreas");
        plan.focusAreas = new ArrayList<>();
        for (int i = 0; i < focusArr.length(); i++) {
            JSONObject fj = focusArr.getJSONObject(i);
            FocusArea area = new FocusArea();
            area.area = fj.getString("area");
            area.priority = fj.getString("priority");
            area.currentLevel = fj.getInt("currentLevel");
            area.targetLevel = fj.getInt("targetLevel");
            area.estimatedHours = fj.getInt("estimatedHours");
            area.keyTopics = parseStringArray(fj.getJSONArray("keyTopics"));
            area.resources = parseResources(fj.getJSONArray("resources"));
            plan.focusAreas.add(area);
        }

        // Parse weekly plan
        JSONArray weeksArr = json.getJSONArray("weeklyPlan");
        plan.weeklyPlan = new ArrayList<>();
        for (int i = 0; i < weeksArr.length(); i++) {
            JSONObject wj = weeksArr.getJSONObject(i);
            WeeklyPlan week = new WeeklyPlan();
            week.week = wj.getInt("week");
            week.theme = wj.getString("theme");
            week.studyTime = wj.getString("studyTime");
            week.practiceTime = wj.getString("practiceTime");
            week.topics = parseStringArray(wj.getJSONArray("topics"));
            week.practiceProblems = parsePracticeProblems(wj.getJSONArray("practiceProblems"));
            week.projects = parseStringArray(wj.getJSONArray("projects"));
            week.weekendTask = wj.getString("weekendTask");
            plan.weeklyPlan.add(week);
        }

        // Parse milestones
        JSONArray milestonesArr = json.getJSONArray("milestones");
        plan.milestones = new ArrayList<>();
        for (int i = 0; i < milestonesArr.length(); i++) {
            JSONObject mj = milestonesArr.getJSONObject(i);
            Milestone milestone = new Milestone();
            milestone.week = mj.getInt("week");
            milestone.milestone = mj.getString("milestone");
            milestone.verification = mj.getString("verification");
            plan.milestones.add(milestone);
        }

        Log.d(TAG, "✅ Parsed training plan with " + plan.weeklyPlan.size() + " weeks");

        return plan;
    }

    private List<Resource> parseResources(JSONArray arr) throws Exception {
        List<Resource> resources = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Resource resource = new Resource();
            resource.type = obj.getString("type");
            resource.title = obj.getString("title");
            resource.link = obj.getString("link");
            resource.duration = obj.getString("duration");
            resources.add(resource);
        }
        return resources;
    }

    private List<PracticeProblem> parsePracticeProblems(JSONArray arr) throws Exception {
        List<PracticeProblem> problems = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            PracticeProblem problem = new PracticeProblem();
            problem.problem = obj.getString("problem");
            problem.difficulty = obj.getString("difficulty");
            problem.focusArea = obj.getString("focusArea");
            problems.add(problem);
        }
        return problems;
    }

    private List<String> parseStringArray(JSONArray arr) throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }

    private boolean isValidApiKey() {
        return GROQ_API_KEY != null && !GROQ_API_KEY.isEmpty();
    }

    public int getApiCallCount() {
        return apiCallCount;
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // ==================== CALLBACKS ====================

    public interface BulkQuestionCallback {
        void onSuccess(List<Question> questions);
        void onError(String error);
    }

    public interface FollowUpQuestionCallback {
        void onSuccess(String followUp);
        void onError(String error);
    }

    public interface CombinedEvaluationCallback {
        void onSuccess(CombinedResult result);
        void onError(String error);
    }

    // ==================== DATA CLASSES ====================

    public static class QAPair implements java.io.Serializable {
        public String question;
        public String answer;

        public QAPair(String q, String a) {
            this.question = q;
            this.answer = a;
        }
    }

    public static class CombinedResult {
        public ComprehensiveEvaluation evaluation;
        public TrainingPlan trainingPlan;
    }

    public static class ComprehensiveEvaluation implements java.io.Serializable {
        public double overallScore;
        public Map<String, Double> scoreBreakdown;
        public List<QuestionAnalysis> questionAnalysis;
        public String coachFeedback;
        public List<String> topStrengths;
        public List<String> criticalGaps;
        public List<ImmediateAction> immediateActions;
        public List<String> weeklyGoals;
    }

    public static class QuestionAnalysis implements java.io.Serializable {
        public int questionNumber;
        public double score;
        public String whatYouAnswered;
        public String whatWasGood;
        public String whatWasMissing;
        public String idealAnswer;
    }

    public static class ImmediateAction implements java.io.Serializable {
        public String priority;
        public String action;
        public String why;
        public List<String> resources;
    }

    public static class FocusArea implements java.io.Serializable {
        public String area;
        public String priority;
        public int currentLevel;
        public int targetLevel;
        public int estimatedHours;
        public List<String> keyTopics;
        public List<Resource> resources;
    }

    public static class Resource implements java.io.Serializable {
        public String type;
        public String title;
        public String link;
        public String duration;
    }

    public static class WeeklyPlan implements java.io.Serializable {
        public int week;
        public String theme;
        public String studyTime;
        public String practiceTime;
        public List<String> topics;
        public List<PracticeProblem> practiceProblems;
        public List<String> projects;
        public String weekendTask;
    }

    public static class PracticeProblem implements java.io.Serializable {
        public String problem;
        public String difficulty;
        public String focusArea;
    }

    public static class Milestone implements java.io.Serializable {
        public int week;
        public String milestone;
        public String verification;
    }

    public static class TrainingPlan implements java.io.Serializable {
        public int readinessScore;
        public int targetScore;
        public String timeToTarget;
        public List<FocusArea> focusAreas;
        public List<WeeklyPlan> weeklyPlan;
        public List<Milestone> milestones;
    }

    public static class Question implements java.io.Serializable {
        public String type; // "open_ended", "mcq_all_correct", "mcq_proper"
        public String text;
        public List<String> options;
        public int correctIndex;

        // ✅ NEW: Pre-generated explanation (NO runtime API call needed)
        public String correctExplanation; // Why correct answer is right
        public List<String> wrongExplanations; // Why each wrong answer is wrong
    }

}