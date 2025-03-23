package com.example.cv_builder;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPhone, editTextAddress, editTextLinkedIn;
    private Button buttonSave, buttonCancel;
    private PersonalDetailsDatabaseHelper dbHelper;
    private PersonalDetails existingDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = PersonalDetailsDatabaseHelper.getInstance(this);

        // Initialize views
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextLinkedIn = findViewById(R.id.editTextLinkedIn);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing personal details from database if available
        loadPersonalDetails();

        // Set up button click listeners
        buttonSave.setOnClickListener(v -> {
            if (validateInputs()) {
                savePersonalDetails();
                finish();
            }
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadPersonalDetails() {
        // Ensure the personal details table exists before trying to access it
        dbHelper.ensureTableExists();

        // Now it's safe to get the personal details
        existingDetails = dbHelper.getPersonalDetails();

        if (existingDetails != null) {
            // Populate the form with existing data
            editTextFullName.setText(existingDetails.getFullName());
            editTextEmail.setText(existingDetails.getEmail());
            editTextPhone.setText(existingDetails.getPhone());
            editTextAddress.setText(existingDetails.getAddress());
            editTextLinkedIn.setText(existingDetails.getLinkedin());
        }
    }

    private boolean validateInputs() {
        // Check if required fields are filled
        if (TextUtils.isEmpty(editTextFullName.getText())) {
            editTextFullName.setError("Full name is required");
            return false;
        }

        if (TextUtils.isEmpty(editTextEmail.getText())) {
            editTextEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(editTextPhone.getText())) {
            editTextPhone.setError("Phone number is required");
            return false;
        }

        // Simple email validation
        String email = editTextEmail.getText().toString().trim();
        if (!email.contains("@") || !email.contains(".")) {
            editTextEmail.setError("Please enter a valid email address");
            return false;
        }

        return true;
    }

    private void savePersonalDetails() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String linkedin = editTextLinkedIn.getText().toString().trim();

        // Create a new PersonalDetails object
        PersonalDetails personalDetails = new PersonalDetails(fullName, email, phone, address, linkedin);

        // If we have existing details, set the ID for updating
        if (existingDetails != null) {
            personalDetails.setId(existingDetails.getId());
        }

        // Save to database
        long result = dbHelper.savePersonalDetails(personalDetails);

        if (result != -1) {
            Toast.makeText(this, "Personal details saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save personal details", Toast.LENGTH_SHORT).show();
        }
    }
}