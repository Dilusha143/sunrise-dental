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
        response.sendError(403, "You do not have permission to register staff.");
        return;
    }
    List<Dentist> dentistOptions = (List<Dentist>) request.getAttribute("dentistOptions");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Register Staff - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Register Dentist / Receptionist</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <p class="success"><%= request.getAttribute("success") %></p>
    <% } %>

    <form action="register-staff" method="post" id="staffForm">
        <label>Full Name:</label>
        <input type="text" name="fullName" required>

        <label>Role:</label>
        <select name="role" id="roleSelect" required onchange="toggleDentistLink()">
            <option value="RECEPTIONIST">Receptionist</option>
            <option value="DENTIST">Dentist</option>
        </select>

        <div id="dentistLinkGroup" style="display:none">
            <label>Link to Dentist Record:</label>
            <select name="dentistId">
                <option value="">-- Select the dentist this login belongs to --</option>
                <% if (dentistOptions != null) {
                    for (Dentist d : dentistOptions) { %>
                        <option value="<%= d.getDentistId() %>">
                            <%= d.getFullName() %><% if (d.getSpecialization() != null) { %> (<%= d.getSpecialization() %>)<% } %>
                        </option>
                <% } } %>
            </select>
            <p style="font-size:12.5px;color:#607d8b;margin:4px 0 16px">
                This links the new login to that row in the dentists roster, so appointments
                and the "My Appointments" view can be tied back to this account.
            </p>
        </div>

        <label>Username:</label>
        <input type="text" name="username" required>

        <label>Password:</label>
        <input type="password" name="password" minlength="8" required>

        <label>Confirm Password:</label>
        <input type="password" name="confirmPassword" minlength="8" required>

        <button type="submit">Register Account</button>
    </form>
    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
<script>
    function toggleDentistLink() {
        var role = document.getElementById('roleSelect').value;
        var group = document.getElementById('dentistLinkGroup');
        var dentistSelect = group.querySelector('select[name="dentistId"]');
        if (role === 'DENTIST') {
            group.style.display = 'block';
            dentistSelect.required = true;
        } else {
            group.style.display = 'none';
            dentistSelect.required = false;
        }
    }
    toggleDentistLink();
</script>
</body>
</html>
