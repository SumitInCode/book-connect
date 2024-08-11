package com.ssuamkiett.BookConnect.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCodes {
    NO_CODE(0, "No Code", NOT_IMPLEMENTED),

    ACCOUNT_LOCKED(302, "User account is locked", FORBIDDEN),

    INCORRECT_CURRENT_PASSWORD(300, "Current password is incorrect", BAD_REQUEST),

    ACCOUNT_DISABLED(303, "User account is disabled", FORBIDDEN),

    BAD_CREDENTIALS(304, "Username or Password not correct", FORBIDDEN),

    NEW_PASSWORD_DOES_NOT_MATCH(301, "The new password does not match", BAD_REQUEST);

    @Getter
    private final int code;

    @Getter
    private final String description;

    @Getter
    private final HttpStatus httpStatus;

    ErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
