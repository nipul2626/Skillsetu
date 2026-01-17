package com.example.skilllsetujava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skilllsetujava.api.ApiService;
import com.example.skilllsetujava.api.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.*;

import com.example.skilllsetujava.api.models.StudentFilterRequest;

/**
 * 📊 TPO Dashboard - Complete Analytics & Student Management
 * API 23 SAFE VERSION
 */
public class TPODashboardActivity extends AppCompatActivity {

    private static final String TAG = "TPODashboard";

    // 🔹 Backend
    private ApiService apiService;
    private String authToken;
    private Long collegeId;

    // Header
    private ImageView ivBack, ivExport, ivRefresh;
    private TextView tvCollegeName, tvTPOName;

    // Stats
    private TextView tvTotalStudents, tvTotalInterviews, tvAvgScore, tvActiveRoadmaps;
    private ProgressBar pbStats1, pbStats2, pbStats3, pbStats4;

    // Filters
    private TabLayout tabFilter;
    private Spinner spinnerJobRole, spinnerScoreFilter;
    private EditText etSearchStudent;
    private MaterialButton btnApplyFilters, btnClearFilters;

    // Students
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private TextView tvNoStudents, tvStudentCount;

    // Charts
    private CardView performanceChartCard, roleDistributionCard;

    // Local data (will be replaced by backend)
    private InterviewDataHelper dataHelper;
    private List<InterviewDataHelper.InterviewRecord> allInterviews = new ArrayList<>();
    private List<StudentSummary> allStudents = new ArrayList<>();
    private List<StudentSummary> filteredStudents = new ArrayList<>();

