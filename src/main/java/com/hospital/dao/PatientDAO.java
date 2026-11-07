package com.hospital.dao;

// PatientDAO - patients table se data padhna aur update karna.
// Schema truth:
//   patients columns: patient_id, user_id, full_name, contact_number,
//                     dob, gender, blood_group, address
//   Email patients mein NAHI hai - users table se JOIN karo.
//
// Isliye getByUserId aur searchByName mein JOIN lagaya hai:
//   JOIN users ON users.user_id = patients.user_id
// Taaki email bhi aa sake Patient object mein.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hospital.db.DBConnect;
import com.hospital.model.Patient;

public class PatientDAO {

    // user_id se patient fetch karna. JOIN se email bhi milega.
    // Login ke baad Session.currentPatient set karne ke liye use hota hai.
    public Patient getByUserId(int userId) throws SQLException {

        // JOIN zaroori hai kyunki email sirf users table mein hai
        String sql = "SELECT p.*, u.email "
                   + "FROM patients p "
                   + "JOIN users u ON u.user_id = p.user_id "
                   + "WHERE p.user_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildPatient(rs);
                }
            }
        }

        return null;
    }

    // Naam se patient search karna - partial match (LIKE).
    // JOIN se email bhi milega.
    public List<Patient> searchByName(String name) throws SQLException {

        String sql = "SELECT p.*, u.email "
                   + "FROM patients p "
                   + "JOIN users u ON u.user_id = p.user_id "
                   + "WHERE p.full_name LIKE ?";

        List<Patient> list = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildPatient(rs));
                }
            }
        }

        return list;
    }

    // Patient profile update karna - sirf patients table ke columns.
    // Email update nahi hota kyunki wo users table mein hai.
    public boolean updateProfile(int patientId, String fullName, String contactNumber,
                                  String dob, String gender, String bloodGroup,
                                  String address) throws SQLException {

        // Email yahan nahi hai - patients table mein column hi nahi
        String sql = "UPDATE patients SET full_name = ?, contact_number = ?, dob = ?, "
                   + "gender = ?, blood_group = ?, address = ? WHERE patient_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, contactNumber);
            if (dob == null || dob.trim().isEmpty()) {
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setString(3, dob);
            }
            ps.setString(4, gender);
            ps.setString(5, bloodGroup);
            ps.setString(6, address);
            ps.setInt(7, patientId);

            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // --- Private helper ---

    // ResultSet ki row se Patient object banana.
    // Email u.email alias se aata hai (JOIN ki wajah se).
    private Patient buildPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("patient_id"));
        p.setFullName(rs.getString("full_name"));
        p.setEmail(rs.getString("email"));         // u.email from JOIN
        p.setContactNumber(rs.getString("contact_number"));
        p.setDob(rs.getString("dob"));
        p.setGender(rs.getString("gender"));
        p.setBloodGroup(rs.getString("blood_group"));
        p.setAddress(rs.getString("address"));
        return p;
    }
}
