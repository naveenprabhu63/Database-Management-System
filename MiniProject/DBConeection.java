package com.crime;

import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:h2:./crime_db;DB_CLOSE_DELAY=-1;MODE=MySQL;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            createTablesIfNotExist(conn);
            return conn;
        } catch (SQLException e) {
            System.err.println("H2 Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }

    private static void createTablesIfNotExist(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Users Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    role ENUM('ADMIN', 'USER') NOT NULL,
                    name VARCHAR(100),
                    mobile_no VARCHAR(15),
                    email VARCHAR(100),
                    age INT,
                    gender ENUM('Male', 'Female', 'Other')
                )
                """);

            // Crimes Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS crimes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    crime_type VARCHAR(100) NOT NULL,
                    location VARCHAR(150),
                    date_reported DATE,
                    suspect_name VARCHAR(100),
                    status VARCHAR(50) DEFAULT 'Pending',
                    description TEXT
                )
                """);

            // Insert Default Admin
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, role, name, mobile_no, email, age, gender) " +
                        "VALUES ('admin', 'admin123', 'ADMIN', 'System Admin', '9999999999', 'admin@crime.com', 30, 'Male')");
                System.out.println("Default admin created: admin / admin123");
            }

            // Insert 15 Sample Crimes
            rs = stmt.executeQuery("SELECT COUNT(*) FROM crimes");
            if (rs.next() && rs.getInt(1) == 0) {
                insertSampleCrimes(conn);
                System.out.println("15 sample crimes inserted!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // FIXED: Use PreparedStatement to avoid SQL injection & escape issues
    private static void insertSampleCrimes(Connection conn) throws SQLException {
        String sql = "INSERT INTO crimes (crime_type, location, date_reported, suspect_name, status, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            addCrime(ps, "Negligent Homicide at Rally", "Karur", "2025-09-27", "TVK Party Leaders", "Under Investigation", "41 killed in stampede at actor Vijay's TVK rally. Police filed case.");
            addCrime(ps, "Unlawful Assembly", "Chennai", "2025-03-17", "K. Annamalai", "Pending", "BJP leaders arrested during TASMAC protest.");
            addCrime(ps, "Murder", "Madurai", "2025-03-19", "Unknown", "Under Investigation", "DMK worker killed in party feud.");
            addCrime(ps, "Assault", "Tiruppur", "2025-04-14", "Unknown", "Closed", "Dalit activist assaulted at political meeting.");
            addCrime(ps, "Murder", "Sivaganga", "2025-03-19", "Unknown", "Pending", "AIADMK worker murdered.");
            addCrime(ps, "Murder", "Coimbatore", "2025-10-19", "Youth", "Under Investigation", "Man stabbed in hospital maternity ward.");
            addCrime(ps, "Murder", "Unnamed Town", "2025-08-28", "Father", "Under Investigation", "2-year-old girl killed by father.");
            addCrime(ps, "Robbery", "Music College", "2025-08-28", "2 Rowdies", "Closed", "Student robbed after honey trap.");
            addCrime(ps, "Murder", "Unnamed Village", "2025-08-28", "Sanjay", "Pending", "Man faked snake bite to plot murder.");
            addCrime(ps, "Murder", "Mayiladuthurai", "2025-07-30", "Vendors", "Under Investigation", "Two youngsters killed by illicit liquor sellers.");
            addCrime(ps, "Murder", "Pudukkottai", "2025-02-15", "Unknown", "Under Investigation", "Anti-quarrying activist Jagabar Ali murdered.");
            addCrime(ps, "Murder", "Unnamed District", "2025-09-12", "Unknown", "Closed", "Dalit techie killed in honour killing.");
            addCrime(ps, "Murder", "Udumalpet", "2025-09-12", "Unknown", "Under Investigation", "Police officer Shanmugavel hacked to death.");
            addCrime(ps, "Burglary", "Vellore", "2025-09-12", "Thieves", "Pending", "CRPF personnel home burgled.");
            addCrime(ps, "Murder", "Erode", "2025-03-19", "Unknown", "Closed", "Murder in personal dispute.");
        }
    }

    private static void addCrime(PreparedStatement ps, String type, String loc, String date, String suspect, String status, String desc) throws SQLException {
        ps.setString(1, type);
        ps.setString(2, loc);
        ps.setDate(3, Date.valueOf(date));
        ps.setString(4, suspect);
        ps.setString(5, status);
        ps.setString(6, desc);
        ps.executeUpdate();
    }
}
