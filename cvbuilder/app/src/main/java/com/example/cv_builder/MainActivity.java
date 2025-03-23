package com.example.cv_builder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for all sections
        setupSectionClickListeners();
    }

    private void setupSectionClickListeners() {
        // Profile Picture
        LinearLayout profilePictureSection = findViewById(R.id.section_profile_picture);
        profilePictureSection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfilePictureActivity.class);
            startActivity(intent);
        });

        // Personal Details
        LinearLayout personalDetailsSection = findViewById(R.id.section_personal_details);
        personalDetailsSection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PersonalDetailsActivity.class);
            startActivity(intent);
        });

        // Summary
        LinearLayout summarySection = findViewById(R.id.section_summary);
        summarySection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(intent);
        });

        // Education
        LinearLayout educationSection = findViewById(R.id.section_education);
        educationSection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EducationActivity.class);
            startActivity(intent);
        });

        // Experience
        LinearLayout experienceSection = findViewById(R.id.section_experience);
        experienceSection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExperienceActivity.class);
            startActivity(intent);
        });

        // Certifications
        LinearLayout certificationsSection = findViewById(R.id.section_certifications);
        certificationsSection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CertificationsActivity.class);
            startActivity(intent);
        });

        // References
        LinearLayout referencesSection = findViewById(R.id.section_references);
        referencesSection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReferencesActivity.class);
            startActivity(intent);
        });

        // Preview CV
        Button previewCVButton = findViewById(R.id.btn_preview_cv);
        previewCVButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FinalActivity.class);
            startActivity(intent);
        });
    }
}