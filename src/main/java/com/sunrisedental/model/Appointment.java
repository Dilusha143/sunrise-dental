package com.sunrisedental.model;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class Appointment {

    private String appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private int dentistId;
    private String dentistName;                 
    private List<TreatmentType> treatments = new ArrayList<>(); 
    private Date appointmentDate;
    private Time appointmentTime;
    private String status;

    public Appointment() {
    }

    public Appointment(String appointmentNumber, String patientName, String address,
                        String contactNumber, int dentistId,
                        Date appointmentDate, Time appointmentTime) {
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistId = dentistId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = "SCHEDULED";
    }

   

    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public List<TreatmentType> getTreatments() { return treatments; }
    public void setTreatments(List<TreatmentType> treatments) { this.treatments = treatments; }

   
    public String getTreatmentNamesJoined() {
        StringBuilder sb = new StringBuilder();
        for (TreatmentType t : treatments) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(t.getTreatmentName());
        }
        return sb.toString();
    }

   
    public double getTotalTreatmentFee() {
        double total = 0;
        for (TreatmentType t : treatments) {
            total += t.getFee();
        }
        return total;
    }

    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }

    public Time getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(Time appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
