package com.sunrisedental.servlet;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.TreatmentType;
import com.sunrisedental.model.User;
import com.sunrisedental.util.ValidationUtil;
import com.sunrisedental.util.AppointmentNumberGenerator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class RegisterAppointmentServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final DentistDAO dentistDAO = new DentistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;
        req.setAttribute("treatmentOptions", treatmentDAO.findAll());
        req.setAttribute("dentistOptions", dentistDAO.findAll());
        req.getRequestDispatcher("/register-appointment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;

        List<TreatmentType> treatmentOptions = treatmentDAO.findAll();
        req.setAttribute("treatmentOptions", treatmentOptions);
        req.setAttribute("dentistOptions", dentistDAO.findAll());

        String patientName = req.getParameter("patientName");
        String address = req.getParameter("address");
        String contactNumber = req.getParameter("contactNumber");
        String dentistIdStr = req.getParameter("dentistId");
        String[] treatmentIdStrs = req.getParameterValues("treatmentIds");
        String dateStr = req.getParameter("appointmentDate");
        String timeStr = req.getParameter("appointmentTime");

        
        StringBuilder errors = new StringBuilder();

        if (!ValidationUtil.isValidName(patientName)) {
            errors.append("Patient name must contain only letters and be 2-100 characters. ");
        }
        if (!ValidationUtil.isNotEmpty(address)) {
            errors.append("Address is required. ");
        }
        if (!ValidationUtil.isValidPhone(contactNumber)) {
            errors.append("Contact number must be a valid 10-digit number starting with 0. ");
        }
        if (!ValidationUtil.isNotEmpty(dentistIdStr)) {
            errors.append("Dentist must be selected. ");
        }
        if (treatmentIdStrs == null || treatmentIdStrs.length == 0) {
            errors.append("Select at least one treatment or consultation. ");
        }
        if (!ValidationUtil.isNotEmpty(dateStr) || !ValidationUtil.isNotEmpty(timeStr)) {
            errors.append("Appointment date and time are required. ");
        }

        if (errors.length() > 0) {
            req.setAttribute("error", errors.toString());
            req.getRequestDispatcher("/register-appointment.jsp").forward(req, resp);
            return;
        }

        try {
            Appointment appt = new Appointment();
            appt.setAppointmentNumber(AppointmentNumberGenerator.generate());
            appt.setPatientName(patientName.trim());
            appt.setAddress(address.trim());
            appt.setContactNumber(contactNumber.trim());
            appt.setDentistId(Integer.parseInt(dentistIdStr));
            appt.setAppointmentDate(Date.valueOf(dateStr));
            appt.setAppointmentTime(Time.valueOf(timeStr + ":00"));
            appt.setStatus("SCHEDULED");

            List<TreatmentType> selected = new ArrayList<>();
            for (String idStr : treatmentIdStrs) {
                int id = Integer.parseInt(idStr);
                for (TreatmentType option : treatmentOptions) {
                    if (option.getTreatmentId() == id) {
                        selected.add(option);
                        break;
                    }
                }
            }
            appt.setTreatments(selected);

            boolean saved = appointmentDAO.save(appt);

            if (saved) {
                req.setAttribute("success", "Appointment registered successfully. Number: " + appt.getAppointmentNumber());
            } else {
                req.setAttribute("error", "Failed to save appointment. Please try again.");
            }

        } catch (com.sunrisedental.dao.DoubleBookingException e) {
           
            req.setAttribute("error", "This dentist already has an appointment at that date and time. Please choose a different slot.");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Invalid date, time, or numeric field format.");
        }

        req.getRequestDispatcher("/register-appointment.jsp").forward(req, resp);
    }

    private boolean isAuthorised(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (!user.canRegisterAppointments()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to register appointments.");
            return false;
        }
        return true;
    }
}
