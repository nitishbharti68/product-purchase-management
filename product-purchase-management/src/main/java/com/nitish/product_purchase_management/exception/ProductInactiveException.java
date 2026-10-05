package com.nitish.product_purchase_management.exception;

public class ProductInactiveException
        extends RuntimeException {

    public ProductInactiveException(
            String message) {
        super(message);
    }
}
