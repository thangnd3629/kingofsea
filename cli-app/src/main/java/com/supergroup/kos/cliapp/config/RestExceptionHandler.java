package com.supergroup.kos.cliapp.config;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * All exception are gathered here, so feel free to throw it any where you want
 */
@RestControllerAdvice(basePackages = { "com.supergroup.admin.api" })
public class
RestExceptionHandler {
    private final Logger logger = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(KOSException.class)
    public ResponseEntity<KOSExceptionResponse> bitplayException(KOSException ex) {
        return new ResponseEntity<>(new KOSExceptionResponse()
                                            .status(ex.getCode())
                                            .timestamp(LocalDateTime.now()),
                                    HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<BitplayExceptionResponse> otherException(Exception ex) {
//        return new ResponseEntity<>(new BitplayExceptionResponse()
//                                            .status(ErrorCode.SERVER_ERROR)
//                                            .data(List.of(ex))
//                                            .timestamp(LocalDateTime.now()),
//                                    HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<KOSExceptionResponse> validationException(MethodArgumentNotValidException ex) {
        // throw it by default, spring boot validation takecare of it
        logger.error(ex);
        return new ResponseEntity<>(new KOSExceptionResponse()
                                            .status(ErrorCode.BAD_REQUEST_ERROR)
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

    @ExceptionHandler(UnexpectedRollbackException.class)
    public ResponseEntity<KOSExceptionResponse> unExpectedRollback(UnexpectedRollbackException ex) {
        return new ResponseEntity<>(HttpStatus.OK);
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
        private List<Object> data;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        private LocalDateTime timestamp;
    }

    // Define more exception handler here
}
