package com.sunrisedental.dao;

import com.sunrisedental.model.Bill;
import com.sunrisedental.util.DBConnectionManager;

import java.sql.*;
import java.math.BigDecimal;

public class BillDAO {

    private static final double CONSULTATION_FEE = 1000.00;

    
    public Bill generateBill(String appointmentNumber) {
        String sql = "{CALL sp_generate_bill(?, ?, ?, ?)}";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, appointmentNumber);
            cs.registerOutParameter(2, Types.DECIMAL);
            cs.registerOutParameter(3, Types.DECIMAL);
            cs.registerOutParameter(4, Types.DECIMAL);
            cs.execute();

            BigDecimal treatmentFee = cs.getBigDecimal(2);
            if (treatmentFee == null) {
                return null; // cancelled or not found - no bill
            }
            double consultationFee = cs.getBigDecimal(3).doubleValue();
            return new Bill(appointmentNumber, treatmentFee.doubleValue(), consultationFee);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean save(Bill bill) {
        String sql = "INSERT INTO bills (appointment_number, treatment_fee, consultation_fee, total_amount) " +
                "VALUES (?,?,?,?)";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bill.getAppointmentNumber());
            ps.setDouble(2, bill.getTreatmentFee());
            ps.setDouble(3, bill.getConsultationFee());
            ps.setDouble(4, bill.getTotalAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getStandardConsultationFee() {
        return CONSULTATION_FEE;
    }
}
