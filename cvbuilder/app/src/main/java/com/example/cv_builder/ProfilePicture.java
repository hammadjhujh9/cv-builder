// ProfilePicture.java - Model class
package com.example.cv_builder;

public class ProfilePicture {
    private long id;
    private String imagePath;

    // Default constructor
    public ProfilePicture() {
    }

    // Constructor without ID (for creating new entries)
    public ProfilePicture(String imagePath) {
        this.imagePath = imagePath;
    }

    // Constructor with ID (for retrieving from database)
    public ProfilePicture(long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}