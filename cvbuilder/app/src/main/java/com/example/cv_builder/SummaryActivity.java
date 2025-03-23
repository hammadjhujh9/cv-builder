package com.example.cv_builder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SummaryActivity extends AppCompatActivity {

    private EditText editTextSummaryTitle;
    private EditText editTextSummaryContent;
    private Button buttonSave;
    private Button buttonCancel;

    private SummaryDatabaseHelper dbHelper;
    private Summary currentSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = SummaryDatabaseHelper.getInstance(this);

        // Initialize views
        editTextSummaryTitle = findViewById(R.id.editTextSummaryTitle);
        editTextSummaryContent = findViewById(R.id.editTextSummaryContent);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing summary if it exists
        loadExistingSummary();

        // Set up button click listeners
        setupButtons();
    }

    private void loadExistingSummary() {
        // Try to get the default summary (which will be the only one)
        currentSummary = dbHelper.getDefaultProfessionalSummary();

        // If a summary exists, populate the fields
        if (currentSummary != null) {
            editTextSummaryTitle.setText(currentSummary.getTitle());
            editTextSummaryContent.setText(currentSummary.getContent());
        }
    }

    private void setupButtons() {
        // Save button click listener
        buttonSave.setOnClickListener(v -> saveSummary());

        // Cancel button click listener
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveSummary() {
        // Get values from inputs
        String title = editTextSummaryTitle.getText().toString().trim();
        String content = editTextSummaryContent.getText().toString().trim();

        // Validate inputs
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create or update summary object
        if (currentSummary == null) {
            // Create new summary
            currentSummary = new Summary(title, content);
            currentSummary.setIsDefault(true); // Always set as default since it's the only one
        } else {
            // Update existing summary
            currentSummary.setTitle(title);
            currentSummary.setContent(content);
            currentSummary.setIsDefault(true); // Ensure it's set as default
        }

        // Save to database
        long id = dbHelper.saveProfessionalSummary(currentSummary);

        if (id > 0) {
            Toast.makeText(this, "Summary saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving summary", Toast.LENGTH_SHORT).show();
        }
    }
}