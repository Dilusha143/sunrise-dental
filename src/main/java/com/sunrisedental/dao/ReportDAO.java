package com.sunrisedental.dao;

import com.sunrisedental.util.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReportDAO {

    public static class DentistWorkloadRow {
        public String dentistName;
        public int scheduledCount;
        public int completedCount;
        public int cancelledCount;
        public int totalCount;
    }

    public static class TreatmentPopularityRow {
        public String treatmentName;
        public int timesPerformed;
        public double totalFeeBilled;
    }

    
    public List<DentistWorkloadRow> getDentistWorkload() {
        String sql =
            "SELECT d.full_name AS dentist_name, " +
            "SUM(CASE WHEN a.status = 'SCHEDULED' THEN 1 ELSE 0 END) AS scheduled_count, " +
            "SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count, " +
            "SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_count, " +
            "COUNT(a.appointment_number) AS total_count " +
            "FROM dentists d LEFT JOIN appointments a ON a.dentist_id = d.dentist_id " +
            "GROUP BY d.dentist_id, d.full_name " +
            "ORDER BY total_count DESC";

        List<DentistWorkloadRow> rows = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DentistWorkloadRow row = new DentistWorkloadRow();
                row.dentistName = rs.getString("dentist_name");
                row.scheduledCount = rs.getInt("scheduled_count");
                row.completedCount = rs.getInt("completed_count");
                row.cancelledCount = rs.getInt("cancelled_count");
                row.totalCount = rs.getInt("total_count");
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

   
    public double getTotalRevenueBilled() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total FROM bills";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    
    public int getBillCount() {
        String sql = "SELECT COUNT(*) AS c FROM bills";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("c");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public List<TreatmentPopularityRow> getTreatmentPopularity() {
        String sql =
            "SELECT t.treatment_name, " +
            "COUNT(at.appointment_number) AS times_performed, " +
            "COALESCE(SUM(CASE WHEN a.status = 'COMPLETED' THEN t.fee ELSE 0 END), 0) AS total_fee_billed " +
            "FROM treatment_types t " +
            "LEFT JOIN appointment_treatments at ON at.treatment_id = t.treatment_id " +
            "LEFT JOIN appointments a ON a.appointment_number = at.appointment_number " +
            "GROUP BY t.treatment_id, t.treatment_name " +
            "ORDER BY times_performed DESC";

        List<TreatmentPopularityRow> rows = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TreatmentPopularityRow row = new TreatmentPopularityRow();
                row.treatmentName = rs.getString("treatment_name");
                row.timesPerformed = rs.getInt("times_performed");
                row.totalFeeBilled = rs.getDouble("total_fee_billed");
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
