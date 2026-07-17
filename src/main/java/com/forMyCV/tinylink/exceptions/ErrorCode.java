package com.forMyCV.tinylink.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    VALIDATION_FAILED("VALIDATION_FAILED","Validation failed for the request", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Cannot find user with the provided '%s' ", NOT_FOUND),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    CUSTOM_ALIAS_EXISTS("CUSTOM_ALIAS_EXISTS", "Custom alias already exists", HttpStatus.BAD_REQUEST),


    AWS_FILE_UPLOAD_FAILED("AWS_FILE_UPLOAD_FAILED", "Failed to upload file to AWS S3", HttpStatus.INTERNAL_SERVER_ERROR),
    AWS_FILE_DELETE_FAILED("AWS_FILE_DELETE_FAILED", "Failed to delete file from AWS S3", HttpStatus.INTERNAL_SERVER_ERROR),


    USER_EMAIL_ALREADY_EXISTS("USER_EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.CONFLICT),
    USER_EMAIL_NOT_FOUND("USER_EMAIL_NOT_FOUND", "User Not Found", NOT_FOUND),
    ACCOUNT_NON_ACTIVE("ACCOUNT_NON_ACTIVE", "Account not active", FORBIDDEN),
    USER_INCORRECT_PASSWORD("USER_INCORRECT_PASSWORD", "Incorrect password", HttpStatus.UNAUTHORIZED),
    ROLE_NAME_ALREADY_EXISTS("ROLE_NAME_ALREADY_EXISTS","Role with name '%s' already exists",HttpStatus.CONFLICT),
    ROLE_NAME_NOT_FOUND("ROLE_NAME_NOT_FOUND","Role with Name '%s' not found",HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND","Role not found with the given id",HttpStatus.NOT_FOUND),

    NO_SHORTENED_URLS_FOUND("NO_SHORTENED_URLS_FOUND", "No shortened URLs were found for the current user.", NOT_FOUND),
    SHORT_CODE_NOT_FOUND("SHORT_CODE_NOT_FOUND", "Cannot find short code with the provided '%s' ", NOT_FOUND),
    SHORT_CODE_NOT_ACTIVE("SHORT_CODE_NOT_ACTIVE", "this short code is not active '%s' ", BAD_REQUEST),
    SHORT_CODE_IS_EXPIRED("SHORT_CODE_IS_EXPIRED", "this short code is expired '%s' ", BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }

}
