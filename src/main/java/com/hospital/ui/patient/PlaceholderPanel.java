package com.hospital.ui.patient;

// PlaceholderPanel.java - Temporarily dikhane ke liye simple panel.
// Stage 3 mein My Appointments, Medical History aur Profile se replace hoga.

import com.hospital.ui.Theme;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {

    // Message dikhao centered
    public PlaceholderPanel(String message) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        JLabel lbl = new JLabel(message, SwingConstants.CENTER);
        lbl.setFont(Theme.fontSection());
        lbl.setForeground(Theme.MUTED);
        add(lbl, BorderLayout.CENTER);
    }
}

