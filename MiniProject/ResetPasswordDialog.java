package com.crime;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ResetPasswordDialog extends JDialog {
    private JTextField txtUser, txtMobile;
    private JPasswordField txtNewPass, txtConfirmPass;

    public ResetPasswordDialog(JFrame parent) {
        super(parent, "Reset Password", true);
        setSize(450, 300);
        setLocationRelativeTo(parent);

        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        p.add(new JLabel("Username:"));
        txtUser = new JTextField();
        p.add(txtUser);

        p.add(new JLabel("Mobile:"));
        txtMobile = new JTextField();
        p.add(txtMobile);

        p.add(new JLabel("New Password:"));
        txtNewPass = new JPasswordField();
        p.add(txtNewPass);

        p.add(new JLabel("Re-enter Password:"));
        txtConfirmPass = new JPasswordField();
        p.add(txtConfirmPass);

        JButton btnReset = new JButton("Reset Password");
        btnReset.setForeground(Color.BLUE.darker());
        btnReset.addActionListener(e -> resetPassword());
        p.add(new JLabel(""));
        p.add(btnReset);

        add(p);
    }

    private void resetPassword() {
        String username = txtUser.getText().trim();
        String mobile = txtMobile.getText().trim();
        String pass1 = new String(txtNewPass.getPassword());
        String pass2 = new String(txtConfirmPass.getPassword());

        if (username.isEmpty() || mobile.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pass1.equals(pass2)) {
            JOptionPane.showMessageDialog(this, "Sorry! You're Fake.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE users SET password = ? WHERE username = ? AND mobile_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pass1);
            ps.setString(2, username);
            ps.setString(3, mobile);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!\nYou can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or mobile!", "Not Found", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
