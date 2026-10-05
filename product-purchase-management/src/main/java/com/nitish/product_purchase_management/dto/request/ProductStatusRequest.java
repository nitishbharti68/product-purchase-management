package com.nitish.product_purchase_management.dto.request;

import jakarta.validation.constraints.NotNull;

public class ProductStatusRequest {

    @NotNull(message = "Active status is required")
    private Boolean active;

    public ProductStatusRequest() {
    }

    public ProductStatusRequest(
            Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(
            Boolean active) {
        this.active = active;
    }
}