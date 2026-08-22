package com.sunrisedental.servlet;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class AppointmentsListServlet extends HttpServlet {

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
        if (!user.isDentist() && !user.isReceptionist()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view the appointments list.");
            return;
        }

        String status = req.getParameter("status");
        String scope = req.getParameter("scope"); // "mine" or "all"

        Integer dentistFilter = null;
        if ("mine".equalsIgnoreCase(scope) && user.isLinkedToDentist()) {
            dentistFilter = user.getDentistId();
        }

        List<Appointment> appointments = appointmentDAO.findAll(status, dentistFilter);

        req.setAttribute("appointments", appointments);
        req.setAttribute("selectedStatus", (status == null || status.trim().isEmpty()) ? "ALL" : status.trim().toUpperCase());
        req.setAttribute("selectedScope", dentistFilter != null ? "mine" : "all");
        req.getRequestDispatcher("/appointments-list.jsp").forward(req, resp);
    }
}
