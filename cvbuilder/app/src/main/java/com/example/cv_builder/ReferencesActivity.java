package com.example.cv_builder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.List;

public class ReferencesActivity extends AppCompatActivity {

    private LinearLayout referencesContainer;
    private Button buttonAddMore, buttonSave, buttonCancel;
    private List<View> referenceViews = new ArrayList<>();
    private ReferencesDatabaseHelper dbHelper;
    private List<Reference> existingReferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_references);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = ReferencesDatabaseHelper.getInstance(this);

        // Initialize views
        referencesContainer = findViewById(R.id.linearLayoutReferences);
        buttonAddMore = findViewById(R.id.buttonAddMore);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing reference entries from database
        loadReferences();

        // If no existing reference entries, add an empty one
        if (existingReferences.isEmpty()) {
            addEmptyReferenceView();
        }

        // Set up button click listeners
        buttonAddMore.setOnClickListener(v -> addEmptyReferenceView());

        buttonSave.setOnClickListener(v -> {
            saveReferences();
            finish();
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadReferences() {
        // Ensure the references table exists before trying to access it
        dbHelper.ensureTableExists();

        // Now it's safe to get the reference entries
        existingReferences = dbHelper.getAllReferences();

        for (Reference reference : existingReferences) {
            addReferenceView(reference);
        }
    }

    private void addEmptyReferenceView() {
        addReferenceView(null);
    }

    private void addReferenceView(Reference reference) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View referenceView = inflater.inflate(R.layout.reference_item, null);

        // Get references to fields
        EditText editTextName = referenceView.findViewById(R.id.editTextName);
        EditText editTextEmail = referenceView.findViewById(R.id.editTextEmail);
        EditText editTextPhone = referenceView.findViewById(R.id.editTextPhone);
        EditText editTextOrganization = referenceView.findViewById(R.id.editTextOrganization);
        EditText editTextPosition = referenceView.findViewById(R.id.editTextPosition);

        // If we have an existing reference entry, populate the fields
        if (reference != null) {
            editTextName.setText(reference.getName());
            editTextEmail.setText(reference.getEmail());
            editTextPhone.setText(reference.getPhone());
            editTextOrganization.setText(reference.getOrganization());
            editTextPosition.setText(reference.getPosition());

            // Store the reference ID in the tag for later retrieval
            referenceView.setTag(reference.getId());
        }

        // Set up remove button for this reference item
        ImageButton removeButton = referenceView.findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(v -> {
            // Don't allow removing if it's the last reference entry
            if (referenceViews.size() > 1) {
                // If this is an existing reference entry (has an ID), delete it from the database
                if (referenceView.getTag() != null) {
                    long referenceId = (long) referenceView.getTag();
                    dbHelper.deleteReference(referenceId);
                }

                referencesContainer.removeView(referenceView);
                referenceViews.remove(referenceView);
            } else {
                Toast.makeText(ReferencesActivity.this,
                        "You need at least one reference entry",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add the view to the container and list
        referencesContainer.addView(referenceView, referencesContainer.getChildCount() - 1); // Add before the "Add More" button
        referenceViews.add(referenceView);
    }

    private void saveReferences() {
        // Loop through all reference views and extract data
        for (View view : referenceViews) {
            String name = ((EditText) view.findViewById(R.id.editTextName)).getText().toString();
            String email = ((EditText) view.findViewById(R.id.editTextEmail)).getText().toString();
            String phone = ((EditText) view.findViewById(R.id.editTextPhone)).getText().toString();
            String organization = ((EditText) view.findViewById(R.id.editTextOrganization)).getText().toString();
            String position = ((EditText) view.findViewById(R.id.editTextPosition)).getText().toString();

            // Validate (simple validation - just checking if name is not empty)
            if (!name.isEmpty()) {
                // Check if this is an update or a new reference entry
                Object tag = view.getTag();

                if (tag != null) {
                    // This is an existing reference entry, update it
                    long referenceId = (long) tag;
                    Reference reference = new Reference(referenceId, name, email, phone, organization, position);
                    dbHelper.updateReference(reference);
                } else {
                    // This is a new reference entry, add it
                    Reference reference = new Reference(name, email, phone, organization, position);
                    dbHelper.addReference(reference);
                }
            }
        }

        Toast.makeText(this, "Reference details saved successfully", Toast.LENGTH_SHORT).show();
    }
}