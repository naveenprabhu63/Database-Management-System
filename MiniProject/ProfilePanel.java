package com.crime;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {
    private User user;
    private JTextField txtName, txtUser, txtMobile, txtEmail, txtAge;
    private JComboBox<String> cmbGender;
    private JLabel lblRole;

    public ProfilePanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        form.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        form.add(txtName);

        form.add(new JLabel("Username:"));
        txtUser = new JTextField();
        txtUser.setEditable(false);
        form.add(txtUser);

        form.add(new JLabel("Role:"));
        lblRole = new JLabel();
        lblRole.setFont(lblRole.getFont().deriveFont(Font.BOLD));
        form.add(lblRole);

        form.add(new JLabel("Mobile:"));
        txtMobile = new JTextField();
        form.add(txtMobile);

        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        form.add(new JLabel("Age:"));
        txtAge = new JTextField();
        form.add(txtAge);

        form.add(new JLabel("Gender:"));
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        form.add(cmbGender);

        JButton btnUpdate = new JButton("Update Profile");
        btnUpdate.setForeground(Color.GREEN.darker());
        btnUpdate.addActionListener(e -> updateProfile());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnUpdate);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadProfile();
    }

    private void loadProfile() {
        String sql = "SELECT name, username, role, mobile_no, email, age, gender FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtUser.setText(rs.getString("username"));
                lblRole.setText(rs.getString("role"));
                txtMobile.setText(rs.getString("mobile_no"));
                txtEmail.setText(rs.getString("email"));
                txtAge.setText(rs.getString("age"));
                String gender = rs.getString("gender");
                if (gender != null) cmbGender.setSelectedItem(gender);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateProfile() {
        String name = txtName.getText().trim();
        String mobile = txtMobile.getText().trim();
        String email = txtEmail.getText().trim();
        String ageStr = txtAge.getText().trim();
        String gender = (String) cmbGender.getSelectedItem();

        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 13 || age > 100) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valid age (13-100)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE users SET name=?, mobile_no=?, email=?, age=?, gender=? WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, mobile);
            ps.setString(3, email);
            ps.setInt(4, age);
            ps.setString(5, gender);
            ps.setString(6, user.getUsername());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated!");
            loadProfile();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }
}
