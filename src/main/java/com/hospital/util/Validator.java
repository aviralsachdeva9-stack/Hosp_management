package com.hospital.util;

// Validator - input data check karna UI se DAO ke beech.
// Simple static methods hain - koi object banana nahi padega.
// Yeh sirf check karta hai, error throw nahi karta -
// caller decide karega kya message dikhana hai.

public class Validator {

    // Email valid hai ya nahi - basic format check.
    // "@" aur "." hona zaroori hai.
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Simple regex: kuch@kuch.kuch
        return email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    // Phone number valid hai ya nahi.
    // 10 se 15 digits honi chahiye, optionally + se start ho sakta hai.
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return phone.matches("^\\+?[0-9]{10,15}$");
    }

    // Field khali hai ya nahi - null aur whitespace dono check karta hai.
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

