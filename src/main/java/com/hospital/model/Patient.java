package com.hospital.model;

// Patient class - Person ka child.
// Patient ke extra fields yahan hain: DOB, gender, blood group, address.
// 'patients' table ke columns se match karta hai.

public class Patient extends Person {

    private String dob;         // Date of birth, "YYYY-MM-DD" format
    private String gender;      // "Male", "Female", "Other"
    private String bloodGroup;  // e.g. "O+", "AB-"
    private String address;

    // Default constructor
    public Patient() {}

    // Pura constructor - sab fields ek saath set karne ke liye
    public Patient(int id, String fullName, String email, String contactNumber,
                   String dob, String gender, String bloodGroup, String address) {
        super(id, fullName, email, contactNumber);
        this.dob        = dob;
        this.gender     = gender;
        this.bloodGroup = bloodGroup;
        this.address    = address;
    }

    // Person ka abstract method implement karna zaroori hai
    @Override
    public String getRole() {
        return "Patient";
    }

    // --- Getters ---

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    // --- Setters ---

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

