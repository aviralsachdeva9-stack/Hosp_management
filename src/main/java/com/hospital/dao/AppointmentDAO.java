package com.hospital.dao;

// AppointmentDAO - appointments table ke saare operations.
// book, cancel, markCompleted, aur fetch methods hain.
// JOIN use karta hai taaki patient aur doctor ka naam bhi aaye.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hospital.db.DBConnect;
import com.hospital.model.Appointment;

public class AppointmentDAO {

    // Naya appointment book karna.
    // Returns naya appointment_id.
    public int book(int patientId, int doctorId, String date,
                    String time, String notes) throws SQLException {

        String sql = "INSERT INTO appointments (patient_id, doc_id, appointment_date, appointment_time, reason) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, notes);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Appointment book hua lekin ID nahi mili");
    }

    // Appointment cancel karna - sirf 'Scheduled' appointments cancel ho sakti hain.
    // Row DELETE hoti hai (soft delete nahi).
    // Returns true agar cancel hua, false agar nahi mila ya already Completed/Cancelled.
    public boolean cancel(int appointmentId) throws SQLException {

        // Status check bhi WHERE mein daal di taaki sirf Scheduled hi delete ho
        String sql = "DELETE FROM appointments WHERE appointment_id = ? AND status = 'Scheduled'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            int rows = ps.executeUpdate();
            return rows > 0; // 1 matlab cancel hua, 0 matlab nahi hua
        }
    }

    public boolean cancel(int appointmentId, int patientId) throws SQLException {
        String sql = "DELETE FROM appointments WHERE appointment_id = ? "
                   + "AND patient_id = ? AND status = 'Scheduled'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, patientId);
            return ps.executeUpdate() > 0;
        }
    }

    // Appointment ko 'Completed' mark karna - doctor ke liye.
    // Returns true agar update hua.
    public boolean markCompleted(int appointmentId) throws SQLException {

        String sql = "UPDATE appointments SET status = 'Completed' WHERE appointment_id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public boolean markCompleted(int appointmentId, int doctorId) throws SQLException {
        String sql = "UPDATE appointments SET status = 'Completed' "
                   + "WHERE appointment_id = ? AND doc_id = ? AND status = 'Scheduled'";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, doctorId);
            return ps.executeUpdate() > 0;
        }
    }

    // Patient ki saari appointments fetch karna (newest pehle).
    // JOIN se doctor ka naam bhi aa jaata hai.
    public List<Appointment> getForPatient(int patientId) throws SQLException {

        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id = p.patient_id "
                   + "JOIN doctors  d ON a.doc_id      = d.doc_id "
                   + "WHERE a.patient_id = ? "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        return fetchList(sql, patientId);
    }

    // Doctor ki saari appointments fetch karna.
    // JOIN se patient ka naam bhi aa jaata hai.
    public List<Appointment> getForDoctor(int doctorId) throws SQLException {

        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id = p.patient_id "
                   + "JOIN doctors  d ON a.doc_id      = d.doc_id "
                   + "WHERE a.doc_id = ? "
                   + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        return fetchList(sql, doctorId);
    }

    // Doctor ke aaj ke appointments - dashboard ke liye.
    // CURDATE() MySQL ki built-in function hai jo aaj ki date deti hai.
    public List<Appointment> getForDoctorToday(int doctorId) throws SQLException {

        String sql = "SELECT a.*, p.full_name AS patient_name, d.full_name AS doctor_name "
                   + "FROM appointments a "
                   + "JOIN patients p ON a.patient_id = p.patient_id "
                   + "JOIN doctors  d ON a.doc_id      = d.doc_id "
                   + "WHERE a.doc_id = ? AND a.appointment_date = CURDATE() "
                   + "ORDER BY a.appointment_time ASC";

        return fetchList(sql, doctorId);
    }

    // Ek doctor ki ek particular date ke already booked time slots.
    // Naye appointment book karte waqt conflict avoid karne ke liye.
    // Returns list of "HH:MM:SS" strings.
    public List<String> getBookedTimes(int doctorId, String date) throws SQLException {

        String sql = "SELECT appointment_time FROM appointments "
                   + "WHERE doc_id = ? AND appointment_date = ? AND status = 'Scheduled'";

        List<String> times = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setString(2, date);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    times.add(rs.getString("appointment_time"));
                }
            }
        }

        return times;
    }

    // --- Private helper ---

    // Ek shared method jo SQL + ek int param se List<Appointment> return karta hai.
    // getForPatient aur getForDoctor dono iski madad lete hain - duplication avoid.
    private List<Appointment> fetchList(String sql, int id) throws SQLException {

        List<Appointment> list = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildAppointment(rs));
                }
            }
        }

        return list;
    }

    // ResultSet ki row se Appointment object banana
    private Appointment buildAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doc_id"));
        a.setApptDate(rs.getString("appointment_date"));
        a.setApptTime(rs.getString("appointment_time"));
        a.setStatus(rs.getString("status"));
        a.setNotes(rs.getString("reason"));
        a.setPatientName(rs.getString("patient_name"));
        a.setDoctorName(rs.getString("doctor_name"));
        return a;
    }
}

