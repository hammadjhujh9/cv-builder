package com.example.cv_builder;

public class Education {
    private long id;
    private String degreeLevel;
    private String institution;
    private String startDate;
    private String endDate;

    // Constants for degree levels
    public static final String LEVEL_MATRIC = "Matriculation";
    public static final String LEVEL_INTER = "Intermediate";
    public static final String LEVEL_BACHELORS = "Bachelors";

    // Default constructor
    public Education() {
    }

    // Constructor without ID (for creating new entries)
    public Education(String degreeLevel, String institution, String startDate, String endDate) {
        this.degreeLevel = degreeLevel;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor with ID (for retrieving from database)
    public Education(long id, String degreeLevel, String institution, String startDate, String endDate) {
        this.id = id;
        this.degreeLevel = degreeLevel;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}