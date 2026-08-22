package com.sunrisedental.servlet;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;


public class BillingServlet extends HttpServlet {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;

        String appointmentNumber = req.getParameter("appointmentNumber");
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            req.getRequestDispatcher("/billing.jsp").forward(req, resp);
            return;
        }

        
        renderBill(req, resp, appointmentNumber, false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isAuthorised(req, resp)) return;

        String appointmentNumber = req.getParameter("appointmentNumber");
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            req.setAttribute("error", "Please provide an appointment number.");
            req.getRequestDispatcher("/billing.jsp").forward(req, resp);
            return;
        }

       
        renderBill(req, resp, appointmentNumber, true);
    }

    private void renderBill(HttpServletRequest req, HttpServletResponse resp,
                             String appointmentNumber, boolean persistAndPrint)
            throws ServletException, IOException {

        Appointment appt = appointmentDAO.findByNumber(appointmentNumber.trim());
        if (appt == null) {
            req.setAttribute("error", "No appointment found with number: " + appointmentNumber);
            req.getRequestDispatcher("/billing.jsp").forward(req, resp);
            return;
        }

        
        Bill bill = billDAO.generateBill(appointmentNumber.trim());
        if (bill == null) {
            req.setAttribute("error", "This appointment was cancelled - no bill can be generated.");
            req.getRequestDispatcher("/billing.jsp").forward(req, resp);
            return;
        }

        if (persistAndPrint) {
            billDAO.save(bill);
            req.setAttribute("justRecorded", true);
        }

        req.setAttribute("appointment", appt);
        req.setAttribute("bill", bill);
        req.getRequestDispatcher("/billing.jsp").forward(req, resp);
    }

    private boolean isAuthorised(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        if (!user.isReceptionist()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to bill patients.");
            return false;
        }
        return true;
    }
}
