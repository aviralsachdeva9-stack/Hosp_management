package com.hospital.dao;

// RecordDAO - medical_records table ke operations.
// Doctor appointment complete hone ke baad record add karta hai.
// Patient apni history dekh sakta hai.

import com.hospital.db.DBConnect;
import com.hospital.model.MedicalRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecordDAO {

    // Naya medical record add karna.
    // appointmentId 0 pass karo agar appointment nahi thi (walk-in).
    // Returns naya record_id.
    public int add(int patientId, int doctorId, int appointmentId,
                   String diagnosis, String prescription,
                   String recordDate) throws SQLException {

        String sql = "INSERT INTO medical_records "
                   + "(patient_id, doc_id, appointment_id, diagnosis_log, prescription, record_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);

            // appointment_id optional hai - 0 aya to NULL store karo
            if (appointmentId > 0) {
                ps.setInt(3, appointmentId);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setString(4, diagnosis);
            ps.setString(5, prescription);
            ps.setString(6, recordDate);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Record add hua lekin ID nahi mili");
    }

    // Patient ki poori medical history fetch karna (newest pehle).
    // JOIN se doctor ka naam bhi aa jaata hai.
    public List<MedicalRecord> getForPatient(int patientId) throws SQLException {

        String sql = "SELECT r.*, p.full_name AS patient_name, d.full_name AS doctor_name "
                   + "FROM medical_records r "
                   + "JOIN patients p ON r.patient_id = p.patient_id "
                   + "JOIN doctors  d ON r.doc_id      = d.doc_id "
                   + "WHERE r.patient_id = ? "
                   + "ORDER BY r.record_date DESC";

        List<MedicalRecord> list = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(buildRecord(rs));
                }
            }
        }

        return list;
    }

    // --- Private helper ---

    // ResultSet ki row se MedicalRecord object banana
    private MedicalRecord buildRecord(ResultSet rs) throws SQLException {
        MedicalRecord r = new MedicalRecord();
        r.setRecordId(rs.getInt("record_id"));
        r.setPatientId(rs.getInt("patient_id"));
        r.setDoctorId(rs.getInt("doc_id"));
        r.setAppointmentId(rs.getInt("appointment_id")); // 0 agar NULL tha
        r.setDiagnosis(rs.getString("diagnosis_log"));
        r.setPrescription(rs.getString("prescription"));
        r.setRecordDate(rs.getString("record_date"));
        r.setPatientName(rs.getString("patient_name"));
        r.setDoctorName(rs.getString("doctor_name"));
        return r;
    }
}

