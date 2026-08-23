<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.Appointment" %>
<%@ page import="com.sunrisedental.model.Bill" %>
<!DOCTYPE html>
<html>
<head>
    <title>Billing - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container">
    <h2>Calculate &amp; Print Bill</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <form action="billing" method="get">
        <label>Appointment Number:</label>
        <input type="text" name="appointmentNumber" placeholder="e.g. APT1A2B3C4D" required>
        <button type="submit">Generate Bill</button>
    </form>

    <%
        Appointment appt = (Appointment) request.getAttribute("appointment");
        Bill bill = (Bill) request.getAttribute("bill");
        if (appt != null && bill != null) {
    %>
        <div class="printable-bill">
            <h3>Sunrise Dental Clinic - Receipt</h3>
            <p>Appointment Number: <%= appt.getAppointmentNumber() %></p>
            <p>Patient: <%= appt.getPatientName() %></p>
            <p>Dentist: <%= appt.getDentistName() %></p>
            <p>Status: <span class="status-pill <%= appt.getStatus() %>"><%= appt.getStatus() %></span></p>
            <p>Treatments: <%= appt.getTreatmentNamesJoined() %></p>
            <% if (!"COMPLETED".equals(appt.getStatus())) { %>
                <p class="error" style="margin-bottom:14px">
                    This appointment has not been marked Completed yet, so only the consultation
                    fee is billed. Treatment fees will be added once the dentist completes the procedure.
                </p>
            <% } %>
            <table class="details-table">
                <tr><th>Treatment Fees (total)</th><td>Rs. <%= String.format("%.2f", bill.getTreatmentFee()) %></td></tr>
                <tr><th>Consultation Fee</th><td>Rs. <%= String.format("%.2f", bill.getConsultationFee()) %></td></tr>
                <tr><th>Total</th><td><b>Rs. <%= String.format("%.2f", bill.getTotalAmount()) %></b></td></tr>
            </table>

            <% if (Boolean.TRUE.equals(request.getAttribute("justRecorded"))) { %>
                <p class="success" style="margin-top:10px">
                    Bill recorded and added to the clinic's Revenue Summary.
                </p>
                <script>window.onload = function() { window.print(); };</script>
            <% } else { %>
                <p style="font-size:12.5px;color:#607d8b;margin:10px 0">
                    This is a preview only - it has not been recorded yet. Click "Print Receipt" below
                    to record this bill and print it.
                </p>
                <form action="billing" method="post">
                    <input type="hidden" name="appointmentNumber" value="<%= appt.getAppointmentNumber() %>">
                    <button type="submit">Print Receipt</button>
                </form>
            <% } %>
        </div>
    <% } %>

    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
