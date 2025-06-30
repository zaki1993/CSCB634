package com.nbu.CSCB634.service.exceptions;

public class ThesisReviewNotFoundException extends BaseValidationException {
    public ThesisReviewNotFoundException(Long id) {
        super(String.format("Thesis review with id %s is not found", id));
    }
}
