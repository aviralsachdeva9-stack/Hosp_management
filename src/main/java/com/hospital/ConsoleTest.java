package com.hospital;

// ConsoleTest - poora flow test karne ke liye.
// Koi UI nahi - sirf System.out se PASS / FAIL print karta hai.
// Flow: register -> login -> book appointment -> complete -> add record -> history print.
// Yeh file UI banana shuru karne se pehle backend verify karne ke liye hai.

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.RecordDAO;
import com.hospital.dao.UserDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.util.Session;

import java.sql.SQLException;
import java.util.List;

public class ConsoleTest {

    // DAO objects - test methods mein share karne ke liye static
    static UserDAO        userDAO        = new UserDAO();
    static PatientDAO     patientDAO     = new PatientDAO();
    static DoctorDAO      doctorDAO      = new DoctorDAO();
    static AppointmentDAO appointmentDAO = new AppointmentDAO();
    static RecordDAO      recordDAO      = new RecordDAO();

    // IDs store karne ke liye - ek test ka result doosre mein use hoga
    static int patientUserId = -1;
    static int doctorUserId  = -1;
    static int patientId     = -1;
    static int doctorId      = -1;
    static int appointmentId = -1;
    static int recordId      = -1;

    // Yahan se program shuru hota hai
    public static void main(String[] args) {

        System.out.println("===========================================");
        System.out.println("   HOSPITAL MANAGEMENT - CONSOLE TEST");
        System.out.println("===========================================\n");

        testRegisterPatient();
        testRegisterDoctor();
        testLoginPatient();
        testLoginDoctor();
        testBookAppointment();
        testMarkCompleted();
        testAddMedicalRecord();
        testPrintHistory();
        testDuplicateEmail();

        System.out.println("\n===========================================");
        System.out.println("   SAARE TESTS COMPLETE");
        System.out.println("===========================================");
    }

    // --- Test methods ---
    // Har method ek kaam test karta hai aur PASS / FAIL print karta hai

