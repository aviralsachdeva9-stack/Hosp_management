package com.hospital.dao;

// UserDAO - sirf registration aur login ka kaam karta hai.
// Schema truth:
//   users   : user_id, email, password, role, created_at
//   patients: patient_id, user_id, full_name, contact_number, dob, gender, blood_group, address
//   doctors : doctor_id, user_id, full_name, contact_number, specialization, experience_years
//
// IMPORTANT: patients aur doctors tables mein email column NAHI hai.
// Email sirf users table mein hai. Isliye INSERT mein email nahi daalta.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hospital.db.DBConnect;
import com.hospital.util.PasswordUtil;

public class UserDAO {

    // Patient register karna - users + patients table mein ek transaction.
    // users mein: email, password, role
    // patients mein: user_id, full_name, contact_number, dob, gender, blood_group, address
    // NOTE: email patients mein nahi jaata - schema mein column hi nahi hai.
    public int registerPatient(String fullName, String email, String password,
                               String contactNumber, String dob,
                               String gender, String bloodGroup,
                               String address) throws SQLException {

        String insertUser = "INSERT INTO users (email, password, role) VALUES (?, ?, 'Patient')";

        // patients table mein SIRF yeh columns hain - email nahi
        String insertPatient = "INSERT INTO patients "
                + "(user_id, full_name, contact_number, dob, gender, blood_group, address) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = insertUserRow(conn, insertUser, email, password);
                insertPatientRow(conn, insertPatient, userId, fullName,
                        contactNumber, dob, gender, bloodGroup, address);
                conn.commit();
                return userId;
            } catch (SQLException e) {
                conn.rollback();
                handleDuplicateEmail(e);
                throw e;
            }
        }
    }

    // Doctor register karna - users + doctors table mein ek transaction.
    // doctors table mein SIRF yeh columns hain - email nahi.
    public int registerDoctor(String fullName, String email, String password,
                              String contactNumber, String specialization,
                              int experienceYears) throws SQLException {

        String insertUser = "INSERT INTO users (email, password, role) VALUES (?, ?, 'Doctor')";

        // doctors table mein SIRF yeh columns hain - email nahi
        String insertDoctor = "INSERT INTO doctors "
                + "(user_id, full_name, contact_number, specialization, experience_years) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = insertUserRow(conn, insertUser, email, password);
                insertDoctorRow(conn, insertDoctor, userId, fullName,
                        contactNumber, specialization, experienceYears);
                conn.commit();
                return userId;
            } catch (SQLException e) {
                conn.rollback();
                handleDuplicateEmail(e);
                throw e;
            }
        }
    }

    // Login check karna - email users table mein hai, wahan se check karo.
    // Returns user_id agar credentials sahi hain, -1 agar galat.
    public int login(String email, String password, String role) throws SQLException {

        // Email se search karo taaki role aur user row ko independently check kar sakein
        String sql = "SELECT user_id, password, role FROM users WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email != null ? email.trim() : "");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbRole = rs.getString("role");
                    String storedHash = rs.getString("password");
                    boolean bcryptMatch = PasswordUtil.check(password, storedHash);

                    boolean roleMatch = dbRole != null && dbRole.equalsIgnoreCase(role != null ? role.trim() : "");

                    if (roleMatch && bcryptMatch) {
                        return rs.getInt("user_id");
                    }
                }
            }
        }

        return -1;
    }

    // --- Private helpers ---

    // users table mein row insert karta hai, generated user_id return karta hai.
    // Sirf 2 params: email aur hashed password. Role SQL literal mein hai.
    private int insertUserRow(Connection conn, String sql,
                               String email, String password) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, PasswordUtil.hash(password));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("User insert ke baad generated ID nahi mili");
    }

    // patients table mein row insert karta hai.
    // Email parameter NAHI hai - schema mein column hi nahi.
    private void insertPatientRow(Connection conn, String sql, int userId,
                                   String fullName, String contactNumber,
                                   String dob, String gender, String bloodGroup,
                                   String address) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, contactNumber);
            if (dob == null || dob.trim().isEmpty()) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setString(4, dob);
            }
            ps.setString(5, gender);
            ps.setString(6, bloodGroup);
            ps.setString(7, address);
            ps.executeUpdate();
        }
    }

    // doctors table mein row insert karta hai.
    // Email parameter NAHI hai - schema mein column hi nahi.
    private void insertDoctorRow(Connection conn, String sql, int userId,
                                  String fullName, String contactNumber,
                                  String specialization, int experienceYears) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, fullName);
            ps.setString(3, contactNumber);
            ps.setString(4, specialization);
            ps.setInt(5, experienceYears);
            ps.executeUpdate();
        }
    }

    // MySQL error 1062 = Duplicate Entry (UNIQUE constraint fail on email)
    private void handleDuplicateEmail(SQLException e) throws SQLException {
        if (e.getErrorCode() == 1062) {
            throw new SQLException("Email already registered");
        }
    }
}