    // Filter state
    private String currentTab = "all";
    private String selectedJobRole = "All Roles";
    private String selectedScoreFilter = "All Scores";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpodashboard);

        // ✅ Retrofit init
        apiService = RetrofitClient.getApiService();

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        authToken = "Bearer " + prefs.getString("token", "");
        collegeId = prefs.getLong("college_id", 1L);

        initViews();
        loadDashboardFromBackend();
        loadStudentsFromBackend();
        setupFilters();
        setupListeners();

    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivExport = findViewById(R.id.ivExport);
        ivRefresh = findViewById(R.id.ivRefresh);

        tvCollegeName = findViewById(R.id.tvCollegeName);
        tvTPOName = findViewById(R.id.tvTPOName);

        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvTotalInterviews = findViewById(R.id.tvTotalInterviews);
        tvAvgScore = findViewById(R.id.tvAvgScore);
        tvActiveRoadmaps = findViewById(R.id.tvActiveRoadmaps);

        pbStats1 = findViewById(R.id.pbStats1);
        pbStats2 = findViewById(R.id.pbStats2);
        pbStats3 = findViewById(R.id.pbStats3);
        pbStats4 = findViewById(R.id.pbStats4);

        tabFilter = findViewById(R.id.tabFilter);
        spinnerJobRole = findViewById(R.id.spinnerJobRole);
        spinnerScoreFilter = findViewById(R.id.spinnerScoreFilter);
        etSearchStudent = findViewById(R.id.etSearchStudent);

        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);

        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        tvNoStudents = findViewById(R.id.tvNoStudents);
        tvStudentCount = findViewById(R.id.tvStudentCount);

        performanceChartCard = findViewById(R.id.performanceChartCard);
        roleDistributionCard = findViewById(R.id.roleDistributionCard);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        tvTPOName.setText("Welcome, " + prefs.getString("full_name", "TPO"));
        tvCollegeName.setText("Test College");
    }

    // 🔹 TEMP local data (we replace with backend next)
    private void loadLocalData() {
        dataHelper = new InterviewDataHelper(this);
        allInterviews = dataHelper.getAllInterviews();
        allStudents = aggregateStudentData();

        filteredStudents.clear();
        filteredStudents.addAll(allStudents);

        Log.d(TAG, "Loaded students: " + allStudents.size());
    }


    private void loadDashboardFromBackend() {

        apiService.getTPODashboardStats(authToken, collegeId)
                .enqueue(new retrofit2.Callback<Map<String, Object>>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<Map<String, Object>> call,
                            retrofit2.Response<Map<String, Object>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(
                                    TPODashboardActivity.this,
                                    "Dashboard load failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        Map<String, Object> data = response.body();

                        int totalStudents =
                                ((Double) data.get("totalStudents")).intValue();
                        int totalInterviews =
                                ((Double) data.get("totalInterviews")).intValue();
                        int activeRoadmaps =
                                ((Double) data.get("activeRoadmaps")).intValue();
                        double avgScore =
                                (Double) data.get("avgInterviewScore");

                        tvTotalStudents.setText(String.valueOf(totalStudents));
                        tvTotalInterviews.setText(String.valueOf(totalInterviews));
                        tvActiveRoadmaps.setText(String.valueOf(activeRoadmaps));
                        tvAvgScore.setText(String.format(Locale.US, "%.1f/10", avgScore));
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<Map<String, Object>> call,
                            Throwable t) {

                        Toast.makeText(
                                TPODashboardActivity.this,
                                "Network error (dashboard)",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadStudentsFromBackend() {

        StudentFilterRequest request = new StudentFilterRequest();
        request.page = 0;
        request.size = 20;
        request.searchQuery = searchQuery;

        apiService.getFilteredStudents(authToken, collegeId, request)
                .enqueue(new retrofit2.Callback<Map<String, Object>>() {

                    @Override
                    public void onResponse(
                            retrofit2.Call<Map<String, Object>> call,
                            retrofit2.Response<Map<String, Object>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(
                                    TPODashboardActivity.this,
                                    "Students load failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        List<Map<String, Object>> list =
                                (List<Map<String, Object>>) response.body().get("students");

                        allStudents.clear();

                        for (Map<String, Object> s : list) {
                            StudentSummary student = new StudentSummary();

                            student.studentId = String.valueOf(s.get("studentId"));
                            student.studentName = (String) s.get("fullName");
                            student.averageScore =
                                    s.get("averageScore") == null ? 0 :
                                            ((Double) s.get("averageScore"));
                            student.totalInterviews =
                                    s.get("totalInterviews") == null ? 0 :
                                            ((Double) s.get("totalInterviews")).intValue();
                            student.readinessLevel =
                                    getReadinessLevel(student.averageScore);

                            allStudents.add(student);
                        }

                        filteredStudents.clear();
                        filteredStudents.addAll(allStudents);

                        displayStudents();
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<Map<String, Object>> call,
                            Throwable t) {

                        Toast.makeText(
                                TPODashboardActivity.this,
                                "Network error (students)",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }


    private List<StudentSummary> aggregateStudentData() {
        Map<String, StudentSummary> map = new HashMap<>();

        for (InterviewDataHelper.InterviewRecord i : allInterviews) {
            String id = i.studentId == null ? "unknown" : i.studentId;

            StudentSummary s = map.get(id);
            if (s == null) {
                s = new StudentSummary();
                s.studentId = id;
                s.studentName = i.studentName;
                s.interviews = new ArrayList<>();
                map.put(id, s);
            }
            s.interviews.add(i);
        }

        List<StudentSummary> list = new ArrayList<>(map.values());
        for (StudentSummary s : list) calculateStudentStats(s);

        Collections.sort(list, (a, b) ->
                Double.compare(b.averageScore, a.averageScore));

        return list;
    }

    private void calculateStudentStats(StudentSummary s) {
        double total = 0;
        Set<String> roles = new HashSet<>();

        InterviewDataHelper.InterviewRecord latest = null;
        long latestTime = 0;

        for (InterviewDataHelper.InterviewRecord i : s.interviews) {
            total += i.overallScore;
            roles.add(i.jobRole);

            try {
                long t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                        .parse(i.timestamp).getTime();
                if (t > latestTime) {
                    latestTime = t;
                    latest = i;
                }
            } catch (Exception ignored) {}
        }

        s.totalInterviews = s.interviews.size();
        s.averageScore = s.totalInterviews == 0 ? 0 : total / s.totalInterviews;
        s.jobRoles = new ArrayList<>(roles);
        s.latestInterview = latest;
        s.readinessLevel = getReadinessLevel(s.averageScore);
    }

    private String getReadinessLevel(double score) {
        if (score >= 8) return "Excellent";
        if (score >= 6) return "Good";
        if (score >= 4) return "Fair";
        return "Needs Work";
    }

    private void displayAnalytics() {
        tvTotalStudents.setText(String.valueOf(allStudents.size()));
        tvTotalInterviews.setText(String.valueOf(allInterviews.size()));
        tvActiveRoadmaps.setText(String.valueOf(allStudents.size()));

        double total = 0;
        for (InterviewDataHelper.InterviewRecord i : allInterviews) total += i.overallScore;
        double avg = allInterviews.isEmpty() ? 0 : total / allInterviews.size();

        tvAvgScore.setText(String.format(Locale.US, "%.1f/10", avg));
    }

    private void setupFilters() {
        tabFilter.addTab(tabFilter.newTab().setText("All"));
        tabFilter.addTab(tabFilter.newTab().setText("Excellent"));
        tabFilter.addTab(tabFilter.newTab().setText("Good"));
        tabFilter.addTab(tabFilter.newTab().setText("Needs Work"));

        tabFilter.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getText().toString().toLowerCase();
                applyFilters();
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());
    }

    private void applyFilters() {
        filteredStudents.clear();
        for (StudentSummary s : allStudents) {
            if (currentTab.equals("all") ||
                    s.readinessLevel.equalsIgnoreCase(currentTab)) {
                filteredStudents.add(s);
            }
        }
        displayStudents();
    }

    private void clearFilters() {
        tabFilter.selectTab(tabFilter.getTabAt(0));
        applyFilters();
    }

    private void displayStudents() {
        if (studentAdapter == null) {
            studentAdapter = new StudentAdapter(filteredStudents, this::onStudentClick);
            recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewStudents.setAdapter(studentAdapter);
        } else {
            studentAdapter.updateData(filteredStudents);
        }
    }

    private void onStudentClick(StudentSummary s) {
        Intent i = new Intent(this, StudentDetailActivity.class);
        i.putExtra("student_name", s.studentName);
        startActivity(i);
    }

    static class StudentSummary {
        String studentId;
        String studentName;
        int totalInterviews;
        double averageScore;
        List<String> jobRoles;
        String readinessLevel;
        List<InterviewDataHelper.InterviewRecord> interviews;
        InterviewDataHelper.InterviewRecord latestInterview;
    }
}
