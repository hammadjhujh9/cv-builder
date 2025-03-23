package com.example.cv_builder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.List;

public class FinalActivity extends AppCompatActivity {

    private TextView textViewFullName, textViewEmail, textViewPhone, textViewAddress, textViewLinkedIn;
    private TextView textViewSummaryTitle, textViewSummaryContent;
    private ImageView imageViewProfilePicture;
    private Button buttonShare;
    private CardView cardPersonalDetails, cardProfilePicture, cardProfessionalSummary;

    // Database helpers
    private PersonalDetailsDatabaseHelper personalDetailsDbHelper;
    private ProfilePictureDatabaseHelper profilePictureDbHelper;
    private SummaryDatabaseHelper summaryDbHelper;

    private EducationDatabaseHelper educationDbHelper;
    private LinearLayout educationItemsContainer;
    private CardView cardEducation;

    // Experience section
    private ExperienceDatabaseHelper experienceDbHelper;
    private LinearLayout experienceItemsContainer;
    private CardView cardExperience;
    private CertificationDatabaseHelper certificationDbHelper;
    private LinearLayout certificationItemsContainer;
    private CardView cardCertification;
    private ReferencesDatabaseHelper referencesDbHelper;
    private LinearLayout referencesItemsContainer;
    private CardView cardReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_final);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helpers
        personalDetailsDbHelper = PersonalDetailsDatabaseHelper.getInstance(this);
        profilePictureDbHelper = ProfilePictureDatabaseHelper.getInstance(this);
        summaryDbHelper = SummaryDatabaseHelper.getInstance(this);
        educationDbHelper = EducationDatabaseHelper.getInstance(this);
        experienceDbHelper = ExperienceDatabaseHelper.getInstance(this);
        certificationDbHelper = CertificationDatabaseHelper.getInstance(this);
        referencesDbHelper = ReferencesDatabaseHelper.getInstance(this);



        // Initialize views
        initializeViews();

        // Load data
        loadPersonalDetails();
        loadProfilePicture();
        loadProfessionalSummary();
        loadEducation();
        loadExperience();
        loadCertifications();
        loadReferences();


        // Set up button click listener
        buttonShare.setOnClickListener(v -> {
            // Implement share functionality
            Toast.makeText(FinalActivity.this, "Share functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeViews() {
        // Card views
        cardPersonalDetails = findViewById(R.id.cardPersonalDetails);
        cardProfilePicture = findViewById(R.id.cardProfilePicture);
        cardProfessionalSummary = findViewById(R.id.cardProfessionalSummary);

        // TextViews for personal details
        textViewFullName = findViewById(R.id.textViewFullName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewLinkedIn = findViewById(R.id.textViewLinkedIn);

        // TextViews for professional summary
        textViewSummaryTitle = findViewById(R.id.textViewSummaryTitle);
        textViewSummaryContent = findViewById(R.id.textViewSummaryContent);

        // ImageView for profile picture
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture);

        cardEducation = findViewById(R.id.cardEducation);
        educationItemsContainer = findViewById(R.id.educationItemsContainer);

        // Experience section
        cardExperience = findViewById(R.id.cardExperience);
        experienceItemsContainer = findViewById(R.id.experienceItemsContainer);

        cardCertification = findViewById(R.id.cardCertification);
        certificationItemsContainer = findViewById(R.id.certificationItemsContainer);

        cardReferences = findViewById(R.id.cardReferences);
        referencesItemsContainer = findViewById(R.id.referencesItemsContainer);


        // Button
        buttonShare = findViewById(R.id.buttonShare);
    }

    private void loadPersonalDetails() {
        // Ensure table exists
        personalDetailsDbHelper.ensureTableExists();

        // Get personal details from database
        PersonalDetails personalDetails = personalDetailsDbHelper.getPersonalDetails();

        if (personalDetails != null) {
            // Populate TextViews with data
            textViewFullName.setText(personalDetails.getFullName());
            textViewEmail.setText(personalDetails.getEmail());
            textViewPhone.setText(personalDetails.getPhone());
            textViewAddress.setText(personalDetails.getAddress());
            textViewLinkedIn.setText(personalDetails.getLinkedin());
        } else {
            // Handle case where no personal details are available
            cardPersonalDetails.setVisibility(View.GONE);
            Toast.makeText(this, "No personal details found. Please add your details first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfilePicture() {
        // Ensure table exists
        profilePictureDbHelper.ensureTableExists();

        // Get profile picture from database
        ProfilePicture profilePicture = profilePictureDbHelper.getProfilePicture();

        if (profilePicture != null && profilePicture.getImagePath() != null) {
            // Load and display the profile picture
            String imagePath = profilePicture.getImagePath();
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                // Get dimensions for the ImageView
                int targetW = imageViewProfilePicture.getWidth();
                int targetH = imageViewProfilePicture.getHeight();

                // If ImageView dimensions are not set yet, use some default values
                if (targetW == 0) targetW = 300;
                if (targetH == 0) targetH = 300;

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
                imageViewProfilePicture.setImageBitmap(bitmap);
            } else {
                // Set default image if file doesn't exist
                imageViewProfilePicture.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        } else {
            // Handle case where no profile picture is available
            imageViewProfilePicture.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    private void loadProfessionalSummary() {
        // Ensure table exists
        summaryDbHelper.ensureTableExists();

        // Get professional summary from database
        Summary summary = summaryDbHelper.getDefaultProfessionalSummary();

        if (summary != null) {
            // Populate TextViews with data
            textViewSummaryTitle.setText(summary.getTitle());
            textViewSummaryContent.setText(summary.getContent());

            // Make the card visible
            cardProfessionalSummary.setVisibility(View.VISIBLE);
        } else {
            // Hide the card if no summary is available
            cardProfessionalSummary.setVisibility(View.GONE);
        }
    }

    private void loadEducation() {
        // Ensure table exists
        educationDbHelper.ensureTableExists();

        // Get education entries from database
        List<Education> educationList = educationDbHelper.getAllEducation();

        if (educationList != null && !educationList.isEmpty()) {
            // Show education section
            cardEducation.setVisibility(View.VISIBLE);

            // Clear any existing education items
            educationItemsContainer.removeAllViews();

            // Add each education entry to the layout
            for (Education education : educationList) {
                View educationItemView = LayoutInflater.from(this).inflate(
                        R.layout.education_item_display, educationItemsContainer, false);

                TextView textViewDegreeLevel = educationItemView.findViewById(R.id.textViewDegreeLevel);
                TextView textViewInstitution = educationItemView.findViewById(R.id.textViewInstitution);
                TextView textViewPeriod = educationItemView.findViewById(R.id.textViewPeriod);

                // Set the education details
                textViewDegreeLevel.setText(education.getDegreeLevel());
                textViewInstitution.setText(education.getInstitution());

                // Format the period (start date - end date)
                String period = education.getStartDate() + " - " + education.getEndDate();
                textViewPeriod.setText(period);

                // Add a divider after each item except the last one
                if (educationList.indexOf(education) < educationList.size() - 1) {
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) getResources().getDimension(R.dimen.divider_height)));
                    divider.setBackgroundColor(getResources().getColor(R.color.divider_color));

                    educationItemsContainer.addView(educationItemView);
                    educationItemsContainer.addView(divider);
                } else {
                    educationItemsContainer.addView(educationItemView);
                }
            }
        } else {
            // Hide education section if no entries are available
            cardEducation.setVisibility(View.GONE);
        }
    }

    private void loadExperience() {
        // Ensure table exists
        experienceDbHelper.ensureTableExists();

        // Get experience entries from database
        List<Experience> experienceList = experienceDbHelper.getAllExperience();

        if (experienceList != null && !experienceList.isEmpty()) {
            // Show experience section
            cardExperience.setVisibility(View.VISIBLE);

            // Clear any existing experience items
            experienceItemsContainer.removeAllViews();

            // Add each experience entry to the layout
            for (Experience experience : experienceList) {
                View experienceItemView = LayoutInflater.from(this).inflate(
                        R.layout.experience_item_display, experienceItemsContainer, false);

                TextView textViewCompany = experienceItemView.findViewById(R.id.textViewCompany);
                TextView textViewPosition = experienceItemView.findViewById(R.id.textViewPosition);
                TextView textViewPeriod = experienceItemView.findViewById(R.id.textViewPeriod);
                TextView textViewDescription = experienceItemView.findViewById(R.id.textViewDescription);

                // Set the experience details
                textViewCompany.setText(experience.getCompany());
                textViewPosition.setText(experience.getPosition());

                // Format the period (start date - end date)
                String period = experience.getStartDate() + " - " + experience.getEndDate();
                textViewPeriod.setText(period);

                // Set description (if available)
                if (experience.getDescription() != null && !experience.getDescription().isEmpty()) {
                    textViewDescription.setText(experience.getDescription());
                    textViewDescription.setVisibility(View.VISIBLE);
                } else {
                    textViewDescription.setVisibility(View.GONE);
                }

                // Add a divider after each item except the last one
                if (experienceList.indexOf(experience) < experienceList.size() - 1) {
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) getResources().getDimension(R.dimen.divider_height)));
                    divider.setBackgroundColor(getResources().getColor(R.color.divider_color));

                    experienceItemsContainer.addView(experienceItemView);
                    experienceItemsContainer.addView(divider);
                } else {
                    experienceItemsContainer.addView(experienceItemView);
                }
            }
        } else {
            // Hide experience section if no entries are available
            cardExperience.setVisibility(View.GONE);
        }
    }
    private void loadCertifications() {
        // Ensure table exists
        certificationDbHelper.ensureTableExists();

        // Get certification entries from database
        List<Certification> certificationList = certificationDbHelper.getAllCertifications();

        if (certificationList != null && !certificationList.isEmpty()) {
            // Show certification section
            cardCertification.setVisibility(View.VISIBLE);

            // Clear any existing certification items
            certificationItemsContainer.removeAllViews();

            // Add each certification entry to the layout
            for (Certification certification : certificationList) {
                View certificationItemView = LayoutInflater.from(this).inflate(
                        R.layout.certification_item_display, certificationItemsContainer, false);

                TextView textViewTitle = certificationItemView.findViewById(R.id.textViewCertificationTitle);
                TextView textViewPeriod = certificationItemView.findViewById(R.id.textViewCertificationPeriod);

                // Set the certification details
                textViewTitle.setText(certification.getTitle());

                // Format the period (start date - end date)
                String period = certification.getStartDate();
                if (certification.getEndDate() != null && !certification.getEndDate().isEmpty()) {
                    period += " - " + certification.getEndDate();
                }
                textViewPeriod.setText(period);

                // Add a divider after each item except the last one
                if (certificationList.indexOf(certification) < certificationList.size() - 1) {
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) getResources().getDimension(R.dimen.divider_height)));
                    divider.setBackgroundColor(getResources().getColor(R.color.divider_color));

                    certificationItemsContainer.addView(certificationItemView);
                    certificationItemsContainer.addView(divider);
                } else {
                    certificationItemsContainer.addView(certificationItemView);
                }
            }
        } else {
            // Hide certification section if no entries are available
            cardCertification.setVisibility(View.GONE);
        }
    }
    private void loadReferences() {
        // Ensure table exists
        referencesDbHelper.ensureTableExists();

        // Get reference entries from database
        List<Reference> referencesList = referencesDbHelper.getAllReferences();

        if (referencesList != null && !referencesList.isEmpty()) {
            // Show references section
            cardReferences.setVisibility(View.VISIBLE);

            // Clear any existing reference items
            referencesItemsContainer.removeAllViews();

            // Add each reference entry to the layout
            for (Reference reference : referencesList) {
                View referenceItemView = LayoutInflater.from(this).inflate(
                        R.layout.reference_item_display, referencesItemsContainer, false);

                TextView textViewName = referenceItemView.findViewById(R.id.textViewName);
                TextView textViewPosition = referenceItemView.findViewById(R.id.textViewPosition);
                TextView textViewOrganization = referenceItemView.findViewById(R.id.textViewOrganization);
                TextView textViewContact = referenceItemView.findViewById(R.id.textViewContact);

                // Set the reference details
                textViewName.setText(reference.getName());
                textViewPosition.setText(reference.getPosition());
                textViewOrganization.setText(reference.getOrganization());

                // Format contact info (email and phone)
                String contactInfo = reference.getEmail();
                if (reference.getPhone() != null && !reference.getPhone().isEmpty()) {
                    if (!contactInfo.isEmpty()) {
                        contactInfo += " | ";
                    }
                    contactInfo += reference.getPhone();
                }
                textViewContact.setText(contactInfo);

                // Add a divider after each item except the last one
                if (referencesList.indexOf(reference) < referencesList.size() - 1) {
                    View divider = new View(this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) getResources().getDimension(R.dimen.divider_height)));
                    divider.setBackgroundColor(getResources().getColor(R.color.divider_color));

                    referencesItemsContainer.addView(referenceItemView);
                    referencesItemsContainer.addView(divider);
                } else {
                    referencesItemsContainer.addView(referenceItemView);
                }
            }
        } else {
            // Hide references section if no entries are available
            cardReferences.setVisibility(View.GONE);
        }
    }
}