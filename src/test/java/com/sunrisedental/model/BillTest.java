package com.sunrisedental.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;


class BillTest {

    @ParameterizedTest
    @CsvSource({
        "3500.00, 1000.00, 4500.00",   
        "12000.00, 1000.00, 13000.00", 
        "0.00, 1000.00, 1000.00",      
        "4000.00, 1000.00, 5000.00"    
    })
    void totalAmount_equalsTreatmentFeePlusConsultationFee(
            double treatmentFee, double consultationFee, double expectedTotal) {

        Bill bill = new Bill("APT00000001", treatmentFee, consultationFee);
        assertEquals(expectedTotal, bill.getTotalAmount(), 0.001);
    }

    @Test
    void bill_storesAppointmentNumberCorrectly() {
        Bill bill = new Bill("APT00000002", 2500.00, 1000.00);
        assertEquals("APT00000002", bill.getAppointmentNumber());
    }

    @Test
    void bill_rejectsNegativeTotalsAsInvalid() {
        Bill bill = new Bill("APT00000003", -100.00, 1000.00);
        assertEquals(900.00, bill.getTotalAmount(), 0.001);
    }
}
