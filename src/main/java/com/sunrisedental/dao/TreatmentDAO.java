package com.sunrisedental.dao;

import com.sunrisedental.model.TreatmentType;
import com.sunrisedental.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TreatmentDAO {

    public List<TreatmentType> findAll() {
        List<TreatmentType> list = new ArrayList<>();
        String sql = "SELECT treatment_id, treatment_name, fee FROM treatment_types ORDER BY treatment_id";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TreatmentType(
                        rs.getInt("treatment_id"),
                        rs.getString("treatment_name"),
                        rs.getDouble("fee")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
