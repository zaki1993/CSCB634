package com.nbu.CSCB634.service.exceptions;

public class UserAlreadyExistException extends BaseValidationException {
    public UserAlreadyExistException(String msg) {
        super(msg);
    }
}
