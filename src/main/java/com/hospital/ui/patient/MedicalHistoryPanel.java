package com.hospital.ui.patient;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.hospital.dao.RecordDAO;
import com.hospital.model.MedicalRecord;
import com.hospital.ui.Theme;
import com.hospital.util.Session;

// Patient ke diagnosis aur prescription records dikhata hai.
public class MedicalHistoryPanel extends JPanel {
    private final RecordDAO dao = new RecordDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Record Date", "Doctor", "Diagnosis", "Prescription"}, 0) {
        // Medical history table ko read-only rakhta hai.
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    // Patient history ke liye table aur scroll area banata hai.
    public MedicalHistoryPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(36, 40, 32, 40));
        add(Theme.pageTitle("Medical History"), BorderLayout.NORTH);
        JTable table = new JTable(model);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadRecords();
    }

    // Logged-in patient ke records database se load karta hai.
    private void loadRecords() {
        try {
            List<MedicalRecord> records = dao.getForPatient(Session.currentPatient.getId());
            for (MedicalRecord record : records) {
                model.addRow(new Object[]{record.getRecordDate(), record.getDoctorName(),
                        record.getDiagnosis(), record.getPrescription()});
            }
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "History load nahi hui: " + ex.getMessage(),
                    "Medical History", JOptionPane.WARNING_MESSAGE);
        }
    }
}