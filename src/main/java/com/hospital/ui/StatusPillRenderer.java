package com.hospital.ui;

// StatusPillRenderer.java - table mein status column ke liye custom renderer.
// "Scheduled" -> amber pill, "Completed" -> green pill, "Cancelled" -> red pill.
// Yeh sirf paint() override karta hai - koi logic nahi.

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusPillRenderer extends DefaultTableCellRenderer {

    // Har cell draw hone se pehle yeh method call hota hai.
    // Status text ke hisaab se background aur foreground set karta hai.
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        String status = value == null ? "" : value.toString();

        // Pill ka background aur text color set karo
        lbl.setBackground(Theme.statusBgColor(status));
        lbl.setForeground(Theme.statusColor(status));
        lbl.setFont(new Font("Dialog", Font.BOLD, 12));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setOpaque(true);

        // Agar row selected hai to normal selection color use karo
        if (isSelected) {
            lbl.setBackground(Theme.ROW_HOVER);
            lbl.setForeground(Theme.statusColor(status));
        }

        return lbl;
    }
}

