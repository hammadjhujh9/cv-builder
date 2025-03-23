package com.example.cv_builder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfilePictureActivity extends AppCompatActivity {

    private ImageView imageViewProfile;
    private Button buttonGallery, buttonCamera, buttonSave, buttonCancel;
    private String currentPhotoPath;
    private ProfilePictureDatabaseHelper dbHelper;
    private boolean imageChanged = false;

    // Activity result launchers
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            // Save a copy of the image to app-specific storage
                            currentPhotoPath = saveImageFromUri(selectedImageUri);
                            displayProfileImage();
                            imageChanged = true;
                        } catch (IOException e) {
                            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    displayProfileImage();
                    imageChanged = true;
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // Permission granted, proceed with camera access
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_picture);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = ProfilePictureDatabaseHelper.getInstance(this);

        // Initialize views
        imageViewProfile = findViewById(R.id.imageViewProfile);
        buttonGallery = findViewById(R.id.buttonGallery);
        buttonCamera = findViewById(R.id.buttonCamera);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Load existing profile picture if it exists
        loadProfilePicture();

        // Set click listeners
        buttonGallery.setOnClickListener(v -> openGallery());
        buttonCamera.setOnClickListener(v -> checkCameraPermission());
        buttonSave.setOnClickListener(v -> {
            saveProfilePicture();
            finish();
        });
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadProfilePicture() {
        // Get profile picture from database
        ProfilePicture profilePicture = dbHelper.getProfilePicture();
        if (profilePicture != null) {
            currentPhotoPath = profilePicture.getImagePath();
            displayProfileImage();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            // Permission already granted
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.cv_builder.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file path for use with camera intent
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String saveImageFromUri(Uri uri) throws IOException {
        // Create a destination file
        File destFile = createImageFile();

        // Copy the image data to our app-specific storage
        try (InputStream is = getContentResolver().openInputStream(uri);
             FileOutputStream os = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }

        return destFile.getAbsolutePath();
    }

    private void displayProfileImage() {
        if (currentPhotoPath != null) {
            // Get dimensions for the ImageView
            int targetW = imageViewProfile.getWidth();
            int targetH = imageViewProfile.getHeight();

            // If ImageView dimensions are not set yet, use some default values
            if (targetW == 0) targetW = 300;
            if (targetH == 0) targetH = 300;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            imageViewProfile.setImageBitmap(bitmap);
        }
    }

    private void saveProfilePicture() {
        if (currentPhotoPath != null && imageChanged) {
            // Save to database
            dbHelper.saveProfilePicture(currentPhotoPath);
            Toast.makeText(this, "Profile picture saved", Toast.LENGTH_SHORT).show();
        }
    }
}