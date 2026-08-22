package com.sunrisedental.dto;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.TreatmentType;

import java.util.ArrayList;
import java.util.List;


public class AppointmentResponseDTO {

    private String appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private List<String> treatments;
    private double treatmentFee;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    public AppointmentResponseDTO() {
    }

    public static AppointmentResponseDTO fromModel(Appointment a) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.appointmentNumber = a.getAppointmentNumber();
        dto.patientName = a.getPatientName();
        dto.address = a.getAddress();
        dto.contactNumber = a.getContactNumber();
        dto.dentistName = a.getDentistName();

        List<String> names = new ArrayList<>();
        for (TreatmentType t : a.getTreatments()) {
            names.add(t.getTreatmentName());
        }
        dto.treatments = names;
        dto.treatmentFee = a.getTotalTreatmentFee();

        dto.appointmentDate = a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : null;
        dto.appointmentTime = a.getAppointmentTime() != null ? a.getAppointmentTime().toString() : null;
        dto.status = a.getStatus();
        return dto;
    }

    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }

    public List<String> getTreatments() { return treatments; }
    public void setTreatments(List<String> treatments) { this.treatments = treatments; }

    public double getTreatmentFee() { return treatmentFee; }
    public void setTreatmentFee(double treatmentFee) { this.treatmentFee = treatmentFee; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
