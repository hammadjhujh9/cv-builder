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

public class CertificationsActivity extends AppCompatActivity {

    private LinearLayout certificationContainer;
    private Button buttonAddMore, buttonSave, buttonCancel;
    private List<View> certificationViews = new ArrayList<>();
    private CertificationDatabaseHelper dbHelper;
    private List<Certification> existingCertifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_certifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = CertificationDatabaseHelper.getInstance(this);

        // Initialize views
        certificationContainer = findViewById(R.id.linearLayoutCertifications);
        buttonAddMore = findViewById(R.id.buttonAddMore);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing certifications from database
        loadCertifications();

        // If no existing certifications, add an empty one
        if (existingCertifications.isEmpty()) {
            addEmptyCertificationView();
        }

        // Set up button click listeners
        buttonAddMore.setOnClickListener(v -> addEmptyCertificationView());

        buttonSave.setOnClickListener(v -> {
            saveCertifications();
            finish();
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadCertifications() {
        // Ensure the certifications table exists before trying to access it
        dbHelper.ensureTableExists();

        // Now it's safe to get the certifications
        existingCertifications = dbHelper.getAllCertifications();

        for (Certification certification : existingCertifications) {
            addCertificationView(certification);
        }
    }

    private void addEmptyCertificationView() {
        addCertificationView(null);
    }

    private void addCertificationView(Certification certification) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View certificationView = inflater.inflate(R.layout.certification_item, null);

        // Get references to the EditText fields
        EditText editTextTitle = certificationView.findViewById(R.id.editTextCertificationTitle);
        EditText editTextStartDate = certificationView.findViewById(R.id.editTextStartDate);
        EditText editTextEndDate = certificationView.findViewById(R.id.editTextEndDate);

        // If we have an existing certification, populate the fields
        if (certification != null) {
            editTextTitle.setText(certification.getTitle());
            editTextStartDate.setText(certification.getStartDate());
            editTextEndDate.setText(certification.getEndDate());

            // Store the certification ID in the tag for later retrieval
            certificationView.setTag(certification.getId());
        }

        // Set up remove button for this certification item
        ImageButton removeButton = certificationView.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> {
            // Don't allow removing if it's the last certification
            if (certificationViews.size() > 1) {
                // If this is an existing certification (has an ID), delete it from the database
                if (certificationView.getTag() != null) {
                    long certificationId = (long) certificationView.getTag();
                    dbHelper.deleteCertification(certificationId);
                }

                certificationContainer.removeView(certificationView);
                certificationViews.remove(certificationView);
            } else {
                Toast.makeText(CertificationsActivity.this,
                        "You need at least one certification",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add the view to the container and list
        certificationContainer.addView(certificationView, certificationContainer.getChildCount() - 1); // Add before the "Add More" button
        certificationViews.add(certificationView);
    }

    private void saveCertifications() {
        // Loop through all certification views and extract data
        for (View view : certificationViews) {
            String title = ((EditText) view.findViewById(R.id.editTextCertificationTitle)).getText().toString();
            String startDate = ((EditText) view.findViewById(R.id.editTextStartDate)).getText().toString();
            String endDate = ((EditText) view.findViewById(R.id.editTextEndDate)).getText().toString();

            // Validate (simple validation - just checking if title is not empty)
            if (!title.isEmpty()) {
                // Check if this is an update or a new certification
                Object tag = view.getTag();

                if (tag != null) {
                    // This is an existing certification, update it
                    long certificationId = (long) tag;
                    Certification certification = new Certification(certificationId, title, startDate, endDate);
                    dbHelper.updateCertification(certification);
                } else {
                    // This is a new certification, add it
                    Certification certification = new Certification(title, startDate, endDate);
                    dbHelper.addCertification(certification);
                }
            }
        }

        Toast.makeText(this, "Certifications saved successfully", Toast.LENGTH_SHORT).show();
    }
}