package com.nbu.CSCB634.service.exceptions;

public class BaseValidationException extends RuntimeException {
    public BaseValidationException(String msg) {
        super(msg);
    }
}
