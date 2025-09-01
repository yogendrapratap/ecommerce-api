package com.ecommerceapi.ecommerceapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class HandelExceptionController {

    @ExceptionHandler(ECommerceAPIValidationException.class)
    public ErrorMessage handleProjectValidationException(ECommerceAPIValidationException ex, WebRequest request) {
        ex.printStackTrace();
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(Exception.class)
    public ErrorMessage handleProjectValidationException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
    }
}
