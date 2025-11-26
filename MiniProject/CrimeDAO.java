package com.crime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrimeDAO {

    public void addCrime(Crime crime) {
        String sql = "INSERT INTO crimes (crime_type, location, date_reported, suspect_name, status, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, crime.getCrimeType());
            ps.setString(2, crime.getLocation());
            ps.setString(3, crime.getDateReported());
            ps.setString(4, crime.getSuspectName());
            ps.setString(5, crime.getStatus());
            ps.setString(6, crime.getDescription());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateCrime(Crime crime) {
        String sql = "UPDATE crimes SET crime_type=?, location=?, date_reported=?, suspect_name=?, status=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, crime.getCrimeType());
            ps.setString(2, crime.getLocation());
            ps.setString(3, crime.getDateReported());
            ps.setString(4, crime.getSuspectName());
            ps.setString(5, crime.getStatus());
            ps.setString(6, crime.getDescription());
            ps.setInt(7, crime.getId());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void deleteCrime(int id) {
        String sql = "DELETE FROM crimes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<Crime> getAllCrimes() {
        List<Crime> list = new ArrayList<>();
        String sql = "SELECT * FROM crimes ORDER BY date_reported DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Crime(
                        rs.getInt("id"),
                        rs.getString("crime_type"),
                        rs.getString("location"),
                        rs.getString("date_reported"),
                        rs.getString("suspect_name"),
                        rs.getString("status"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Crime> searchCrimes(String query) {
        List<Crime> list = new ArrayList<>();
        String sql = "SELECT * FROM crimes WHERE id LIKE ? OR suspect_name LIKE ? OR crime_type LIKE ? OR description LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            for (int i = 1; i <= 4; i++) ps.setString(i, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Crime(
                        rs.getInt("id"),
                        rs.getString("crime_type"),
                        rs.getString("location"),
                        rs.getString("date_reported"),
                        rs.getString("suspect_name"),
                        rs.getString("status"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Crime> filterCrimes(String type, String status, String fromDate, String toDate) {
        List<Crime> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM crimes WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null && !type.isEmpty()) { sql.append(" AND crime_type LIKE ?"); params.add("%" + type + "%"); }
        if (status != null && !status.isEmpty()) { sql.append(" AND status = ?"); params.add(status); }
        if (fromDate != null && !fromDate.isEmpty()) { sql.append(" AND date_reported >= ?"); params.add(fromDate); }
        if (toDate != null && !toDate.isEmpty()) { sql.append(" AND date_reported <= ?"); params.add(toDate); }
        sql.append(" ORDER BY date_reported DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Crime(
                        rs.getInt("id"),
                        rs.getString("crime_type"),
                        rs.getString("location"),
                        rs.getString("date_reported"),
                        rs.getString("suspect_name"),
                        rs.getString("status"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
