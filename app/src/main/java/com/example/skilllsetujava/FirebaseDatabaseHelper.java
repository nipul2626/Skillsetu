package com.example.skilllsetujava;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🗄️ Firebase Firestore Database Helper
 * Handles all database operations
 */
public class FirebaseDatabaseHelper {

    private static final String TAG = "FirebaseDBHelper";

    private FirebaseFirestore db;

    // Collection names
    private static final String USERS = "users";
    private static final String INTERVIEWS = "interviews";
    private static final String ROADMAPS = "roadmaps";

    public FirebaseDatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // ==================== USER OPERATIONS ====================

    /**
     * ✅ Create New User Profile
     */
    public void createUserProfile(String userId, UserProfile profile, DatabaseCallback callback) {
        Map<String, Object> userData = new HashMap<>();

        // Personal Info
        Map<String, Object> personalInfo = new HashMap<>();
        personalInfo.put("fullName", profile.fullName);
        personalInfo.put("email", profile.email);
        personalInfo.put("phone", profile.phone);
        personalInfo.put("dob", profile.dob);
        personalInfo.put("profileImageUrl", profile.profileImageUrl);
        personalInfo.put("role", profile.role); // "student" or "tpo"
        personalInfo.put("createdAt", FieldValue.serverTimestamp());

        // Academic Info
        Map<String, Object> academicInfo = new HashMap<>();
        academicInfo.put("college", profile.college);
        academicInfo.put("branch", profile.branch);
        academicInfo.put("year", profile.year);
        academicInfo.put("cgpa", profile.cgpa);
        academicInfo.put("updatedAt", FieldValue.serverTimestamp());

        // Stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInterviews", 0);
        stats.put("averageScore", 0.0);
        stats.put("skillsLearned", 0);
        stats.put("placementReadiness", 0);
        stats.put("lastUpdated", FieldValue.serverTimestamp());

        userData.put("personalInfo", personalInfo);
        userData.put("academicInfo", academicInfo);
        userData.put("stats", stats);

        db.collection(USERS).document(userId)
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ User profile created: " + userId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to create profile", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 📖 Get User Profile
     */
    public void getUserProfile(String userId, UserProfileCallback callback) {
        db.collection(USERS).document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        UserProfile profile = documentToUserProfile(document);
                        callback.onSuccess(profile);
                    } else {
                        callback.onError("User profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to get profile", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 🔄 Update User Profile
     */
    public void updateUserProfile(String userId, Map<String, Object> updates, DatabaseCallback callback) {
        db.collection(USERS).document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Profile updated: " + userId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to update profile", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 📊 Update User Stats
     */
    public void updateUserStats(String userId, double newScore, DatabaseCallback callback) {
        DocumentReference userRef = db.collection(USERS).document(userId);

        userRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Map<String, Object> stats = (Map<String, Object>) document.get("stats");
                if (stats == null) stats = new HashMap<>();

                int totalInterviews = ((Number) stats.getOrDefault("totalInterviews", 0)).intValue();
                double currentAverage = ((Number) stats.getOrDefault("averageScore", 0.0)).doubleValue();

                // Calculate new average
                double newAverage = ((currentAverage * totalInterviews) + newScore) / (totalInterviews + 1);

                Map<String, Object> updatedStats = new HashMap<>();
                updatedStats.put("stats.totalInterviews", totalInterviews + 1);
                updatedStats.put("stats.averageScore", newAverage);
                updatedStats.put("stats.placementReadiness", (int) (newAverage * 10));
                updatedStats.put("stats.lastUpdated", FieldValue.serverTimestamp());

                userRef.update(updatedStats)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "✅ Stats updated");
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "❌ Failed to update stats", e);
                            callback.onError(e.getMessage());
                        });
            }
        });
    }

    // ==================== INTERVIEW OPERATIONS ====================

    /**
     * 💾 Save Interview Data
     */
    public void saveInterview(String userId, InterviewData interviewData, DatabaseCallback callback) {
        String interviewId = "interview_" + System.currentTimeMillis();

        Map<String, Object> interview = new HashMap<>();
        interview.put("interviewType", interviewData.interviewType);
        interview.put("jobRole", interviewData.jobRole);
        interview.put("totalTime", interviewData.totalTime);
        interview.put("overallScore", interviewData.evaluation.overallScore);
        interview.put("timestamp", FieldValue.serverTimestamp());
        interview.put("isRetake", interviewData.isRetake);
        interview.put("aiSource", interviewData.aiSource);

        // Evaluation
        interview.put("evaluation", evaluationToMap(interviewData.evaluation));

        // Training Plan
        interview.put("trainingPlan", trainingPlanToMap(interviewData.trainingPlan));

        // QA History
        interview.put("qaHistory", qaHistoryToList(interviewData.qaHistory));

        db.collection(USERS).document(userId)
                .collection(INTERVIEWS).document(interviewId)
                .set(interview)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Interview saved: " + interviewId);

                    // Update user stats
                    updateUserStats(userId, interviewData.evaluation.overallScore,
                            new DatabaseCallback() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess();
                                }

                                @Override
                                public void onError(String error) {
                                    // Still consider interview save successful
                                    callback.onSuccess();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to save interview", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 📖 Get All User Interviews
     */
    public void getUserInterviews(String userId, InterviewListCallback callback) {
        db.collection(USERS).document(userId)
                .collection(INTERVIEWS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InterviewData> interviews = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> {
                        InterviewData interview = documentToInterviewData(doc);
                        if (interview != null) {
                            interviews.add(interview);
                        }
                    });
                    Log.d(TAG, "✅ Fetched " + interviews.size() + " interviews");
                    callback.onSuccess(interviews);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to fetch interviews", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 📖 Get Interview by ID
     */
    public void getInterview(String userId, String interviewId, InterviewCallback callback) {
        db.collection(USERS).document(userId)
                .collection(INTERVIEWS).document(interviewId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        InterviewData interview = documentToInterviewData(document);
                        callback.onSuccess(interview);
                    } else {
                        callback.onError("Interview not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to get interview", e);
                    callback.onError(e.getMessage());
                });
    }

    // ==================== ROADMAP OPERATIONS ====================

    /**
     * 💾 Save Roadmap Progress
     */
    public void saveRoadmapProgress(String userId, String roadmapId,
                                    RoadmapProgress progress, DatabaseCallback callback) {
        Map<String, Object> roadmap = new HashMap<>();
        roadmap.put("completedTasks", progress.completedTasks);
        roadmap.put("totalTasks", progress.totalTasks);
        roadmap.put("lastUpdated", FieldValue.serverTimestamp());
        roadmap.put("completedTaskIds", progress.completedTaskIds);

        db.collection(ROADMAPS).document(userId)
                .collection("user_roadmaps").document(roadmapId)
                .set(roadmap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Roadmap progress saved");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to save roadmap", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * 📖 Get Roadmap Progress
     */
    public void getRoadmapProgress(String userId, String roadmapId, RoadmapCallback callback) {
        db.collection(ROADMAPS).document(userId)
                .collection("user_roadmaps").document(roadmapId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        RoadmapProgress progress = documentToRoadmapProgress(document);
                        callback.onSuccess(progress);
                    } else {
                        // Return empty progress if not found
                        callback.onSuccess(new RoadmapProgress());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Failed to get roadmap", e);
                    callback.onError(e.getMessage());
                });
    }

    // ==================== HELPER METHODS ====================

    private UserProfile documentToUserProfile(DocumentSnapshot doc) {
        UserProfile profile = new UserProfile();

        Map<String, Object> personalInfo = (Map<String, Object>) doc.get("personalInfo");
        if (personalInfo != null) {
            profile.fullName = (String) personalInfo.get("fullName");
            profile.email = (String) personalInfo.get("email");
            profile.phone = (String) personalInfo.get("phone");
            profile.dob = (String) personalInfo.get("dob");
            profile.profileImageUrl = (String) personalInfo.get("profileImageUrl");
            profile.role = (String) personalInfo.get("role");
        }

        Map<String, Object> academicInfo = (Map<String, Object>) doc.get("academicInfo");
        if (academicInfo != null) {
            profile.college = (String) academicInfo.get("college");
            profile.branch = (String) academicInfo.get("branch");
            profile.year = (String) academicInfo.get("year");
            profile.cgpa = (String) academicInfo.get("cgpa");
        }

        Map<String, Object> stats = (Map<String, Object>) doc.get("stats");
        if (stats != null) {
            profile.totalInterviews = ((Number) stats.getOrDefault("totalInterviews", 0)).intValue();
            profile.averageScore = ((Number) stats.getOrDefault("averageScore", 0.0)).doubleValue();
            profile.skillsLearned = ((Number) stats.getOrDefault("skillsLearned", 0)).intValue();
            profile.placementReadiness = ((Number) stats.getOrDefault("placementReadiness", 0)).intValue();
        }

        return profile;
    }

    private InterviewData documentToInterviewData(DocumentSnapshot doc) {
        // Convert Firestore document to InterviewData object
        // Implementation depends on your InterviewData class structure
        return null; // Implement based on your needs
    }

    private RoadmapProgress documentToRoadmapProgress(DocumentSnapshot doc) {
        RoadmapProgress progress = new RoadmapProgress();
        progress.completedTasks = ((Number) doc.get("completedTasks")).intValue();
        progress.totalTasks = ((Number) doc.get("totalTasks")).intValue();
        progress.completedTaskIds = (List<String>) doc.get("completedTaskIds");
        return progress;
    }

    private Map<String, Object> evaluationToMap(GroqAPIService.ComprehensiveEvaluation evaluation) {
        Map<String, Object> map = new HashMap<>();
        map.put("overallScore", evaluation.overallScore);
        map.put("coachFeedback", evaluation.coachFeedback);
        map.put("topStrengths", evaluation.topStrengths);
        map.put("criticalGaps", evaluation.criticalGaps);
        // Add other fields as needed
        return map;
    }

    private Map<String, Object> trainingPlanToMap(GroqAPIService.TrainingPlan plan) {
        Map<String, Object> map = new HashMap<>();
        map.put("readinessScore", plan.readinessScore);
        map.put("timeToTarget", plan.timeToTarget);
        // Add other fields as needed
        return map;
    }

    private List<Map<String, Object>> qaHistoryToList(List<GroqAPIService.QAPair> qaHistory) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GroqAPIService.QAPair qa : qaHistory) {
            Map<String, Object> map = new HashMap<>();
            map.put("question", qa.question);
            map.put("answer", qa.answer);
            list.add(map);
        }
        return list;
    }

    // ==================== DATA CLASSES ====================

    public static class UserProfile {
        public String fullName;
        public String email;
        public String phone;
        public String dob;
        public String profileImageUrl;
        public String role; // "student" or "tpo"
        public String college;
        public String branch;
        public String year;
        public String cgpa;
        public int totalInterviews;
        public double averageScore;
        public int skillsLearned;
        public int placementReadiness;
    }

    public static class InterviewData {
        public String interviewType;
        public String jobRole;
        public String totalTime;
        public boolean isRetake;
        public String aiSource;
        public GroqAPIService.ComprehensiveEvaluation evaluation;
        public GroqAPIService.TrainingPlan trainingPlan;
        public List<GroqAPIService.QAPair> qaHistory;
    }

    public static class RoadmapProgress {
        public int completedTasks = 0;
        public int totalTasks = 0;
        public List<String> completedTaskIds = new ArrayList<>();
    }

    // ==================== CALLBACKS ====================

    public interface DatabaseCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface UserProfileCallback {
        void onSuccess(UserProfile profile);
        void onError(String error);
    }

    public interface InterviewCallback {
        void onSuccess(InterviewData interview);
        void onError(String error);
    }

    public interface InterviewListCallback {
        void onSuccess(List<InterviewData> interviews);
        void onError(String error);
    }

    public interface RoadmapCallback {
        void onSuccess(RoadmapProgress progress);
        void onError(String error);
    }
}