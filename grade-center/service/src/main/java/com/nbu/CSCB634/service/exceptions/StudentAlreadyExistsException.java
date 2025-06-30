package com.nbu.CSCB634.service.exceptions;

public class StudentAlreadyExistsException extends BaseValidationException {
    public StudentAlreadyExistsException(String fn) {
        super(String.format("Student with faculty number %s already exists", fn));
    }
}
