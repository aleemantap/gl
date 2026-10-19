package com.project.gl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
                Map<String, Object> response = Map.of(
                        "status", "ERROR",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                );

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
                Map<String, Object> response = Map.of(
                        "status", "ERROR",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) 
        {
                Map<String, Object> response = Map.of("status", "ERROR", "message", ex.getMessage(),"timestamp", LocalDateTime.now());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // @ExceptionHandler(RuntimeException.class)
        // public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        //         Map<String, Object> response = new HashMap<>();
        //         response.put("timestamp", LocalDateTime.now());
        //         response.put("message", ex.getMessage());
        //         response.put("status", HttpStatus.BAD_REQUEST.value());
        //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        // }

        // @ExceptionHandler(IllegalArgumentException.class)
        // public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        //         Map<String, Object> response = new HashMap<>();
        //         response.put("timestamp", LocalDateTime.now());
        //         response.put("message", ex.getMessage());
        //         response.put("status", HttpStatus.BAD_REQUEST.value());
        //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        // }
}