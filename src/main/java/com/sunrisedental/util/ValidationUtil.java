package com.sunrisedental.util;

import java.util.regex.Pattern;


public class ValidationUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$"); 
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z. ]{2,100}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]{4,30}$");

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidAppointmentNumber(String number) {
        return number != null && number.trim().matches("^APT[0-9A-Fa-f]{4,}$");
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    /** Roles that an administrator is permitted to register via the staff form. */
    public static boolean isValidStaffRole(String role) {
        return "DENTIST".equals(role) || "RECEPTIONIST".equals(role);
    }
}
