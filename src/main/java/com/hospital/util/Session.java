package com.hospital.util;

// Session class - logged-in user ki info memory mein rakhna.
// Yeh ek global "notice board" ki tarah hai.
// Jab user login karta hai, yahan details save hoti hain.
// Jab logout karta hai, sab clear ho jata hai.
// static fields use kiye hain taaki koi bhi class seedha access kar sake.

import com.hospital.model.Doctor;
import com.hospital.model.Patient;

public class Session {

    // Currently logged-in user ka database ID
    public static int    currentUserId = -1;

    // Role: "Patient" ya "Doctor" (ya null agar logged out)
    public static String currentRole   = null;

    // Agar Patient login hai to unka object yahan hoga
    public static Patient currentPatient = null;

    // Agar Doctor login hai to unka object yahan hoga
    public static Doctor  currentDoctor  = null;

    // Login ke time call karo - sab values set ho jayegi
    public static void login(int userId, String role) {
        currentUserId = userId;
        currentRole   = role;
    }

    // Logout ke time call karo - sab reset ho jayega
    public static void logout() {
        currentUserId   = -1;
        currentRole     = null;
        currentPatient  = null;
        currentDoctor   = null;
    }

    // Check karna ki koi login hai ya nahi
    public static boolean isLoggedIn() {
        return currentUserId != -1;
    }
}

