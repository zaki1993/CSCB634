package com.nbu.CSCB869.service.exceptions;

public class BaseValidationException extends RuntimeException {
    public BaseValidationException(String msg) {
        super(msg);
    }
}
