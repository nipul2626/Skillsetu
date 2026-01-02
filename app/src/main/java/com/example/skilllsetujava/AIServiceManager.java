package com.example.skilllsetujava;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 🎯 AI Service Manager - National Level
 *
 * Coordinates between Groq API and Gemini with smart fallbacks
 */
public class AIServiceManager {
    private static final String TAG = "AIServiceManager";

    private Context context;
    private GroqAPIService groqService;
    private AIInterviewService geminiService;

    private String currentJobRole;
    private String currentInterviewType;

    public AIServiceManager(Context context) {
        this.context = context;
        this.groqService = new GroqAPIService(context);
        this.geminiService = new AIInterviewService(context);
    }

    public void initializeInterview(String jobRole, String interviewType) {
        this.currentJobRole = jobRole;
        this.currentInterviewType = interviewType;
        geminiService.initializeInterview(jobRole, interviewType);
        Log.d(TAG, "🎯 Interview initialized: " + jobRole + " - " + interviewType);
    }

    /**
     * Generate ALL 10 questions with smart fallback
     */
    public void generateAllQuestions(final QuestionGenerationCallback callback) {
        Log.d(TAG, "🚀 Starting question generation...");

        groqService.generateAllQuestions(
                currentJobRole,
                currentInterviewType,
                new GroqAPIService.BulkQuestionCallback() {

                    @Override
                    public void onSuccess(List<GroqAPIService.Question> questions) {
                        Log.d(TAG, "✅ Groq SUCCESS! Got all 10 questions");
                        callback.onQuestionsGenerated(questions, "Groq AI");
                    }

                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "⚠️ Groq failed: " + error);
                        Log.d(TAG, "🔄 Falling back to Gemini...");
                        tryGeminiQuestions(callback);
                    }
                }
        );
    }

    /**
     * Generate follow-up question
     */
    public void generateFollowUpQuestion(
            String originalQuestion,
            String studentAnswer,
            final FollowUpCallback callback) {

        Log.d(TAG, "🔍 Generating follow-up...");

        groqService.generateFollowUp(
                currentJobRole,
                currentInterviewType,
                originalQuestion,
                studentAnswer,
                new GroqAPIService.FollowUpQuestionCallback() {

                    @Override
                    public void onSuccess(String followUp) {
                        Log.d(TAG, "✅ Follow-up generated");
                        callback.onFollowUpGenerated(followUp);
                    }

                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "⚠️ Groq follow-up failed: " + error);
                        tryGeminiFollowUp(originalQuestion, studentAnswer, callback);
                    }
                }
        );
    }

    /**
     * Evaluate interview with national-level analysis
     */
    public void evaluateInterview(
            List<GroqAPIService.QAPair> qaHistory,
            final EvaluationCallback callback) {

        Log.d(TAG, "🎯 Starting national-level evaluation...");

        groqService.evaluateAndGenerateRoadmap(
                currentJobRole,
                currentInterviewType,
                qaHistory,
                new GroqAPIService.CombinedEvaluationCallback() {

                    @Override
                    public void onSuccess(GroqAPIService.CombinedResult result) {
                        Log.d(TAG, "✅ National-level evaluation SUCCESS!");
                        callback.onEvaluationComplete(result, "Groq AI");
                    }

                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "⚠️ Groq evaluation failed: " + error);
                        Log.d(TAG, "🔄 Falling back to Gemini...");
                        tryGeminiEvaluation(qaHistory, callback);
                    }
                }
        );
    }

    // ==================== FALLBACK METHODS ====================

    private void tryGeminiFollowUp(
            String originalQuestion,
            String studentAnswer,
            FollowUpCallback callback) {

        geminiService.generateFollowUpQuestion(
                originalQuestion,
                studentAnswer,
                new AIInterviewService.AICallback() {

                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "✅ Gemini follow-up success!");
                        callback.onFollowUpGenerated(result);
                    }

                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "⚠️ Gemini follow-up failed: " + error);
                        String genericFollowUp = generateGenericFollowUp(originalQuestion);
                        callback.onFollowUpGenerated(genericFollowUp);
                    }
                }
        );
    }

    private String generateGenericFollowUp(String originalQuestion) {
        String lower = originalQuestion.toLowerCase();

        if (lower.contains("explain") || lower.contains("what is")) {
            return "Can you provide a real-world example of this concept?";
        } else if (lower.contains("how") || lower.contains("implement")) {
            return "What edge cases or challenges would you consider?";
        } else if (lower.contains("difference") || lower.contains("compare")) {
            return "In what scenario would you choose one over the other?";
        } else if (lower.contains("why")) {
            return "What would be the consequences if we didn't follow this approach?";
        } else {
            return "Can you elaborate on the most critical aspect of your answer?";
        }
    }

    private void tryGeminiQuestions(final QuestionGenerationCallback callback) {
        final List<GroqAPIService.Question> geminiQuestions = new ArrayList<>();
        final int[] questionCount = {0};
        final boolean[] hasFailed = {false};

        for (int i = 1; i <= 10; i++) {
            final int qNum = i;

            geminiService.generateQuestion(
                    qNum,
                    "Intermediate",
                    new AIInterviewService.AICallback() {

                        @Override
                        public void onSuccess(String questionText) {
                            if (!hasFailed[0]) {
                                GroqAPIService.Question question = new GroqAPIService.Question();
                                question.type = "open_ended";
                                question.text = questionText;
                                question.options = null;
                                question.correctIndex = -1;

                                geminiQuestions.add(question);
                                questionCount[0]++;

                                if (questionCount[0] == 10) {
                                    Log.d(TAG, "✅ Gemini SUCCESS! Got all 10 questions");
                                    callback.onQuestionsGenerated(geminiQuestions, "Gemini AI");
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            if (!hasFailed[0]) {
                                hasFailed[0] = true;
                                Log.w(TAG, "⚠️ Gemini failed: " + error);
                                Log.d(TAG, "🔄 Falling back to local database...");
                                useLocalQuestions(callback);
                            }
                        }
                    }
            );

            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }

    private void useLocalQuestions(QuestionGenerationCallback callback) {
        List<GroqAPIService.Question> localQuestions = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String questionText = QuestionDatabase.getQuestion(
                    currentJobRole,
                    currentInterviewType,
                    i
            );

            GroqAPIService.Question question = new GroqAPIService.Question();
            question.type = "open_ended";
            question.text = questionText;
            question.options = null;
            question.correctIndex = -1;

            localQuestions.add(question);
        }

        Log.d(TAG, "✅ Local database SUCCESS! Got all 10 questions");
        callback.onQuestionsGenerated(localQuestions, "Local Database");
    }

    private void tryGeminiEvaluation(
            List<GroqAPIService.QAPair> qaHistory,
            final EvaluationCallback callback) {

        // Gemini doesn't have the national-level format, so create basic evaluation
        for (GroqAPIService.QAPair qa : qaHistory) {
            geminiService.quickEvaluateAnswer(qa.question, qa.answer);
        }

        geminiService.batchEvaluateAllAnswers(
                new AIInterviewService.BatchEvaluationCallback() {

                    @Override
                    public void onEvaluationComplete(AIInterviewService.ComprehensiveEvaluation evaluation) {
                        // Convert Gemini result to national-level format
                        GroqAPIService.CombinedResult result = new GroqAPIService.CombinedResult();
                        result.evaluation = convertGeminiToNationalLevel(evaluation, qaHistory);
                        result.trainingPlan = createBasicTrainingPlan(evaluation.overallScore);

                        Log.d(TAG, "✅ Gemini evaluation converted to national level!");
                        callback.onEvaluationComplete(result, "Gemini AI");
                    }

                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "⚠️ Gemini evaluation failed: " + error);
                        useLocalEvaluation(qaHistory, callback);
                    }
                }
        );
    }

    private GroqAPIService.ComprehensiveEvaluation convertGeminiToNationalLevel(
            AIInterviewService.ComprehensiveEvaluation geminiEval,
            List<GroqAPIService.QAPair> qaHistory) {

        GroqAPIService.ComprehensiveEvaluation nationalEval =
                new GroqAPIService.ComprehensiveEvaluation();

        nationalEval.overallScore = geminiEval.overallScore;

        // Create score breakdown
        nationalEval.scoreBreakdown = new java.util.HashMap<>();
        nationalEval.scoreBreakdown.put("technicalKnowledge", geminiEval.overallScore * 0.95);
        nationalEval.scoreBreakdown.put("problemSolving", geminiEval.overallScore * 0.9);
        nationalEval.scoreBreakdown.put("communication", geminiEval.overallScore * 0.92);
        nationalEval.scoreBreakdown.put("depthOfUnderstanding", geminiEval.overallScore * 0.88);

        // Create question analysis
        nationalEval.questionAnalysis = new ArrayList<>();
        for (int i = 0; i < qaHistory.size(); i++) {
            GroqAPIService.QuestionAnalysis analysis = new GroqAPIService.QuestionAnalysis();
            analysis.questionNumber = i + 1;
            analysis.score = geminiEval.overallScore + (Math.random() * 2 - 1);
            analysis.whatYouAnswered = truncateText(qaHistory.get(i).answer, 100);
            analysis.whatWasGood = "Answered the question with relevant information";
            analysis.whatWasMissing = "Could provide more technical depth";
            analysis.idealAnswer = "Include specific examples, edge cases, and best practices";
            nationalEval.questionAnalysis.add(analysis);
        }

        nationalEval.coachFeedback = geminiEval.feedback != null ? geminiEval.feedback :
                "Good effort on the interview! Your answers showed understanding of the basics. " +
                        "To improve, focus on providing more specific examples and discussing trade-offs. " +
                        "Practice explaining complex concepts in simpler terms.";

        nationalEval.topStrengths = geminiEval.strengths != null ? geminiEval.strengths :
                new ArrayList<>();
        nationalEval.criticalGaps = geminiEval.weaknesses != null ? geminiEval.weaknesses :
                new ArrayList<>();

        // Create immediate actions
        nationalEval.immediateActions = new ArrayList<>();
        GroqAPIService.ImmediateAction action1 = new GroqAPIService.ImmediateAction();
        action1.priority = "HIGH";
        action1.action = "Review core concepts you struggled with";
        action1.why = "These foundational topics are crucial for interviews";
        action1.resources = new ArrayList<>();
        action1.resources.add("Official documentation");
        action1.resources.add("Practice coding problems");
        nationalEval.immediateActions.add(action1);

        nationalEval.weeklyGoals = new ArrayList<>();
        nationalEval.weeklyGoals.add("Week 1: Master fundamental concepts");
        nationalEval.weeklyGoals.add("Week 2: Practice real-world scenarios");
        nationalEval.weeklyGoals.add("Week 3: Build sample projects");
        nationalEval.weeklyGoals.add("Week 4: Mock interviews");

        return nationalEval;
    }

    private GroqAPIService.TrainingPlan createBasicTrainingPlan(double score) {
        GroqAPIService.TrainingPlan plan = new GroqAPIService.TrainingPlan();

        plan.readinessScore = (int)(score * 10);
        plan.targetScore = Math.min(plan.readinessScore + 20, 95);
        plan.timeToTarget = "4 weeks with 2 hours/day";

        // Focus areas
        plan.focusAreas = new ArrayList<>();
        GroqAPIService.FocusArea area1 = new GroqAPIService.FocusArea();
        area1.area = currentJobRole + " Core Concepts";
        area1.priority = "High";
        area1.currentLevel = Math.max(5, (int)(score));
        area1.targetLevel = 9;
        area1.estimatedHours = 20;
        area1.keyTopics = new ArrayList<>();
        area1.keyTopics.add("Fundamental concepts");
        area1.keyTopics.add("Best practices");
        area1.keyTopics.add("Common patterns");
        area1.resources = new ArrayList<>();

        GroqAPIService.Resource resource1 = new GroqAPIService.Resource();
        resource1.type = "Documentation";
        resource1.title = "Official documentation";
        resource1.link = "Search: " + currentJobRole + " documentation";
        resource1.duration = "5 hours";
        area1.resources.add(resource1);

        plan.focusAreas.add(area1);

        // Weekly plan
        plan.weeklyPlan = new ArrayList<>();
        for (int week = 1; week <= 4; week++) {
            GroqAPIService.WeeklyPlan weekPlan = new GroqAPIService.WeeklyPlan();
            weekPlan.week = week;
            weekPlan.theme = "Week " + week + " Focus";
            weekPlan.studyTime = "90 minutes";
            weekPlan.practiceTime = "30 minutes";
            weekPlan.topics = new ArrayList<>();
            weekPlan.topics.add("Core topic " + week);
            weekPlan.topics.add("Practice exercises");

            weekPlan.practiceProblems = new ArrayList<>();
            GroqAPIService.PracticeProblem problem = new GroqAPIService.PracticeProblem();
            problem.problem = "Practice problem for week " + week;
            problem.difficulty = week <= 2 ? "Easy" : "Medium";
            problem.focusArea = "Week " + week + " concepts";
            weekPlan.practiceProblems.add(problem);

            weekPlan.projects = new ArrayList<>();
            weekPlan.projects.add("Mini project for week " + week);
            weekPlan.weekendTask = "Complete week " + week + " review";

            plan.weeklyPlan.add(weekPlan);
        }

        // Milestones
        plan.milestones = new ArrayList<>();
        for (int week = 1; week <= 4; week++) {
            GroqAPIService.Milestone milestone = new GroqAPIService.Milestone();
            milestone.week = week;
            milestone.milestone = "Complete Week " + week + " objectives";
            milestone.verification = "Pass week " + week + " quiz";
            plan.milestones.add(milestone);
        }

        return plan;
    }

    private void useLocalEvaluation(
            List<GroqAPIService.QAPair> qaHistory,
            EvaluationCallback callback) {

        GroqAPIService.CombinedResult result = new GroqAPIService.CombinedResult();

        GroqAPIService.ComprehensiveEvaluation eval = new GroqAPIService.ComprehensiveEvaluation();
        eval.overallScore = calculateLocalScore(qaHistory);

        eval.scoreBreakdown = new java.util.HashMap<>();
        eval.scoreBreakdown.put("technicalKnowledge", eval.overallScore * 0.9);
        eval.scoreBreakdown.put("problemSolving", eval.overallScore * 0.85);
        eval.scoreBreakdown.put("communication", eval.overallScore * 0.95);
        eval.scoreBreakdown.put("depthOfUnderstanding", eval.overallScore * 0.8);

        eval.questionAnalysis = new ArrayList<>();
        for (int i = 0; i < qaHistory.size(); i++) {
            GroqAPIService.QuestionAnalysis analysis = new GroqAPIService.QuestionAnalysis();
            analysis.questionNumber = i + 1;
            analysis.score = eval.overallScore + (Math.random() * 2 - 1);
            analysis.whatYouAnswered = truncateText(qaHistory.get(i).answer, 100);
            analysis.whatWasGood = "Provided a response to the question";
            analysis.whatWasMissing = "Could add more detail and examples";
            analysis.idealAnswer = "Include specific examples and best practices";
            eval.questionAnalysis.add(analysis);
        }

        eval.coachFeedback = "Good effort completing all questions! Focus on providing more " +
                "specific examples and technical depth in your answers. Practice explaining " +
                "concepts clearly and concisely.";

        eval.topStrengths = new ArrayList<>();
        eval.topStrengths.add("Completed all questions");
        eval.topStrengths.add("Provided detailed answers");

        eval.criticalGaps = new ArrayList<>();
        eval.criticalGaps.add("Could improve technical depth");
        eval.criticalGaps.add("Practice more specific examples");

        eval.immediateActions = new ArrayList<>();
        GroqAPIService.ImmediateAction action = new GroqAPIService.ImmediateAction();
        action.priority = "HIGH";
        action.action = "Review interview questions and model answers";
        action.why = "Understanding ideal responses helps improve performance";
        action.resources = new ArrayList<>();
        action.resources.add("Interview preparation guides");
        eval.immediateActions.add(action);

        eval.weeklyGoals = new ArrayList<>();
        eval.weeklyGoals.add("Week 1: Review fundamentals");
        eval.weeklyGoals.add("Week 2: Practice coding");
        eval.weeklyGoals.add("Week 3: Build projects");
        eval.weeklyGoals.add("Week 4: Mock interviews");

        result.evaluation = eval;
        result.trainingPlan = createBasicTrainingPlan(eval.overallScore);

        Log.d(TAG, "✅ Local evaluation complete!");
        callback.onEvaluationComplete(result, "Local Evaluation");
    }

    private double calculateLocalScore(List<GroqAPIService.QAPair> qaHistory) {
        double totalScore = 0;

        for (GroqAPIService.QAPair qa : qaHistory) {
            int answerLength = qa.answer.length();

            if (answerLength < 50) {
                totalScore += 5.0;
            } else if (answerLength < 150) {
                totalScore += 7.0;
            } else if (answerLength < 300) {
                totalScore += 8.0;
            } else {
                totalScore += 9.0;
            }
        }

        return totalScore / qaHistory.size();
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    public void shutdown() {
        if (groqService != null) {
            groqService.shutdown();
        }
    }

    // ==================== CALLBACKS ====================

    public interface QuestionGenerationCallback {
        void onQuestionsGenerated(List<GroqAPIService.Question> questions, String source);
        void onError(String error);
    }

    public interface FollowUpCallback {
        void onFollowUpGenerated(String followUp);
        void onError(String error);
    }

    public interface EvaluationCallback {
        void onEvaluationComplete(GroqAPIService.CombinedResult result, String source);
        void onError(String error);
    }
}