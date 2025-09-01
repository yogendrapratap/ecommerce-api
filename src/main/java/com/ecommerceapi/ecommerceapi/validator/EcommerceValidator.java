package com.ecommerceapi.ecommerceapi.validator;

import com.ecommerceapi.ecommerceapi.entity.Product;
import com.ecommerceapi.ecommerceapi.exception.ECommerceAPIValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EcommerceValidator {

    public void validate(BigDecimal amount, BigDecimal basketPrice) {
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) == 0){
            throw new ECommerceAPIValidationException("Transaction not successful !!");
        }

        if (amount.compareTo(basketPrice) !=0){
            throw new ECommerceAPIValidationException("Transaction not successful  !!");
        }
    }

    public void validateProduct(Product products) {
        if (products == null) {
            throw new ECommerceAPIValidationException("Product not found");
        }
    }
}

