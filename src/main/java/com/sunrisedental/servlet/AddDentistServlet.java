package com.sunrisedental.servlet;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.User;
import com.sunrisedental.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class AddDentistServlet extends HttpServlet {

    private final DentistDAO dentistDAO = new DentistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;
        req.setAttribute("dentistOptions", dentistDAO.findAll());
        req.getRequestDispatcher("/add-dentist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;

        String fullName = req.getParameter("fullName");
        String specialization = req.getParameter("specialization");

        StringBuilder errors = new StringBuilder();
        if (!ValidationUtil.isValidName(fullName)) {
            errors.append("Full name must contain only letters and be 2-100 characters. ");
        }
        if (specialization != null && !specialization.trim().isEmpty()
                && !ValidationUtil.isValidName(specialization)) {
            errors.append("Specialization must contain only letters and be 2-100 characters. ");
        }

        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString());
            req.setAttribute("dentistOptions", dentistDAO.findAll());
            req.getRequestDispatcher("/add-dentist.jsp").forward(req, resp);
            return;
        }

        boolean saved = dentistDAO.createDentist(fullName.trim(), specialization);

        if (saved) {
            req.setAttribute("success", "Added " + fullName.trim() + " to the dentist roster. "
                    + "You can now link a login to them from Register Dentist/Receptionist.");
        } else {
            req.setAttribute("error", "Failed to add dentist. Please try again.");
        }

        List<Dentist> dentistOptions = dentistDAO.findAll();
        req.setAttribute("dentistOptions", dentistOptions);
        req.getRequestDispatcher("/add-dentist.jsp").forward(req, resp);
    }

    private boolean isAuthorised(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (!user.canManageStaff()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to manage the dentist roster.");
            return false;
        }
        return true;
    }
}
