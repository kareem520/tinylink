package com.forMyCV.tinylink.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException ex) {

        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(errorCode.getCode())
                .build();

        log.info("Business Exception: {}", errorResponse);
        log.debug(ex.getMessage(), ex);

        return ResponseEntity.status(errorCode.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {

        final ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

        final List<ErrorResponse.ValidationError> errorsList = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            final String fieldName = fieldError.getField();
            final String message = fieldError.getDefaultMessage();

            errorsList.add(ErrorResponse.ValidationError.builder()
                            .message(message)
                            .field(fieldName)
                            .code(fieldError.getCode())
                    .build());});

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.toString())
                .code(errorCode.getCode())
                .validationErrorList(errorsList)
                .build();

        log.info("MethodArgumentNotValid Exception: {}", errorResponse);
        log.debug(errorCode.getDefaultMessage(), errorCode);

        return ResponseEntity.status(errorCode.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(final BadCredentialsException exception) {

        ErrorCode errorCode = ErrorCode.BAD_CREDENTIALS;

        final ErrorResponse response = ErrorResponse.builder()
                .message(errorCode.getDefaultMessage())
                .code(errorCode.getCode())
                .build();

            log.info("BadCredentialsException: {}", response);
            log.debug(exception.getMessage(), exception);

        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex) {

        ErrorResponse response = ErrorResponse.builder()
                .code("ACCESS_DENIED")
                .message("Access denied")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(final Exception ex) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(errorCode.getDefaultMessage())
                .code(errorCode.getCode())
                .build();

        log.info("Unknown Exception: {}", errorResponse);
        log.debug(errorCode.getDefaultMessage(), ex);
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(errorCode.getStatus())
                .body(errorResponse);
    }


    //payment

}
