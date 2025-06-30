package com.nbu.CSCB634.service.exceptions;

public class DiplomaThesisNotFoundException extends BaseValidationException {
    public DiplomaThesisNotFoundException(Long id) {
        super(String.format("Diploma thesis with id %s is not found", id));
    }
}
