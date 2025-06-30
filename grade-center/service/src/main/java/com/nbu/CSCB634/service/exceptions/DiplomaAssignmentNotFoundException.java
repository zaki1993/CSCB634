package com.nbu.CSCB634.service.exceptions;

public class DiplomaAssignmentNotFoundException extends BaseValidationException {
    public DiplomaAssignmentNotFoundException(Long id) {
        super(String.format("Diploma assignment with id %s is not found", id));
    }
}
