package com.crime;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrimeDialog extends JDialog {
    private JTextField txtType, txtLoc, txtSuspect, txtStatus;
    private JTextArea txtDesc;
    private JSpinner dateSpinner;
    private Crime crime;
    private boolean isAdd;
    private CrimeDAO dao = new CrimeDAO();

    public CrimeDialog(JFrame parent, Crime crime, boolean isAdd) {
        super(parent, isAdd ? "Add Crime" : "Update Crime", true);
        this.crime = crime;
        this.isAdd = isAdd;
        setSize(500, 500);
        setLocationRelativeTo(parent);
        initUI();
        if (!isAdd) loadCrimeData();
    }

    private void initUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        form.add(new JLabel("Crime Type:"));
        txtType = new JTextField();
        form.add(txtType);

        form.add(new JLabel("Location:"));
        txtLoc = new JTextField();
        form.add(txtLoc);

        form.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        form.add(dateSpinner);

        form.add(new JLabel("Suspect Name:"));
        txtSuspect = new JTextField();
        form.add(txtSuspect);

        form.add(new JLabel("Status:"));
        txtStatus = new JTextField();
        form.add(txtStatus);

        form.add(new JLabel("Description:"));
        txtDesc = new JTextArea(4, 20);
        txtDesc.setLineWrap(true);
        form.add(new JScrollPane(txtDesc));

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> save());
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSave);

        p.add(form, BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        add(p);
    }

    private void loadCrimeData() {
        txtType.setText(crime.getCrimeType());
        txtLoc.setText(crime.getLocation());
        txtSuspect.setText(crime.getSuspectName());
        txtStatus.setText(crime.getStatus());
        txtDesc.setText(crime.getDescription());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(crime.getDateReported());
            dateSpinner.setValue(date);
        } catch (Exception e) {}
    }

    private void save() {
        String type = txtType.getText().trim();
        String loc = txtLoc.getText().trim();
        String suspect = txtSuspect.getText().trim();
        String status = txtStatus.getText().trim();
        String desc = txtDesc.getText().trim();

        if (type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Crime Type required!");
            return;
        }

        Date utilDate = (Date) dateSpinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(utilDate);

        try {
            if (isAdd) {
                dao.addCrime(new Crime(0, type, loc, dateStr, suspect, status, desc));
            } else {
                dao.updateCrime(new Crime(crime.getId(), type, loc, dateStr, suspect, status, desc));
            }
            JOptionPane.showMessageDialog(this, "Saved!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
