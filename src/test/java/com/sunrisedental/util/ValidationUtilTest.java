package com.sunrisedental.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


class ValidationUtilTest {

   

    @ParameterizedTest
    @ValueSource(strings = {"Nimal Perera", "A. B. Silva", "Ruwan"})
    void isValidName_acceptsWellFormedNames(String name) {
        assertTrue(ValidationUtil.isValidName(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "N", "Nimal123", "Nimal_Perera", "N@me"})
    void isValidName_rejectsInvalidNames(String name) {
        assertFalse(ValidationUtil.isValidName(name));
    }

    @Test
    void isValidName_rejectsNull() {
        assertFalse(ValidationUtil.isValidName(null));
    }

    

    @ParameterizedTest
    @ValueSource(strings = {"0712345678", "0771234567", "0112345678"})
    void isValidPhone_acceptsValidSriLankanNumbers(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"712345678", "071234567", "07123456789", "+94712345678", "abcdefghij"})
    void isValidPhone_rejectsInvalidFormats(String phone) {
        assertFalse(ValidationUtil.isValidPhone(phone));
    }

    @Test
    void isValidPhone_rejectsNull() {
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    

    @ParameterizedTest
    @CsvSource({
        "'123 Main St, Colombo', true",
        "'', false",
        "'   ', false"
    })
    void isNotEmpty_behavesAsExpected(String value, boolean expected) {
        assertEquals(expected, ValidationUtil.isNotEmpty(value));
    }

    @Test
    void isNotEmpty_rejectsNull() {
        assertFalse(ValidationUtil.isNotEmpty(null));
    }

 

    @ParameterizedTest
    @ValueSource(strings = {"APT1234", "APT12345678"})
    void isValidAppointmentNumber_acceptsCorrectFormat(String number) {
        assertTrue(ValidationUtil.isValidAppointmentNumber(number));
    }

    @ParameterizedTest
    @ValueSource(strings = {"apt1234", "APT12", "12345678", "APT-1234", ""})
    void isValidAppointmentNumber_rejectsIncorrectFormat(String number) {
        assertFalse(ValidationUtil.isValidAppointmentNumber(number));
    }
}
