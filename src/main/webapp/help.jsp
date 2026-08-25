<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Help - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Help - How to Use the System</h2>
    <ol>
        <li><b>Login:</b> Enter your staff username and password. Contact Admin if you don't have an account.</li>
        <li><b>Register New Appointment:</b> (Receptionist) Fill in patient details, choose a dentist, then tick one or more treatments/consultation from the checklist. A unique appointment number is generated automatically.</li>
        <li><b>Search Appointment:</b> Enter the appointment number (format APTxxxxxxxx) to view full patient and appointment details.</li>
        <li><b>View Appointments:</b> (Dentist/Receptionist) Browse all appointments in a sortable grid, filterable by status (Scheduled, Completed, Cancelled). A Dentist login linked to a roster record can also switch to "My appointments only".</li>
        <li><b>Calculate &amp; Print Bill:</b> (Receptionist/Admin) Enter the appointment number to generate a bill combining the treatment fee and consultation fee, then print it.</li>
        <li><b>Register Dentist / Receptionist:</b> (Admin) Create new staff login accounts. When the role is Dentist, the admin must also link the account to a record in the dentist roster.</li>
        <li><b>Exit:</b> Click Logout to safely end your session.</li>
    </ol>
    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
