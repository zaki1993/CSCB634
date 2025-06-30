package com.nbu.CSCB869.service.exceptions;

public class StudentNotFoundException extends BaseValidationException {
    public StudentNotFoundException(String fn) {
        super(String.format("Student with faculty number %s is not found", fn));
    }
}
