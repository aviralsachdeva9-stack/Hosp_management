package com.hospital.ui;

// Theme.java - poori app ka design system.
// Colors, fonts, aur reusable UI helper methods SIRF yahan hain.
// Koi bhi screen Theme.BG, Theme.ACCENT etc. seedha use karti hai.
// Agar design change karna ho, sirf yeh file edit karo.

import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Theme {

    // ── Colors ──────────────────────────────────────────────
    public static final Color BG        = new Color(0xF7F5F0); // Page background
    public static final Color SIDEBAR   = new Color(0x14332E); // Left green sidebar
    public static final Color ACCENT    = new Color(0x1F7A5C); // Buttons, highlights
    public static final Color TEXT      = new Color(0x1C2321); // Main body text
    public static final Color MUTED     = new Color(0x6B7672); // Labels, hints
    public static final Color CARD      = Color.WHITE;         // Card background
    public static final Color BORDER    = new Color(0xE4E0D6); // Card border color
    public static final Color ROW_HOVER = new Color(0xF0EDE8); // Table hover row

    // Status pill colors
    public static final Color STATUS_SCHEDULED  = new Color(0xFFA500); // Amber
    public static final Color STATUS_COMPLETED  = new Color(0x2E7D32); // Green
    public static final Color STATUS_CANCELLED  = new Color(0xC62828); // Red
    public static final Color STATUS_SCHED_BG   = new Color(0xFFF3E0);
    public static final Color STATUS_COMP_BG    = new Color(0xE8F5E9);
    public static final Color STATUS_CANC_BG    = new Color(0xFFEBEE);

    // ── Sizes ────────────────────────────────────────────────
    public static final int RADIUS  = 14;  // Corner radius everywhere
    public static final int PAD     = 24;  // Standard padding everywhere
    public static final int ROW_H   = 44;  // Table row height

    // ── Fonts ────────────────────────────────────────────────
    // Inter font install karna - FlatLaf ke bundled fonts se
    public static void installFonts() {
        FlatInterFont.install();
        UIManager.put("defaultFont", new Font(FlatInterFont.FAMILY, Font.PLAIN, 14));
    }

    // Page title font - bada heading (28px bold)
    public static Font fontTitle() {
        return new Font(FlatInterFont.FAMILY, Font.BOLD, 28);
    }

    // Section heading font (18px semi-bold shown as BOLD in Java)
    public static Font fontSection() {
        return new Font(FlatInterFont.FAMILY, Font.BOLD, 18);
    }

    // Normal body text (14px)
    public static Font fontBody() {
        return new Font(FlatInterFont.FAMILY, Font.PLAIN, 14);
    }

    // Small muted labels (12px)
    public static Font fontSmall() {
        return new Font(FlatInterFont.FAMILY, Font.PLAIN, 12);
    }

    // ── Helper: Page Title Label ──────────────────────────────
    // Bada heading label banata hai - har screen ke top pe
    public static JLabel pageTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontTitle());
        lbl.setForeground(TEXT);
        return lbl;
    }

    // ── Helper: Section Label ─────────────────────────────────
    public static JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontSection());
        lbl.setForeground(TEXT);
        return lbl;
    }

    // ── Helper: Muted small label ─────────────────────────────
    public static JLabel mutedLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(fontSmall());
        lbl.setForeground(MUTED);
        return lbl;
    }

    // ── Helper: Primary Button (green accent) ─────────────────
    // Login, Confirm, Save jaise important buttons ke liye
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 14));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Helper: Secondary Button (outline style) ───────────────
    // Cancel, Back jaise secondary actions ke liye
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(fontBody());
        btn.setBackground(CARD);
        btn.setForeground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Helper: Link-style button ─────────────────────────────
    // "New here? Register" aur "Back to login" ke liye
    public static JButton linkButton(String text) {
        JButton btn = new JButton("<html><u>" + text + "</u></html>");
        btn.setFont(fontSmall());
        btn.setForeground(ACCENT);
        btn.setBackground(null);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Helper: Sidebar Button (menu items) ───────────────────
    // Left sidebar ke navigation buttons ke liye
    public static JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(fontBody());
        btn.setForeground(new Color(0xC8D8D4)); // Light green-white
        btn.setBackground(SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        return btn;
    }

    // Sidebar button active state (highlighted)
    public static void setSidebarActive(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(ACCENT);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(SIDEBAR);
            btn.setForeground(new Color(0xC8D8D4));
        }
    }

    // ── Helper: White card panel ──────────────────────────────
    // White background + 1px border - stat cards, sections ke liye
    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return panel;
    }

    // ── Helper: Standard form field styling ───────────────────
    public static JTextField styledField() {
        JTextField field = new JTextField();
        field.setFont(fontBody());
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    // ── Helper: Password field styling ───────────────────────
    public static JPasswordField styledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(fontBody());
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    // ── Helper: Styled ComboBox ───────────────────────────────
    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(fontBody());
        combo.setForeground(TEXT);
        combo.setBackground(Color.WHITE);
        return combo;
    }

    // ── Helper: Table styling ─────────────────────────────────
    // Row height 44, no vertical gridlines, light header
    public static void styleTable(JTable table) {
        table.setRowHeight(ROW_H);
        table.setFont(fontBody());
        table.setForeground(TEXT);
        table.setBackground(CARD);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(ROW_HOVER);
        table.setSelectionForeground(TEXT);
        table.setFocusable(false);
        table.getTableHeader().setReorderingAllowed(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(FlatInterFont.FAMILY, Font.BOLD, 12));
        header.setBackground(new Color(0xF0EDE8));
        header.setForeground(MUTED);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        // Center-align all columns by default
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    // ── Helper: Status pill color ──────────────────────────────
    // "Scheduled" -> amber, "Completed" -> green, "Cancelled" -> red
    public static Color statusColor(String status) {
        if ("Completed".equals(status))  return STATUS_COMPLETED;
        if ("Cancelled".equals(status))  return STATUS_CANCELLED;
        return STATUS_SCHEDULED; // Default = Scheduled = amber
    }

    // Status pill background color
    public static Color statusBgColor(String status) {
        if ("Completed".equals(status))  return STATUS_COMP_BG;
        if ("Cancelled".equals(status))  return STATUS_CANC_BG;
        return STATUS_SCHED_BG;
    }

    // ── Helper: Thin divider line ─────────────────────────────
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        return sep;
    }

    // ── Helper: Standard form panel (MigLayout) ───────────────
    // 2-column form: label | field
    public static JPanel formPanel() {
        JPanel p = new JPanel(new MigLayout("insets 0, gapy 10", "[120px][grow]", "[]"));
        p.setBackground(BG);
        return p;
    }
}

