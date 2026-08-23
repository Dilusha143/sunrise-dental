<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.TreatmentType" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="java.util.List" %>
<%
    List<TreatmentType> treatmentOptions = (List<TreatmentType>) request.getAttribute("treatmentOptions");
    List<Dentist> dentistOptions = (List<Dentist>) request.getAttribute("dentistOptions");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Register Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Register New Appointment</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <p class="success"><%= request.getAttribute("success") %></p>
    <% } %>

    <form action="register-appointment" method="post">
        <label>Patient Name:</label>
        <input type="text" name="patientName" required>

        <label>Address:</label>
        <input type="text" name="address" required>

        <label>Contact Number (e.g. 0712345678):</label>
        <input type="text" name="contactNumber" required>

        <label>Dentist:</label>
        <select name="dentistId" required>
            <option value="">-- Select a dentist --</option>
            <% if (dentistOptions != null) {
                for (Dentist d : dentistOptions) { %>
                    <option value="<%= d.getDentistId() %>">
                        <%= d.getFullName() %><%= d.getSpecialization() != null ? " - " + d.getSpecialization() : "" %>
                    </option>
            <% } } %>
        </select>

        <label>Treatment / Consultation (select one or more):</label>
        <div class="checkbox-group">
            <% if (treatmentOptions != null) {
                for (TreatmentType t : treatmentOptions) { %>
                    <label class="checkbox-option">
                        <input type="checkbox" name="treatmentIds" value="<%= t.getTreatmentId() %>">
                        <%= t.getTreatmentName() %> - Rs. <%= String.format("%.2f", t.getFee()) %>
                    </label>
            <% } } %>
        </div>

        <label>Appointment Date:</label>
        <input type="date" name="appointmentDate" required>

        <label>Appointment Time:</label>
        <input type="time" name="appointmentTime" required>

        <button type="submit">Register</button>
    </form>
    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
