package com.example.skilllsetujava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    public interface OnStudentClickListener {
        void onClick(TPODashboardActivity.StudentSummary student);
    }

    private List<TPODashboardActivity.StudentSummary> students;
    private OnStudentClickListener listener;

    public StudentAdapter(List<TPODashboardActivity.StudentSummary> students,
                          OnStudentClickListener listener) {
        this.students = students;
        this.listener = listener;
    }

    public void updateData(List<TPODashboardActivity.StudentSummary> newData) {
        this.students = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TPODashboardActivity.StudentSummary s = students.get(position);
        holder.title.setText(s.studentName);
        holder.subtitle.setText(
                "Avg: " + String.format("%.1f", s.averageScore) +
                        " | " + s.readinessLevel
        );
        holder.itemView.setOnClickListener(v -> listener.onClick(s));
    }

    @Override
    public int getItemCount() {
        return students == null ? 0 : students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
