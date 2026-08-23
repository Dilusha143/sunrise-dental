<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.dao.AppointmentDAO" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
    // Read-only, non-mutating count query - deliberately kept as a direct
    // DAO call from this single JSP rather than routed through a new
    // Servlet, to avoid touching the login redirect target and every
    // "Back to Dashboard" link elsewhere in the app for one small banner.
    // Every data-mutating operation in the system still goes through a
    // Servlet; this is the one intentional, documented exception.
    Integer dentistFilter = user.isLinkedToDentist() ? user.getDentistId() : null;
    int todaysCount = new AppointmentDAO().countTodaysScheduled(dentistFilter);
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Welcome, <%= user.getFullName() %></h2>
    <span class="badge"><%= user.getRole() %></span>
    <% if (user.isLinkedToDentist() && user.getDentistSpecialization() != null) { %>
        <span class="badge" style="background:#e0f2f1;color:#00695c">Roster: <%= user.getDentistSpecialization() %></span>
    <% } %>

    <% if (todaysCount > 0 && (user.isDentist() || user.isReceptionist())) { %>
        <p class="success" style="margin-top:14px">
            <% if (user.isLinkedToDentist()) { %>
                You have <%= todaysCount %> appointment<%= todaysCount == 1 ? "" : "s" %> scheduled today.
            <% } else { %>
                The clinic has <%= todaysCount %> appointment<%= todaysCount == 1 ? "" : "s" %> scheduled today.
            <% } %>
        </p>
    <% } %>

    <ul class="menu">
        <% if (user.canRegisterAppointments()) { %>
            <li><a href="register-appointment">Register New Appointment</a></li>
        <% } %>
        <li><a href="search-appointment">Search / Display Appointment</a></li>
        <% if (user.isDentist() || user.isReceptionist()) { %>
            <li><a href="appointments">View Appointments</a></li>
        <% } %>
        <% if (user.isReceptionist()) { %>
            <li><a href="billing">Calculate &amp; Print Bill</a></li>
        <% } %>
        <% if (user.canManageStaff()) { %>
            <li><a href="register-staff">Register Dentist / Receptionist</a></li>
            <li><a href="add-dentist">Add Dentist to Roster</a></li>
            <li><a href="reports">Clinic Reports</a></li>
        <% } %>
        <li><a href="help.jsp">Help</a></li>
        <li><a href="logout">Exit / Logout</a></li>
    </ul>
</div>
</body>
</html>
