package com.hospital.model;

// Appointment class - patient aur doctor ki ek meeting.
// 'appointments' table ke har column ke liye ek field hai.
// patientName aur doctorName extra rakhte hain
// taaki UI ko dobara query na karni pade (JOIN se milenge).

public class Appointment {

    private int    appointmentId;
    private int    patientId;
    private int    doctorId;
    private String apptDate;    // "YYYY-MM-DD"
    private String apptTime;    // "HH:MM:SS"
    private String status;      // "Scheduled", "Completed", "Cancelled"
    private String notes;

    // Yeh do fields DB mein nahi hain, JOIN se aate hain
    // UI mein naam dikhane ke liye zaroori hain
    private String patientName;
    private String doctorName;

    // Default constructor
    public Appointment() {}

    // --- Getters ---

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getApptDate() {
        return apptDate;
    }

    public String getApptTime() {
        return apptTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    // --- Setters ---

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public void setApptTime(String apptTime) {
        this.apptTime = apptTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}

