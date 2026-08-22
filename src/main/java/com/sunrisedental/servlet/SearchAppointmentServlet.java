package com.sunrisedental.servlet;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.User;
import com.sunrisedental.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;


public class SearchAppointmentServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String appointmentNumber = req.getParameter("appointmentNumber");

        if (appointmentNumber != null && !appointmentNumber.trim().isEmpty()) {
            if (!ValidationUtil.isValidAppointmentNumber(appointmentNumber.trim())) {
                req.setAttribute("error", "Invalid appointment number format (expected e.g. APT1A2B3C4D).");
            } else {
                Appointment appt = appointmentDAO.findByNumber(appointmentNumber.trim());
                if (appt == null) {
                    req.setAttribute("error", "No appointment found with number: " + appointmentNumber);
                } else {
                    req.setAttribute("appointment", appt);
                   
                    Cookie lastSearched = new Cookie("lastSearchedAppointment", appt.getAppointmentNumber());
                    lastSearched.setMaxAge(60 * 60 * 24 * 30); // 30 days
                    lastSearched.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
                    resp.addCookie(lastSearched);
                }
            }
        } else {
          
            if (req.getCookies() != null) {
                for (Cookie c : req.getCookies()) {
                    if ("lastSearchedAppointment".equals(c.getName())) {
                        req.setAttribute("lastSearchedAppointment", c.getValue());
                        break;
                    }
                }
            }
        }

        req.getRequestDispatcher("/search-appointment.jsp").forward(req, resp);
    }
}
