package com.example.skilllsetujava;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skilllsetujava.api.ApiService;
import com.example.skilllsetujava.api.RetrofitClient;
import com.example.skilllsetujava.api.models.StudentFilterRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.*;

/**
 * 📊 TPO Dashboard – Backend Driven (FINAL FIX)
 */
public class TPODashboardActivity extends AppCompatActivity {

    private static final String TAG = "TPODashboard";

    // Backend
    private ApiService apiService;
    private String authToken;
    private Long collegeId;

    // Header
    private ImageView ivBack;
    private TextView tvCollegeName, tvTPOName;

    // Stats
    private TextView tvTotalStudents, tvTotalInterviews, tvAvgScore, tvActiveRoadmaps;

    // Filters
    private TabLayout tabFilter;
    private EditText etSearchStudent;
    private MaterialButton btnApplyFilters, btnClearFilters;

    // Students
    private RecyclerView recyclerViewStudents;
    private StudentAdapter studentAdapter;
    private TextView tvNoStudents, tvStudentCount;

    // Data
    private final List<StudentSummary> allStudents = new ArrayList<>();
    private final List<StudentSummary> filteredStudents = new ArrayList<>();

    private String currentTab = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpodashboard);

        // 🔐 Role Guard
        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        if (!"ROLE_TPO".equals(authPrefs.getString("role", ""))) {
            startActivity(new Intent(this, activity_login.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService();
        authToken = "Bearer " + authPrefs.getString("jwt_token", "");
        collegeId = authPrefs.getLong("college_id", -1);

        if (collegeId == -1) {
            Toast.makeText(this, "College ID missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupFilters();
        setupListeners();

        loadDashboardStats();
        loadStudentsFromBackend();
    }

    // ================= UI =================

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        tvCollegeName = findViewById(R.id.tvCollegeName);
        tvTPOName = findViewById(R.id.tvTPOName);

        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvTotalInterviews = findViewById(R.id.tvTotalInterviews);
        tvAvgScore = findViewById(R.id.tvAvgScore);
        tvActiveRoadmaps = findViewById(R.id.tvActiveRoadmaps);

        tabFilter = findViewById(R.id.tabFilter);
        etSearchStudent = findViewById(R.id.etSearchStudent);

        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);

        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        tvNoStudents = findViewById(R.id.tvNoStudents);
        tvStudentCount = findViewById(R.id.tvStudentCount);

        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        tvTPOName.setText("Welcome, " + prefs.getString("full_name", "TPO"));
        tvCollegeName.setText("Test College");
    }

    // ================= DASHBOARD STATS =================

    private void loadDashboardStats() {
        apiService.getTPODashboardStats(authToken, collegeId)
                .enqueue(new retrofit2.Callback<Map<String, Object>>() {

                    @Override
                    public void onResponse(retrofit2.Call<Map<String, Object>> call,
                                           retrofit2.Response<Map<String, Object>> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        Map<String, Object> d = response.body();

                        tvTotalStudents.setText(String.valueOf(((Number) d.get("totalStudents")).intValue()));
                        tvTotalInterviews.setText(String.valueOf(((Number) d.get("totalInterviews")).intValue()));
                        tvActiveRoadmaps.setText(String.valueOf(((Number) d.get("activeRoadmaps")).intValue()));
                        tvAvgScore.setText(String.format(Locale.US, "%.1f/10",
                                ((Number) d.get("avgInterviewScore")).doubleValue()));
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Map<String, Object>> call, Throwable t) {
                        Log.e(TAG, "Dashboard load failed", t);
                    }
                });
    }

    // ================= STUDENTS =================

    private void loadStudentsFromBackend() {

        StudentFilterRequest request = new StudentFilterRequest();
        request.page = 0;
        request.size = 50;
        request.searchQuery = etSearchStudent.getText().toString().trim();

        apiService.getFilteredStudents(authToken, collegeId, request)
                .enqueue(new retrofit2.Callback<Map<String, Object>>() {

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onResponse(retrofit2.Call<Map<String, Object>> call,
                                           retrofit2.Response<Map<String, Object>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            showNoStudents();
                            return;
                        }

                        List<Map<String, Object>> list =
                                (List<Map<String, Object>>) response.body().get("students");

                        allStudents.clear();

                        if (list == null || list.isEmpty()) {
                            showNoStudents();
                            return;
                        }

                        for (Map<String, Object> s : list) {
                            StudentSummary st = new StudentSummary();
                            st.studentId = String.valueOf(s.get("studentId"));
                            st.studentName = (String) s.get("fullName");
                            st.averageScore = toDouble(s.get("averageScore"));
                            st.totalInterviews = toInt(s.get("totalInterviews"));
                            st.readinessLevel = getReadinessLevel(
                                    toDouble(s.get("placementReadinessScore"))
                            );
                            allStudents.add(st);
                        }

                        applyFilters();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Map<String, Object>> call, Throwable t) {
                        Log.e(TAG, "Student load failed", t);
                        showNoStudents();
                    }
                });
    }

    private void showNoStudents() {
        tvNoStudents.setVisibility(TextView.VISIBLE);
        recyclerViewStudents.setVisibility(RecyclerView.GONE);
        tvStudentCount.setText("0 students");
    }

    // ================= FILTERS =================

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

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnApplyFilters.setOnClickListener(v -> loadStudentsFromBackend());
        btnClearFilters.setOnClickListener(v -> {
            etSearchStudent.setText("");
            tabFilter.selectTab(tabFilter.getTabAt(0));
            loadStudentsFromBackend();
        });
    }

    private void displayStudents() {
        tvStudentCount.setText(filteredStudents.size() + " students");

        if (studentAdapter == null) {
            studentAdapter = new StudentAdapter(filteredStudents, this::onStudentClick);
            recyclerViewStudents.setAdapter(studentAdapter);
        } else {
            studentAdapter.updateData(filteredStudents);
        }

        recyclerViewStudents.setVisibility(RecyclerView.VISIBLE);
        tvNoStudents.setVisibility(TextView.GONE);
    }

    private void onStudentClick(StudentSummary s) {
        Intent i = new Intent(this, StudentDetailActivity.class);
        i.putExtra("student_id", s.studentId);
        i.putExtra("student_name", s.studentName);
        startActivity(i);
    }

    // ================= HELPERS =================

    private double toDouble(Object o) {
        return o == null ? 0 : ((Number) o).doubleValue();
    }

    private int toInt(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }

    private String getReadinessLevel(double score) {
        if (score >= 75) return "Excellent";
        if (score >= 50) return "Good";
        return "Needs Work";
    }

    // ================= MODEL =================

    static class StudentSummary {
        String studentId;
        String studentName;
        int totalInterviews;
        double averageScore;
        String readinessLevel;
    }
}
