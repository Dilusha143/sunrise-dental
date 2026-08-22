package com.sunrisedental.servlet;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;


public class UpdateAppointmentStatusServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!user.isDentist() && !user.isReceptionist()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to update appointments.");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");
        String newStatus = req.getParameter("newStatus");
        String redirectBack = req.getContextPath() + "/appointments";

        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()
                || newStatus == null || (!newStatus.equals("COMPLETED") && !newStatus.equals("CANCELLED"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status update request.");
            return;
        }

        Appointment appt = appointmentDAO.findByNumber(appointmentNumber.trim());
        if (appt == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Appointment not found.");
            return;
        }

        
        boolean isPureReceptionist = "RECEPTIONIST".equals(user.getRole());
        if (isPureReceptionist && "COMPLETED".equals(newStatus)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only a dentist can mark an appointment as completed.");
            return;
        }

       
        boolean isPureDentist = "DENTIST".equals(user.getRole());
        if (isPureDentist && (user.getDentistId() == null || user.getDentistId() != appt.getDentistId())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only update your own appointments.");
            return;
        }

        appointmentDAO.updateStatus(appointmentNumber.trim(), newStatus);
        resp.sendRedirect(redirectBack);
    }
}
