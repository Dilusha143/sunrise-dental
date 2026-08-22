package com.sunrisedental.dto;

import java.util.List;


public class AppointmentRequestDTO {

    private String patientName;
    private String address;
    private String contactNumber;
    private int dentistId;
    private List<Integer> treatmentIds;
    private String appointmentDate; 
    private String appointmentTime; 

    public AppointmentRequestDTO() {
    }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public List<Integer> getTreatmentIds() { return treatmentIds; }
    public void setTreatmentIds(List<Integer> treatmentIds) { this.treatmentIds = treatmentIds; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
}
