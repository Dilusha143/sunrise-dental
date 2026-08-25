package com.sunrisedental.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PasswordUtilTest {

    @Test
    void hash_isDeterministic_sameInputProducesSameHash() {
        String hash1 = PasswordUtil.hash("admin123");
        String hash2 = PasswordUtil.hash("admin123");
        assertEquals(hash1, hash2);
    }

    @Test
    void hash_producesA64CharacterHexString() {
        String hash = PasswordUtil.hash("admin123");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("^[0-9a-f]{64}$"));
    }

    @Test
    void hash_differentInputsProduceDifferentHashes() {
        String hash1 = PasswordUtil.hash("admin123");
        String hash2 = PasswordUtil.hash("admin124");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void hash_neverReturnsThePlaintextItself() {
        String plain = "admin123";
        String hash = PasswordUtil.hash(plain);
        assertNotEquals(plain, hash);
    }
}
