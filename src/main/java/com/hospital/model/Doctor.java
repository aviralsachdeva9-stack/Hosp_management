package com.hospital.model;

// Doctor class - Person ka child.
// Doctor ke extra fields: specialization aur experience.
// 'doctors' table ke columns se match karta hai.

public class Doctor extends Person {

    private String specialization;   // e.g. "Cardiology", "Dentistry"
    private int    experienceYears;  // kitne saal ka experience hai

    // Default constructor
    public Doctor() {}

    // Pura constructor - sab fields ek saath
    public Doctor(int id, String fullName, String email, String contactNumber,
                  String specialization, int experienceYears) {
        super(id, fullName, email, contactNumber);
        this.specialization  = specialization;
        this.experienceYears = experienceYears;
    }

    // Person ka abstract method - batata hai yeh "Doctor" hai
    @Override
    public String getRole() {
        return "Doctor";
    }

    // --- Getters ---

    public String getSpecialization() {
        return specialization;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    // --- Setters ---

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
}

