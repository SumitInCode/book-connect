package com.ssuamkiett.bookconnect.handler;

import com.ssuamkiett.bookconnect.exception.OperationNotPermittedException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import static com.ssuamkiett.bookconnect.handler.ErrorCodes.*;
import static com.ssuamkiett.bookconnect.handler.ErrorCodes.INTERNAL_SERVER_ERROR;
import static com.ssuamkiett.bookconnect.handler.ErrorCodes.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({LockedException.class})
    public ResponseEntity<ExceptionResponse> handleLockedException(LockedException ex) {
        return buildResponse(ACCOUNT_LOCKED, ex.getMessage(), FORBIDDEN);
    }

    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<ExceptionResponse> handleDisabledException(DisabledException ex) {
        return buildResponse(ACCOUNT_DISABLED, ex.getMessage(), FORBIDDEN);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return buildResponse(BAD_CREDENTIALS, ex.getMessage(), FORBIDDEN);
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ExceptionResponse> handleMessagingException(MessagingException ex) {
        return buildResponse(INTERNAL_SERVER_ERROR, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return buildResponse(null, ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({OperationNotPermittedException.class})
    public ResponseEntity<ExceptionResponse> handleOperationNotPermittedException(OperationNotPermittedException ex) {
        return buildResponse(OPERATION_NOT_PERMITTED, ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponse(ENTITY_NOT_FOUND, ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(METHOD_NOT_ALLOWED, ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ExceptionResponse> handleInvalidApiPath(NoResourceFoundException ex) {
        return buildResponse(METHOD_NOT_ALLOWED, ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({WeakKeyException.class})
    public ResponseEntity<ExceptionResponse> handleWeakKeyException(WeakKeyException ex) {
        logger.warn(ex.getMessage(), ex);
        return buildResponse(INTERNAL_SERVER_ERROR, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<ExceptionResponse> handleJwtException(JwtException ex) {
        logger.warn(ex.getMessage(), ex);
        return buildResponse(null, "Invalid Token", FORBIDDEN);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ExceptionResponse> handleValidationException(ValidationException ex) {
        return buildResponse(null, ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleGeneralException(Exception ex) {
        logger.warn(ex.getMessage(), ex);
        return buildResponse(INTERNAL_SERVER_ERROR, "Server Error", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error, contact the admin email: ssuamkiett@gmail.com");
    }

    private ResponseEntity<ExceptionResponse> buildResponse(ErrorCodes errorCode, String errorMessage, HttpStatus httpStatus) {
        return buildResponse(errorCode, errorMessage, httpStatus, null);
    }

    private ResponseEntity<ExceptionResponse> buildResponse(ErrorCodes errorCode, String errorMessage, HttpStatus httpStatus, String detailedMessage) {
        ExceptionResponse response = ExceptionResponse.builder()
                .errorCode(errorCode != null ? errorCode.getCode() : null)
                .errorDescription(errorCode != null ? errorCode.getDescription() : null)
                .error(errorMessage)
                .contactMessage(detailedMessage)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }
}
