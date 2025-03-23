package com.example.cv_builder;

public class PersonalDetails {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String linkedin;

    // Default constructor
    public PersonalDetails() {
    }

    // Constructor without ID (for creating new entry)
    public PersonalDetails(String fullName, String email, String phone, String address, String linkedin) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.linkedin = linkedin;
    }

    // Constructor with ID (for retrieving from database)
    public PersonalDetails(long id, String fullName, String email, String phone, String address, String linkedin, String summary) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.linkedin = linkedin;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
}