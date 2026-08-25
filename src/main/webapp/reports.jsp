<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sunrisedental.model.User" %>
<%@ page import="com.sunrisedental.dao.ReportDAO" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
    if (!user.isAdmin()) {
        response.sendError(403, "Only an Administrator can view clinic reports.");
        return;
    }
    List<ReportDAO.DentistWorkloadRow> dentistWorkload =
            (List<ReportDAO.DentistWorkloadRow>) request.getAttribute("dentistWorkload");
    List<ReportDAO.TreatmentPopularityRow> treatmentPopularity =
            (List<ReportDAO.TreatmentPopularityRow>) request.getAttribute("treatmentPopularity");
    double totalRevenueBilled = (double) request.getAttribute("totalRevenueBilled");
    int billCount = (int) request.getAttribute("billCount");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Clinic Reports - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="topbar">
    <h1>Sunrise Dental Clinic</h1>
    <span>Appointment &amp; Patient Management System</span>
</div>
<div class="form-container" style="max-width:900px">
    <h2>Clinic Reports</h2>
    <p style="font-size:12.5px;color:#607d8b;margin:4px 0 20px">
        Read-only management summaries. These aggregate existing appointment and billing
        data and do not change any records.
    </p>

    <h3>Revenue Summary</h3>
    <table class="grid-table">
        <tbody>
            <tr><td>Total bills generated</td><td><%= billCount %></td></tr>
            <tr><td>Total revenue billed</td><td>Rs. <%= String.format("%.2f", totalRevenueBilled) %></td></tr>
        </tbody>
    </table>

    <h3 style="margin-top:28px">Dentist Workload</h3>
    <table class="grid-table">
        <thead>
            <tr><th>Dentist</th><th>Scheduled</th><th>Completed</th><th>Cancelled</th><th>Total</th></tr>
        </thead>
        <tbody>
        <% if (dentistWorkload != null) {
            for (ReportDAO.DentistWorkloadRow row : dentistWorkload) { %>
            <tr>
                <td><%= row.dentistName %></td>
                <td><%= row.scheduledCount %></td>
                <td><%= row.completedCount %></td>
                <td><%= row.cancelledCount %></td>
                <td><%= row.totalCount %></td>
            </tr>
        <% } } %>
        </tbody>
    </table>

    <h3 style="margin-top:28px">Treatment Popularity</h3>
    <table class="grid-table">
        <thead>
            <tr><th>Treatment</th><th>Times Performed (Completed)</th><th>Total Fee Billed</th></tr>
        </thead>
        <tbody>
        <% if (treatmentPopularity != null) {
            for (ReportDAO.TreatmentPopularityRow row : treatmentPopularity) { %>
            <tr>
                <td><%= row.treatmentName %></td>
                <td><%= row.timesPerformed %></td>
                <td>Rs. <%= String.format("%.2f", row.totalFeeBilled) %></td>
            </tr>
        <% } } %>
        </tbody>
    </table>

    <a href="dashboard.jsp" class="back-link">&larr; Back to Dashboard</a>
</div>
</body>
</html>
