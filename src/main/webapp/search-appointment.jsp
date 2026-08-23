<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Search Appointment</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <% String lastSearched = (String) request.getAttribute("lastSearchedAppointment"); %>
    <form action="search-appointment" method="get">
        <label>Appointment Number:</label>
        <input type="text" name="appointmentNumber" placeholder="e.g. APT1A2B3C4D"
               value="<%= lastSearched != null ? lastSearched : "" %>" required>
        <button type="submit">Search</button>
    </form>
    <% if (lastSearched != null && request.getAttribute("appointment") == null && request.getAttribute("error") == null) { %>
        <p style="font-size:12px;color:#607d8b;margin-top:-8px">
            Pre-filled with your last searched appointment number (remembered via cookie).
        </p>
    <% } %>

    <% Appointment appt = (Appointment) request.getAttribute("appointment");
       if (appt != null) { %>
        <table class="details-table">
            <tr><th>Appointment Number</th><td><%= appt.getAppointmentNumber() %></td></tr>
            <tr><th>Patient Name</th><td><%= appt.getPatientName() %></td></tr>
            <tr><th>Address</th><td><%= appt.getAddress() %></td></tr>
            <tr><th>Contact Number</th><td><%= appt.getContactNumber() %></td></tr>
            <tr><th>Dentist</th><td><%= appt.getDentistName() %></td></tr>
            <tr><th>Treatments / Consultation</th><td><%= appt.getTreatmentNamesJoined() %></td></tr>
            <tr><th>Total Treatment Fee</th><td>Rs. <%= String.format("%.2f", appt.getTotalTreatmentFee()) %></td></tr>
            <tr><th>Date</th><td><%= appt.getAppointmentDate() %></td></tr>
            <tr><th>Time</th><td><%= appt.getAppointmentTime() %></td></tr>
            <tr><th>Status</th><td><%= appt.getStatus() %></td></tr>
        </table>
    <% } %>

    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
