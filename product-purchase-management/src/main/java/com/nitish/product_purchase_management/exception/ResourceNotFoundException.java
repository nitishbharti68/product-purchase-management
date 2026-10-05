package com.nitish.product_purchase_management.exception;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException(
            String message) {
        super(message);
    }
}
