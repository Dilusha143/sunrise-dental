<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
    if (!user.isDentist() && !user.isReceptionist()) {
        response.sendError(403, "You do not have permission to view the appointments list.");
        return;
    }
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    if (selectedStatus == null) selectedStatus = "ALL";
    String selectedScope = (String) request.getAttribute("selectedScope");
    if (selectedScope == null) selectedScope = "all";
%>
<!DOCTYPE html>
<html>
<head>
    <title>Appointments - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container wide">
    <h2>Appointments</h2>

    <form action="appointments" method="get" class="filter-bar">
        <label>Status:</label>
        <select name="status" onchange="this.form.submit()">
            <option value="ALL" <%= "ALL".equals(selectedStatus) ? "selected" : "" %>>All</option>
            <option value="SCHEDULED" <%= "SCHEDULED".equals(selectedStatus) ? "selected" : "" %>>Scheduled</option>
            <option value="COMPLETED" <%= "COMPLETED".equals(selectedStatus) ? "selected" : "" %>>Completed</option>
            <option value="CANCELLED" <%= "CANCELLED".equals(selectedStatus) ? "selected" : "" %>>Cancelled</option>
        </select>

        <% if (user.isLinkedToDentist()) { %>
            <label style="margin-left:14px">Show:</label>
            <select name="scope" onchange="this.form.submit()">
                <option value="all" <%= "all".equals(selectedScope) ? "selected" : "" %>>All dentists</option>
                <option value="mine" <%= "mine".equals(selectedScope) ? "selected" : "" %>>My appointments only</option>
            </select>
        <% } %>
        <noscript><button type="submit">Filter</button></noscript>
    </form>

    <% if (appointments == null || appointments.isEmpty()) { %>
        <p class="empty-state">No appointments found for this filter.</p>
    <% } else { %>
        <table class="grid-table">
            <thead>
                <tr>
                    <th>Appointment #</th>
                    <th>Patient</th>
                    <th>Dentist</th>
                    <th>Treatment</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for (Appointment appt : appointments) { %>
                    <tr>
                        <td><%= appt.getAppointmentNumber() %></td>
                        <td><%= appt.getPatientName() %></td>
                        <td><%= appt.getDentistName() %></td>
                        <td><%= appt.getTreatmentNamesJoined() %></td>
                        <td><%= appt.getAppointmentDate() %></td>
                        <td><%= appt.getAppointmentTime() %></td>
                        <td><span class="status-pill <%= appt.getStatus() %>"><%= appt.getStatus() %></span></td>
                        <td>
                            <% boolean isPureDentist = "DENTIST".equals(user.getRole());
                               boolean isPureReceptionist = "RECEPTIONIST".equals(user.getRole());
                               boolean ownsAppointment = !isPureDentist
                                       || (user.getDentistId() != null && user.getDentistId() == appt.getDentistId());
                               if ("SCHEDULED".equals(appt.getStatus()) && ownsAppointment) { %>
                                <% if (!isPureReceptionist) { %>
                                    <form action="update-appointment-status" method="post" class="inline-action-form">
                                        <input type="hidden" name="appointmentNumber" value="<%= appt.getAppointmentNumber() %>">
                                        <input type="hidden" name="newStatus" value="COMPLETED">
                                        <button type="submit" class="btn-small btn-complete"
                                                onclick="return confirm('Mark this appointment as completed?');">Complete</button>
                                    </form>
                                <% } %>
                                <form action="update-appointment-status" method="post" class="inline-action-form">
                                    <input type="hidden" name="appointmentNumber" value="<%= appt.getAppointmentNumber() %>">
                                    <input type="hidden" name="newStatus" value="CANCELLED">
                                    <button type="submit" class="btn-small btn-cancel"
                                            onclick="return confirm('Cancel this appointment?');">Cancel</button>
                                </form>
                            <% } else { %>
                                &mdash;
                            <% } %>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>

    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
