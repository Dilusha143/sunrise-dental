package com.sunrisedental.dao;

import com.sunrisedental.model.User;
import com.sunrisedental.util.DBConnectionManager;
import com.sunrisedental.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

   
    public User authenticate(String username, String plainPassword) {
        String sql = "SELECT u.*, d.specialization AS dentist_specialization " +
                "FROM users u " +
                "LEFT JOIN dentists d ON u.dentist_id = d.dentist_id " +
                "WHERE u.username = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String suppliedHash = PasswordUtil.hash(plainPassword);

                if (storedHash.equals(suppliedHash)) {
                    return mapRow(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

   
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Creates a RECEPTIONIST or ADMIN account (no dentist link). */
    public boolean createUser(String username, String plainPassword, String fullName, String role) {
        return createUser(username, plainPassword, fullName, role, null);
    }

    
    public boolean createUser(String username, String plainPassword, String fullName, String role, Integer dentistId) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role, dentist_id) VALUES (?,?,?,?,?)";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(plainPassword));
            ps.setString(3, fullName);
            ps.setString(4, role);
            if (dentistId != null) {
                ps.setInt(5, dentistId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));

        int dentistId = rs.getInt("dentist_id");
        if (!rs.wasNull()) {
            user.setDentistId(dentistId);
            user.setDentistSpecialization(rs.getString("dentist_specialization"));
        }
        return user;
    }
}
