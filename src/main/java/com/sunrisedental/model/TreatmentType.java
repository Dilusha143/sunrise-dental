package com.sunrisedental.model;


public class TreatmentType {

    private int treatmentId;
    private String treatmentName;
    private double fee;

    public TreatmentType() {
    }

    public TreatmentType(int treatmentId, String treatmentName, double fee) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.fee = fee;
    }

    public int getTreatmentId() { return treatmentId; }
    public void setTreatmentId(int treatmentId) { this.treatmentId = treatmentId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
}
