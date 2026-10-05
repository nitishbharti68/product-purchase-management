package com.nitish.product_purchase_management.exception;

public class InsufficientStockException
        extends RuntimeException {

    public InsufficientStockException(
            String message) {
        super(message);
    }
}
