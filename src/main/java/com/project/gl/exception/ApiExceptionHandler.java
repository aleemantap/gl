package com.project.gl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.gl.model.response.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        if (request.getRequestURI().startsWith("/api/")) {

            ApiErrorResponse response = new ApiErrorResponse(
                false,
                ex.getMessage()
            );

            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
        }

        throw ex;
    }

    @ExceptionHandler(InvalidApiClientException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidApiClient(
            InvalidApiClientException ex) {

        ApiErrorResponse response =
            new ApiErrorResponse(
                false,
                ex.getMessage()
            );

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }
}