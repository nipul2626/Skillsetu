package com.example.skilllsetujava;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setPadding(32, 32, 32, 32);
        tv.setTextSize(18);

        String name = getIntent().getStringExtra("student_name");
        tv.setText("Student Details\n\nName: " + name);

        setContentView(tv);
    }
}
