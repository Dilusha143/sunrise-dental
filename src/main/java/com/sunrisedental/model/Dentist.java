package com.sunrisedental.model;


public class Dentist {

    private int dentistId;
    private String fullName;
    private String specialization;

    public Dentist() {
    }

    public Dentist(int dentistId, String fullName, String specialization) {
        this.dentistId = dentistId;
        this.fullName = fullName;
        this.specialization = specialization;
    }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
}
