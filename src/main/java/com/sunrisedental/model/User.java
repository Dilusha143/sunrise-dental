package com.sunrisedental.model;


public class User {

    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String role; 
    private Integer dentistId; 
    private String dentistSpecialization; 

    public User() {
    }

    public User(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getDentistId() { return dentistId; }
    public void setDentistId(Integer dentistId) { this.dentistId = dentistId; }

    public String getDentistSpecialization() { return dentistSpecialization; }
    public void setDentistSpecialization(String dentistSpecialization) { this.dentistSpecialization = dentistSpecialization; }

    /** True when this login is tied to a row in the dentists roster table. */
    public boolean isLinkedToDentist() { return dentistId != null; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isReceptionist() { return "RECEPTIONIST".equals(role) || isAdmin(); }
    public boolean isDentist() { return "DENTIST".equals(role) || isAdmin(); }

   
    public boolean canRegisterAppointments() { return "RECEPTIONIST".equals(role); }

   
    public boolean canManageStaff() { return isAdmin(); }
}
