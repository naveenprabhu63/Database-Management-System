package com.crime;

import javax.swing.*;
import java.awt.*;

public class CrimeViewDialog extends JDialog {
    public CrimeViewDialog(JFrame parent, Crime crime) {
        super(parent, "Crime Details - ID: " + crime.getId(), true);
        setSize(500, 500);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addField(panel, "Crime Type:", crime.getCrimeType());
        addField(panel, "Location:", crime.getLocation());
        addField(panel, "Date Reported:", crime.getDateReported());
        addField(panel, "Suspect Name:", crime.getSuspectName());
        addField(panel, "Status:", crime.getStatus());

        panel.add(new JLabel("Description:"));
        JTextArea descArea = new JTextArea(crime.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new Color(240, 240, 240));
        panel.add(new JScrollPane(descArea));

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, String label, String value) {
        panel.add(new JLabel("<html><b>" + label + "</b></html>"));
        JTextField field = new JTextField(value != null ? value : "N/A");
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        panel.add(field);
    }
}
