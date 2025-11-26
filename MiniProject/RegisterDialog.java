package com.crime;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterDialog extends JDialog {
    private JTextField txtName, txtUser, txtMobile, txtEmail, txtAge;
    private JPasswordField txtPass;
    private JComboBox<String> cmbGender;

    public RegisterDialog(JFrame parent) {
        super(parent, "Register New User", true);
        setSize(500, 450);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        p.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        p.add(txtName);

        p.add(new JLabel("Username:"));
        txtUser = new JTextField();
        p.add(txtUser);

        p.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        p.add(txtPass);

        p.add(new JLabel("Mobile:"));
        txtMobile = new JTextField();
        p.add(txtMobile);

        p.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        p.add(txtEmail);

        p.add(new JLabel("Age:"));
        txtAge = new JTextField();
        p.add(txtAge);

        p.add(new JLabel("Gender:"));
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        p.add(cmbGender);

        JButton btnRegister = new JButton("Register");
        btnRegister.setForeground(Color.BLUE.darker());
        btnRegister.addActionListener(e -> register());
        p.add(new JLabel(""));
        p.add(btnRegister);

        add(p);
    }

    private void register() {
        String name = txtName.getText().trim();
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        String mobile = txtMobile.getText().trim();
        String email = txtEmail.getText().trim();
        String ageStr = txtAge.getText().trim();
        String gender = (String) cmbGender.getSelectedItem();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || mobile.isEmpty() || email.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 13 || age > 100) throw new Exception();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Valid age (13-100) required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO users (name, username, password, mobile_no, email, age, gender, role) VALUES (?, ?, ?, ?, ?, ?, ?, 'USER')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, mobile);
            ps.setString(5, email);
            ps.setInt(6, age);
            ps.setString(7, gender);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User '" + name + "' registered successfully!\nYou can now login.");
            dispose();

        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Username or Mobile already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}
