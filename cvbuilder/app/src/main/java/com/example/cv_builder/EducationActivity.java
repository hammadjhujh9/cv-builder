package com.example.cv_builder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EducationActivity extends AppCompatActivity {

    private LinearLayout educationContainer;
    private Button buttonAddMore, buttonSave, buttonCancel;
    private List<View> educationViews = new ArrayList<>();
    private EducationDatabaseHelper dbHelper;
    private List<Education> existingEducation = new ArrayList<>();

    // Degree level options
    private final String[] degreeLevels = {
            Education.LEVEL_MATRIC,
            Education.LEVEL_INTER,
            Education.LEVEL_BACHELORS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_education);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = EducationDatabaseHelper.getInstance(this);

        // Initialize views
        educationContainer = findViewById(R.id.linearLayoutEducation);
        buttonAddMore = findViewById(R.id.buttonAddMore);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing education entries from database
        loadEducation();

        // If no existing education entries, add an empty one
        if (existingEducation.isEmpty()) {
            addEmptyEducationView();
        }

        // Set up button click listeners
        buttonAddMore.setOnClickListener(v -> addEmptyEducationView());

        buttonSave.setOnClickListener(v -> {
            saveEducation();
            finish();
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadEducation() {
        // Ensure the education table exists before trying to access it
        dbHelper.ensureTableExists();

        // Now it's safe to get the education entries
        existingEducation = dbHelper.getAllEducation();

        for (Education education : existingEducation) {
            addEducationView(education);
        }
    }

    private void addEmptyEducationView() {
        addEducationView(null);
    }

    private void addEducationView(Education education) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View educationView = inflater.inflate(R.layout.education_item, null);

        // Set up the spinner for degree level
        Spinner spinnerDegreeLevel = educationView.findViewById(R.id.spinnerDegreeLevel);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, degreeLevels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDegreeLevel.setAdapter(adapter);

        // Get references to the other fields
        EditText editTextInstitution = educationView.findViewById(R.id.editTextInstitution);
        EditText editTextStartDate = educationView.findViewById(R.id.editTextStartDate);
        EditText editTextEndDate = educationView.findViewById(R.id.editTextEndDate);
        CheckBox checkBoxPresent = educationView.findViewById(R.id.checkBoxPresent);

        // Set up date pickers
        editTextStartDate.setOnClickListener(v -> {
            showDatePickerDialog(editTextStartDate);
        });

        editTextEndDate.setOnClickListener(v -> {
            showDatePickerDialog(editTextEndDate);
        });

        // Set up checkbox for present/current education
        checkBoxPresent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextEndDate.setText("Present");
                editTextEndDate.setEnabled(false);
            } else {
                editTextEndDate.setText("");
                editTextEndDate.setEnabled(true);
            }
        });

        // If we have an existing education entry, populate the fields
        if (education != null) {
            // Find the position of the degree level in the array
            int position = 0;
            for (int i = 0; i < degreeLevels.length; i++) {
                if (degreeLevels[i].equals(education.getDegreeLevel())) {
                    position = i;
                    break;
                }
            }
            spinnerDegreeLevel.setSelection(position);

            editTextInstitution.setText(education.getInstitution());
            editTextStartDate.setText(education.getStartDate());

            if ("Present".equals(education.getEndDate())) {
                checkBoxPresent.setChecked(true);
                editTextEndDate.setText("Present");
                editTextEndDate.setEnabled(false);
            } else {
                checkBoxPresent.setChecked(false);
                editTextEndDate.setText(education.getEndDate());
                editTextEndDate.setEnabled(true);
            }

            // Store the education ID in the tag for later retrieval
            educationView.setTag(education.getId());
        }

        // Set up remove button for this education item
        ImageButton removeButton = educationView.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> {
            // Don't allow removing if it's the last education entry
            if (educationViews.size() > 1) {
                // If this is an existing education entry (has an ID), delete it from the database
                if (educationView.getTag() != null) {
                    long educationId = (long) educationView.getTag();
                    dbHelper.deleteEducation(educationId);
                }

                educationContainer.removeView(educationView);
                educationViews.remove(educationView);
            } else {
                Toast.makeText(EducationActivity.this,
                        "You need at least one education entry",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add the view to the container and list
        educationContainer.addView(educationView, educationContainer.getChildCount() - 1); // Add before the "Add More" button
        educationViews.add(educationView);
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

    private void saveEducation() {
        // Loop through all education views and extract data
        for (View view : educationViews) {
            Spinner spinnerDegreeLevel = view.findViewById(R.id.spinnerDegreeLevel);
            String degreeLevel = spinnerDegreeLevel.getSelectedItem().toString();

            String institution = ((EditText) view.findViewById(R.id.editTextInstitution)).getText().toString();
            String startDate = ((EditText) view.findViewById(R.id.editTextStartDate)).getText().toString();
            String endDate = ((EditText) view.findViewById(R.id.editTextEndDate)).getText().toString();

            // Validate (simple validation - just checking if institution is not empty)
            if (!institution.isEmpty()) {
                // Check if this is an update or a new education entry
                Object tag = view.getTag();

                if (tag != null) {
                    // This is an existing education entry, update it
                    long educationId = (long) tag;
                    Education education = new Education(educationId, degreeLevel, institution, startDate, endDate);
                    dbHelper.updateEducation(education);
                } else {
                    // This is a new education entry, add it
                    Education education = new Education(degreeLevel, institution, startDate, endDate);
                    dbHelper.addEducation(education);
                }
            }
        }

        Toast.makeText(this, "Education details saved successfully", Toast.LENGTH_SHORT).show();
    }
}