package com.klem.catalog.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> handleHttpMessageNotReadable(final HttpMessageNotReadableException e,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {
        e.printStackTrace();
        ErrorResponse errorDetails = new ErrorResponse(new Date(), "Bad JSON sent", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ResourceConflictException.class, ConstraintViolationException.class})
    public ResponseEntity<?> resourceConflictException(Exception ex, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleCustomException(final javax.validation.ConstraintViolationException exception) {
        final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        final String errors =  violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(""));
        final ErrorResponse errorDetails = new ErrorResponse(new Date(), errors, "");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleGlobalExceptions(MethodArgumentNotValidException ex,
                                                           WebRequest request) {
        String[] errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
        ErrorResponse errorDetails = new ErrorResponse(new Date(), "Bad Request", errors);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
