package com.sunrisedental.dao;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.TreatmentType;
import com.sunrisedental.util.DBConnectionManager;

import java.sql.*;
import java.util.*;


public class AppointmentDAO {

    /** Saves the appointment header plus its list of selected treatments. */
    public boolean save(Appointment appt) {
        String insertAppointment = "INSERT INTO appointments (appointment_number, patient_name, address, " +
                "contact_number, dentist_id, appointment_date, appointment_time, status) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        String insertTreatment = "INSERT INTO appointment_treatments (appointment_number, treatment_id) VALUES (?,?)";

        Connection conn = null;
        try {
            conn = DBConnectionManager.getInstance().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertAppointment)) {
                ps.setString(1, appt.getAppointmentNumber());
                ps.setString(2, appt.getPatientName());
                ps.setString(3, appt.getAddress());
                ps.setString(4, appt.getContactNumber());
                ps.setInt(5, appt.getDentistId());
                ps.setDate(6, appt.getAppointmentDate());
                ps.setTime(7, appt.getAppointmentTime());
                ps.setString(8, appt.getStatus());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertTreatment)) {
                for (TreatmentType t : appt.getTreatments()) {
                    ps.setString(1, appt.getAppointmentNumber());
                    ps.setInt(2, t.getTreatmentId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            }
            
            if ("45000".equals(e.getSQLState())) {
                throw new DoubleBookingException(e.getMessage());
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException closeEx) { closeEx.printStackTrace(); }
            }
        }
    }

    public Appointment findByNumber(String appointmentNumber) {
        String sql = "SELECT a.*, d.full_name AS dentist_name " +
                "FROM appointments a " +
                "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                "WHERE a.appointment_number = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, appointmentNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Appointment appt = mapRow(rs);
                appt.setTreatments(findTreatmentsFor(conn, appointmentNumber));
                return appt;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Appointment> findAll() {
        return findAll(null, null);
    }

  
    public List<Appointment> findAll(String statusFilter) {
        return findAll(statusFilter, null);
    }

    
    public List<Appointment> findAll(String statusFilter, Integer dentistId) {
        List<Appointment> list = new ArrayList<>();
        boolean statusFiltered = statusFilter != null && !statusFilter.trim().isEmpty()
                && !"ALL".equalsIgnoreCase(statusFilter.trim());
        boolean dentistFiltered = dentistId != null;

        StringBuilder sql = new StringBuilder(
                "SELECT a.*, d.full_name AS dentist_name " +
                "FROM appointments a " +
                "JOIN dentists d ON a.dentist_id = d.dentist_id ");

        List<String> conditions = new ArrayList<>();
        if (statusFiltered) conditions.add("a.status = ?");
        if (dentistFiltered) conditions.add("a.dentist_id = ?");
        if (!conditions.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", conditions)).append(" ");
        }
        sql.append("ORDER BY a.appointment_date, a.appointment_time");

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (statusFiltered) ps.setString(idx++, statusFilter.trim().toUpperCase());
            if (dentistFiltered) ps.setInt(idx++, dentistId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

           
            Map<String, List<TreatmentType>> treatmentsByAppointment = findAllTreatmentsGrouped(conn);
            for (Appointment appt : list) {
                appt.setTreatments(treatmentsByAppointment.getOrDefault(appt.getAppointmentNumber(), new ArrayList<>()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

   
    public boolean updateStatus(String appointmentNumber, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_number = ? AND status = 'SCHEDULED'";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, appointmentNumber);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public int countTodaysScheduled(Integer dentistIdFilter) {
        String sql = dentistIdFilter == null
            ? "SELECT COUNT(*) AS c FROM appointments WHERE appointment_date = CURDATE() AND status = 'SCHEDULED'"
            : "SELECT COUNT(*) AS c FROM appointments WHERE appointment_date = CURDATE() AND status = 'SCHEDULED' AND dentist_id = ?";

        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (dentistIdFilter != null) ps.setInt(1, dentistIdFilter);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("c");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean existsByNumber(String appointmentNumber) {
        String sql = "SELECT 1 FROM appointments WHERE appointment_number = ?";
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<TreatmentType> findTreatmentsFor(Connection conn, String appointmentNumber) throws SQLException {
        String sql = "SELECT t.treatment_id, t.treatment_name, t.fee " +
                "FROM appointment_treatments at " +
                "JOIN treatment_types t ON at.treatment_id = t.treatment_id " +
                "WHERE at.appointment_number = ? " +
                "ORDER BY t.treatment_id";

        List<TreatmentType> treatments = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    treatments.add(new TreatmentType(
                            rs.getInt("treatment_id"),
                            rs.getString("treatment_name"),
                            rs.getDouble("fee")));
                }
            }
        }
        return treatments;
    }

    private Map<String, List<TreatmentType>> findAllTreatmentsGrouped(Connection conn) throws SQLException {
        String sql = "SELECT at.appointment_number, t.treatment_id, t.treatment_name, t.fee " +
                "FROM appointment_treatments at " +
                "JOIN treatment_types t ON at.treatment_id = t.treatment_id " +
                "ORDER BY at.appointment_number, t.treatment_id";

        Map<String, List<TreatmentType>> result = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String apptNumber = rs.getString("appointment_number");
                TreatmentType t = new TreatmentType(
                        rs.getInt("treatment_id"),
                        rs.getString("treatment_name"),
                        rs.getDouble("fee"));
                result.computeIfAbsent(apptNumber, k -> new ArrayList<>()).add(t);
            }
        }
        return result;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setPatientName(rs.getString("patient_name"));
        a.setAddress(rs.getString("address"));
        a.setContactNumber(rs.getString("contact_number"));
        a.setDentistId(rs.getInt("dentist_id"));
        a.setDentistName(rs.getString("dentist_name"));
        a.setAppointmentDate(rs.getDate("appointment_date"));
        a.setAppointmentTime(rs.getTime("appointment_time"));
        a.setStatus(rs.getString("status"));
        return a;
    }
}
