package com.nbu.CSCB869.service.exceptions;

import com.nbu.CSCB869.service.exceptions.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
/*
    @ExceptionHandler(BaseValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorDto> handleBaseValidationException(BaseValidationException ex) {
        return ResponseEntity.unprocessableEntity().body(createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()));
    }*/

    @ExceptionHandler(BaseValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleBaseValidationException(BaseValidationException ex) {
        log.trace("Validation exception ", ex);
        return "404";
        //return ResponseEntity.unprocessableEntity().body(createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()));
    }

    private ErrorDto createHttpErrorInfo(HttpStatus httpStatus, String message) {
        log.debug("Returning HTTP status: {}, message: {}", httpStatus, message);
        return new ErrorDto(String.valueOf(httpStatus.value()), httpStatus.getReasonPhrase(), message);
    }


    /**
     * Handles all unexpected exceptions and redirects to a user-friendly error page.
     *8
     * @param model The model to add attributes.
     * @return The error page template.
     */
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception ex, Model
            model) {
        log.trace("An unexpected error occurred: ", ex);

        // Add a user-friendly error message to the model
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");

        return "404";
        //return "error";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return "404";
    }

    /**
     * Handles 404 errors (Page Not Found) and displays a friendly 404 page.
     *
     * @return The 404 error page template.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Page not found.");
        return "404";
    }
}
