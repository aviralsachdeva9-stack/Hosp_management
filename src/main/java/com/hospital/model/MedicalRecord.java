package com.hospital.model;

// MedicalRecord class - doctor ke notes ek appointment ke baad.
// 'medical_records' table ke columns se match karta hai.
// patientName aur doctorName UI ke liye extra rakhe hain.

public class MedicalRecord {

    private int    recordId;
    private int    patientId;
    private int    doctorId;
    private int    appointmentId;   // 0 agar koi appointment nahi
    private String diagnosis;
    private String prescription;
    private String recordDate;      // "YYYY-MM-DD"

    // JOIN se milenge - UI mein naam dikhane ke liye
    private String patientName;
    private String doctorName;

    // Default constructor
    public MedicalRecord() {}

    // --- Getters ---

    public int getRecordId() {
        return recordId;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    // --- Setters ---

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}

