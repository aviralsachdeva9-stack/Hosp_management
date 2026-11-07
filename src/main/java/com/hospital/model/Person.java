package com.hospital.model;

// Yeh abstract class hai - seedha object nahi ban sakta.
// Patient aur Doctor dono isse extend karte hain.
// Common fields (id, name, email, phone) yahan rakhte hain
// taaki baar baar likhna na pade (DRY principle).

public abstract class Person {

    // Private fields - sirf getters/setters se access hoga
    private int    id;
    private String fullName;
    private String email;
    private String contactNumber;

    // Default constructor - zaruri hai subclasses ke liye
    public Person() {}

    // Sab fields ek saath set karne ka constructor
    public Person(int id, String fullName, String email, String contactNumber) {
        this.id            = id;
        this.fullName      = fullName;
        this.email         = email;
        this.contactNumber = contactNumber;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    // --- Setters ---

    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    // Abstract method - har subclass batayega apna role kya hai
    // e.g. "Patient" ya "Doctor"
    public abstract String getRole();
}

