package com.ssuamkiett.bookconnect.handler;

import com.ssuamkiett.bookconnect.exception.OperationNotPermittedException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static com.ssuamkiett.bookconnect.handler.ErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({LockedException.class})
    public ResponseEntity<ExceptionResponse> handleException(LockedException lockedException) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ExceptionResponse.
                        builder()
                        .errorCode(ACCOUNT_LOCKED.getCode())
                        .errorDescription(ACCOUNT_LOCKED.getDescription())
                        .error(lockedException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<ExceptionResponse> handleException(DisabledException disabledException) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ExceptionResponse.
                        builder()
                        .errorCode(ACCOUNT_DISABLED.getCode())
                        .errorDescription(ACCOUNT_DISABLED.getDescription())
                        .error(disabledException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException badCredentialsException) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(ExceptionResponse.
                        builder()
                        .errorCode(BAD_CREDENTIALS.getCode())
                        .errorDescription(BAD_CREDENTIALS.getDescription())
                        .error(badCredentialsException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ExceptionResponse> handleException(MessagingException messagingException) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.
                        builder()
                        .error(messagingException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        Set<String> errors = new HashSet<>();
        methodArgumentNotValidException.getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.
                        builder()
                        .validationError(errors)
                        .build()
                );
    }

    @ExceptionHandler({OperationNotPermittedException.class})
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException messagingException) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.
                        builder()
                        .error(messagingException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException entityNotFoundException) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(entityNotFoundException.getMessage())
                        .build());
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ExceptionResponse> handleException(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(httpRequestMethodNotSupportedException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler({WeakKeyException.class})
    public ResponseEntity<ExceptionResponse> handleException(
            WeakKeyException weakKeyException
    ) {
        logger.warn(weakKeyException.getMessage(), weakKeyException);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .error("Internal Server Error")
                        .build()
                );
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<ExceptionResponse> handleException(
            JwtException jwtException) {
        logger.warn(jwtException.getMessage(), jwtException);
        return ResponseEntity
                .status(FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .error("Invalid Token")
                        .build()
                );
    }
    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ExceptionResponse> handleException(ValidationException validationException) {
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(ExceptionResponse.
                        builder()
                        .error(validationException.getMessage() )
                        .build()
                );
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        logger.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.
                        builder()
                        .errorDescription("Internal Server Error, contact the admin email: sumitkumar7033@gmail.com")
                        .error("Something Went Wrong!")
                        .build()
                );
    }
}
