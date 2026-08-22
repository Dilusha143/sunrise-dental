package com.sunrisedental.servlet;

import com.sunrisedental.dao.ReportDAO;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;


public class ReportsServlet extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!user.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Only an Administrator can view clinic reports.");
            return;
        }

        req.setAttribute("dentistWorkload", reportDAO.getDentistWorkload());
        req.setAttribute("treatmentPopularity", reportDAO.getTreatmentPopularity());
        req.setAttribute("totalRevenueBilled", reportDAO.getTotalRevenueBilled());
        req.setAttribute("billCount", reportDAO.getBillCount());

        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }
}
