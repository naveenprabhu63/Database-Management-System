package com.crime;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private User user;
    private CrimeDAO dao = new CrimeDAO();
    private JTable crimeTable, userTable;
    private DefaultTableModel crimeModel, userModel;
    private JTextField txtTypeFilter, txtFrom, txtTo;
    private JComboBox<String> cmbStatus;
    private JTabbedPane tabbedPane;

    public MainFrame(User user) {
        this.user = user;
        setTitle("Crime Management System - " + user.getUsername() + " (" + (user.isAdmin() ? "ADMIN" : "USER") + ")");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // === MENU BAR WITH LOGOUT ===
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        menu.add(logoutItem);
        menu.addSeparator();
        menu.add(exitItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Crimes", createCrimePanel());
        tabbedPane.addTab("Profile", new ProfilePanel(user));
        if (user.isAdmin()) tabbedPane.addTab("Manage Users", createUserPanel());
        add(tabbedPane);

        loadCrimeData();
        if (user.isAdmin()) loadUserData();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private JPanel createCrimePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === FILTERS ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTypeFilter = new JTextField(10);
        cmbStatus = new JComboBox<>(new String[]{"", "Pending", "Under Investigation", "Closed"});
        txtFrom = new JTextField(10);
        txtTo = new JTextField(10);
        JButton btnFilter = new JButton("Filter");
        JButton btnClear = new JButton("Clear");

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(txtTypeFilter);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cmbStatus);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(txtFrom);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(txtTo);
        filterPanel.add(btnFilter);
        filterPanel.add(btnClear);

        // === EXPORT ONLY ===
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExport = new JButton("Export");
        rightPanel.add(btnExport);

        top.add(filterPanel, BorderLayout.WEST);
        top.add(rightPanel, BorderLayout.EAST);

        // === TABLE ===
        String[] cols = {"ID", "Type", "Location", "Date", "Suspect", "Status", "Description"};
        crimeModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        crimeTable = new JTable(crimeModel);
        crimeTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        JScrollPane scroll = new JScrollPane(crimeTable);

        // === BUTTONS ===
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Crime");
        JButton btnView = new JButton("View Selected");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        btnPanel.add(btnAdd); btnPanel.add(btnView); btnPanel.add(btnUpdate); btnPanel.add(btnDelete);

        if (!user.isAdmin()) {
            btnAdd.setEnabled(false); btnUpdate.setEnabled(false); btnDelete.setEnabled(false);
        }

        btnAdd.addActionListener(e -> { new CrimeDialog(this, null, true).setVisible(true); loadCrimeData(); });
        btnView.addActionListener(e -> viewCrime());
        btnUpdate.addActionListener(e -> updateCrime());
        btnDelete.addActionListener(e -> deleteCrime());
        btnFilter.addActionListener(e -> filterCrimes());
        btnClear.addActionListener(e -> { txtTypeFilter.setText(""); cmbStatus.setSelectedIndex(0); txtFrom.setText(""); txtTo.setText(""); loadCrimeData(); });
        btnExport.addActionListener(e -> ExportManager.export(crimesList(), "crimes_report"));

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void filterCrimes() {
        String type = txtTypeFilter.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();
        crimeModel.setRowCount(0);
        for (Crime c : dao.filterCrimes(type, status.isEmpty() ? null : status, from, to)) {
            String desc = c.getDescription(); if (desc == null) desc = "";
            if (desc.length() > 40) desc = desc.substring(0, 37) + "...";
            crimeModel.addRow(new Object[]{c.getId(), c.getCrimeType(), c.getLocation(), c.getDateReported(), c.getSuspectName(), c.getStatus(), desc});
        }
    }

    private void loadCrimeData() { filterCrimes(); }

    private List<Crime> crimesList() {
        List<Crime> list = new ArrayList<>();
        for (int i = 0; i < crimeModel.getRowCount(); i++) {
            int id = (int) crimeModel.getValueAt(i, 0);
            dao.getAllCrimes().stream()
                    .filter(c -> c.getId() == id)
                    .findFirst()
                    .ifPresent(list::add);
        }
        return list;
    }

    private void viewCrime() {
        int row = crimeTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a crime!"); return; }
        int id = (int) crimeModel.getValueAt(row, 0);
        Crime c = dao.getAllCrimes().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (c != null) new CrimeViewDialog(this, c).setVisible(true);
    }

    private void updateCrime() {
        int row = crimeTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a crime!"); return; }
        int id = (int) crimeModel.getValueAt(row, 0);
        Crime c = dao.getAllCrimes().stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        new CrimeDialog(this, c, false).setVisible(true); loadCrimeData();
    }

    private void deleteCrime() {
        int row = crimeTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a crime!"); return; }
        int id = (int) crimeModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete crime ID " + id + "?") == 0) {
            dao.deleteCrime(id); loadCrimeData();
        }
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] userCols = {"ID", "Username", "Role"};
        userModel = new DefaultTableModel(userCols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        userTable = new JTable(userModel);
        JScrollPane scroll = new JScrollPane(userTable);
        JButton btnDeleteUser = new JButton("Delete Selected User");
        btnDeleteUser.setForeground(Color.RED.darker());
        btnDeleteUser.addActionListener(e -> deleteUser());
        JPanel bottom = new JPanel();
        bottom.add(btnDeleteUser);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadUserData() {
        userModel.setRowCount(0);
        String sql = "SELECT id, username, role FROM users ORDER BY role DESC, username";
        try (var conn = DBConnection.getConnection();
             var st = conn.createStatement();
             var rs = st.executeQuery(sql)) {
            while (rs.next()) {
                userModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user!"); return; }
        int id = (int) userModel.getValueAt(row, 0);
        String username = (String) userModel.getValueAt(row, 1);
        String role = (String) userModel.getValueAt(row, 2);

        if (username.equals(user.getUsername())) {
            JOptionPane.showMessageDialog(this, "You cannot delete yourself!");
            return;
        }
        if ("ADMIN".equals(role)) {
            int adminCount = 0;
            try (var conn = DBConnection.getConnection();
                 var ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'")) {
                var rs = ps.executeQuery();
                if (rs.next()) adminCount = rs.getInt(1);
            } catch (Exception e) { e.printStackTrace(); }
            if (adminCount <= 1) {
                JOptionPane.showMessageDialog(this, "Cannot delete the only Admin!");
                return;
            }
        }

        if (JOptionPane.showConfirmDialog(this, "Delete user '" + username + "'?") == 0) {
            try (var conn = DBConnection.getConnection();
                 var ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadUserData();
                JOptionPane.showMessageDialog(this, "User deleted!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
