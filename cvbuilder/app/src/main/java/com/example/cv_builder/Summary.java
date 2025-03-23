package com.example.cv_builder;

public class Summary {
    private long id;
    private String title;
    private String content;
    private boolean isDefault;

    // Default constructor
    public Summary() {
    }

    // Constructor without ID (for creating new entry)
    public Summary(String title, String content) {
        this.title = title;
        this.content = content;
        this.isDefault = false;
    }

    // Constructor with all fields
    public Summary(long id, String title, String content, boolean isDefault) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return title;
    }
}