    // Patient register karna
    static void testRegisterPatient() {
        System.out.print("TEST: Patient Register ... ");
        try {
            patientUserId = userDAO.registerPatient(
                "Rahul Sharma",
                "rahul_test_" + System.currentTimeMillis() + "@example.com", // Unique email
                "Secret123",
                "9876543210",
                "2000-05-15",
                "Male",
                "O+",
                "123, MG Road, Delhi"
            );
            System.out.println("PASS (user_id = " + patientUserId + ")");
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Doctor register karna
    static void testRegisterDoctor() {
        System.out.print("TEST: Doctor Register  ... ");
        try {
            doctorUserId = userDAO.registerDoctor(
                "Dr. Priya Verma",
                "drpriya_test_" + System.currentTimeMillis() + "@example.com",
                "DocPass456",
                "9123456780",
                "Cardiology",
                8
            );
            System.out.println("PASS (user_id = " + doctorUserId + ")");
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Patient login karna aur Session mein save karna
    static void testLoginPatient() {
        System.out.print("TEST: Patient Login    ... ");

        // Pehle patient ka email fetch karna padega - hum directly DB se nahi padhenge
        // isliye getByUserId se profile lete hain
        try {
            Patient p = patientDAO.getByUserId(patientUserId);
            if (p == null) {
                System.out.println("FAIL - Patient profile nahi mila");
                return;
            }

            // Ab login try karo
            int uid = userDAO.login(p.getEmail(), "Secret123", "Patient");
            if (uid != -1) {
                Session.login(uid, "Patient");
                Session.currentPatient = p;
                patientId = p.getId(); // patient_id (not user_id) appointments ke liye
                System.out.println("PASS (Session set: userId=" + uid + ", name=" + p.getFullName() + ")");
            } else {
                System.out.println("FAIL - Login ne -1 return kiya");
            }
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Doctor login karna
    static void testLoginDoctor() {
        System.out.print("TEST: Doctor Login     ... ");
        try {
            Doctor d = doctorDAO.getByUserId(doctorUserId);
            if (d == null) {
                System.out.println("FAIL - Doctor profile nahi mila");
                return;
            }

            int uid = userDAO.login(d.getEmail(), "DocPass456", "Doctor");
            if (uid != -1) {
                Session.login(uid, "Doctor");
                Session.currentDoctor = d;
                doctorId = d.getId();
                System.out.println("PASS (Session set: userId=" + uid + ", name=" + d.getFullName() + ")");
            } else {
                System.out.println("FAIL - Login ne -1 return kiya");
            }
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Appointment book karna
    static void testBookAppointment() {
        System.out.print("TEST: Book Appointment ... ");
        if (patientId == -1 || doctorId == -1) {
            System.out.println("SKIP - Patient ya Doctor ID nahi mila (pehle wale tests fail hue)");
            return;
        }
        try {
            appointmentId = appointmentDAO.book(
                patientId,
                doctorId,
                "2026-12-01",   // Date
                "10:00:00",     // Time
                "Routine checkup"
            );
            System.out.println("PASS (appointment_id = " + appointmentId + ")");
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Appointment complete mark karna
    static void testMarkCompleted() {
        System.out.print("TEST: Mark Completed   ... ");
        if (appointmentId == -1) {
            System.out.println("SKIP - Appointment nahi bani");
            return;
        }
        try {
            boolean ok = appointmentDAO.markCompleted(appointmentId);
            System.out.println(ok ? "PASS" : "FAIL - 0 rows updated");
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Medical record add karna
    static void testAddMedicalRecord() {
        System.out.print("TEST: Add Medical Rec  ... ");
        if (patientId == -1 || doctorId == -1) {
            System.out.println("SKIP");
            return;
        }
        try {
            recordId = recordDAO.add(
                patientId,
                doctorId,
                appointmentId,          // Linked appointment
                "Blood Pressure High",  // Diagnosis
                "Amlodipine 5mg daily", // Prescription
                "2026-12-01"
            );
            System.out.println("PASS (record_id = " + recordId + ")");
        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Patient ki history print karna
    static void testPrintHistory() {
        System.out.println("\n--- PATIENT KI APPOINTMENT HISTORY ---");
        if (patientId == -1) {
            System.out.println("SKIP");
            return;
        }
        try {
            List<Appointment> appointments = appointmentDAO.getForPatient(patientId);

            // Plain for loop - no streams
            for (int i = 0; i < appointments.size(); i++) {
                Appointment a = appointments.get(i);
                System.out.println((i + 1) + ". Date: " + a.getApptDate()
                        + " | Time: " + a.getApptTime()
                        + " | Doctor: " + a.getDoctorName()
                        + " | Status: " + a.getStatus());
            }

            System.out.println("\n--- PATIENT KI MEDICAL HISTORY ---");
            List<MedicalRecord> records = recordDAO.getForPatient(patientId);

            for (int i = 0; i < records.size(); i++) {
                MedicalRecord r = records.get(i);
                System.out.println((i + 1) + ". Date: " + r.getRecordDate()
                        + " | Doctor: " + r.getDoctorName()
                        + " | Diagnosis: " + r.getDiagnosis()
                        + " | Prescription: " + r.getPrescription());
            }

        } catch (SQLException e) {
            System.out.println("FAIL - " + e.getMessage());
        }
    }

    // Duplicate email register karna - error aana chahiye
    static void testDuplicateEmail() {
        System.out.print("\nTEST: Duplicate Email  ... ");
        try {
            // Pehle patient ka email dobara register karne ki koshish
            Patient p = patientDAO.getByUserId(patientUserId);
            if (p == null) {
                System.out.println("SKIP");
                return;
            }

            userDAO.registerPatient(
                "Duplicate User",
                p.getEmail(), // Same email - error aana chahiye
                "AnyPass123",
                "9999999999",
                "1990-01-01",
                "Female",
                "A+",
                "Some Address"
            );

            // Agar yahan pahuncha to test FAIL hai (error aana chahiye tha)
            System.out.println("FAIL - Duplicate email accept ho gayi (nahi honi chahiye thi)");

        } catch (SQLException e) {
            if (e.getMessage().contains("Email already registered")) {
                System.out.println("PASS (Correct error: " + e.getMessage() + ")");
            } else {
                System.out.println("FAIL - Galat error: " + e.getMessage());
            }
        }
    }
}

