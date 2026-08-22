package com.sunrisedental.dao;

import com.sunrisedental.model.Dentist;
import com.sunrisedental.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DentistDAO {

    public List<Dentist> findAll() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT dentist_id, full_name, specialization FROM dentists ORDER BY full_name";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Dentist(
                        rs.getInt("dentist_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean existsById(int dentistId) {
        String sql = "SELECT 1 FROM dentists WHERE dentist_id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean createDentist(String fullName, String specialization) {
        String sql = "INSERT INTO dentists (full_name, specialization) VALUES (?, ?)";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, (specialization == null || specialization.trim().isEmpty()) ? null : specialization.trim());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean isLinkedToUser(int dentistId) {
        String sql = "SELECT 1 FROM users WHERE dentist_id = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
