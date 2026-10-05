package com.nitish.product_purchase_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(DuplicateEmailException.class)
        public ResponseEntity<String> handleDuplicateEmail(
                        DuplicateEmailException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> handleIllegalArgument(
                        IllegalArgumentException ex) {

                return ResponseEntity
                                .badRequest()
                                .body(ex.getMessage());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                return ResponseEntity
                                .badRequest()
                                .body(errors);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<String> handleInvalidCredentials(
                        InvalidCredentialsException ex) {

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<String> handleResourceNotFound(
                        ResourceNotFoundException ex) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(InsufficientStockException.class)
        public ResponseEntity<String> handleInsufficientStock(
                        InsufficientStockException ex) {
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(ProductInactiveException.class)
        public ResponseEntity<String> handleProductInactive(
                        ProductInactiveException ex) {
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ex.getMessage());
        }

        @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
        public ResponseEntity<String> handleOptimisticLockingFailure(
                        org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(
                                                "Product stock changed while purchasing. "
                                                                + "Please try again.");
        }

        @ExceptionHandler(PasswordChangeException.class)
        public ResponseEntity<String> handlePasswordChange(
                        PasswordChangeException ex) {

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ex.getMessage());
        }

}