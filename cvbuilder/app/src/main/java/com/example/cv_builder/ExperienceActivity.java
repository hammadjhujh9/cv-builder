package com.example.cv_builder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExperienceActivity extends AppCompatActivity {

    private LinearLayout experienceContainer;
    private Button buttonAddMore, buttonSave, buttonCancel;
    private List<View> experienceViews = new ArrayList<>();
    private ExperienceDatabaseHelper dbHelper;
    private List<Experience> existingExperience = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_experience);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = ExperienceDatabaseHelper.getInstance(this);

        // Initialize views
        experienceContainer = findViewById(R.id.linearLayoutExperience);
        buttonAddMore = findViewById(R.id.buttonAddMore);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing experience entries from database
        loadExperience();

        // If no existing experience entries, add an empty one
        if (existingExperience.isEmpty()) {
            addEmptyExperienceView();
        }

        // Set up button click listeners
        buttonAddMore.setOnClickListener(v -> addEmptyExperienceView());

        buttonSave.setOnClickListener(v -> {
            saveExperience();
            finish();
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadExperience() {
        // Ensure the table exists before trying to read from it
        dbHelper.ensureTableExists();

        existingExperience = dbHelper.getAllExperience();

        for (Experience experience : existingExperience) {
            addExperienceView(experience);
        }
    }

    private void addEmptyExperienceView() {
        addExperienceView(null);
    }

    private void addExperienceView(Experience experience) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View experienceView = inflater.inflate(R.layout.experience_item, null);

        // Get references to all fields
        EditText editTextCompany = experienceView.findViewById(R.id.editTextCompany);
        EditText editTextPosition = experienceView.findViewById(R.id.editTextPosition);
        EditText editTextStartDate = experienceView.findViewById(R.id.editTextStartDate);
        EditText editTextEndDate = experienceView.findViewById(R.id.editTextEndDate);
        EditText editTextDescription = experienceView.findViewById(R.id.editTextDescription);
        CheckBox checkBoxPresent = experienceView.findViewById(R.id.checkBoxPresent);

        // Set up date pickers
        editTextStartDate.setOnClickListener(v -> {
            showDatePickerDialog(editTextStartDate);
        });

        editTextEndDate.setOnClickListener(v -> {
            showDatePickerDialog(editTextEndDate);
        });

        // Set up checkbox for present/current employment
        checkBoxPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextEndDate.setText("Present");
                editTextEndDate.setEnabled(false);
            } else {
                editTextEndDate.setText("");
                editTextEndDate.setEnabled(true);
            }
        });

        // If we have an existing experience entry, populate the fields
        if (experience != null) {
            editTextCompany.setText(experience.getCompany());
            editTextPosition.setText(experience.getPosition());
            editTextStartDate.setText(experience.getStartDate());
            editTextDescription.setText(experience.getDescription());

            if ("Present".equals(experience.getEndDate())) {
                checkBoxPresent.setChecked(true);
                editTextEndDate.setText("Present");
                editTextEndDate.setEnabled(false);
            } else {
                checkBoxPresent.setChecked(false);
                editTextEndDate.setText(experience.getEndDate());
                editTextEndDate.setEnabled(true);
            }

            // Store the experience ID in the tag for later retrieval
            experienceView.setTag(experience.getId());
        }

        // Set up remove button for this experience item
        ImageButton removeButton = experienceView.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> {
            // Don't allow removing if it's the last experience entry
            if (experienceViews.size() > 1) {
                // If this is an existing experience entry (has an ID), delete it from the database
                if (experienceView.getTag() != null) {
                    long experienceId = (long) experienceView.getTag();
                    dbHelper.deleteExperience(experienceId);
                }

                experienceContainer.removeView(experienceView);
                experienceViews.remove(experienceView);
            } else {
                Toast.makeText(ExperienceActivity.this,
                        "You need at least one experience entry",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add the view to the container and list
        experienceContainer.addView(experienceView, experienceContainer.getChildCount() - 1); // Add before the "Add More" button
        experienceViews.add(experienceView);
    }

    private void showDatePickerDialog(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format as MM/YYYY
                    String date = (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(date);
                },
                year, month, 1);

        // Only show month and year
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void saveExperience() {
        // Loop through all experience views and extract data
        for (View view : experienceViews) {
            String company = ((EditText) view.findViewById(R.id.editTextCompany)).getText().toString();
            String position = ((EditText) view.findViewById(R.id.editTextPosition)).getText().toString();
            String startDate = ((EditText) view.findViewById(R.id.editTextStartDate)).getText().toString();
            String endDate = ((EditText) view.findViewById(R.id.editTextEndDate)).getText().toString();
            String description = ((EditText) view.findViewById(R.id.editTextDescription)).getText().toString();

            // Validate (simple validation - just checking if company is not empty)
            if (!company.isEmpty()) {
                // Check if this is an update or a new experience entry
                Object tag = view.getTag();

                if (tag != null) {
                    // This is an existing experience entry, update it
                    long experienceId = (long) tag;
                    Experience experience = new Experience(experienceId, company, position, startDate, endDate, description);
                    dbHelper.updateExperience(experience);
                } else {
                    // This is a new experience entry, add it
                    Experience experience = new Experience(company, position, startDate, endDate, description);
                    dbHelper.addExperience(experience);
                }
            }
        }

        Toast.makeText(this, "Experience details saved successfully", Toast.LENGTH_SHORT).show();
    }
}