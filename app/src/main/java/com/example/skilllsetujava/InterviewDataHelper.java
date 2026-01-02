package com.example.skilllsetujava;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 📊 TPO Interview Data Access Helper
 *
 * Use this class in your TPO Dashboard to access all student interviews
 *
 * EXAMPLE USAGE:
 * ```
 * InterviewDataHelper helper = new InterviewDataHelper(context);
 * List<InterviewRecord> allInterviews = helper.getAllInterviews();
 *
 * // Display in RecyclerView or ListView
 * for (InterviewRecord interview : allInterviews) {
 *     Log.d("TPO", interview.studentName + " scored " + interview.overallScore);
 * }
 * ```
 */
public class InterviewDataHelper {

    private SharedPreferences prefs;
    private Context context;

    public InterviewDataHelper(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("interview_history", Context.MODE_PRIVATE);
    }

    /**
     * Get all interviews from all students
     * @return List of all interview records
     */
    public List<InterviewRecord> getAllInterviews() {
        List<InterviewRecord> interviews = new ArrayList<>();

        try {
            String jsonData = prefs.getString("all_interviews", "[]");
            JSONArray array = new JSONArray(jsonData);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                InterviewRecord record = parseInterviewRecord(obj);
                interviews.add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return interviews;
    }

    /**
     * Get interviews for specific student
     * Note: You'll need to add studentId to interview data first
     */
    public List<InterviewRecord> getInterviewsByStudent(String studentId) {
        List<InterviewRecord> allInterviews = getAllInterviews();
        List<InterviewRecord> studentInterviews = new ArrayList<>();

        for (InterviewRecord interview : allInterviews) {
            if (interview.studentId != null && interview.studentId.equals(studentId)) {
                studentInterviews.add(interview);
            }
        }

        return studentInterviews;
    }

    /**
     * Get interviews by job role
     */
    public List<InterviewRecord> getInterviewsByJobRole(String jobRole) {
        List<InterviewRecord> allInterviews = getAllInterviews();
        List<InterviewRecord> filtered = new ArrayList<>();

        for (InterviewRecord interview : allInterviews) {
            if (interview.jobRole.equals(jobRole)) {
                filtered.add(interview);
            }
        }

        return filtered;
    }

    /**
     * Get interviews by type
     */
    public List<InterviewRecord> getInterviewsByType(String interviewType) {
        List<InterviewRecord> allInterviews = getAllInterviews();
        List<InterviewRecord> filtered = new ArrayList<>();

        for (InterviewRecord interview : allInterviews) {
            if (interview.interviewType.equals(interviewType)) {
                filtered.add(interview);
            }
        }

        return filtered;
    }

    /**
     * Get interviews above certain score
     */
    public List<InterviewRecord> getInterviewsAboveScore(double minScore) {
        List<InterviewRecord> allInterviews = getAllInterviews();
        List<InterviewRecord> filtered = new ArrayList<>();

        for (InterviewRecord interview : allInterviews) {
            if (interview.overallScore >= minScore) {
                filtered.add(interview);
            }
        }

        return filtered;
    }

    /**
     * Calculate average score for student
     */
    public double getStudentAverageScore(String studentId) {
        List<InterviewRecord> interviews = getInterviewsByStudent(studentId);

        if (interviews.isEmpty()) return 0.0;

        double total = 0;
        for (InterviewRecord interview : interviews) {
            total += interview.overallScore;
        }

        return total / interviews.size();
    }

    /**
     * Get student's best score
     */
    public double getStudentBestScore(String studentId) {
        List<InterviewRecord> interviews = getInterviewsByStudent(studentId);

        if (interviews.isEmpty()) return 0.0;

        double best = 0;
        for (InterviewRecord interview : interviews) {
            if (interview.overallScore > best) {
                best = interview.overallScore;
            }
        }

        return best;
    }

    /**
     * Get total interview count
     */
    public int getTotalInterviewCount() {
        return getAllInterviews().size();
    }

    /**
     * Get interview count for student
     */
    public int getStudentInterviewCount(String studentId) {
        return getInterviewsByStudent(studentId).size();
    }

    /**
     * Delete specific interview
     */
    public void deleteInterview(String interviewId) {
        try {
            String jsonData = prefs.getString("all_interviews", "[]");
            JSONArray array = new JSONArray(jsonData);
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.getString("id").equals(interviewId)) {
                    newArray.put(obj);
                }
            }

            prefs.edit().putString("all_interviews", newArray.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear all interviews (use with caution!)
     */
    public void clearAllInterviews() {
        prefs.edit().remove("all_interviews").apply();
    }

    /**
     * Export interviews as JSON string (for backup/export)
     */
    public String exportAsJson() {
        return prefs.getString("all_interviews", "[]");
    }

    // ==================== HELPER METHODS ====================

    private InterviewRecord parseInterviewRecord(JSONObject obj) throws Exception {
        InterviewRecord record = new InterviewRecord();

        record.id = obj.getString("id");
        record.timestamp = obj.getString("timestamp");
        record.jobRole = obj.getString("jobRole");
        record.interviewType = obj.getString("interviewType");
        record.totalTime = obj.getString("totalTime");
        record.overallScore = obj.getDouble("overallScore");
        record.isRetake = obj.getBoolean("isRetake");

        // Parse skill scores
        JSONObject skillScores = obj.getJSONObject("skillScores");
        record.knowledgeScore = skillScores.getDouble("knowledge");
        record.clarityScore = skillScores.getDouble("clarity");
        record.confidenceScore = skillScores.getDouble("confidence");
        record.communicationScore = skillScores.getDouble("communication");

        // Parse strengths
        JSONArray strengthsArray = obj.getJSONArray("strengths");
        record.strengths = new ArrayList<>();
        for (int i = 0; i < strengthsArray.length(); i++) {
            record.strengths.add(strengthsArray.getString(i));
        }

        // Parse weaknesses
        JSONArray weaknessesArray = obj.getJSONArray("weaknesses");
        record.weaknesses = new ArrayList<>();
        for (int i = 0; i < weaknessesArray.length(); i++) {
            record.weaknesses.add(weaknessesArray.getString(i));
        }

        record.feedback = obj.getString("feedback");

        // Parse Q&A history
        JSONArray qaArray = obj.getJSONArray("qaHistory");
        record.qaHistory = new ArrayList<>();
        for (int i = 0; i < qaArray.length(); i++) {
            JSONObject qaObj = qaArray.getJSONObject(i);
            QAPair pair = new QAPair();
            pair.question = qaObj.getString("question");
            pair.answer = qaObj.getString("answer");
            record.qaHistory.add(pair);
        }

        // Note: studentId and studentName will be null unless you add them
        record.studentId = obj.optString("studentId", null);
        record.studentName = obj.optString("studentName", null);

        return record;
    }

    // ==================== DATA CLASSES ====================

    /**
     * Interview Record - Complete data for one interview
     */
    public static class InterviewRecord {
        public String id;                    // Unique interview ID
        public String timestamp;             // When interview was taken
        public String studentId;             // Student ID (add this!)
        public String studentName;           // Student name (add this!)
        public String jobRole;               // e.g., "Android Developer"
        public String interviewType;         // e.g., "Technical"
        public String totalTime;             // e.g., "12:34"
        public double overallScore;          // 0.0 - 10.0
        public boolean isRetake;             // Was this a retake?

        // Skill scores (0.0 - 10.0 each)
        public double knowledgeScore;
        public double clarityScore;
        public double confidenceScore;
        public double communicationScore;

        // Feedback
        public List<String> strengths;
        public List<String> weaknesses;
        public String feedback;

        // Complete Q&A pairs
        public List<QAPair> qaHistory;
    }

    /**
     * Question-Answer Pair
     */
    public static class QAPair {
        public String question;
        public String answer;
    }
}

/**
 * 📊 EXAMPLE: TPO Dashboard Activity (Sample Implementation)
 *
 * Create this activity to show all interviews to TPO
 */
/*
public class TPODashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InterviewDataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpo_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        dataHelper = new InterviewDataHelper(this);

        // Get all interviews
        List<InterviewDataHelper.InterviewRecord> interviews = dataHelper.getAllInterviews();

        // Display statistics
        int totalInterviews = interviews.size();
        double averageScore = calculateAverage(interviews);

        Log.d("TPO", "Total Interviews: " + totalInterviews);
        Log.d("TPO", "Average Score: " + averageScore);

        // Setup RecyclerView with interviews
        InterviewAdapter adapter = new InterviewAdapter(interviews);
        recyclerView.setAdapter(adapter);

        // Add filters
        setupFilters();
    }

    private void setupFilters() {
        // Filter by job role
        Spinner roleSpinner = findViewById(R.id.roleSpinner);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = parent.getItemAtPosition(position).toString();
                List<InterviewDataHelper.InterviewRecord> filtered =
                    dataHelper.getInterviewsByJobRole(selectedRole);
                updateRecyclerView(filtered);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Filter by minimum score
        SeekBar scoreSeekBar = findViewById(R.id.scoreSeekBar);
        scoreSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double minScore = progress / 10.0; // 0-100 -> 0.0-10.0
                List<InterviewDataHelper.InterviewRecord> filtered =
                    dataHelper.getInterviewsAboveScore(minScore);
                updateRecyclerView(filtered);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private double calculateAverage(List<InterviewDataHelper.InterviewRecord> interviews) {
        if (interviews.isEmpty()) return 0.0;

        double total = 0;
        for (InterviewDataHelper.InterviewRecord interview : interviews) {
            total += interview.overallScore;
        }

        return total / interviews.size();
    }

    private void updateRecyclerView(List<InterviewDataHelper.InterviewRecord> interviews) {
        InterviewAdapter adapter = new InterviewAdapter(interviews);
        recyclerView.setAdapter(adapter);
    }
}
*/

/**
 * 🎯 NEXT STEPS: Add Student ID to Interviews
 *
 * In EvaluationActivity.java, modify autoSaveToHistory():
 *
 * ```java
 * // Get current student ID (from login or SharedPreferences)
 * String studentId = prefs.getString("current_student_id", "unknown");
 * String studentName = prefs.getString("current_student_name", "Unknown");
 *
 * // Add to interview data
 * interviewData.put("studentId", studentId);
 * interviewData.put("studentName", studentName);
 * ```
 *
 * Then TPO can filter by student!
 */