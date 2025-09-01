package com.ecommerceapi.ecommerceapi.validator;

import com.ecommerceapi.ecommerceapi.exception.ECommerceAPIValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EcommerceValidatorTest {

    private EcommerceValidator validator;

    @BeforeEach
    void setUp() {
        validator = new EcommerceValidator();
    }

    @Test
    void testValidate_SuccessfulTransaction() {
        // arrange
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal basketPrice = BigDecimal.valueOf(100);

        // act & assert
        assertDoesNotThrow(() -> validator.validate(amount, basketPrice));
    }

    @Test
    void testValidate_NullAmount_ThrowsException() {
        BigDecimal basketPrice = BigDecimal.valueOf(100);

        ECommerceAPIValidationException ex = assertThrows(
                ECommerceAPIValidationException.class,
                () -> validator.validate(null, basketPrice)
        );
        assertEquals("Transaction not successful !!", ex.getMessage());
    }

    @Test
    void testValidate_ZeroAmount_ThrowsException() {
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal basketPrice = BigDecimal.valueOf(100);

        ECommerceAPIValidationException ex = assertThrows(
                ECommerceAPIValidationException.class,
                () -> validator.validate(amount, basketPrice)
        );
        assertEquals("Transaction not successful !!", ex.getMessage());
    }

    @Test
    void testValidate_AmountNotEqualToBasketPrice_ThrowsException() {
        BigDecimal amount = BigDecimal.valueOf(99);
        BigDecimal basketPrice = BigDecimal.valueOf(100);

        ECommerceAPIValidationException ex = assertThrows(
                ECommerceAPIValidationException.class,
                () -> validator.validate(amount, basketPrice)
        );
        assertEquals("Transaction not successful  !!", ex.getMessage()); // Note the double space
    }
}

