package com.nbu.CSCB634.service.exceptions;

public class StudentNotFoundException extends BaseValidationException {
    public StudentNotFoundException(String fn) {
        super(String.format("Student with faculty number %s is not found", fn));
    }
}
