package com.crime;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private int attempts = 0;
    private long lockUntil = 0;

    public LoginFrame() {
        setTitle("Crime Management - Login");
        setSize(480, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 10));
        p1.add(new JLabel("Username:"));
        txtUser = new JTextField(20);
        p1.add(txtUser);
        panel.add(p1);

        JPanel p2 = new JPanel(new GridLayout(1, 2, 10, 10));
        p2.add(new JLabel("Password:"));
        txtPass = new JPasswordField(20);
        p2.add(txtPass);
        panel.add(p2);

        panel.add(Box.createVerticalStrut(15));

        JPanel btnRow = new JPanel(new FlowLayout());
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> login());

        JButton btnRegister = new JButton("Register");
        btnRegister.setForeground(Color.BLUE.darker());
        btnRegister.addActionListener(e -> new RegisterDialog(this).setVisible(true));

        JButton btnForgot = new JButton("Forgot Password?");
        btnForgot.setForeground(Color.RED.darker());
        btnForgot.addActionListener(e -> new ResetPasswordDialog(this).setVisible(true));

        btnRow.add(btnLogin);
        btnRow.add(btnRegister);
        btnRow.add(btnForgot);

        panel.add(btnRow);
        add(panel);
    }

    private void login() {
        if (System.currentTimeMillis() < lockUntil) {
            JOptionPane.showMessageDialog(this, "Account locked. Try again later.", "Locked", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT role FROM users WHERE username=? AND password=?")) {
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                attempts = 0;
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + user);
                dispose();
                new MainFrame(new User(user, rs.getString("role"))).setVisible(true);
            } else {
                attempts++;
                if (attempts >= 3) {
                    lockUntil = System.currentTimeMillis() + 5 * 60 * 1000;
                    JOptionPane.showMessageDialog(this, "3 failed attempts. Locked for 5 minutes.", "Locked", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid! Attempts: " + attempts + "/3", "Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) { e.printStackTrace(); }
            new LoginFrame().setVisible(true);
        });
    }
}
