package com.hospital.util;

// PasswordUtil - BCrypt se password hash aur check karna.
// BCrypt ek one-way hashing algorithm hai.
// Same password har baar alag hash deta hai (salt ki wajah se),
// isliye equals() se compare nahi kar sakte - checkpw() use karte hain.

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Password ko hash karna - signup ke time use hota hai.
    // BCrypt automatically random salt add karta hai.
    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Entered password ko stored hash se compare karna - login ke time.
    // Returns true agar password sahi hai, false agar galat.
    public static boolean check(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword.trim());
        } catch (Exception e) {
            return false;
        }
    }
}
