<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.model.Dentist" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
    if (!user.canManageStaff()) {
        response.sendError(403, "You do not have permission to manage the dentist roster.");
        return;
    }
    List<Dentist> dentistOptions = (List<Dentist>) request.getAttribute("dentistOptions");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Dentist - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Add Dentist to Roster</h2>
    <p style="font-size:12.5px;color:#607d8b;margin:4px 0 16px">
        This creates a new row in the clinical dentist roster. It does not create a login
        account &mdash; once added, use "Register Dentist / Receptionist" to give this
        dentist system access.
    </p>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <p class="success"><%= request.getAttribute("success") %></p>
    <% } %>

    <form action="add-dentist" method="post">
        <label>Full Name:</label>
        <input type="text" name="fullName" required>

        <label>Specialization (optional):</label>
        <input type="text" name="specialization">

        <button type="submit">Add Dentist</button>
    </form>

    <h3 style="margin-top:32px">Current Roster</h3>
    <table class="grid-table">
        <thead>
            <tr><th>Full Name</th><th>Specialization</th></tr>
        </thead>
        <tbody>
        <% if (dentistOptions != null) {
            for (Dentist d : dentistOptions) { %>
            <tr>
                <td><%= d.getFullName() %></td>
                <td><%= d.getSpecialization() != null ? d.getSpecialization() : "-" %></td>
            </tr>
        <% } } %>
        </tbody>
    </table>

    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
