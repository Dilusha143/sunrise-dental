package com.sunrisedental.model;

public class Bill {

    private int billId;
    private String appointmentNumber;
    private double treatmentFee;
    private double consultationFee;
    private double totalAmount;

    public Bill() {
    }

    public Bill(String appointmentNumber, double treatmentFee, double consultationFee) {
        this.appointmentNumber = appointmentNumber;
        this.treatmentFee = treatmentFee;
        this.consultationFee = consultationFee;
        this.totalAmount = treatmentFee + consultationFee;
    }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public double getTreatmentFee() { return treatmentFee; }
    public void setTreatmentFee(double treatmentFee) { this.treatmentFee = treatmentFee; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
