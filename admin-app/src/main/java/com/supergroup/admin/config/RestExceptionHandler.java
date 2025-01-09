package com.supergroup.admin.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.exception.EffectIsActivatedException;

import io.jsonwebtoken.ExpiredJwtException;
import io.sentry.Sentry;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * All exception are gathered here, so feel free to throw it any where you want
 */
@RestControllerAdvice(basePackages = { "com.supergroup.admin.api" })
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(KOSException.class)
    public ResponseEntity<KOSExceptionResponse> kosException(KOSException ex) {
        return new ResponseEntity<>(new KOSExceptionResponse()
                                            .status(ex.getCode())
                                            .timestamp(LocalDateTime.now()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EffectIsActivatedException.class)
    public ResponseEntity<EffectIsActivatedExceptionResponse> effectIsActivatedException(EffectIsActivatedException ex) {
        return new ResponseEntity<>(new EffectIsActivatedExceptionResponse()
                                            .status(ex.getCode())
                                            .timestamp(LocalDateTime.now()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<KOSExceptionResponse> validationException(MethodArgumentNotValidException ex) {
        // throw it by default, spring boot validation takecare of it
        ex.printStackTrace();
        return new ResponseEntity<>(new KOSExceptionResponse()
                                            .status(ErrorCode.BAD_REQUEST_ERROR)
                                            .data(List.of(ex.getMessage()))
                                            .timestamp(LocalDateTime.now()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<KOSExceptionResponse> expiredJwtException(ExpiredJwtException ex) {
        // throw it by default, spring boot validation takecare of it
        return new ResponseEntity<>(new KOSExceptionResponse()
                                            .status(ErrorCode.TOKEN_EXPIRED)
                                            .timestamp(LocalDateTime.now()),
                                    HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ BindException.class })
    public ResponseEntity<KOSExceptionResponse> validationException(BindException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new KOSExceptionResponse()
                                            .status(ErrorCode.BAD_REQUEST_ERROR)
                                            .data(List.of(ex.getMessage()))
                                            .timestamp(LocalDateTime.now()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<KOSExceptionResponse> handleException(Exception exception) {
        if (exception instanceof KOSException) {
            exception.printStackTrace();
            return new ResponseEntity<>(new KOSExceptionResponse()
                                                .status(ErrorCode.SERVER_ERROR)
                                                .data(List.of(exception.getMessage()))
                                                .timestamp(LocalDateTime.now()),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            exception.printStackTrace();
            Sentry.captureException(exception);
            return new ResponseEntity<>(new KOSExceptionResponse()
                                                .status(ErrorCode.SERVER_ERROR)
                                                .data(List.of(exception.getMessage()))
                                                .timestamp(LocalDateTime.now()),
                                        HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Response data
     */
    @Data
    @Accessors(fluent = true)
    class KOSExceptionResponse {
        @JsonProperty
        private ErrorCode status;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty(required = false)
        private Object data;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime timestamp;
    }

    @Data
    @Accessors(fluent = true)
    class EffectIsActivatedExceptionResponse {
        @JsonProperty
        private String status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime timestamp;
    }
}
