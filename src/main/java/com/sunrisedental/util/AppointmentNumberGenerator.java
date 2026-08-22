package com.sunrisedental.util;

import java.util.UUID;


public class AppointmentNumberGenerator {

    private AppointmentNumberGenerator() {
        
    }

    public static String generate() {
        return "APT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
