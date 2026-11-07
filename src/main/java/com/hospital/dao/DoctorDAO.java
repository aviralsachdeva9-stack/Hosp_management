package com.hospital.dao;

// DoctorDAO - doctors table se data padhna aur update karna.
// Schema truth:
//   doctors columns: doctor_id, user_id, full_name, contact_number,
//                    specialization, experience_years
//   Email doctors mein NAHI hai - users table se JOIN karo.
//
// Isliye getAll, getBySpecialization, getByUserId mein JOIN lagaya hai:
//   JOIN users ON users.user_id = doctors.user_id
// Taaki email bhi aa sake Doctor object mein.

import com.hospital.db.DBConnect;
import com.hospital.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // Saare doctors fetch karna. JOIN se email bhi milega.
    public List<Doctor> getAll() throws SQLException {

        // JOIN zaroori hai - email sirf users table mein hai
        String sql = "SELECT d.*, u.email "
                   + "FROM doctors d "
                   + "JOIN users u ON u.user_id = d.user_id "
                   + "ORDER BY d.full_name";

        List<Doctor> list = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(buildDoctor(rs));
            }
        }

        return list;
    }

    // Specialization se doctors filter karna. JOIN se email bhi milega.
    public List<Doctor> getBySpecialization(String specialization) throws SQLException {

        String sql = "SELECT d.*, u.email "
                   + "FROM doctors d "
                   + "JOIN users u ON u.user_id = d.user_id "
                   + "WHERE d.specialization = ? "
                   + "ORDER BY d.full_name";

        List<Doctor> list = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, specialization);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildDoctor(rs));
                }
            }
        }

        return list;
    }

    // Distinct specializations ki list - dropdown ke liye.
    // Yahan JOIN nahi chahiye, sirf doctors table kaafi hai.
    public List<String> getAllSpecializations() throws SQLException {

        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization";
        List<String> list = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("specialization"));
            }
        }

        return list;
    }

    // user_id se doctor fetch karna - login ke baad Session set karne ke liye.
    // JOIN se email bhi milega.
    public Doctor getByUserId(int userId) throws SQLException {

        String sql = "SELECT d.*, u.email "
                   + "FROM doctors d "
                   + "JOIN users u ON u.user_id = d.user_id "
                   + "WHERE d.user_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return buildDoctor(rs);
                }
            }
        }

        return null;
    }

    // Doctor profile update - sirf doctors table ke columns.
    // Email update nahi hota kyunki wo users table mein hai.
    public boolean updateProfile(int doctorId, String fullName, String contactNumber,
                                  String specialization, int experienceYears) throws SQLException {

        // Email yahan nahi - doctors table mein column hi nahi
        String sql = "UPDATE doctors SET full_name = ?, contact_number = ?, "
                   + "specialization = ?, experience_years = ? WHERE doc_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, contactNumber);
            ps.setString(3, specialization);
            ps.setInt(4, experienceYears);
            ps.setInt(5, doctorId);

            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    // --- Private helper ---

    // ResultSet ki row se Doctor object banana.
    // Email u.email alias se aata hai (JOIN ki wajah se).
    private Doctor buildDoctor(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getInt("doc_id"));
        d.setFullName(rs.getString("full_name"));
        d.setEmail(rs.getString("email"));           // u.email from JOIN
        d.setContactNumber(rs.getString("contact_number"));
        d.setSpecialization(rs.getString("specialization"));
        d.setExperienceYears(rs.getInt("experience_years"));
        return d;
    }
}
