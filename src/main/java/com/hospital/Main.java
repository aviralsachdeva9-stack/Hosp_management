package com.hospital;

// Main.java - program ka entry point. Sirf ek kaam: app start karna.
// FlatLaf theme aur Inter font set karta hai, phir MainFrame dikhata hai.
// SwingUtilities.invokeLater: Swing thread pe safely run karne ke liye.

import com.formdev.flatlaf.FlatLightLaf;
import com.hospital.ui.MainFrame;
import com.hospital.ui.Theme;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // FlatLaf ko system look-and-feel set karna
        FlatLightLaf.setup();

        // Inter font install karna (Theme.java mein defined)
        Theme.installFonts();

        // Swing thread pe safely window banana aur dikhana
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

