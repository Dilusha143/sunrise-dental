package com.sunrisedental.servlet;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.User;
import com.sunrisedental.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;


public class RegisterStaffServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final DentistDAO dentistDAO = new DentistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;
        req.setAttribute("dentistOptions", dentistDAO.findAll());
        req.getRequestDispatcher("/register-staff.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;

        List<Dentist> dentistOptions = dentistDAO.findAll();
        req.setAttribute("dentistOptions", dentistOptions);

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String fullName = req.getParameter("fullName");
        String role = req.getParameter("role");
        String dentistIdStr = req.getParameter("dentistId");

       
        StringBuilder errors = new StringBuilder();

        if (!ValidationUtil.isValidUsername(username)) {
            errors.append("Username must be 4-30 characters (letters, numbers, '.', '_' only). ");
        }
        if (!ValidationUtil.isValidName(fullName)) {
            errors.append("Full name must contain only letters and be 2-100 characters. ");
        }
        if (!ValidationUtil.isValidStaffRole(role)) {
            errors.append("Role must be either Dentist or Receptionist. ");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            errors.append("Password must be at least 8 characters. ");
        }
        if (password != null && !password.equals(confirmPassword)) {
            errors.append("Passwords do not match. ");
        }
        if (ValidationUtil.isValidUsername(username) && userDAO.usernameExists(username.trim())) {
            errors.append("That username is already taken. ");
        }

        Integer dentistId = null;
        if ("DENTIST".equals(role)) {
            if (dentistIdStr == null || dentistIdStr.trim().isEmpty()) {
                errors.append("Select which dentist record this login belongs to. ");
            } else {
                try {
                    dentistId = Integer.parseInt(dentistIdStr.trim());
                    if (!dentistDAO.existsById(dentistId)) {
                        errors.append("Selected dentist record does not exist. ");
                    } else if (dentistDAO.isLinkedToUser(dentistId)) {
                        errors.append("That dentist record is already linked to a login account. ");
                    }
                } catch (NumberFormatException e) {
                    errors.append("Invalid dentist selection. ");
                }
            }
        }

        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString());
            req.getRequestDispatcher("/register-staff.jsp").forward(req, resp);
            return;
        }

        boolean saved = userDAO.createUser(username.trim(), password, fullName.trim(), role, dentistId);

        if (saved) {
            req.setAttribute("success", "Account created for " + fullName.trim() + " (" + role + ").");
        } else {
            req.setAttribute("error", "Failed to create account. Please try again.");
        }

        req.getRequestDispatcher("/register-staff.jsp").forward(req, resp);
    }

    private boolean isAuthorised(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (!user.canManageStaff()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to register staff.");
            return false;
        }
        return true;
    }
}
