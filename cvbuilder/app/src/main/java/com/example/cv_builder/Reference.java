package com.example.cv_builder;

public class Reference {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String organization;
    private String position;

    // Default constructor
    public Reference() {
    }

    // Constructor without ID (for creating new entries)
    public Reference(String name, String email, String phone, String organization, String position) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
        this.position = position;
    }

    // Constructor with ID (for retrieving from database)
    public Reference(long id, String name, String email, String phone, String organization, String position) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.organization = organization;
        this.position = position;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}